/**
 * 
 */
package com.example.rovercontrol.mission;

import android.util.Log;
import android.widget.Toast;

import com.example.rovercontrol.Robot;
import com.example.rovercontrol.control.State;

/**
 * @author kyle
 *
 */
public class GPS_Test_State implements State<Robot> {
	
	//private final long SECOND = 1000000000;
	
	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#onEnter()
	 */
	@Override
	public void onEnter(Robot robot) {
		
	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#onExit()
	 */
	@Override
	public void onExit(Robot robot) {
		
	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#update(long, com.example.rovercontrol.StateMachine)
	 */
	@Override
	public void update(long dtNanos, Robot robot) {
		
		 //Log.v("test", "test");
		
		 
		 if(robot.gps != null){
			if(robot.gps.canGetLocation()){
	            
	            //double latitude = robot.gps.getLatitude();
	            //double longitude = robot.gps.getLongitude();
	            
	          Log.v("GPS LAT", String.valueOf(robot.gps.getLatitude()));
	          Log.v("GPS LONG", String.valueOf(robot.gps.getLongitude()));
	             
	            // \n is for new line
	            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();   
	        }else{
	            // can't get location
	            // GPS or Network is not enabled
	            // Ask user to enable GPS/network in settings
	            robot.gps.showSettingsAlert();
	        }
		 }
		
		 //Log.v("GPS LAT", String.valueOf(robot.gps.getLatitude()));
         //Log.v("GPS LONG", String.valueOf(robot.gps.getLongitude()));
	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Trying to walk straight. (Motion Test)";
	}

}
