package ca.jvsh.cpu;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;


public class myServiceConnection implements ServiceConnection
{
	public CPUStatusLEDService mService;
	CPUStatusLEDActivity activity;
	
	myServiceConnection(CPUStatusLEDActivity activity)
	{
		this.activity = activity;
	}
	
	public void onServiceConnected(ComponentName className, IBinder service)
	{
		// Get reference to (local) service from binder
		mService = ((CPUStatusLEDService.CPUStatusLEDBinder) service).getService();
		// link up the display elements to be updated by the service
		mService.setGui(activity);
	}
	
	public void onServiceDisconnected(ComponentName className)
	{
		mService = null;
	}
}
