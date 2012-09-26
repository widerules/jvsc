package ca.jvsh.networkbenchmark;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.model.Point;
import org.apache.http.conn.util.InetAddressUtils;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ServerFragment extends Fragment
{
	int							mNum;
	EditText					et;
	ToggleButton				serverToggleButton;
	private GraphicalView		mChartView;
	private ServerLineGraph		line	= new ServerLineGraph();
	Point						p		= new Point();

	Context						context;

	boolean						first;
	float						start;

	int							nReadBytes;
	int							nReadBytesTotal;
	byte[]						data	= new byte[16384];
	InputStream					is;

	private static boolean		mActive	= false;

	Thread						serverThread;

	// Debugging tag.
	private static final String	TAG		= "ServerFragment";

	ServerSocket				ss		= null;

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
		context = v.getContext();

		TextView tv = (TextView) (v.findViewById(R.id.ip));

		String ip = ServerFragment.getLocalIpAddress();
		if (ip == null)
		{
			tv.setText("No Internet connection");
		}
		else
		{
			tv.setText("My ip is " + ip);
		}

		et = (EditText) v.findViewById(R.id.editTextPort);

		serverToggleButton = (ToggleButton) v.findViewById(R.id.serverSocketButton);
		serverToggleButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if (serverToggleButton.isChecked())
				{
					socketStart();
				}
				else
				{
					socketStop();
				}
			}
		});

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

		//restore prt that server will open
		et.setText(PreferenceManager.getDefaultSharedPreferences(context).getString("server_open_port", "6000"));

	}

	@Override
	public void onStop()
	{
		//////////////////////////////////
		//saving parameters
		//////////////////////////////////
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

		//save port that server would open
		editor.putString("server_open_port", et.getText().toString());

		editor.commit();

		//stop client threads
		super.onStop();
	}

	protected void socketStart()
	{
		first = true;

		mActive = true;
		serverThread = new Thread()
		{
			public void run()
			{
				Socket s = null;
				//android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				try
				{
					int port = Integer.parseInt(et.getText().toString());
					ss = new ServerSocket(port);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				while (mActive)
				{

					try
					{
						if (s == null)
							s = ss.accept();

						is = s.getInputStream();
						nReadBytesTotal = 0;

						while ((nReadBytes = is.read(data)) > 0)
						{

							nReadBytesTotal += nReadBytes;
						}

						if (first)
						{
							first = false;
							start = System.currentTimeMillis() / 1000.0f;
						}

						//in order to have a spiky output we should do the following
						//put zero at the beginning
						{
							p.setX(System.currentTimeMillis() / 1000.0f - start);
							p.setY(0);
							line.addNewPoints(p);
						}
						//put data in the middle
						{
							p.setX(System.currentTimeMillis() / 1000.0f - start);
							p.setY((float) nReadBytesTotal);
							line.addNewPoints(p);
						}
						//put zero at the end
						{
							p.setX(System.currentTimeMillis() / 1000.0f - start);
							p.setY(0);
							line.addNewPoints(p);
						}
						mChartView.repaint();
						s = null;
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}

			}
		};
		serverThread.start();
	}

	protected void socketStop()
	{
		mActive = false;
		serverThread = null;
	}

	public static String getLocalIpAddress()
	{
		try
		{
			String ipv4;
			List<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface ni : nilist)
			{
				List<InetAddress> ialist = Collections.list(ni.getInetAddresses());
				for (InetAddress address : ialist)
				{
					if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = address.getHostAddress()))
					{
						return ipv4;
					}
				}

			}

		}
		catch (SocketException ex)
		{
			Log.e("ClientSocketActivity", ex.toString());
		}
		return null;
	}
}
