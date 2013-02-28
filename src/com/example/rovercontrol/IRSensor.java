/**
 * 
 */
package com.example.rovercontrol;

import ioio.lib.api.IOIO;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * @author kyle
 * Represents analog range-finder attached to IOIO 
 */
public class IRSensor {
	public IRSensor(IOIO ioio, int pin) throws ConnectionLostException {
		input_ = ioio.openAnalogInput(pin);
	}
	/**
	 * Gets raw analog value
	 * @return read voltage / reference voltage as double (0.0 to 1.0)
	 * @throws InterruptedException
	 * @throws ConnectionLostException
	 */
	public double voltage() throws InterruptedException, ConnectionLostException {
		return (1 / input_.read()) - 0.42;
	}
	/**
	 * Gets distance in centimeters, not tested!
	 * Constants based on GPD120, need to add configuration to support others! 
	 * @return double centimeters
	 * @throws InterruptedException
	 * @throws ConnectionLostException
	 */
	public double centimeters() throws InterruptedException, ConnectionLostException {
		return (1 / input_.read()) - 0.42;
	}
	
	private AnalogInput input_;
}
