package com.example.rovercontrol;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import android.content.ServiceConnection;
import android.content.Context;
import android.widget.Toast;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends Activity {

	private RoverService roverService_;
	private boolean serviceBound_;
	
	private TextView currentState_;
	private SeekBar throttleBar_;
	private SeekBar steeringBar_;
	
	private Timer refreshInfo_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		currentState_ = (TextView)findViewById(R.id.currentState);
		throttleBar_ = (SeekBar)findViewById(R.id.throttleBar);
		steeringBar_ = (SeekBar)findViewById(R.id.steeringBar);
		
		startService(new Intent(this, RoverService.class));
		doBindService();
		
		/*losePuck_.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(serviceBound_ && roverService_ != null) {
                	roverService_.losePuck();
                }
            }
        });*/
	}
	
	@Override
	public void onResume() {
	  super.onResume();
	  refreshInfo_ = new Timer();
	  refreshInfo_.schedule(new TimerTask() {
		  @Override
		  public void run() {
			  runOnUiThread(new Runnable() {
				  public void run() {
					  updateInfo();
				  }
			  });
		  }
	  }, 0, 500);
	}
	@Override
	public void onPause() {
		refreshInfo_.cancel();
		super.onPause();
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        roverService_ = ((RoverService.MyBinder)service).getService();

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
	        roverService_ = null;
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
	
	private void updateInfo() {
		if(serviceBound_ && roverService_ != null) {
			//roverService_.setThrottle(normalized(throttleBar_));
			//roverService_.setSteering(normalized(steeringBar_));
			currentState_.setText(roverService_.getCurrentState());
		}

	}
	
	private float normalized(SeekBar bar) {
		return (bar.getProgress()/(bar.getMax()/2)) - 1;
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
