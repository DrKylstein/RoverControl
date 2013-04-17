package com.example.rovercontrol.io;

import java.io.IOException;
import java.io.OutputStream;

import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;

public class MotorDriver {
	
	private IOIO ioio;
	private Uart uart;
	private OutputStream out;
	private boolean _ready;
	private int _txPin;

	/**
	 * 
	 * @param ioioInstance IOIO object providing the UART
	 * @param txPin pin connected to compatible UART input
	 * @param AddressIn Address of compatible device
	 * @throws ConnectionLostException
	 * @throws IOException 
	 */
	public MotorDriver(int txPin) {
		_ready = false;
		_txPin = txPin;
	}
	
	public void reset(IOIO ioioInstance) {
		ioio = ioioInstance;
		try {
			uart = ioio.openUart(IOIO.INVALID_PIN, _txPin, 9600, Uart.Parity.NONE, Uart.StopBits.ONE);
			out = uart.getOutputStream();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			_ready = true;
		} catch (ConnectionLostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			_ready = false;
		}

	}
	
	public boolean isAvailable() {
		return _ready;
	}
	
	public boolean setSpeed(double speed)
	{
		if(!_ready) return false;
		if(speed > 1) speed = 1;
		if(speed < -1) speed = -1; 
		try {
			sendPacket(speed, 1);
		} catch (IOException e) {
			_ready = false;
			return false;
		}
		return true;
	}
	
	public boolean setRotationSpeed(double rotationalSpeed)
	{
		if(!_ready) return false;
		if(rotationalSpeed > 1) rotationalSpeed = 1;
		if(rotationalSpeed < -1) rotationalSpeed = -1;
		try {
			sendPacket(rotationalSpeed, 0);
		} catch (IOException e) {
			_ready = false;
			return false;
		}
		return true;
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
