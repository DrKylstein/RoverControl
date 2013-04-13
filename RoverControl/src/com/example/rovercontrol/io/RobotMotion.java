/**
 * 
 */
package com.example.rovercontrol.io;

import java.util.Timer;
import java.util.TimerTask;

import com.example.rovercontrol.control.PID;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;

/**
 * @author kyle
 *
 */
public class RobotMotion{
	
	private MotorDriver driver_;
	private PID pid_;
	private Timer pidTimer_;
	
	private double _actualRotationSpeed;
	private double _speed;
	private double _targetRotation;
	private double _lastPIDResult;
	
	private RobotOrientation orientation_;
	
	private final double P_GAIN_ = 0.06;
	private final double I_GAIN_ = 0.0;
	private final double D_GAIN_ = 0.005;
	private final double _INTERVAL = 0.01;
	
	private class PidTask_ extends TimerTask {
		@Override
		public void run() {
			_actualRotationSpeed = orientation_.getOrientation()[2];
			System.out.printf("rover_debug gyro: %f", _actualRotationSpeed);
			//driver_.setRotationSpeed(pid_.UpdatePID(rotationSpeed_ - actualRotationSpeed_));
			_lastPIDResult = pid_.update(_actualRotationSpeed);
			driver_.setRotationSpeed(_lastPIDResult);
			driver_.setSpeed(_speed);
		}

	}
	
	public RobotMotion(MotorDriver driver, RobotOrientation orientation) {
		driver_ = driver;
		pid_ = new PID(P_GAIN_, I_GAIN_, D_GAIN_, _INTERVAL);
		pidTimer_ = new Timer();
		pidTimer_.scheduleAtFixedRate(new PidTask_(), 0, (long) (_INTERVAL*1000));
		orientation_ = orientation;
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
		pid_.setTarget(_targetRotation);
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
