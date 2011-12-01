package ca.jvsh.audicy;



import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DrumFragment extends Fragment
{
	TenoriView tenoriView;
	//external thread that updates game view
	private TenoriThread	mTenoriThread;
	private ChimeThread mChimeThread;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.drum_fragment, container, false);
		View tv = v.findViewById(R.id.drumframeLayout);
		FrameLayout frameLayout = (FrameLayout) tv;

		tenoriView = new TenoriView(v.getContext());
		frameLayout.addView(tenoriView);
		return v;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(mChimeThread == null)
		{
			mChimeThread = new ChimeThread();
			mChimeThread.start();
			tenoriView.setChimeThread(mChimeThread);
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();

		// start the background thread
		if (mTenoriThread == null)
		{
			mTenoriThread = new TenoriThread(tenoriView);
			mTenoriThread.start();
		}
	}
	
	@Override
	public void onPause()
	{
		//stop background thread
		if (mChimeThread != null)
		{
			tenoriView.setChimeThread(null);
			mChimeThread.requestStop();
			mChimeThread = null;
		}
		super.onPause();

	}
	
	@Override
	public void onStop()
	{
		if (mTenoriThread != null)
		{
			mTenoriThread.requestStop();
			mTenoriThread = null;
		}
		super.onStop();
	}
}
