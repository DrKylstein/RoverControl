package com.example.rovercontrol.io;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class RotationSensor implements SensorEventListener {
	
	public RotationSensor(Context context) {
		_context = context;
		PackageManager PM= _context.getPackageManager();
		_hasGyro = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
		_sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		if(!_hasGyro) {
			_sensorManager.registerListener(this, _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
			_sensorManager.registerListener(this, _sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
		} else {
			_sensorManager.registerListener(this, _sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
		}
	}
	
	private Context _context;
	private boolean _hasGyro;
	private SensorManager _sensorManager;
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
	}
}

/*
  	private float[] _accel;
	private float[] _magnet;
   float r[] = new float[9];
			float i[] = new float[9];
			float values[] = new float[3];
			SensorManager.getRotationMatrix(r, i, _accel, _magnet);
			SensorManager.getOrientation(r, values);
			_actualRotationSpeed = (_lastOrientation - values[0]) / _INTERVAL; 
			_lastOrientation = values[0];
 */
*/