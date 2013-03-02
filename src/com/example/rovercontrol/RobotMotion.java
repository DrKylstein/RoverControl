/**
 * 
 */
package com.example.rovercontrol;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * @author kyle
 *
 */
public class RobotMotion implements SensorEventListener {
	
	private MotorDriver driver_;
	private PidController pid_;
	private Timer pidTimer_;
	private final SensorManager sensorManager_;
    private final Sensor gyroscope_;
	private double rotationSpeed_;
	private double actualRotationSpeed_;
	
	private final int TX_PIN_ = 14;
	private final int ST_ADDR_ = 128;
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
				driver_.setSpeed(pid_.UpdatePID(rotationSpeed_ - actualRotationSpeed_));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public RobotMotion(IOIO ioio, Context context) throws ConnectionLostException, IOException {
		driver_ = new MotorDriver(ioio, TX_PIN_, ST_ADDR_);
		pid_ = new PidController();
		pid_.Reset(0, MAX_RADPS_, 0, P_GAIN_, I_GAIN_, D_GAIN_);
		pidTimer_ = new Timer();
		pidTimer_.scheduleAtFixedRate(new PidTask_(), 0, 10000);
		sensorManager_ = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		gyroscope_ = sensorManager_.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		sensorManager_.registerListener(this, gyroscope_, SensorManager.SENSOR_DELAY_GAME);
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
		rotationSpeed_ = radps;
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		// get rotation about negative y of the phone, which is positive z for the robot
		actualRotationSpeed_ = event.values[2] * -1;
	}
}
