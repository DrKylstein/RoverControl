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
public class RobotMotion implements SensorEventListener {
	
	private MotorDriver driver_;
	private PID pid_;
	private Timer pidTimer_;
	private final SensorManager sensorManager_;
    private final Sensor gyroscope_;
	private double actualRotationSpeed_;
	private double _speed;
	
	private final double P_GAIN_ = 0.06;
	private final double I_GAIN_ = 0.0;
	private final double D_GAIN_ = 0.005;
	private final double _INTERVAL = 0.01;
	
	private class PidTask_ extends TimerTask {
		@Override
		public void run() {
			System.out.printf("rover_debug gyro: %f", actualRotationSpeed_);
			//driver_.setRotationSpeed(pid_.UpdatePID(rotationSpeed_ - actualRotationSpeed_));
			driver_.setRotationSpeed(pid_.update(actualRotationSpeed_));
			driver_.setSpeed(_speed);
		}
	}
	
	public RobotMotion(MotorDriver driver, Context context) {
		driver_ = driver;
		pid_ = new PID(P_GAIN_, I_GAIN_, D_GAIN_, _INTERVAL);
		pidTimer_ = new Timer();
		pidTimer_.scheduleAtFixedRate(new PidTask_(), 0, (long) (_INTERVAL*1000));
		sensorManager_ = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		gyroscope_ = sensorManager_.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		sensorManager_.registerListener(this, gyroscope_, SensorManager.SENSOR_DELAY_FASTEST);
	}
	/**
	 * 
	 * @param mps speed in approximate meters/second
	 */
	public void setSpeed(double speed) {
		_speed = speed; //driver_.setSpeed(mps);//driver_.setSpeed((mps / WHEEL_CIRC) * ONE_RPS);
	}
	/**
	 * 
	 * @param radps rotation speed in radians/second
	 */
	public void setRotationSpeed(double radps) {
		pid_.setTarget(radps);
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		// get rotation about negative y of the phone, which is positive z for the robot
		actualRotationSpeed_ = event.values[1];// * -1;
	}
}
