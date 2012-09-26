package ca.jvsh.networkbenchmark;

import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.achartengine.GraphicalView;
import org.achartengine.model.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class ClientFragment extends Fragment
{
	int							mNum;

	EditText					serverIpEdit;
	EditText					serverPortEdit;
	EditText					configurationFilePath;
	Button						openFileButton;
	ToggleButton				clientToggleButton;
	
	Context context;

	private GraphicalView		mChartView;
	private ClientLineGraph		line				= new ClientLineGraph();
	Point						p					= new Point();

	int							port;
	InetAddress					ip;

	boolean						first;
	long						start;
	long						now;
	private static boolean		mActive				= false;
	Thread						clientThreads;

	// Debugging tag.
	private static final String	TAG					= "ServerFragment";

	Socket						socket				= null;

	final int					MAGIC				= 50;
	int							nWriteBytesTotal	= MAGIC;
	byte[]						data				= new byte[MAGIC];

	ConfigFile					inputConfigFile;

	/**
	 * Create a new instance of CountingFragment, providing "num"
	 * as an argument.
	 */
	static ClientFragment newInstance(int num)
	{
		ClientFragment f = new ClientFragment();

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
		View v = inflater.inflate(R.layout.fragment_client, container, false);
		context = v.getContext();

		serverIpEdit = (EditText) v.findViewById(R.id.editTextServerIp);
		serverIpEdit.setKeyListener(IPAddressKeyListener.getInstance());
		

		serverPortEdit = (EditText) v.findViewById(R.id.editTextServerPort);
		configurationFilePath = (EditText) v.findViewById(R.id.editTextConfigurationFilePath);

		clientToggleButton = (ToggleButton) v.findViewById(R.id.toggleButtonClient);
		clientToggleButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if (clientToggleButton.isChecked())
				{
					socketStart();
				}
				else
				{
					socketStop();
				}
			}
		});

		openFileButton = (Button) v.findViewById(R.id.buttonOpenFile);
		openFileButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), FileDialog.class);
				intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());

				//can user select directories or not
				intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
				intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);

				//alternatively you can set file filter
				intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "xml" });

				startActivityForResult(intent, SelectionMode.MODE_OPEN);
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

		//restore saved state
		//restore server IP
		serverIpEdit.setText(PreferenceManager.getDefaultSharedPreferences(context).getString("server_ip", "") );

		//restore server port
		serverPortEdit.setText(PreferenceManager.getDefaultSharedPreferences(context).getString("server_port", "") );
		
		//restore path to configuration file
		configurationFilePath.setText(PreferenceManager.getDefaultSharedPreferences(context).getString("configuration_file", "") );
	}

	@Override
	public void onStop()
	{
		//////////////////////////////////
		//saving parameters
		//////////////////////////////////
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		
		//save server IP
		editor.putString("server_ip", serverIpEdit.getText().toString());
		
		//save server port
		editor.putString("server_port", serverPortEdit.getText().toString());
		
		//save path to the configuration file
		editor.putString("configuration_file", configurationFilePath.getText().toString());
		
		editor.commit();
		
		//stop client threads
		super.onStop();
	}

	protected void socketStart()
	{
		first = true;
		line.clearPoints();

		mActive = true;

		parseInputFile(configurationFilePath.getText().toString());

		clientThreads = new Thread()
		{
			public void run()
			{
				//Socket s = null;
				//android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				/*try
				{
					port = Integer.parseInt(serverPortEdit.getText().toString());
					 ip = InetAddress.getByName(serverIpEdit.getText().toString());

				}
				catch (IOException e)
				{
					e.printStackTrace();
				}*/

				while (mActive)
				{

					try
					{

						//socket = new Socket(ip, port);
						//OutputStream os = socket.getOutputStream();
						//os.write(data);

						if (first)
						{
							first = false;
							start = System.currentTimeMillis();// 1000.0f;
						}

						{
							p.setX((System.currentTimeMillis() - start) / 1000.0f);
							p.setY((float) nWriteBytesTotal);
							line.addNewPoints(p);
						}

						mChartView.repaint();
						//socket.close();

						Thread.sleep(1000, 0);

					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

			}
		};
		clientThreads.start();
	}

	protected void socketStop()
	{
		mActive = false;
		//serverThread = null;
	}

	public synchronized void onActivityResult(final int requestCode,
			int resultCode, final Intent data)
	{

		if (resultCode == Activity.RESULT_OK)
		{

			if (requestCode == SelectionMode.MODE_OPEN)
			{
				Log.d(TAG, "Loading...");
			}

			String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
			configurationFilePath.setText(filePath);

		}
		else if (resultCode == Activity.RESULT_CANCELED)
		{
			Log.d(TAG, "file not selected");
		}

	}

	void parseInputFile(String filePath)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		File file = new File(filePath);

		//List<Message> messages = new ArrayList<Message>();
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(file);

			Element root = dom.getDocumentElement();
			root.normalize();

			NodeList threadNodes = root.getElementsByTagName("network_thread");
			inputConfigFile = new ConfigFile(threadNodes.getLength());
			for (int i = 0; i < threadNodes.getLength(); i++)
			{
				Node threadNode = threadNodes.item(i);

				if (threadNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eThread = (Element) threadNode;

					NodeList sequenceNodes = eThread.getElementsByTagName("sequence");

					inputConfigFile.threads[i] = inputConfigFile.new TestingThread(sequenceNodes.getLength());

					for (int j = 0; j < sequenceNodes.getLength(); j++)
					{
						inputConfigFile.threads[i].sequences[j] = inputConfigFile.threads[i].new TestingSequence();
						Node sequence = sequenceNodes.item(j);
						if (sequence.getNodeType() == Node.ELEMENT_NODE)
						{
							Element eElement = (Element) sequence;

							inputConfigFile.threads[i].sequences[j].time_total = Integer.parseInt(eElement.getAttribute("time_total_ms").toString());
							inputConfigFile.threads[i].sequences[j].bytes_send = Integer.parseInt(eElement.getAttribute("bytes").toString());
							inputConfigFile.threads[i].sequences[j].delay_ms = Integer.parseInt(eElement.getAttribute("delay_ms").toString());

						}

					}

				}
				//inputConfigFile.threads
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
