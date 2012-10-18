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

import org.apache.http.conn.util.InetAddressUtils;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ServerFragment extends SherlockFragment
{
	private EditText			mServerOpenPortEdit;
	private ToggleButton		mServerOnOffToggleButton;
	private TextView			mBytesReceivedTextView;
	private TextView 			mIpTextView;
	
	private Context				mContext;

	private int					mReadBytes;
	private int					mReadBytesTotal;
	private byte[]				mDataBuffer				= new byte[16384];
	private InputStream			mInputStream;

	private static boolean		mActive					= false;

	private Thread				mServerThread;
	private ServerSocket		mServerSocket			= null;
	private int					mServerPort;

	// Debugging tag.
	private static final String	TAG						= "ServerFragment";
	protected static final int	MSG_SERVER_SOCKET_ERR	= 0;
	protected static final int	MSG_BYTES_RECEIVED		= 1;

	/**
	 * Create a new instance of CountingFragment, providing "num"
	 * as an argument.
	 */
	static ServerFragment newInstance(int num)
	{
		return new ServerFragment();
	}

	/**
	 * The Fragment's UI is just a simple text view showing its
	 * instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_server, container, false);
		mContext = view.getContext();

		//set ip
		{
			mIpTextView = (TextView) (view.findViewById(R.id.ip));

			String ip = ServerFragment.getLocalIpAddress();
			if (ip == null)
			{
				mIpTextView.setText("No Internet connection");
			}
			else
			{
				mIpTextView.setText("My ip is " + ip);
			}
		}

		mServerOpenPortEdit = (EditText) view.findViewById(R.id.editTextPort);
		mBytesReceivedTextView = (TextView) view.findViewById(R.id.textViewBytesReceived);

		mServerOnOffToggleButton = (ToggleButton) view.findViewById(R.id.serverSocketButton);
		mServerOnOffToggleButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if (mServerOnOffToggleButton.isChecked())
				{
					if (!socketStart())
						mServerOnOffToggleButton.setChecked(false);
				}
				else
				{
					socketStop();
				}
			}
		});

		//restore port that server will open
		mServerOpenPortEdit.setText(PreferenceManager.getDefaultSharedPreferences(mContext).getString("server_open_port", "6000"));

		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		String ip = ServerFragment.getLocalIpAddress();
		if (ip == null)
		{
			mIpTextView.setText("No Internet connection");
		}
		else
		{
			mIpTextView.setText("My ip is " + ip);
		}
	}
	
	@Override
	public void onStop()
	{
		//////////////////////////////////
		//saving parameters
		//////////////////////////////////
		Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();

		//save port that server would open
		editor.putString("server_open_port", mServerOpenPortEdit.getText().toString());

		editor.commit();

		//stop client threads
		super.onStop();
	}

	protected boolean socketStart()
	{
		//check if we have something in the port edit
		try
		{
			mServerPort = Integer.parseInt(mServerOpenPortEdit.getText().toString());
		}
		catch (NumberFormatException ex)
		{
			Log.d(TAG, "Can't read port number");
			ex.printStackTrace();
			Toast.makeText(mContext, "Can't read port number", Toast.LENGTH_SHORT).show();
			return false;
		}
		mReadBytesTotal = 0;
		mBytesReceivedTextView.setText("0");
		mActive = true;
		mServerThread = new Thread()
		{
			public void run()
			{
				try
				{
					mServerSocket = new ServerSocket(mServerPort);
				}
				catch (IOException ex)
				{

					Log.d(TAG, "Can't open server socket");
					ex.printStackTrace();
					Message m = new Message();
					m.what = MSG_SERVER_SOCKET_ERR;
					mToastHandler.sendMessage(m);
					mActive = false;
					mServerThread = null;
					return;
				}

				Socket s = null;

				while (mActive)
				{

					try
					{
						if (s == null)
							s = mServerSocket.accept();

						mInputStream = s.getInputStream();

						while ((mReadBytes = mInputStream.read(mDataBuffer)) > 0)
						{

							mReadBytesTotal += mReadBytes;
						}

						Message m = new Message();
						m.what = MSG_BYTES_RECEIVED;
						m.arg1 = mReadBytesTotal;
						mToastHandler.sendMessage(m);
						s = null;
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}

			}
		};
		mServerThread.start();

		return true;
	}

	protected void socketStop()
	{
		mActive = false;
		mServerThread = null;
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

	Handler	mToastHandler	= new Handler()
							{
								public void handleMessage(Message msg)
								{
									switch (msg.what)
									{
										case MSG_SERVER_SOCKET_ERR:

											Toast.makeText(mContext, "Can't open server socket", Toast.LENGTH_SHORT).show();
											mServerOnOffToggleButton.setChecked(false);
											break;
										case MSG_BYTES_RECEIVED:
											mBytesReceivedTextView.setText(" " + msg.arg1);
										default:
											break;
									}
									super.handleMessage(msg);
								}
							};
}
