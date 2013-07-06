package com.example.rovercontrol.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Multicast implements Runnable{
	
	private InetAddress group;
	private MulticastSocket s;
	private DatagramPacket currentCommand;
	private byte[] buffer;
	private int port;
    
	
	
	public Multicast( String address, int _port) {
		
		port = _port;
		
		buffer = new byte[10*1024];
		try {
			s = new MulticastSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentCommand = new DatagramPacket(buffer, buffer.length);
		try {
			group = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			s.joinGroup(group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	public void send(String msg) {
		DatagramPacket packIt = new DatagramPacket(msg.getBytes(), msg.length(), group, port);
		try {
			s.send(packIt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void leaveGroup() throws IOException {
		s.leaveGroup(group);
	}
	
	public String getCurrentCommand() {
		return new String(buffer, 0, currentCommand.getLength());
	}

	@Override
	public void run() {
		while (true) {
            try {
				s.receive(currentCommand);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println("Received: "+ 
               (new String(buffer, 0, currentCommand.getLength())));
         }
		
	}

}
