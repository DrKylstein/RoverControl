/**
 * 
 */
package com.example.rovercontrol.io;

import ioio.lib.api.IOIO;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * @author kyle
 * Represents analog range-finder attached to IOIO 
 */
public class IRSensor {
	public IRSensor(int pin) {
		_pin = pin;
		_ready = false;
	}
	public void reset(IOIO ioio) {
		try {
			_input = ioio.openAnalogInput(_pin);
			_ready = true;
		} catch (ConnectionLostException e) {
			_ready = false;
		}
		
	}
	public boolean isAvailable() {
		return _ready;
	}
	/**
	 * Gets raw analog value
	 * @return read voltage / reference voltage as double (0.0 to 1.0)
	 */
	public double voltage() {
		if(_ready) {
			try {
				return (1 / _input.read()) - 0.42;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConnectionLostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0.0;
	}
	/**
	 * Gets distance in centimeters, not tested!
	 * Constants based on GPD120, need to add configuration to support others! 
	 * @return double centimeters
	 */
	public double centimeters() {
		return (1 / voltage()) - 0.42;
	}
	
	private AnalogInput _input;
	private int _pin;
	private boolean _ready;
}
