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
	private PID pid_;
	private Timer pidTimer_;
	private final SensorManager sensorManager_;
    private final Sensor gyroscope_;
	private double rotationSpeed_;
	private double actualRotationSpeed_;
	
	private final int TX_PIN_ = 14;
	private final double ONE_RPS = 0.1;
	private final double WHEEL_CIRC = 0.20;
	
	private final double MAX_RADPS_ = 12.0;
	
	private final double P_GAIN_ = 0.06;
	private final double I_GAIN_ = 0.0;
	private final double D_GAIN_ = 0.005;
	private final double _INTERVAL = 0.01;
	
	private class PidTask_ extends TimerTask {
		@Override
		public void run() {
			try {
				System.out.printf("rover_debug gyro: %f", actualRotationSpeed_);
				//driver_.setRotationSpeed(pid_.UpdatePID(rotationSpeed_ - actualRotationSpeed_));
				driver_.setRotationSpeed(pid_.update(actualRotationSpeed_));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public RobotMotion(IOIO ioio, Context context) throws ConnectionLostException, IOException {
		driver_ = new MotorDriver(ioio, TX_PIN_);
		//pid_ = new PidController();
		//pid_.Reset(-1, 1, 0, P_GAIN_, I_GAIN_, D_GAIN_);
		//pid_.SetRate(0.1);
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
	public void setSpeed(double mps) {
		try {
			driver_.setSpeed(mps);//driver_.setSpeed((mps / WHEEL_CIRC) * ONE_RPS);
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
