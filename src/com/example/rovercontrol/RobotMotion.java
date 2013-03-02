/**
 * 
 */
package com.example.rovercontrol;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * @author kyle
 *
 */
public class RobotMotion {
	
	private MotorDriver driver_;
	private PidController pid_;
	private Timer pidTimer_;
	
	private final int TX_PIN_ = 5;
	private final int RX_PIN_ = 6;
	private final int ST_ADDR_ = 129;
	private final double ONE_RPS = 0.1;
	private final double WHEEL_CIRC = 0.20;
	
	private final double MAX_RADPS_ = 12.0;
	private final double P_GAIN_ = 1.0;
	private final double I_GAIN_ = 1.0;
	private final double D_GAIN_ = 1.0;
	
	private class PidTask_ extends TimerTask {
		@Override
		public void run() {
			try {
				// TODO get error from gyro
				driver_.setSpeed(pid_.UpdatePID(0));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public RobotMotion(IOIO ioio) throws ConnectionLostException {
		driver_ = new MotorDriver(ioio, RX_PIN_, TX_PIN_, ST_ADDR_);
		pid_ = new PidController();
		pid_.Reset(0, MAX_RADPS_, 0, P_GAIN_, I_GAIN_, D_GAIN_);
		pidTimer_ = new Timer();
		pidTimer_.scheduleAtFixedRate(new PidTask_(), 0, 10000);
	}
	/**
	 * 
	 * @param mps speed in approximate meters/second
	 */
	public void setSpeed(double mps) {
		try {
			driver_.setSpeed((mps / WHEEL_CIRC) * ONE_RPS);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param radps rotation speed in radians/second
	 */
	public void setRotationSpeed(double radps) {
		
	}
}
