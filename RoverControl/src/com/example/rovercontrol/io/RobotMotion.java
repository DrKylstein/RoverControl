/**
 * 
 */
package com.example.rovercontrol.io;

import java.util.Timer;
import java.util.TimerTask;

import com.example.rovercontrol.control.PID;

/**
 * @author kyle
 *
 */
public class RobotMotion{
	
	public MotorDriver driver;
	private PID _pid;
	private Timer _pidTimer;
	
	private double _actualRotationSpeed;
	private double _speed;
	private double _targetRotation;
	private double _lastPIDResult;
	
	private RobotOrientation _orientation;
	
	private final double _P_GAIN = 1.0;//0.06;
	private final double _I_GAIN = 0.0;
	private final double _D_GAIN = 0.0;//0.005;
	private final double _INTERVAL = 0.01;
	
	private class PidTask_ extends TimerTask {
		@Override
		public void run() {
			_actualRotationSpeed = _orientation.getOrientation()[2];
			_lastPIDResult = _pid.update(_actualRotationSpeed);
			driver.setRotationSpeed(_lastPIDResult);
			driver.setSpeed(_speed);
		}

	}
	
	public void startPID() {
		_pidTimer = new Timer();
		_pidTimer.scheduleAtFixedRate(new PidTask_(), 0, (long) (_INTERVAL*1000));
	}
	
	public void stopPID() {
		_pidTimer.cancel();
	}
	
	public RobotMotion(MotorDriver driver_, RobotOrientation orientation) {
		driver = driver_;
		_pid = new PID(_P_GAIN, _I_GAIN, _D_GAIN, _INTERVAL);
		startPID();
		//_pidTimer = new Timer();
		//_pidTimer.scheduleAtFixedRate(new PidTask_(), 0, (long) (_INTERVAL*1000));
		_orientation = orientation;
	}

	public boolean isAvailable() {
		return driver.isAvailable();
	}
	
	/**
	 * 
	 * @param speed in proportion of maximum
	 */
	public void setSpeed(double speed) {
		_speed = speed; //driver_.setSpeed(mps);//driver_.setSpeed((mps / WHEEL_CIRC) * ONE_RPS); //mps speed in approximate meters/second
	}
	/**
	 * 
	 * @param radps rotation speed in radians/second
	 */
	public void setRotationSpeed(double radps) {
		_targetRotation = radps;
		_pid.setTarget(_targetRotation);
	}
	
	public double getSpeed() {
		return _speed;
	}
	
	public double getTargetRotation() {
		return _targetRotation;
	}	
	public double getActualRotation() {
		return _actualRotationSpeed;
	}
	public double getLastPID() {
		return _lastPIDResult;
	}
	

}
