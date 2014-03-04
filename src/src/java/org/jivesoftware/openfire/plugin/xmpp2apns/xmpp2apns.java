package org.jivesoftware.openfire.plugin;

import org.apache.commons.httpclient.*; 
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.MessageRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.IQRouter;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.vcard.VCardManager;
import org.dom4j.Element;
import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserNotFoundException;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.database.SequenceManager;
import org.jivesoftware.util.NotFoundException;

import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


public class xmpp2apns implements Plugin, PacketInterceptor {
	
	private static final Logger Log = LoggerFactory.getLogger(xmpp2apns.class);
	
    private InterceptorManager interceptorManager;
    
    private xmpp2apnsDBHandler dbManager;
    
    private VCardManager vcardManager;
    
    private UserManager userManager;
    
    private PresenceManager presenceManager;
    
    private XMPPServer server;
    
    public xmpp2apns() {
        interceptorManager = InterceptorManager.getInstance();
        dbManager = new xmpp2apnsDBHandler();       
    }
    
	public void initializePlugin(PluginManager pManager, File pluginDirectory) {		

        interceptorManager.addInterceptor(this);
        server = XMPPServer.getInstance();
        presenceManager = server.getPresenceManager();
        vcardManager = VCardManager.getInstance();
        userManager = server.getUserManager();
        IQHandler myHandler = new xmpp2apnsIQHandler();
        IQRouter iqRouter = server.getIQRouter();       
        iqRouter.addHandler(myHandler);
    }
	
	public void destroyPlugin() {
        presenceManager= null;
        vcardManager = null;
        userManager =null;
        server = null;
        interceptorManager.removeInterceptor(this);
    }
	
	public void interceptPacket(Packet packet, Session session, boolean read, boolean processed) throws PacketRejectedException {
		
		if(isValidTargetPacket(packet,read,processed)) {
			Packet original = packet;			
						
			if(original instanceof Message) {
				Message receivedMessage = (Message)original;
				
				JID targetJID = receivedMessage.getTo();
				
				User user = null;
				try{
					user = userManager.getUser(targetJID.getNode());
				} catch(UserNotFoundException unf){
					Log.error(unf.getMessage(), "");
				}
				if ( user != null && presenceManager.isAvailable(user)) {
					//Log.error("User "+targetJID.toBareJID()+" is online", "");
					return;
				}
				
				String deviceToken = dbManager.getDeviceToken(targetJID);
				if(deviceToken == null) return;
								
				String body = receivedMessage.getBody();
				if ( body == null ) return; 
				
				JID fromJID = receivedMessage.getFrom();
				String fromBareId = fromJID.toBareJID();
				String[] userID = fromBareId.split("@");
				
				String username;
				if( userID[0] == null ) { 
					username = new String("-");
				} else {
					username = new String(userID[0]);
				}
				
				String nickName = vcardManager.getVCardProperty(username, "NICKNAME");
				if (nickName == null) {
					nickName = username;
				}
				
				String payloadString = nickName;
				payloadString = payloadString.concat(": ");
				payloadString = payloadString.concat(body);

				//Log.error("Sending =>"+ payloadString+"to"+deviceToken, "");
				
				pushMessage message = new pushMessage(payloadString, -1, "default", "/usr/local/openfire/directxmpp.p12", "123789", true, deviceToken);

				message.start();		
			}			
			
		}	
		
	
	}
	
	private boolean isValidTargetPacket(Packet packet, boolean read, boolean processed) {
        return  !processed && read && packet instanceof Message;
    }
}