/**
 * 
 */
package com.example.rovercontrol.io;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.api.PwmOutput;

/**
 * @author kyle
 *
 */
public class GrabberPiston {
	public void reset(IOIO ioio) {
		_ioio = ioio;
		_ready = true;
	}
	public GrabberPiston(int pin) {
		_pin = pin;
		_ready = false;
	}
	
	public boolean isAvailable() {
		return _ready;
	}
	
	public boolean grab() {
		if(_ready) {
			try {
				_servo = _ioio.openPwmOutput(_pin, 100);
				_servo.setPulseWidth(PISTON_DOWN);
				Thread.sleep(PISTON_PAUSE);
				_servo.setPulseWidth(PISTON_UP);
				Thread.sleep(PISTON_PAUSE);
				_servo.setPulseWidth(PISTON_DOWN);
				Thread.sleep(PISTON_PAUSE);
				_servo.setPulseWidth(PISTON_UP);
				Thread.sleep(PISTON_PAUSE);
				_servo.close();
				return true;
			} catch (ConnectionLostException e) {
				_ready = false;
			} catch (InterruptedException e) {
				_ready = false;
			}
		}
		return false;
	}
	private final int PISTON_DOWN = 1800;
	private final int PISTON_UP = 1200;
	//private final int PISTON_IDLE = 1500;
	private final int PISTON_PAUSE = 250;

	private PwmOutput _servo;
	private IOIO _ioio;
	private int _pin;
	private boolean _ready;
}
