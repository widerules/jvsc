package ca.jvsh.falldetectionlogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class FallDetectionService extends Service implements SensorEventListener
{
	//Sensors
	private SensorManager		mSensorManager;
	private Sensor				mAccelerometer;

	private PowerManager.WakeLock	wakeLock;

	/*
	 * This is a list of callbacks that have been registered with the
	 * service.  Note that this is package scoped (instead of private) so
	 * that it can be accessed more efficiently from inner classes.
	 */
	final RemoteCallbackList<IRemoteServiceCallback>	mCallbacks	= new RemoteCallbackList<IRemoteServiceCallback>();

	NotificationManager									mNM;
	
	private BufferedWriter			mOutput;

	@Override
	public void onCreate()
	{
		super.onCreate();
		
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Load settings
		acquireWakeLock();
				
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		
		// Display a notification about us starting.
		showNotification();
	}
	
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		
		mSensorManager.registerListener(this,
				mAccelerometer,
				SensorManager.SENSOR_DELAY_FASTEST);
		
		
		mOutput = null;
		Date lm = new Date();
		String fileName = "FallDetectionLogger_" + new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss", Locale.US).format(lm) + ".csv";
		try
		{
			File configFile = new File(Environment.getExternalStorageDirectory().getPath(), fileName);
			FileWriter fileWriter = new FileWriter(configFile);
			mOutput = new BufferedWriter(fileWriter);
		}
		catch (IOException ex)
		{
			Log.e(FallDetectionService.class.getName(), ex.toString());
		}

		try
		{
			mOutput.write("X, Y, Z, Timestamp, ");
			mOutput.newLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
		try
		{
			mBinder.registerCallback(mSelfCallback);
		}
		catch (RemoteException e)
		{
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
		}
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy()
	{
		try
		{
			mOutput.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		mSensorManager.unregisterListener(this);
		
		// Cancel the persistent notification.
		mNM.cancel(R.string.remote_service_started);

		// Tell the user we stopped.
		Toast.makeText(this, R.string.remote_service_stopped, Toast.LENGTH_SHORT).show();

		wakeLock.release();
		
		try
		{
			mBinder.unregisterCallback(mSelfCallback);
		}
		catch (RemoteException e)
		{
			// There is nothing special we need to do if the service
			// has crashed.
		}
		
		// Unregister all callbacks.
		mCallbacks.kill();
		
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// Select the interface to return.  If your service only implements
		// a single interface, you can just return it here without checking
		// the Intent.
		if (IRemoteService.class.getName().equals(intent.getAction()))
		{
			return mBinder;
		}
		return null;
	}

	/**
	 * The IRemoteInterface is defined through IDL
	 */
	private final IRemoteService.Stub	mBinder				= new IRemoteService.Stub()
															{
																public void registerCallback(IRemoteServiceCallback cb)
																{
																	if (cb != null)
																		mCallbacks.register(cb);
																}

																public void unregisterCallback(IRemoteServiceCallback cb)
																{
																	if (cb != null)
																		mCallbacks.unregister(cb);
																}
															};

	@Override
	public void onTaskRemoved(Intent rootIntent)
	{
		Toast.makeText(this, "Task removed: " + rootIntent, Toast.LENGTH_LONG).show();
	}
	
	
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		switch (accuracy)
		{
			case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
				Toast.makeText(this, "maximum accuracy", Toast.LENGTH_LONG).show();
				break;
			case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
				Toast.makeText(this, "average level of accuracy", Toast.LENGTH_LONG).show();
				break;
			case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
				Toast.makeText(this, "low accuracy", Toast.LENGTH_LONG).show();
				break;
			case SensorManager.SENSOR_STATUS_UNRELIABLE:
				Toast.makeText(this, "sensor cannot be trusted", Toast.LENGTH_LONG).show();
				break;

		}
	}

	public void onSensorChanged(SensorEvent event)
	{
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
		{
			synchronized (this)
			{
				// Broadcast to all clients the new value.
				final int N = mCallbacks.beginBroadcast();
				for (int i = 0; i < N; i++)
				{
					try
					{
						mCallbacks.getBroadcastItem(i).accelerometerChanged(event.values[0],
								event.values[1],
								event.values[2],
								event.timestamp);
					}
					catch (RemoteException e)
					{
						// The RemoteCallbackList will take care of removing
						// the dead object for us.
					}
				}
				mCallbacks.finishBroadcast();
				
				
			}
		}
	}
	
	
	private IRemoteServiceCallback	mSelfCallback	= new IRemoteServiceCallback.Stub()
	{
		/**
		 * This is called by the remote service regularly to tell us about
		 * new values.  Note that IPC calls are dispatched through a thread
		 * pool running in each process, so the code executing here will
		 * NOT be running in our main thread like most other things -- so,
		 * to update the UI, we need to use a Handler to hop over there.
		 */
		public void accelerometerChanged(float X, float Y, float Z, long timestamp)
		{
			synchronized (this)
			{
				try
				{
					mOutput.write(String.format("%6.3f, %6.3f, %6.3f, %d", X, Y, Z, timestamp));
					mOutput.newLine();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	};
	
	
	
	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification()
	{
		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = getText(R.string.remote_service_started);
		
		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, FallDetectionActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		Notification notification =new Notification.Builder((Context)this)
		.setContentText(getText(R.string.remote_service_label))
		.setSubText(text)
		.setSmallIcon(R.drawable.ic_launcher)
         .setContentIntent(contentIntent)
         .build(); 
		
		// Send the notification.
		// We use a string id because it is a unique number.  We use it later to cancel.
		mNM.notify(R.string.remote_service_started, notification);
	}

	// ----------------------------------------------------------------------

	private void acquireWakeLock()
	{
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, FallDetectionService.class.getName());
		wakeLock.acquire();
	}
}
