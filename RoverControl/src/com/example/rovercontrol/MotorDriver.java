package com.example.rovercontrol;

import java.io.IOException;
import java.io.OutputStream;

import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;

public class MotorDriver {
	
	private IOIO ioio;
	private Uart uart;
	private OutputStream out;

	/**
	 * 
	 * @param ioioInstance IOIO object providing the UART
	 * @param txPin pin connected to compatible UART input
	 * @param AddressIn Address of compatible device
	 * @throws ConnectionLostException
	 * @throws IOException 
	 */
	public MotorDriver(IOIO ioioInstance, int txPin) throws ConnectionLostException, IOException
	{
		ioio = ioioInstance;
		uart = ioio.openUart(IOIO.INVALID_PIN, txPin, 9600, Uart.Parity.NONE, Uart.StopBits.ONE);
		out = uart.getOutputStream();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setSpeed(double speed) throws IOException
	{
		sendPacket(speed, 1);
	}
	
	public void setRotationSpeed(double rotationalSpeed) throws IOException
	{
		sendPacket(rotationalSpeed, 0);
	}
	
	private void sendPacket(double input, int command) throws IOException
	{
		int value = 0;
		if(input < 0) {
			value = (int) (62-(Math.abs(input)*62));
		} else {
			value = (int) (63+(input*64));
		}
		if(command == 1) value |= 0x80;
		out.write(value);
	}
	
}
