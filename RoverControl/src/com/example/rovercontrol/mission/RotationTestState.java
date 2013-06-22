package com.example.rovercontrol.mission;

import android.util.Log;

import com.example.rovercontrol.Robot;
import com.example.rovercontrol.control.State;

public class RotationTestState implements State<Robot> {

	private long _count;
	private int _step;
	private final long _SECOND = 1000000000;
	
	@Override
	public void onEnter(Robot owner) {
		// TODO Auto-generated method stub
		_count = 0;
		_step = 0;
	}

	@Override
	public void onExit(Robot owner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(long dtNanos, Robot owner) {
		_count += dtNanos;
		if(_count > _SECOND) {
			_count = 0;
			_step = (_step+1)%5;
			switch(_step) {
				case 0:
					owner.motion.driver.setSpeed(0.0);
					owner.motion.driver.setRotationSpeed(1.0);
					Log.v("RotationTestState", String.format("Output: %f", 1.0));
					break;
				case 1:
					owner.motion.driver.setSpeed(0.0);
					owner.motion.driver.setRotationSpeed(-1.0);
					Log.v("RotationTestState", String.format("Output: %f", -1.0));
					break;
				case 2:
					owner.motion.driver.setSpeed(0.0);
					owner.motion.driver.setRotationSpeed(0.5);
					Log.v("RotationTestState", String.format("Output: %f", 0.5));
					break;
				case 3:
					owner.motion.driver.setSpeed(0.0);
					owner.motion.driver.setRotationSpeed(-0.5);
					Log.v("RotationTestState", String.format("Output: %f", -0.5));
					break;
				case 4:
					owner.motion.driver.setSpeed(0.0);
					owner.motion.driver.setRotationSpeed(0.0);
					Log.v("RotationTestState", String.format("Output: %f", 0.0));
					break;
			}
		}
		if(_count % (_SECOND/10) == 0) {
			Log.v("RotationTestState", String.format("Gyro: %f", owner.orientation.getOrientation()[2]));
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
