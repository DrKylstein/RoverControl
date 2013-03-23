package com.example.rovercontrol;

import java.io.IOException;
import java.io.OutputStream;

import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;

public class SabertoothDriver {
	
	private IOIO ioio;
	private Uart uart;
	private OutputStream out;
	private int Address;

	/**
	 * 
	 * @param ioioInstance IOIO object providing the UART
	 * @param txPin pin connected to Sabertooth compatible UART input
	 * @param AddressIn Address of Sabertooth compatible device
	 * @throws ConnectionLostException
	 * @throws IOException 
	 */
	public SabertoothDriver(IOIO ioioInstance, int txPin, int AddressIn) throws ConnectionLostException, IOException
	{
		ioio = ioioInstance;
		uart = ioio.openUart(IOIO.INVALID_PIN, txPin, 9600, Uart.Parity.NONE, Uart.StopBits.ONE);
		out = uart.getOutputStream();
		Address = AddressIn;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.write(170); //send baud rate detection byte
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setSpeed(double speed) throws IOException
	{
		int command = 8;
		int speedInput = 0;
		
		if (speed < 0)
		{
			command = 9;
		}
		else
		{
			command = 8;
		}
		
		speedInput = (int)(Math.abs(speed) * 127);
		sendPacket(speedInput, command);
	}
	
	public void setRotationSpeed(double rotationalSpeed) throws IOException
	{
		int command = 8;
		int speedInput = 0;
		
		if (rotationalSpeed < 0)
		{
			command = 11;
		}
		else
		{
			command = 10;
		}
		
		speedInput = (int)(Math.abs(rotationalSpeed) * 127);
		sendPacket(speedInput, command);
	}
	
	private void sendPacket(int input, int command) throws IOException
	{
		int mask = 127;
		
		input = 64;
		command = 0;
		
		int checksum = (Address+command+input) & mask;
		
		System.out.printf("rover_motor_driver_debug: %d %d %d %d\n", Address, command, input, checksum);
		
		//I can output any binary data, any number of times, it will always end with 0xC2!?
		//doesn't happen, at least not consistently, when with ascii
		
		//out.write(170);
		out.write(Address);
		out.write(command);
		out.write(input);
		out.write(checksum); 
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
