package ca.jvsh.networkbenchmark;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;



import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GeneralInfoFragment extends Fragment
{
	int	mNum;

	/**
	 * Create a new instance of CountingFragment, providing "num"
	 * as an argument.
	 */
	static GeneralInfoFragment newInstance(int num)
	{
		GeneralInfoFragment f = new GeneralInfoFragment();

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
		View v = inflater.inflate(R.layout.fragment_general_info, container, false);
		TextView tv = (TextView) (v.findViewById(R.id.ip));

		String ip = GeneralInfoFragment.getLocalIpAddress();
		if(ip == null)
		{
			tv.setText("No Internet connection");
		}
		else
		{
			tv.setText("My ip is " + ip);
		}
		return v;
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
