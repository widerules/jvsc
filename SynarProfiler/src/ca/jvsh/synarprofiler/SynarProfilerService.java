/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.jvsh.synarprofiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ca.jvsh.synarprofiler.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The {@link StepServiceController}
 * and {@link StepServiceBinding} classes show how to interact with the
 * service.
 *
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */
public class SynarProfilerService extends Service
{
	private static final String		TAG					= "ca.jvsh.falldetection.FallDetectionService";
	private SharedPreferences		mSettings;
	private SynarProfilerSettings	mFallDetectorSettings;

	private int						mSensorRateMilliseconds;

	private PowerManager.WakeLock	wakeLock;
	private NotificationManager		mNM;

	BufferedWriter					mOutput;

	private boolean					mActive				= false;
	Thread							profilingThread;

	private File					mBatteryCurrentFile	= new File("/sys/class/power_supply/battery/batt_current");
	private File					mBatteryVoltageFile	= new File("/sys/class/power_supply/battery/voltage_now");
	private int						current;
	private float					voltage;
	private String					line;
	ArrayList<Integer>				CurrentArray;
	ArrayList<Float>				VoltageArray;

	/**
	 * Class for clients to access.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class StepBinder extends Binder
	{
		SynarProfilerService getService()
		{
			return SynarProfilerService.this;
		}
	}

	@Override
	public void onCreate()
	{
		Log.i(TAG, "[SERVICE] onCreate");
		super.onCreate();

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		showNotification();

		// Load settings
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mFallDetectorSettings = new SynarProfilerSettings(mSettings);

		acquireWakeLock();

		// Start detecting

		mSensorRateMilliseconds = (int) mFallDetectorSettings.getAccelerometerFrequency();
		CurrentArray.clear();
		VoltageArray.clear();
		registerDetector();

		// Register our receiver for the ACTION_SCREEN_OFF action. This will make our receiver
		// code be called whenever the phone enters standby mode.
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);

		// Tell the user we started.
		Toast.makeText(this, getText(R.string.started), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		Log.i(TAG, "[SERVICE] onStart");
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy()
	{
		Log.i(TAG, "[SERVICE] onDestroy");

		// Unregister our receiver.
		unregisterReceiver(mReceiver);
		unregisterDetector();

		int size = Math.min(CurrentArray.size(), VoltageArray.size());

		mOutput = null;
		Date lm = new Date();
		String fileName = "synar_profiler" + new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss").format(lm) + ".csv";
		try
		{

			File configFile = new File(Environment.getExternalStorageDirectory().getPath(), fileName);
			FileWriter fileWriter = new FileWriter(configFile);
			mOutput = new BufferedWriter(fileWriter);
		}
		catch (Exception ex)
		{
			Log.e(TAG, ex.toString());
		}

		try
		{

			mOutput.write("Count, Battery Current, Battery Voltage, Battery Power");

			float power;
			for (int i = 0; i < size; i++)
			{
				current = CurrentArray.get(i);
				voltage = VoltageArray.get(i);
				power = current * voltage;
				mOutput.write(i + ", "+ current + ", "+ voltage + ", "+ power);
			}

			mOutput.flush();

			mOutput.close();

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		mNM.cancel(R.string.app_name);

		wakeLock.release();

		super.onDestroy();

		// Stop detecting
		unregisterDetector();

		// Tell the user we stopped.
		Toast.makeText(this, getText(R.string.stopped) + ". Log saved to" + fileName, Toast.LENGTH_LONG).show();
	}

	private void registerDetector()
	{
		mActive = true;

		profilingThread = new Thread()
		{
			public void run()
			{
				while (mActive)
				{
					try
					{
						if (mBatteryCurrentFile.exists())
						{
							RandomAccessFile localRandomAccessFile = new RandomAccessFile(mBatteryCurrentFile, "r");
							line = localRandomAccessFile.readLine();
							if (line != null)
							{
								current = Integer.valueOf(android.text.TextUtils.split(line, "\\s+")[1]).intValue();
							}
							else
							{
								current = -1;
							}
							localRandomAccessFile.close();
						}

						if (mBatteryVoltageFile.exists())
						{
							RandomAccessFile localRandomAccessFile = new RandomAccessFile(mBatteryVoltageFile, "r");
							line = localRandomAccessFile.readLine();
							if (line != null)
							{
								voltage = (Integer.parseInt(line) / 1000.0F);
							}
							else
							{
								voltage = -1;
							}
							localRandomAccessFile.close();
						}

						CurrentArray.add(current);
						VoltageArray.add(voltage);

						Thread.sleep(mSensorRateMilliseconds, 0);
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		profilingThread.start();
	}

	private void unregisterDetector()
	{
		mActive = false;
		//wait till created thread will finish
		/*try
		{
			profilingThread.interrupt();
			profilingThread.join(1000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		profilingThread = null;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		Log.i(TAG, "[SERVICE] onBind");
		return mBinder;
	}

	/**
	 * Receives messages from activity.
	 */
	private final IBinder	mBinder	= new StepBinder();

	public interface ICallback
	{
		public void stepsChanged(int value);
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification()
	{
		CharSequence text = getText(R.string.app_name);
		Notification notification = new Notification(R.drawable.ic_notification, null,
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		Intent fallDetectorIntent = new Intent();
		fallDetectorIntent.setComponent(new ComponentName(this, SynarProfiler.class));
		fallDetectorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				fallDetectorIntent, 0);
		notification.setLatestEventInfo(this, text,
				getText(R.string.notification_subtitle), contentIntent);

		mNM.notify(R.string.app_name, notification);
	}

	// BroadcastReceiver for handling ACTION_SCREEN_OFF.
	private BroadcastReceiver	mReceiver	= new BroadcastReceiver()
											{
												@Override
												public void onReceive(Context context, Intent intent)
												{
													// Check action just to be on the safe side.
													if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
													{
														// Unregisters the listener and registers it again.
														SynarProfilerService.this.unregisterDetector();
														SynarProfilerService.this.registerDetector();
														if (mFallDetectorSettings.wakeAggressively())
														{
															wakeLock.release();
															acquireWakeLock();
														}
													}
												}
											};

	private void acquireWakeLock()
	{
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		int wakeFlags;
		if (mFallDetectorSettings.wakeAggressively())
		{
			wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
		}
		else if (mFallDetectorSettings.keepScreenOn())
		{
			wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK;
		}
		else
		{
			wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
		}
		wakeLock = pm.newWakeLock(wakeFlags, TAG);
		wakeLock.acquire();
	}

}
