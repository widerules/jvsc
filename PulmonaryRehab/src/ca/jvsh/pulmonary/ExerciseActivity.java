package ca.jvsh.pulmonary;

import java.util.Timer;
import java.util.TimerTask;


import ca.jvsh.pulmonary.ExerciseStartActivity.Point;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ExerciseActivity extends Activity implements GpsStatus.Listener, GpsStatus.NmeaListener, LocationListener
{

	TextView		locationtext;
	LocationManager	mLocMan	= null;
	GpsStatus		mStatus	= null;

	TextView[] Pos=new TextView[3];
	TextView Stat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.exercise_activity);

		this.getActionBar().hide();
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		TextView locationtext = (TextView) findViewById(R.id.fullscreen_content);
		LocationManager lLocMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocMan = lLocMan;

		
		Stat=(TextView) findViewById(R.id.SatStat);
		
		Pos[0]=(TextView) findViewById(R.id.SatLat);
		Pos[1]=(TextView) findViewById(R.id.SatLon);
		Pos[2]=(TextView) findViewById(R.id.SatAlt);
	}

	@Override
	protected void onPause()
	{
		//Remove listener
		LocationManager lLocMan = mLocMan;
		lLocMan.removeGpsStatusListener(this);
		lLocMan.removeNmeaListener(this);
		super.onPause();

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		LocationManager lLocMan = mLocMan;
		//register for the GPS status listener
		lLocMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		lLocMan.addGpsStatusListener(this);
		lLocMan.addNmeaListener(this);

	}

	//GPS Listener implementations
	public void onGpsStatusChanged(int arg0)
	{
		//Print out the status
		if (arg0 == GpsStatus.GPS_EVENT_FIRST_FIX)
			Stat.setText("GPS Status :GPS_EVENT_FIRST_FIX");
		else if (arg0 == GpsStatus.GPS_EVENT_SATELLITE_STATUS)
			Stat.setText("GPS Status :GPS_EVENT_SATELLITE_STATUS");
		else if (arg0 == GpsStatus.GPS_EVENT_STARTED)
			Stat.setText("GPS Status :GPS_EVENT_STARTED");
		else if (arg0 == GpsStatus.GPS_EVENT_STOPPED)
			Stat.setText("GPS Status :GPS_EVENT_STOPPED");

		//Get the status
		GpsStatus lStatus = mStatus;
		
		if (lStatus == null)
			lStatus = mLocMan.getGpsStatus(null);
		else
			mLocMan.getGpsStatus(lStatus);

	}

	//NMEA listener
	public void onNmeaReceived(long arg0, String arg1)
	{
		//tv.addtext("NMEA Status changed\n");
	}

	public void onLocationChanged(Location location)
	{
		TextView[] lPos = Pos;
		lPos[0].setText(Double.toString(location.getLatitude()));
		lPos[1].setText(Double.toString(location.getLongitude()));
		lPos[2].setText(Double.toString(location.getAltitude()));

		//int nsat = location.getExtras().getInt("satellites", -1);
		//lPos[3].setText(Integer.toString(nsat));
	}

	public void onProviderDisabled(String provider)
	{
		//tv.addtext("GPS Provider Disabled\n");
	}

	public void onProviderEnabled(String provider)
	{
		//tv.addtext("GPS Provider Enabled\n");
	}

	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		// TODO Auto-generated method stub
		/*if (status == LocationProvider.OUT_OF_SERVICE)
		{
			tv.addtext(provider + "status:OUT_OF_SERVICE");
		}
		else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE)
		{
			tv.addtext(provider + "status:TEMPORARILY_UNAVAILABLE");
		}
		else if (status == LocationProvider.AVAILABLE)
		{
			tv.addtext(provider + "status:AVAILABLE");
		}*/
	}

}
