package com.example.rovercontrol;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import ioio.lib.util.android.IOIOService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.Uart;
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
	
	/*private final int PISTON_PIN = 12;
	private final int IR_PIN = 40;
	
	
	*/
	private DigitalOutput led_;
	private boolean ledOn = false;
	/*
	private GrabberPiston piston_;
	private IRSensor irSensor_;

	private long _lastNanoTime;
	private StateMachine _stateMachine;
	
	//private MothershipConnection mothershipConnection = new MothershipConnection();
	
	private RobotMotion robotMotion_;
	private final Context context = this;*/
	//for testing only
	private MotorDriver motorDriver_;
	private Uart uart_;
	private OutputStream out_;
	
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new BaseIOIOLooper() {
			

			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				System.out.println("rover_debug: IOIO setup begin");
				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN);
				//irSensor_ = new IRSensor(ioio_, IR_PIN);
				//piston_ = new GrabberPiston(ioio_, PISTON_PIN);
				
				//robotMotion_ = new RobotMotion(ioio_, context);
				
				//testing
				//uart_ = ioio_.openUart(IOIO.INVALID_PIN, 14, 9600, Uart.Parity.NONE, Uart.StopBits.ONE);
				//out_ = uart_.getOutputStream();

				
				try {
					motorDriver_ = new MotorDriver(ioio_, 14);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("rover_debug: IOException in MotorDriver()");
					e1.printStackTrace();
				}
				
				
				//_stateMachine.changeState(new RetrievePuckState(irSensor_, piston_));
				//_stateMachine.changeState(new DrunkTestState(robotMotion_));
				//robotMotion_.setSpeed(1);
				
				
				//_lastNanoTime = System.nanoTime();
				System.out.println("rover_debug: IOIO setup end");
			}

			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				//System.out.println("rover_debug: IOIO loop enter");
				if(!ledOn) {
					led_.write(true);
					ledOn = true;
				}
				/*try {
					out_.write(170);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

				try {
					motorDriver_.setSpeed(1.0);
					motorDriver_.setRotationSpeed(0.0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("rover_debug: IOException in MotorDriver.setSpeed");
					e.printStackTrace();
				}
				//System.out.println("rover_debug: IOIO loop exit");
				//_stateMachine.update(System.nanoTime() - _lastNanoTime);
				//_lastNanoTime = System.nanoTime();
				//led_.write(false);
			}
		};
	}
	
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
