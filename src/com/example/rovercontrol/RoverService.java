package com.example.rovercontrol;
import android.content.Intent;
import android.os.IBinder;
import ioio.lib.util.android.IOIOService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import ioio.lib.api.DigitalOutput;
//import ioio.lib.api.PwmOutput;
//import ioio.lib.api.PulseInput;
//import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import android.os.Binder;
//import java.net.*;
//import java.io.*;

public class RoverService extends IOIOService {
	
	private double irLevel_;
	private final int THROTTLE_PIN = 47;
	private final int STEERING_PIN = 46;
	private final int PISTON_PIN = 12;
	private final int IR_PIN = 40;
	
	/*private final int PISTON_DOWN = 1800;
	private final int PISTON_UP = 1200;
	//private final int PISTON_IDLE = 1500;
	private final int PISTON_PAUSE = 250;*/
	
	private DigitalOutput led_;
	
	//private PwmOutput throttle_;
	//private PwmOutput steering_;
	//private PwmOutput piston_;
	private GrabberPiston piston_;
	
	private IRSensor irSensor_;
	//private PulseInput leftEncoder_;
	//private PulseInput rightEncoder_;

	private long _lastNanoTime;
	private StateMachine _stateMachine;
	//private boolean havePuck_ = false;
	//private boolean seePuck_ = false;
	//private float speed_ = 0;
	//private float direction_ = 0;
	
	//private MothershipConnection mothershipConnection = new MothershipConnection();
	
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new BaseIOIOLooper() {
			

			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN);
				//throttle_ = ioio_.openPwmOutput(THROTTLE_PIN, 20);
				//steering_ = ioio_.openPwmOutput(STEERING_PIN, 20);
				//piston_ = ioio_.openPwmOutput(PISTON_PIN, 20);
				irSensor_ = new IRSensor(ioio_, IR_PIN);
				piston_ = new GrabberPiston(ioio_, PISTON_PIN);
				
				_stateMachine.changeState(new RetrievePuckState(irSensor_, piston_));
				
				_lastNanoTime = System.nanoTime();
			}

			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				
				led_.write(true);
				
				_stateMachine.update(System.nanoTime() - _lastNanoTime);
				
				_lastNanoTime = System.nanoTime();
				
				//move
				/*throttle_.setPulseWidth((speed_ * 500) + 1500);
				steering_.setPulseWidth((direction_ * 500) +1500);
				
				//capture puck
				irLevel_ = irSensor_.voltage();
				seePuck_ = irLevel_ > 0.8;
				if(!havePuck_ && seePuck_) {
					piston_ = ioio_.openPwmOutput(PISTON_PIN, 100);
					piston_.setPulseWidth(PISTON_DOWN);
					Thread.sleep(PISTON_PAUSE);
					piston_.setPulseWidth(PISTON_UP);
					Thread.sleep(PISTON_PAUSE);
					piston_.setPulseWidth(PISTON_DOWN);
					Thread.sleep(PISTON_PAUSE);
					piston_.setPulseWidth(PISTON_UP);
					Thread.sleep(PISTON_PAUSE);
					piston_.close();
					piston_.grab();
					havePuck_ = true;
				}*/
				led_.write(false);
			}
		};
	}

	public double getIRLevel() {
		return irLevel_;
	}
	
	/*public boolean hasPuck() {
		//return havePuck_;
	}	
	
	public boolean seePuck() {
		return seePuck_;
	}
	
	public void losePuck() {
		havePuck_ = false;
	}
	
	public void setThrottle(float value) {
		speed_ = value;
	}
	
	public void setSteering(float value) {
		direction_ = value;
	}
	
	public String getLastCommand() {
		if(!mothershipConnection.messagesFromServer.isEmpty()) {
			return mothershipConnection.messagesFromServer.poll();
		} else {
			return "No commands";
		//}
	}*/
	
	public String getCurrentState() {
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (intent != null && intent.getAction() != null
				&& intent.getAction().equals("stop")) {
			// User clicked the notification. Need to stop the service.
			nm.cancel(0);
			stopSelf();
		} else {
			// Service starting. Create a notification.
			Notification notification = new Notification(
					R.drawable.ic_launcher, "Rover service running",
					System.currentTimeMillis());
			notification
					.setLatestEventInfo(this, "Rover Service", "Click to stop",
							PendingIntent.getService(this, 0, new Intent(
									"stop", null, this, this.getClass()), 0));
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			nm.notify(0, notification);
		}
		//new Thread(mothershipConnection).start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//mothershipConnection.stop();
	}
	
	public class MyBinder extends Binder {
		RoverService getService() {
			return RoverService.this;
		}
	}
	private final IBinder mBinder = new MyBinder();
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

}
