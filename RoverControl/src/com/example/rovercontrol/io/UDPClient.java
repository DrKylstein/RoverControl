package com.example.rovercontrol.io;

import java.io.*; 
import java.net.*;
 


// instantiate UDPClient
// SetState
// SendState

public class UDPClient { 
	
	// States: Wait, Task, Finished, Enroute
	
	private String outmessage;
	private int cPort;
	private String cServerHostName;
	
	public UDPClient(int Port, String serverHostName)
	{
		
		//127.0.0.1, localhost
		
		outmessage = "Test";
		cPort = Port;
		cServerHostName = serverHostName;
	}
	
	public void setMessage(String message)
	{
		outmessage = message;
	}

    public void udpClientSendReceive() throws Exception 
    { 
    	try
    	{
		      DatagramSocket clientSocket = new DatagramSocket(); 
		  
		      InetAddress IPAddress = InetAddress.getByName(cServerHostName); 
		      System.out.println ("Attemping to connect to " + IPAddress + ") via UDP port " + cPort + ".");
		  
		      byte[] sendData = new byte[1024]; 
		      byte[] receiveData = new byte[1024]; 
		  
		      String sentence = outmessage;
		      sendData = sentence.getBytes();         
		
		      System.out.println ("Sending data to " + sendData.length + " bytes to server.");
		      
		      
		      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, cPort); 
		  
		      clientSocket.send(sendPacket); 
		  
		      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
		  
		      System.out.println ("Waiting for return packet");   
		      
		      clientSocket.setSoTimeout(7000);
		
		      try 
		      {
		           clientSocket.receive(receivePacket); 
		           String modifiedSentence = 
		               new String(receivePacket.getData()); 
		  
		           InetAddress returnIPAddress = receivePacket.getAddress();
		     
		           int port = receivePacket.getPort();
		
		           System.out.println ("From server at: " + returnIPAddress + ":" + port);
		           System.out.println("Message: " + modifiedSentence); 
		           System.out.println("\n"); 
		
		      }
		      catch (SocketTimeoutException ste)
		      {
		           System.out.println ("Timeout Occurred: Packet assumed lost");
		           System.out.println("\n"); 
		      }
		  
		      clientSocket.close(); 
		      
		 }
		 catch (UnknownHostException ex) 
		 { 
		     System.err.println(ex);
		 }
		 catch (IOException ex) 
		 {
		     System.err.println(ex);
		 }
    	
    	
    } // END udpClientSendReceive
     
    
} // END CLASS UDPClient

