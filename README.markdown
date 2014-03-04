# XMPP2APNS Plugin Readme

Overview

This plugin is to send APNS notifications to Apple Devices.

## Installation

Login into OpenFire Admin, go to "Plugins" and select xmpp2apns.jar file upload plugin section. If plugin is already installed then uninstall it first.
As soon as the plug-in is installed , the database will automatically generate a table called ofAPNS .
And, copy the P12 certificate file from APNS to /usr/local/openfire.

## Configuration

No configuration required.

## Using the Plugin

Openfire forwards messages to this plugin which are forwarded to APNS (Apple Push Notification Server). 
Here are IQ request formats 

### Register device token

<iq type="set" to="OPENFIRE_SERVER" id="apns68057d6a">
<query xmlns="urn:xmpp:apns">
<token> XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX </ token>
</ query>
</ iq>

### The return value 
<iq type="result" id="OPENFIRE_SERVER" from="210.205.58.23" to="user@OPENFIRE_SERVER/68057d6a">
<query xmlns="urn:xmpp:apns">
<token> XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX </ token>
</ query>
</ iq>

### Get device token

<iq type="get" to="OPENFIRE_SERVER" id="apns68057d6a">
<query xmlns="urn:xmpp:apns"/>
</ iq>

### The return value

<iq type="result" id="apns68057d6a" from="OPENFIRE_SERVER" to="user@OPENFIRE_SERVER/68057d6a">
<query xmlns="urn:xmpp:apns">
<token> XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX </ token>
</ query>
</ iq>

## See Also


The module that the request from the APNS Openfire ('Javapns' http://code.google.com/p/javapns/) using October 
Openfire plugins are compiled to create a jar package (http://community.igniterealtime.org/docs/DOC-1020) for reference. 