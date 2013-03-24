/**
 * 
 */
package com.example.rovercontrol;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.api.PwmOutput;

/**
 * @author kyle
 *
 */
public class GrabberPiston {
	public GrabberPiston(IOIO ioio, int pin) {
		ioio_ = ioio;
		pin_ = pin;
	}
	public void grab() throws ConnectionLostException, InterruptedException {
		servo_ = ioio_.openPwmOutput(pin_, 100);
		servo_.setPulseWidth(PISTON_DOWN);
		Thread.sleep(PISTON_PAUSE);
		servo_.setPulseWidth(PISTON_UP);
		Thread.sleep(PISTON_PAUSE);
		servo_.setPulseWidth(PISTON_DOWN);
		Thread.sleep(PISTON_PAUSE);
		servo_.setPulseWidth(PISTON_UP);
		Thread.sleep(PISTON_PAUSE);
		servo_.close();
	}
	private final int PISTON_DOWN = 1800;
	private final int PISTON_UP = 1200;
	//private final int PISTON_IDLE = 1500;
	private final int PISTON_PAUSE = 250;

	private PwmOutput servo_;
	private IOIO ioio_;
	int pin_;
}
