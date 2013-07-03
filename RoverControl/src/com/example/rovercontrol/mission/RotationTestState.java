package com.example.rovercontrol.mission;

import android.util.Log;

import com.example.rovercontrol.Robot;
import com.example.rovercontrol.control.State;

public class RotationTestState implements State<Robot> {

	private long _count;
	private int _step;
	private final long _SECOND = 1000000000;
	
	private final double[] _speeds = {1.0, -1.0, 0.5, -0.5, 0.0};
	
	@Override
	public void onEnter(Robot owner) {
		owner.motion.stopPID();
		_count = 0;
		_step = 0;
	}

	@Override
	public void onExit(Robot owner) {
		owner.motion.startPID();
		
	}

	@Override
	public void update(long dtNanos, Robot owner) {
		_count += dtNanos;
		if(_count > _SECOND) {
			_count = 0;
			_step = (_step+1)%5;
			owner.motion.driver.setSpeed(0.0);
			owner.motion.driver.setRotationSpeed(_speeds[_step]);
		}
		/*if(_count % (_SECOND/10) == 0) {
			Log.v("RotationTestState", String.format("Gyro: %f", owner.orientation.getOrientation()[2]));
		}*/
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "RotationTestState" + " speed: "+_speeds[_step];
	}

}
