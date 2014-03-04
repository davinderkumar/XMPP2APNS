package org.jivesoftware.openfire.plugin;

import javapns.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class pushMessage extends Thread {

	String message;
	int badge;
	String sound;
	Object keystore;
	String password;
	boolean production;
	String token;
	
	private static final Logger Log = LoggerFactory.getLogger(xmpp2apns.class); 
	
	pushMessage(String message, int badge, String sound, Object keystore, String password, boolean production, String token ) {

	this.message = message;
	this.badge = badge;
	this.sound = sound;
	this.keystore = keystore;
	this.password = password;
	this.production = production;
	this.token = token;
	
	}

	public void run() {
		try {
			Push.combined(message, badge, sound, keystore, password, production, token);
		} catch (Exception e){
			Log.error(e.getMessage(), "");
		} 
	}

}