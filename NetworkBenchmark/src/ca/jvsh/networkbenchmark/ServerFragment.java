package ca.jvsh.networkbenchmark;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ServerFragment extends Fragment
{
	int						mNum;
	private GraphicalView	mChartView;
	private LineGraph		line	= new LineGraph();

	/**
	 * Create a new instance of CountingFragment, providing "num"
	 * as an argument.
	 */
	static ServerFragment newInstance(int num)
	{
		ServerFragment f = new ServerFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);

		return f;
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mNum = getArguments() != null ? getArguments().getInt("num") : 1;
	}

	/**
	 * The Fragment's UI is just a simple text view showing its
	 * instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_server, container, false);

		if (mChartView == null)
		{
			LinearLayout layout = (LinearLayout) v.findViewById(R.id.chart);
			mChartView = line.getView(v.getContext());
			layout.addView(mChartView);

		}
		else
		{
			mChartView.repaint();
		}
		return v;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		if (mChartView != null)
		{
			mChartView.repaint();
		}
	}

}
