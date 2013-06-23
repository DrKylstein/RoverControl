package com.example.rovercontrol;

import com.example.rovercontrol.mission.MotionTestState;
import com.example.rovercontrol.mission.MoveToPuckState;
import com.example.rovercontrol.mission.RotationTestState;
import com.example.rovercontrol.mission.VisionTestState;
import android.content.Intent;
import android.os.IBinder;
import ioio.lib.util.android.IOIOService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import android.os.Binder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class RoverService extends IOIOService {
	
	private DigitalOutput _led;
	private Robot _robot;

	@Override
	public void onCreate() {
		super.onCreate();
		_robot = new Robot();
		_robot.vision.load(this);//, "2013.06.22.07.00.22");
		_robot.orientation.register(this);
		_robot.stateMachine.changeState(new MoveToPuckState());
		_robot.start();
	}
	
	@Override
	public void onDestroy() {
		_robot.stop();
		_robot.vision.unload();
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
		super.onDestroy();
	}
	
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new BaseIOIOLooper() {
			

			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				_led = ioio_.openDigitalOutput(IOIO.LED_PIN);
				
				_robot.motion.driver.reset(ioio_);
				_robot.irSensor.reset(ioio_);
			}

			@Override
			public void loop() throws ConnectionLostException,
				InterruptedException {
				_led.write(true);
				//_robot.update();
				_led.write(false);
			}
		};
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if(intent != null && intent.getAction() != null && intent.getAction().contentEquals("stop")) {
			stopSelf();
		}
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (intent != null && intent.getAction() != null
				&& intent.getAction().equals("stop")) {
			// User clicked the notification. Need to stop the service.
			nm.cancel(0);
			stopSelf();
		} else {
			// Service starting. Create a notification.
			Intent resultIntent = new Intent(this, MainActivity.class);
			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setContentTitle("Rover Service")
				//.setContentText("Click to stop")
				.setSmallIcon(R.drawable.ic_launcher)
				//.setContentIntent(PendingIntent.getService(this, 0, new Intent("stop", null, this, this.getClass()), 0))
				.setContentIntent(resultPendingIntent)
				.setOngoing(true)
				.setTicker("Rover service running");
			nm.notify(0, builder.build());
		}
	}
	
	public class MyBinder extends Binder {
		RoverService getService() {
			return RoverService.this;
		}
		Robot getRobot() {
			return RoverService.this._robot;
		}
	}
	private final IBinder _binder = new MyBinder();
	@Override
	public IBinder onBind(Intent arg0) {
		return _binder;
	}

}
