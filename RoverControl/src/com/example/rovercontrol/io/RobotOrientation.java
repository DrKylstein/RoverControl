package com.example.rovercontrol.io;


import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class RobotOrientation implements SensorEventListener {
    
    private float mMagneticInclination;
    
    private SensorManager mSensMan;
    private float mAzimuth;
    
    private float[] mGravs = new float[3];
    private float[] mGeoMags = new float[3];
    
    private float[] mRotationM = new float[9];
    private float[] mRemapedRotationM = new float[9];
    private boolean mFailed;
    
    
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    
    //G=gyro C=Compass
    private float[] mOrientationC = new float[3];
    private float[] mOrientationG = new float[3];
    

    public RobotOrientation(Context c){
    	
    		//sets up the sensor manager
    	   mSensMan = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
           mSensMan.registerListener(this, mSensMan.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
           mSensMan.registerListener(this, mSensMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
           mSensMan.registerListener(this,mSensMan.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_UI);
    }
         

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing
    }

    
    public float getCompass()
    {
    	return mAzimuth;
    }
    
    public float[] getOrientation()
    {
    	return mOrientationG;
    }
    
    
    @SuppressLint("NewApi")
	@Override
    public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                    /*
                     * NOTE: The data must be copied off the event.values
                     * as the system is reusing that array in all SensorEvents.
                     * Simply assigning:
                     * mGravs = event.values won't work.
                     *
                     * I use a member array in an attempt to reduce garbage production.
                     */
                    System.arraycopy(event.values, 0, mGravs, 0, 3);
                    break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                    for (int i=0;i<3;i++) mGeoMags[i] = event.values[i];
                            break;
                    default:
                            return;
                            
            case Sensor.TYPE_GYROSCOPE:
            	 if (timestamp != 0) {
                     final float dT = (event.timestamp - timestamp) * NS2S;
                     // Axis of the rotation sample, not normalized yet.
                     float axisX = event.values[0];
                     float axisY = event.values[1];
                     float axisZ = event.values[2];

                     // Calculate the angular speed of the sample
                     float omegaMagnitude = (float) java.lang.Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                     // Normalize the rotation vector if it's big enough to get the axis
                     if (omegaMagnitude > 0.5) {
                         axisX /= omegaMagnitude;
                         axisY /= omegaMagnitude;
                         axisZ /= omegaMagnitude;
                     }

                     // Integrate around this axis with the angular speed by the timestep
                     // in order to get a delta rotation from this sample over the timestep
                     // We will convert this axis-angle representation of the delta rotation
                     // into a quaternion before turning it into the rotation matrix.
                     float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                     float sinThetaOverTwo = (float) java.lang.Math.sin(thetaOverTwo);
                     float cosThetaOverTwo = (float) java.lang.Math.cos(thetaOverTwo);
                     deltaRotationVector[0] = sinThetaOverTwo * axisX;
                     deltaRotationVector[1] = sinThetaOverTwo * axisY;
                     deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                     deltaRotationVector[3] = cosThetaOverTwo;
                 }
                 timestamp = event.timestamp;
                 float[] deltaRotationMatrix = new float[9];
                 SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
                 // User code should concatenate the delta rotation we computed with the current rotation
                 // in order to get the updated rotation.
                 // rotationCurrent = rotationCurrent * deltaRotationMatrix;
                 
                 SensorManager.getOrientation(deltaRotationMatrix, mOrientationG);
            }

            if (SensorManager.getRotationMatrix(mRotationM, null, mGravs, mGeoMags)){
                    SensorManager.remapCoordinateSystem(mRotationM, SensorManager.AXIS_X, SensorManager.AXIS_Z, mRemapedRotationM);
                    SensorManager.getOrientation(mRemapedRotationM, mOrientationC);
                    mMagneticInclination = SensorManager.getInclination(mRotationM);
                    mAzimuth = (mOrientationC[0]);
                   
            }
    }


	
}
