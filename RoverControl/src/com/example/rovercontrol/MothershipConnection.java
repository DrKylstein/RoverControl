package com.example.rovercontrol;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * TODO: multicast addresses, phones as AP (direct connect?) string format w/ begin/end, UDP?
 */

public class MothershipConnection implements Runnable {
	
	public ConcurrentLinkedQueue<String> messagesFromServer = new ConcurrentLinkedQueue<String>();
	private InetSocketAddress serverAddress;
	private boolean stop_ = false;
	
	public void stop() {
		stop_ = true;
	}
	public void reset() {
		stop_ = false;
	}
	
	public void run() {
		try {
			serverAddress = new InetSocketAddress(InetAddress.getByName("192.168.10.101"), 6502);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		Socket clientSocket = new Socket();
		DataOutputStream outToServer = null;
		BufferedReader inFromServer = null;
		while(!stop_) { 
			//keep trying to connect
			while(!clientSocket.isConnected() || outToServer == null || inFromServer == null) {
				if(stop_) return;
				try {
					clientSocket.connect(serverAddress);
					outToServer = new DataOutputStream(clientSocket.getOutputStream());
					inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				} catch (UnknownHostException e) {
					e.printStackTrace();
					outToServer = null;
					inFromServer = null;
				} catch (IOException e) {
					e.printStackTrace();
					outToServer = null;
					inFromServer = null;
				}
			}
			try {
				//if(inFromServer.ready()) {
					//messagesFromServer.add(inFromServer.readLine());
				//} else {
					outToServer.writeBytes("Ping");
					Thread.sleep(500);
				//}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
