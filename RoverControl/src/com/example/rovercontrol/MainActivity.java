package com.example.rovercontrol;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.content.ServiceConnection;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
//import android.widget.SeekBar;

public class MainActivity extends Activity {

	private final int _refreshRate = 500;
	
	//private RoverService roverService_;
	private Robot _robot;
	private boolean serviceBound_;
	
	private TextView _currentState, _miscInfo;
	private ImageView _cameraPreview;
	
	private Timer _infoTimer;
	private Timer _cameraTimer;
	private TextView _visionInfo;

	private final long _cameraRate = 1000/12;
	
	private final Context context = this;
	
	public void onRestartService(View view) {
		stopService(new Intent(this, RoverService.class));
		doUnbindService();
		startService(new Intent(this, RoverService.class));
		doBindService();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		_currentState = (TextView)findViewById(R.id.currentState);
		_miscInfo  = (TextView)findViewById(R.id.miscInfo);
		_visionInfo  = (TextView)findViewById(R.id.visionInfo);
		_cameraPreview = (ImageView)findViewById(R.id.cameraPreview);
		_cameraPreview.setAdjustViewBounds(true);
		
		startService(new Intent(this, RoverService.class));
		doBindService();
	}
	
	@Override
	public void onResume() {
	  super.onResume();
	  _infoTimer = new Timer();
	  _infoTimer.schedule(new TimerTask() {
		  @Override
		  public void run() {
			  runOnUiThread(new Runnable() {
				  public void run() {
					if(serviceBound_ && _robot != null) {
						_currentState.setText(_robot.stateMachine.getStateName());
						_miscInfo.setText("Target: " + _robot.motion.getTargetRotation() + ", Actual: " 
										+ _robot.motion.getActualRotation() + ", Correction: " + _robot.motion.getLastPID());
						if(_robot.vision.servicesAvailable()) {
							if(_robot.vision.cameraAvailable()) {
									_visionInfo.setText("OpenCV loaded, camera open");
							} else {
								_visionInfo.setText("OpenCV loaded, camera unavailable");
							}
						} else {
							_visionInfo.setText("OpenCV unavailable");
						}
					}
				  }
			  });
		  }
	  }, 0, _refreshRate);
	}
	@Override
	public void onPause() {
		if(_infoTimer != null) {
			_infoTimer.cancel();
		}
		super.onPause();
	}
	
	public void toggleCamera(View view) {
		if(((ToggleButton)view).isChecked()) {
			_cameraTimer = new Timer();
			_cameraTimer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					if(serviceBound_ && _robot != null) {
						if(_robot.vision.servicesAvailable() && _robot.vision.cameraAvailable()) {
								((Activity)context).runOnUiThread(new Runnable() { 
									public void run() {updatePreview();}
								});
						}
					}
				}
			}, 0, _cameraRate);
		} else {
			if(_cameraTimer != null) {
				_cameraTimer.cancel();
			}
		}
	}
	
	private void updatePreview() {
		if(_robot.vision.currentFrame != null) {
			Mat frame = _robot.vision.currentFrame.clone();
			if(frame != null && frame.width() > 0 && frame.height() > 0) {
				//Mat result = new Mat(frame.height(), frame.width(), frame.type());
				Bitmap resultBitmap = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.ARGB_8888 );
				//Imgproc.cvtColor(frame, result, Imgproc.COLOR_RGB2BGRA);
				Utils.matToBitmap(frame, resultBitmap, true);
				_cameraPreview.setImageBitmap(resultBitmap);
			}
		}
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        //roverService_ = ((RoverService.MyBinder)service).getService();
	        _robot = ((RoverService.MyBinder)service).getRobot();
	        // Tell the user about this for our demo.
	        Toast.makeText(MainActivity.this, "Connected to service.",
	                Toast.LENGTH_SHORT).show();
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        //roverService_ = null;
			_robot = null;
	        Toast.makeText(MainActivity.this, "Service disconnected.",
	                Toast.LENGTH_SHORT).show();
			
		}
	};

	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
	    bindService(new Intent(this, 
	            RoverService.class), mConnection, Context.BIND_AUTO_CREATE);
	    serviceBound_ = true;
	}

	void doUnbindService() {
	    if (serviceBound_) {
	        // Detach our existing connection.
	        unbindService(mConnection);
	        serviceBound_ = false;
	    }
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}
}
