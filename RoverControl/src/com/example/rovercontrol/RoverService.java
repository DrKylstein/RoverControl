package com.example.rovercontrol;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import ioio.lib.util.android.IOIOService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import android.os.Binder;

public class RoverService extends IOIOService {
	
	//private final int PISTON_PIN = 12;
	//private final int IR_PIN = 40;
	private final int _TX_PIN = 14;
	private DigitalOutput _led;
	
	//private GrabberPiston piston_;
	//private IRSensor irSensor_;
	
	private Robot _robot;
	private MotorDriver _motorDriver;
	private final Context context = this;
	
	public RoverService() {
		_motorDriver = new MotorDriver(_TX_PIN);
		_robot = new Robot(new RobotMotion(_motorDriver, context));
	}
	
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new BaseIOIOLooper() {
			

			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				System.out.println("rover_debug: IOIO setup begin");
				_led = ioio_.openDigitalOutput(IOIO.LED_PIN);
				//irSensor_ = new IRSensor(ioio_, IR_PIN);
				//piston_ = new GrabberPiston(ioio_, PISTON_PIN);
				
				try {
					_motorDriver.reset(ioio_);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("rover_debug: IOIO setup end");
			}

			@Override
			public void loop() throws ConnectionLostException,
				InterruptedException {
				_led.write(true);
				_robot.update();
				_led.write(false);
			}
		};
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
	}
	
	public class MyBinder extends Binder {
		RoverService getService() {
			return RoverService.this;
		}
	}
	private final IBinder _binder = new MyBinder();
	@Override
	public IBinder onBind(Intent arg0) {
		return _binder;
	}

	public String getCurrentState() {
		// TODO Auto-generated method stub
		return _robot.stateMachine.getStateName();
	}

}
