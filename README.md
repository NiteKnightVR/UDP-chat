# UDP-chat
Server Client chat program using UDP socket  

Usage:  
1. compile java code  
2. run Server using: java Server `max sequence number`, will listen on port 7777  
3. run Client using: java Client `address`  

Client messages have "DATA" and a sequence number (starts at 0) appended to them.  
Server checks for "DATA" tag and matching sequence number, resending previous ACK if they do not match.  
Client has timeout window of 5 seconds at which point it will resend the missing msg.  
