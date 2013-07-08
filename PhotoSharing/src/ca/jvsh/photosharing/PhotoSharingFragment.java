package ca.jvsh.photosharing;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import ca.jvsh.photosharing.IPAddressKeyListener;
import ca.jvsh.photosharing.R;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.view.ViewCompat;

public class PhotoSharingFragment extends SherlockFragment implements android.widget.CompoundButton.OnCheckedChangeListener
{

	//controls
	private EditText			mConfigurationFilePathEdit;
	private Button				mOpenFileButton;
	private RadioGroup			mSocketTypeRadioGroup;
	ListView					mComputersList;
	private Button				mSendFileButton;

	View						mView;
	Context						mContext;

	//
	ArrayList<Peer>				mPeersList				= new ArrayList<Peer>();
	PeerListAdapter				mPeerListAdapter;
	boolean						mRemovePeerAllowed		= false;

	//between threads communication
	protected static final int	MSG_SERVER_SOCKET_ERR	= 0;
	protected static final int	MSG_BYTES_RECEIVED		= 1;

	private static final int	SELECT_PICTURE			= 1;

	static PhotoSharingFragment newInstance()
	{
		return new PhotoSharingFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	private void initPeersList(Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		try
		{
			mPeersList = (ArrayList<Peer>) ObjectSerializer.deserialize(prefs.getString("peers", ObjectSerializer.serialize(new ArrayList<Peer>())));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		mView = inflater.inflate(R.layout.fragment_photo_sharing, container, false);
		mContext = mView.getContext();
		initPeersList(mContext);

		mConfigurationFilePathEdit = (EditText) mView.findViewById(R.id.editTextEditorConfigurationFilePath);

		mOpenFileButton = (Button) mView.findViewById(R.id.buttonOpenFile);
		mOpenFileButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				mRemovePeerAllowed = false;

				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

				/*Intent intent = new Intent(v.getContext(), FileDialog.class);
				intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/");

				//can user select directories or not
				intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
				intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);

				//alternatively you can set file filter
				intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "jpg" });

				startActivityForResult(intent, SelectionMode.MODE_OPEN);*/
			}
		});

		// Load animation for deleted list items
		final Animation anim = AnimationUtils.loadAnimation(this.mContext, R.anim.fade_anim);

		mComputersList = (ListView) mView.findViewById(R.id.list);
		mPeerListAdapter = new PeerListAdapter(mPeersList, this);
		mComputersList.setAdapter(mPeerListAdapter);
		mComputersList.setLongClickable(true);

		// React to user clicks on item
		mComputersList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parentAdapter, final View view, final int position,
					long id)
			{
				TextView clickedView = (TextView) view.findViewById(R.id.ip);

				Toast.makeText(mContext, "Selected Peer IP [" + clickedView.getText() + "]",
						Toast.LENGTH_SHORT).show();

				CheckBox rb = (CheckBox) view.findViewById(R.id.chk);
				rb.setChecked(!rb.isChecked());
				mRemovePeerAllowed = false;
			}
		});

		mComputersList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> parentAdapter, final View view, final int position,
					long id)
			{
				if (mRemovePeerAllowed)
				{
					anim.setAnimationListener(new Animation.AnimationListener()
					{
						@Override
						public void onAnimationStart(Animation animation)
						{
							ViewCompat.setHasTransientState(view, true);
						}

						@Override
						public void onAnimationRepeat(Animation animation)
						{
						}

						@Override
						public void onAnimationEnd(Animation animation)
						{
							Peer item = mPeerListAdapter.getItem(position);
							mPeerListAdapter.remove(item);
							mRemovePeerAllowed = false;
							ViewCompat.setHasTransientState(view, false);
						}
					});
					view.startAnimation(anim);
				}
				return true;
			}
		});

		mSocketTypeRadioGroup = (RadioGroup) mView.findViewById(R.id.radioGroupClient);
		mSocketTypeRadioGroup.check(PreferenceManager.getDefaultSharedPreferences(mContext).getInt("file_sharing_type", R.id.radioClientTcpOnly));

		mSendFileButton = (Button) mView.findViewById(R.id.buttonSendFile);
		mSendFileButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				for (Peer peer : mPeersList)
				{
					if (peer.isChecked())
					{
						Runnable r = new PhotoSendingThread(mConfigurationFilePathEdit.getText().toString(),
								peer.getIp(), peer.getPort(),
								mSocketTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioClientTcpOnly);
						new Thread(r).start();
					}
				}

			}
		});

		return mView;
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onStop()
	{
		//////////////////////////////////
		//saving parameters
		//////////////////////////////////
		Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();

		editor.putInt("file_sharing_type", mSocketTypeRadioGroup.getCheckedRadioButtonId());

		if (mPeersList.isEmpty())
		{
			editor.remove("peers");

		}
		else
		{
			try
			{
				editor.putString("peers", ObjectSerializer.serialize(mPeersList));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		editor.commit();

		//stop client threads
		super.onStop();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		int pos = mComputersList.getPositionForView(buttonView);
		System.out.println("Pos [" + pos + "]");
		if (pos != ListView.INVALID_POSITION)
		{
			Peer p = mPeersList.get(pos);
			p.setChecked(isChecked);
		}
	}

	public void addPeer()
	{
		final Dialog d = new Dialog(mContext);
		d.setContentView(R.layout.add_peer);
		d.setTitle("Add Peer");
		d.setCancelable(true);

		final EditText editIp = (EditText) d.findViewById(R.id.editTextIp);
		editIp.setKeyListener(IPAddressKeyListener.getInstance());

		final EditText editPort = (EditText) d.findViewById(R.id.editTextPort);

		Button b = (Button) d.findViewById(R.id.addPeerButton);
		b.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View v)
			{
				String peerIp = editIp.getText().toString();
				int peerPort = Integer.parseInt(editPort.getText().toString());
				try
				{
					PhotoSharingFragment.this.mPeersList.add(new Peer(InetAddress.getByName(peerIp), peerPort));
				}
				catch (UnknownHostException e)
				{
					e.printStackTrace();
				}
				PhotoSharingFragment.this.mPeerListAdapter.notifyDataSetChanged(); // We notify the data model is changed
				d.dismiss();
			}
		});

		d.show();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.fragment_photosharing_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
			case R.id.peer_add_menu:

				mRemovePeerAllowed = false;
				addPeer();

				return true;

			case R.id.peer_remove_menu:
				Toast.makeText(mContext, "Long click peer to remove.", Toast.LENGTH_SHORT).show();
				mRemovePeerAllowed = true;

				return true;
		}

		return false;
	}

	@Override
	public synchronized void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == SELECT_PICTURE && data != null && data.getData() != null)
		{
			Uri _uri = data.getData();

			//User had pick an image.
			Cursor cursor = mContext.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
			cursor.moveToFirst();

			//Link to the image
			final String filePath = cursor.getString(0);
			mConfigurationFilePathEdit.setText(filePath);
			mSendFileButton.setEnabled(true);
			cursor.close();
		}
		else
		{
			mConfigurationFilePathEdit.setText("");
			mSendFileButton.setEnabled(false);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/*public synchronized void onActivityResult(final int requestCode,
			int resultCode, final Intent data)
	{

		if (resultCode == Activity.RESULT_OK)
		{
			String filePath = data.getStringExtra(FileDialog.RESULT_PATH);

			mConfigurationFilePathEdit.setText(filePath);
			mSendFileButton.setEnabled(true);
			//mAddNewThreadButton.setEnabled(true);

			if (requestCode == SelectionMode.MODE_OPEN)
			{
				Log.d(PhotoSharingFragment.class.getName(), "Opening old configuration...");

				//loadXmlConfiguration(filePath);
				//mConfigurationChanged = false;
				Log.d(PhotoSharingFragment.class.getName(), "onActivityResult mSaveMenuItem.setVisible(false);");
				//mSaveMenuItem.setVisible(false);
			}

		}
		else if (resultCode == Activity.RESULT_CANCELED)
		{
			mConfigurationFilePathEdit.setText("");
			mSendFileButton.setEnabled(false);
			//mSaveMenuItem.setVisible(false);
			//mAddNewThreadButton.setEnabled(false);
		}

	}*/

	public boolean checkFileExt(String filepath, String checkExt)
	{
		String ext = filepath.substring((filepath.lastIndexOf(".") + 1), filepath.length());
		if (ext.compareToIgnoreCase(checkExt) != 0)
			return false;
		return true;
	}

	/*@Override
	public void onStop()
	{
		//////////////////////////////////
		//saving parameters
		//////////////////////////////////
		Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();

		//save server IP
		editor.putString("server_ip", mServerIpEdit.getText().toString());

		//save server port
		editor.putString("server_port", mServerPortEdit.getText().toString());

		//save path to the configuration file
		editor.putString("configuration_file", mConfigurationFilePathEdit.getText().toString());

		editor.putInt("client_socket_type", mSocketTypeRadioGroup.getCheckedRadioButtonId());

		editor.commit();

		//stop client threads
		super.onStop();
	}*/

	private int							mServerOpenPort;
	private InetAddress					mServerIp;
	private Thread						mNetworkThread;
	//private boolean					mFirstPacketFlag;
	//private long						mStartTime;
	private Integer						mSentBytesTotal;
	//threads
	public SparseArray<TestingThread>	mTestingThreads;
	protected static final int			MSG_INCORRECT_IP	= 0;
	protected static final int			MSG_INCORRECT_PORT	= 1;
	protected static final int			MSG_CANT_CONNECT	= 2;
	public static final int				MSG_BYTES_SENT		= 3;

	protected boolean socketStart()
	{

		if (parseInputFile(mConfigurationFilePathEdit.getText().toString()))
		{
			//	mFirstPacketFlag = true;
			//mNetworkThreadActive = true;
			mSentBytesTotal = Integer.valueOf(0);
			//mBytesSentTextView.setText("0");

			mNetworkThread = new Thread()
			{
				public void run()
				{
					//check connection

					try
					{
						//TODO fix this
						mServerIp = InetAddress.getByName(/*mServerIpEdit.getText().toString()*/"127.2.2.2");
					}
					catch (UnknownHostException ex)
					{
						Log.d(PhotoSharingFragment.class.getName(), "Incorrect server IP address.");

						Message m = new Message();
						m.what = MSG_INCORRECT_IP;
						mToastHandler.sendMessage(m);

						ex.printStackTrace();

						return;
					}

					try
					{
						//TODO fix this
						//mServerOpenPort = Integer.parseInt(mServerPortEdit.getText().toString());
					}
					catch (NumberFormatException ex)
					{
						Log.d(PhotoSharingFragment.class.getName(), "Incorrect server port.");

						Message m = new Message();
						m.what = MSG_INCORRECT_PORT;
						mToastHandler.sendMessage(m);

						ex.printStackTrace();

						return;
					}

					//test socket
					switch (mSocketTypeRadioGroup.getCheckedRadioButtonId())
					{
						case R.id.radioClientTcpOnly:

							Socket TcpSocket = null;
							try
							{
								TcpSocket = new Socket();
								TcpSocket.connect(new InetSocketAddress(mServerIp, mServerOpenPort), 2000);

								TcpSocket.close();

							}
							catch (SocketTimeoutException ex)
							{
								Log.d(PhotoSharingFragment.class.getName(), "SocketTimeoutException: Client can't connect to server with ip " + mServerIp
										+ " on port " + mServerOpenPort);

								Message m = new Message();
								m.what = MSG_CANT_CONNECT;
								m.obj = mContext;
								mToastHandler.sendMessage(m);

								TcpSocket = null;
								ex.printStackTrace();
								return;
							}
							catch (UnknownHostException ex)
							{
								Log.d(PhotoSharingFragment.class.getName(), "UnknownHostException: Client can't connect to server with ip " + mServerIp
										+ " on port " + mServerOpenPort);

								Message m = new Message();
								m.what = MSG_CANT_CONNECT;
								m.obj = mContext;
								mToastHandler.sendMessage(m);

								TcpSocket = null;
								ex.printStackTrace();
								return;
							}
							catch (IOException ex)
							{
								Log.d(PhotoSharingFragment.class.getName(), "IOException: Client can't connect to server with ip " + mServerIp + " on port "
										+ mServerOpenPort);

								Message m = new Message();
								m.what = MSG_CANT_CONNECT;
								m.obj = mContext;
								mToastHandler.sendMessage(m);

								TcpSocket = null;
								ex.printStackTrace();

								return;
							}
							finally
							{
								TcpSocket = null;
							}

							for (int i = 0; i < mTestingThreads.size(); i++)
							{
								mTestingThreads.get(i).setupSocket(mServerIp, mServerOpenPort, PhotoSharingFragment.this, false);
								mTestingThreads.get(i).start();
							}

							break;

						case R.id.radioClientUdpTcp:

							DatagramSocket UdpSocket = null;
							try
							{
								UdpSocket = new DatagramSocket();
								UdpSocket.connect(new InetSocketAddress(mServerIp, mServerOpenPort));

								UdpSocket.close();

							}
							catch (SocketException ex)
							{
								Log.d(PhotoSharingFragment.class.getName(), "SocketException: Client can't connect to server with ip " + mServerIp
										+ " on port " + mServerOpenPort);

								Message m = new Message();
								m.what = MSG_CANT_CONNECT;
								m.obj = mContext;
								mToastHandler.sendMessage(m);

								UdpSocket = null;
								ex.printStackTrace();
								return;
							}

							finally
							{
								UdpSocket = null;
							}

							for (int i = 0; i < mTestingThreads.size(); i++)
							{
								mTestingThreads.get(i).setupSocket(mServerIp, mServerOpenPort, PhotoSharingFragment.this, true);
								mTestingThreads.get(i).start();
							}

							break;
					}

				}
			};
			mNetworkThread.start();

			return true;
		}
		else
		{
			return false;
		}
	}

	protected void socketStop()
	{
		for (int i = 0; i < mTestingThreads.size(); i++)
		{
			TestingThread testingThread = mTestingThreads.valueAt(i);
			testingThread.mNetworkThreadActive = false;
			testingThread = null;
		}

		mTestingThreads.clear();
		mNetworkThread = null;
	}

	/*public synchronized void onActivityResult(final int requestCode,
			int resultCode, final Intent data)
	{

		if (resultCode == Activity.RESULT_OK)
		{

			if (requestCode == SelectionMode.MODE_OPEN)
			{
				Log.d(TAG, "Loading...");
			}

			String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
			mConfigurationFilePathEdit.setText(filePath);

		}
		else if (resultCode == Activity.RESULT_CANCELED)
		{
			Log.d(TAG, "file not selected");
		}

	}*/

	boolean parseInputFile(String filePath)
	{/*
		if (filePath.isEmpty())
		{
			return false;
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		File file = new File(filePath);

		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(file);

			Element root = dom.getDocumentElement();
			root.normalize();

			NodeList threadNodes = root.getElementsByTagName("network_thread");

			if (threadNodes.getLength() == 0)
			{

				Log.d(TAG, "No network threads nodes were found in the XML file.");
				Toast.makeText(mContext, "No network threads nodes were found in the XML file.", Toast.LENGTH_SHORT).show();
				return false;
			}

			mTestingThreads = new SparseArray<TestingThread>(threadNodes.getLength());

			for (int i = 0; i < threadNodes.getLength(); i++)
			{
				Node threadNode = threadNodes.item(i);

				if (threadNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eThread = (Element) threadNode;

					NodeList sequenceNodes = eThread.getElementsByTagName("sequence");

					if (sequenceNodes.getLength() == 0)
					{

						Log.d(TAG, "No test sequence nodes were found in the thread node.");
						Toast.makeText(mContext, "No test sequence nodes were found in the thread node.", Toast.LENGTH_SHORT).show();
						return false;
					}

					mTestingThreads.put(i, new TestingThread(sequenceNodes.getLength(), i));

					for (int j = 0; j < sequenceNodes.getLength(); j++)
					{
						TestingThread testingThread = mTestingThreads.get(i);

						testingThread.mTestingSequences.put(j, testingThread.new TestingSequence());
						Node sequence = sequenceNodes.item(j);
						if (sequence.getNodeType() == Node.ELEMENT_NODE)
						{
							TestingThread.TestingSequence testingSequence = testingThread.mTestingSequences.get(j);

							Element eElement = (Element) sequence;

							String temp = eElement.getAttribute("time_total_ms");
							testingSequence.time_total = temp.isEmpty() ? 1000 : Integer.parseInt(temp);

							temp = eElement.getAttribute("bytes");
							testingSequence.bytes_send = temp.isEmpty() ? 1000 : Integer.parseInt(temp);

							temp = eElement.getAttribute("delay_ns");
							testingSequence.delay_nano =  temp.isEmpty() ? 100000000L : Long.parseLong(temp);

							temp = eElement.getAttribute("repeat");
							testingSequence.repeat = temp.isEmpty() ? -1 : Integer.parseInt(temp);
						}

					}

				}
			}
		}
		catch (ParserConfigurationException ex)
		{
			Log.d(TAG, "Exception in Parser Configuration");
			Toast.makeText(mContext, "Exception in Parser Configuration", Toast.LENGTH_SHORT).show();
			ex.printStackTrace();
			return false;
		}
		catch (DOMException ex1)
		{
			Log.d(TAG, "DOM exception. Malformed or empty XML configuration file.");
			Toast.makeText(mContext, "DOM exception. Malformed configuration XML file.", Toast.LENGTH_SHORT).show();
			ex1.printStackTrace();
			return false;
		}
		catch (SAXException ex1)
		{
			Log.d(TAG, "SAX exception. Malformed or empty XML configuration file.");
			Toast.makeText(mContext, "SAX exception. Malformed configuration XML file.", Toast.LENGTH_SHORT).show();
			ex1.printStackTrace();
			return false;
		}
		catch (IOException ex2)
		{
			Log.d(TAG, "Can't open configuration XML file.");
			Toast.makeText(mContext, "Can't open configuration XML file.", Toast.LENGTH_SHORT).show();
			ex2.printStackTrace();
			return false;
		}*/

		return true;
	}

	public void addSendedBytes(int bytes)
	{
		mSentBytesTotal += bytes;
		Message m = new Message();
		m.what = PhotoSharingFragment.MSG_BYTES_SENT;
		m.arg1 = mSentBytesTotal;
		mToastHandler.sendMessage(m);

	}

	MyInnerHandler	mToastHandler	= new MyInnerHandler(this);

	static class MyInnerHandler extends Handler
	{
		WeakReference<PhotoSharingFragment>	mClientFragment;

		MyInnerHandler(PhotoSharingFragment clientFragment)
		{
			mClientFragment = new WeakReference<PhotoSharingFragment>(clientFragment);
		}

		@Override
		public void handleMessage(Message msg)
		{
			PhotoSharingFragment clientFragment = mClientFragment.get();

			switch (msg.what)
			{
				case MSG_INCORRECT_IP:

					Toast.makeText(clientFragment.mContext, "Incorrect server IP address.", Toast.LENGTH_SHORT).show();
					//TODO: take a look what is going on here
					//clientFragment.mClientOnOffToggleButton.setChecked(false);

					break;
				case MSG_INCORRECT_PORT:

					Toast.makeText(clientFragment.mContext, "Incorrect server port.", Toast.LENGTH_SHORT).show();
					//TODO: take a look what is going on here
					//clientFragment.mClientOnOffToggleButton.setChecked(false);

					break;
				case MSG_CANT_CONNECT:

					Toast.makeText(clientFragment.mContext, "Client can't connect to server.", Toast.LENGTH_SHORT).show();
					//TODO: take a look what is going on here
					//clientFragment.mClientOnOffToggleButton.setChecked(false);

					break;
				case MSG_BYTES_SENT:

					//TODO: take a look what is going on here
					//clientFragment.mBytesSentTextView.setText(" " + msg.arg1);

				default:
					break;
			}
		}
	}
}

//public class ClientFragment extends SherlockFragment
//{
//	//edits
//	private EditText					mServerIpEdit;
//	private EditText					mServerPortEdit;
//	private EditText					mConfigurationFilePathEdit;
//
//	private TextView					mBytesSentTextView;
//
//	//private Button						mOpenFileButton;
//	private ToggleButton				mClientOnOffToggleButton;
//	private RadioGroup					mSocketTypeRadioGroup;
//
//	private Context						mContext;
//
//	private int							mServerOpenPort;
//	private InetAddress					mServerIp;
//
//	//	private boolean					mFirstPacketFlag;
//	//private long				mStartTime;
//
//	//threads
//	public SparseArray<TestingThread>	mTestingThreads;
//
//	//	private static boolean			mNetworkThreadActive				= false;
//
//	private Thread						mNetworkThread;
//	String								mClientMessage		= "";
//
//	private Integer						mSentBytesTotal;
//	//	private Socket					mClientSocket		= null;
//
//	//	private final int				MAGIC				= 50;
//	//	private int						nWriteBytesTotal	= MAGIC;
//	//	private byte[]					mDataBuffer			= new byte[MAGIC];
//
//	// Debugging tag.
//	private static final String			TAG					= "ServerFragment";
//	// Handler message id
//	protected static final int			MSG_INCORRECT_IP	= 0;
//	protected static final int			MSG_INCORRECT_PORT	= 1;
//	protected static final int			MSG_CANT_CONNECT	= 2;
//	public static final int				MSG_BYTES_SENT		= 3;
//
//	/**
//	 * Create a new instance of CountingFragment, providing "num"
//	 * as an argument.
//	 */
//	static ClientFragment newInstance(int num)
//	{
//		return new ClientFragment();
//	}
//
//	/**
//	 * The Fragment's UI is just a simple text view showing its
//	 * instance number.
//	 */
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//	{
//		View view = inflater.inflate(R.layout.fragment_client, container, false);
//		mContext = view.getContext();
//
//		mServerIpEdit = (EditText) view.findViewById(R.id.editTextServerIp);
//		mServerIpEdit.setKeyListener(IPAddressKeyListener.getInstance());
//
//		mServerPortEdit = (EditText) view.findViewById(R.id.editTextServerPort);
//		mConfigurationFilePathEdit = (EditText) view.findViewById(R.id.editTextConfigurationFilePath);
//
//		mBytesSentTextView = (TextView) view.findViewById(R.id.textViewBytesSent);
//
//		mClientOnOffToggleButton = (ToggleButton) view.findViewById(R.id.toggleButtonClient);
//		mClientOnOffToggleButton.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				if (mClientOnOffToggleButton.isChecked())
//				{
//					if (!socketStart())
//						mClientOnOffToggleButton.setChecked(false);
//				}
//				else
//				{
//					socketStop();
//				}
//			}
//		});
//
//		/*mOpenFileButton = (Button) view.findViewById(R.id.buttonOpenFile);
//		mOpenFileButton.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				Intent intent = new Intent(v.getContext(), FileDialog.class);
//				intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());
//
//				//can user select directories or not
//				intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
//				intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
//
//				//alternatively you can set file filter
//				intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "xml" });
//
//				startActivityForResult(intent, SelectionMode.MODE_OPEN);
//			}
//		});
//		*/
//		//restore saved state
//		//restore server IP
//		mServerIpEdit.setText(PreferenceManager.getDefaultSharedPreferences(mContext).getString("server_ip", ""));
//
//		//restore server port
//		mServerPortEdit.setText(PreferenceManager.getDefaultSharedPreferences(mContext).getString("server_port", ""));
//
//		//restore path to configuration file
//		mConfigurationFilePathEdit.setText(PreferenceManager.getDefaultSharedPreferences(mContext).getString("configuration_file", ""));
//
//	
//
//		return view;
//	}
//
//
//	
//}