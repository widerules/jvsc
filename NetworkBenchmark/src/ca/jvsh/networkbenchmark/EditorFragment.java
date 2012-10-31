package ca.jvsh.networkbenchmark;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class EditorFragment extends SherlockFragment
{

	private EditText					mConfigurationFilePathEdit;
	private LinearLayout				mConfigurationLinearLayout;
	private Button						mAddNewThreadButton;

	View								mView;
	private Context						mContext;

	private static final LayoutParams	mBasicLinearLayoutParams	= new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	private static final LayoutParams	mWeightLayoutParams			= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);

	// Debugging tag.
	private static final String			TAG							= "EditorFragment";
	protected static final int			MSG_SERVER_SOCKET_ERR		= 0;
	protected static final int			MSG_BYTES_RECEIVED			= 1;

	protected static final int			MAGIC_THREAD_ID				= 1000;
	protected static final int			MAGIC_SEQ_ID				= 20;

	private static final int			THREAD_ADD_SEQ_BUTTON_ID	= 5;
	private static final int			THREAD_DELETE_BUTTON_ID		= 4;
	private static final int			THREAD_HEADER_TEXT_ID		= 3;
	private static final int			THREAD_HEADER_LAYOUT_ID		= 2;
	private static final int			THREAD_LAYOUT_ID			= 1;
	//this is where we will start
	public SparseArray<TestingThread>	mTestingThreads;

	//for special purposes - to pass it inside the alertbox dialog
	int									mDeleteThreadId;
	int									mDeleteSequenceId;
	
	boolean								mConfigurationChanged = false;

	/**
	 * Create a new instance of CountingFragment, providing "num"
	 * as an argument.
	 */
	static EditorFragment newInstance(int num)
	{
		return new EditorFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		mTestingThreads = new SparseArray<TestingThread>();
	}

	/**
	 * The Fragment's UI is just a simple text view showing its
	 * instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_editor, container, false);
		mContext = mView.getContext();

		//set basic linear layout parameters

		int tenDpInPx = getPixels(10);
		mBasicLinearLayoutParams.setMargins(getPixels(20), tenDpInPx, tenDpInPx, tenDpInPx);
		mWeightLayoutParams.gravity = Gravity.CENTER_VERTICAL;

		mConfigurationFilePathEdit = (EditText) mView.findViewById(R.id.editTextEditorConfigurationFilePath);
		//mConfigurationFilePathEdit.setInputType(InputType.TYPE_NULL);
		mConfigurationFilePathEdit.setText("blalbabla");
		
		mConfigurationLinearLayout = (LinearLayout) mView.findViewById(R.id.linearLayoutThreads);

		mAddNewThreadButton = (Button) mView.findViewById(R.id.buttonAddNewThread);
		mAddNewThreadButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				addThread();
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

		editor.commit();

		//stop client threads
		super.onStop();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{

		inflater.inflate(R.menu.fragment_editor_menu, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
			case R.id.editor_new:
				
				if(mConfigurationChanged)
				{
					
				}
				
				
				Toast.makeText(mContext, "Editor New", Toast.LENGTH_SHORT).show();

				Intent intentSave = new Intent(mContext, FileDialog.class);
				intentSave.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());

				//can user select directories or not
				intentSave.putExtra(FileDialog.CAN_SELECT_DIR, false);
				intentSave.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_CREATE);

				//alternatively you can set file filter
				intentSave.putExtra(FileDialog.FORMAT_FILTER, new String[] { "xml" });

				startActivityForResult(intentSave, SelectionMode.MODE_CREATE);
				return true;

			case R.id.editor_open:
				Toast.makeText(mContext, "Editor Open", Toast.LENGTH_SHORT).show();
				{
					Intent intentOpen = new Intent(mContext, FileDialog.class);
					intentOpen.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());

					//can user select directories or not
					intentOpen.putExtra(FileDialog.CAN_SELECT_DIR, false);
					intentOpen.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);

					//alternatively you can set file filter
					intentOpen.putExtra(FileDialog.FORMAT_FILTER, new String[] { "xml" });

					startActivityForResult(intentOpen, SelectionMode.MODE_OPEN);
				}
				return true;

			case R.id.editor_save:
				Toast.makeText(mContext, "Editor Save", Toast.LENGTH_SHORT).show();
				return true;

			case R.id.editor_help:
				Toast.makeText(mContext, "Editor Help", Toast.LENGTH_SHORT).show();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public synchronized void onActivityResult(final int requestCode,
			int resultCode, final Intent data)
	{

		if (resultCode == Activity.RESULT_OK)
		{
			String filePath = data.getStringExtra(FileDialog.RESULT_PATH);

			mConfigurationFilePathEdit.setText(filePath);
			mAddNewThreadButton.setEnabled(true);

			if (requestCode == SelectionMode.MODE_CREATE)
			{
				Log.d(TAG, "Creating new configuration...");
				
				//if we just created a configuration - load it immidiately
				mConfigurationChanged = true;
				addThread();
				
			}
			else if (requestCode == SelectionMode.MODE_OPEN)
			{
				Log.d(TAG, "Opening old configuration...");
				
				mConfigurationChanged = false;
				loadXmlConfiguration(filePath);
			}


		}
		else if (resultCode == Activity.RESULT_CANCELED)
		{
			mConfigurationFilePathEdit.setText("");
			mAddNewThreadButton.setEnabled(false);
		}

	}

	private void addThread()
	{
		int threadsArraySize = mTestingThreads.size();
		
		if(threadsArraySize >= 10)
		{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

			// set title
			alertDialogBuilder.setTitle("Too many threads");

			// set dialog message
			alertDialogBuilder.setMessage("Configuration can have 10 threads maximum");
			alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
				}
			});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
			
			return;
		}

		int threadId = 0;

		if (threadsArraySize != 0)
		{
			threadId = mTestingThreads.keyAt(threadsArraySize - 1) + 1;
		}

		Log.d(TAG, "Thread id " + threadId);
		mTestingThreads.put(threadId, new TestingThread(0, threadId));

		int basicControlsId = (threadId + 1) * MAGIC_THREAD_ID;

		//this is the bottom, most basic layout
		LinearLayout threadLinearLayout = new LinearLayout(mContext);
		threadLinearLayout.setId(basicControlsId + THREAD_LAYOUT_ID);
		{
			threadLinearLayout.setOrientation(LinearLayout.VERTICAL);
			threadLinearLayout.setBackgroundResource(R.drawable.border);

			threadLinearLayout.setLayoutParams(mBasicLinearLayoutParams);
		}

		LinearLayout threadHeaderLinearLayout = new LinearLayout(mContext);
		threadHeaderLinearLayout.setId(basicControlsId + THREAD_HEADER_LAYOUT_ID);

		TextView threadIdTextView = new TextView(mContext);
		threadHeaderLinearLayout.setId(basicControlsId + THREAD_HEADER_TEXT_ID);

		ImageButton deletethreadButton = new ImageButton(mContext);
		deletethreadButton.setId(basicControlsId + THREAD_DELETE_BUTTON_ID);
		deletethreadButton.setOnClickListener(deleteThreadButtonListener);
		//set button and thread id text view
		{
			threadIdTextView.setText("Thread " + (threadId + 1));
			threadIdTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
			threadIdTextView.setLayoutParams(mWeightLayoutParams);

			deletethreadButton.setImageResource(R.drawable.delete);

			threadHeaderLinearLayout.addView(threadIdTextView);
			threadHeaderLinearLayout.addView(deletethreadButton);

			threadLinearLayout.addView(threadHeaderLinearLayout);
		}

		//////////////////////////////////////////////////////
		//button to add additional sequence 
		////////////////////////////////////////////////////
		Button addSequenceButton = new Button(mContext);
		addSequenceButton.setText("Add sequence");
		addSequenceButton.setOnClickListener(addSequenceButtonListener);
		addSequenceButton.setId(basicControlsId + THREAD_ADD_SEQ_BUTTON_ID);

		threadLinearLayout.addView(addSequenceButton);

		mConfigurationLinearLayout.addView(threadLinearLayout);

		//add at least one new sequence to the thread
		addSequence(threadId);
	}

	OnClickListener	deleteThreadButtonListener	= new OnClickListener()
												{
													public void onClick(View v)
													{

														int buttonId = v.getId();

														mDeleteThreadId = (buttonId / MAGIC_THREAD_ID) - 1;

														AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

														// set title
														alertDialogBuilder.setTitle("Thread deletion");

														// set dialog message
														alertDialogBuilder.setMessage("Delete Thread " + (mDeleteThreadId + 1) + "?");
														//alertDialogBuilder.setCancelable(false)
														alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
														{
															public void onClick(DialogInterface dialog, int id)
															{
																removeThreadAt(mDeleteThreadId);
															}
														});
														alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
														{
															public void onClick(DialogInterface dialog, int id)
															{
																// if this button is clicked, just close
																// the dialog box and do nothing
																dialog.cancel();
															}
														});

														// create alert dialog
														AlertDialog alertDialog = alertDialogBuilder.create();

														// show it
														alertDialog.show();

													}
												};

	private void removeThreadAt(int threadId)
	{
		int basicControlsId = (threadId + 1) * MAGIC_THREAD_ID;

		Log.d(TAG, "Deleting thread with id " + threadId);

		TestingThread testingThread = mTestingThreads.get(threadId);

		//we need to delete all sequences first
		while (testingThread.mTestingSequences.size() != 0)
		{
			int key = testingThread.mTestingSequences.keyAt(0);
			removeSequenceAt(threadId, key);
		}

		mConfigurationLinearLayout.removeView(mConfigurationLinearLayout.findViewById(basicControlsId + THREAD_ADD_SEQ_BUTTON_ID));
		mConfigurationLinearLayout.removeView(mConfigurationLinearLayout.findViewById(basicControlsId + THREAD_DELETE_BUTTON_ID));
		mConfigurationLinearLayout.removeView(mConfigurationLinearLayout.findViewById(basicControlsId + THREAD_HEADER_TEXT_ID));
		mConfigurationLinearLayout.removeView(mConfigurationLinearLayout.findViewById(basicControlsId + THREAD_HEADER_LAYOUT_ID));
		mConfigurationLinearLayout.removeView(mConfigurationLinearLayout.findViewById(basicControlsId + THREAD_LAYOUT_ID));

		mTestingThreads.delete(threadId);
	}

	OnClickListener	addSequenceButtonListener	= new OnClickListener()
												{
													public void onClick(View v)
													{
														int buttonId = v.getId();

														int threadId = (buttonId / MAGIC_THREAD_ID) - 1;

														addSequence(threadId);
													}
												};

	private void addSequence(int threadId)
	{
		int basicControlsId = (threadId + 1) * MAGIC_THREAD_ID;

		TestingThread testingThread = mTestingThreads.get(threadId);

		int seqSize = testingThread.mTestingSequences.size();
		Log.d(TAG, "seqSize " + seqSize);
		
		if(seqSize >= 40)
		{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

			// set title
			alertDialogBuilder.setTitle("Too many sequences");

			// set dialog message
			alertDialogBuilder.setMessage("Thread can have 40 sequences maximum");
			alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
				}
			});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
			
			return;
		}

		int seqId = 0;

		if (seqSize != 0)
		{
			seqId = testingThread.mTestingSequences.keyAt(seqSize - 1) + 1;
		}

		Log.d(TAG, "Thread id " + threadId + " Seq id " + seqId);
		testingThread.mTestingSequences.put(seqId, testingThread.new TestingSequence());

		int sequenceControlsId = basicControlsId + (seqId + 1) * MAGIC_SEQ_ID;

		LinearLayout threadLinearLayout = (LinearLayout) mConfigurationLinearLayout.findViewById(basicControlsId + THREAD_LAYOUT_ID);

		LinearLayout sequenceLinearLayout = new LinearLayout(mContext);
		sequenceLinearLayout.setId(sequenceControlsId + 1);
		{
			sequenceLinearLayout.setOrientation(LinearLayout.VERTICAL);
			sequenceLinearLayout.setLayoutParams(mBasicLinearLayoutParams);
		}

		LinearLayout sequenceHeaderLinearLayout = new LinearLayout(mContext);
		sequenceHeaderLinearLayout.setId(sequenceControlsId + 2);

		TextView sequenceIdTextView = new TextView(mContext);
		sequenceIdTextView.setId(sequenceControlsId + 3);
		ImageButton deleteSequenceButton = new ImageButton(mContext);
		deleteSequenceButton.setId(sequenceControlsId + 4);
		deleteSequenceButton.setOnClickListener(deleteSequenceButtonListener);

		{
			sequenceIdTextView.setText("Sequence " + (seqId + 1));
			sequenceIdTextView.setLayoutParams(mWeightLayoutParams);

			deleteSequenceButton.setImageResource(R.drawable.delete);

			sequenceHeaderLinearLayout.addView(sequenceIdTextView);
			sequenceHeaderLinearLayout.addView(deleteSequenceButton);

			sequenceLinearLayout.addView(sequenceHeaderLinearLayout);
		}

		TableLayout sequenceParametersTableLayout = new TableLayout(mContext);
		sequenceParametersTableLayout.setId(sequenceControlsId + 5);
		sequenceParametersTableLayout.setLayoutParams(mBasicLinearLayoutParams);

		///////////////////////////////////////////////////////////////////////
		//time total layout
		//////////////////////////////////////////////////////////////////////

		TableRow timeTotalRow = new TableRow(mContext);
		timeTotalRow.setId(sequenceControlsId + 6);

		TextView timeTotalTextView = new TextView(mContext);
		timeTotalTextView.setId(sequenceControlsId + 7);
		EditText timeTotalEdit = new EditText(mContext);
		timeTotalEdit.setId(sequenceControlsId + 8);

		{
			timeTotalTextView.setText("Total time (ms)");
			timeTotalEdit.setEms(10);
			timeTotalEdit.setSingleLine(true);
			timeTotalEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

			timeTotalRow.addView(timeTotalTextView);
			timeTotalRow.addView(timeTotalEdit);

			sequenceParametersTableLayout.addView(timeTotalRow);
		}

		///////////////////////////////////////////////////////////////////////
		//delay milliseconds layout
		//////////////////////////////////////////////////////////////////////

		TableRow delayMsRow = new TableRow(mContext);
		delayMsRow.setId(sequenceControlsId + 9);

		TextView delayMsTextView = new TextView(mContext);
		delayMsTextView.setId(sequenceControlsId + 10);
		EditText delayMsEdit = new EditText(mContext);
		delayMsEdit.setId(sequenceControlsId + 11);

		{
			delayMsTextView.setText("Delay (ms)");
			delayMsEdit.setEms(10);
			delayMsEdit.setSingleLine(true);
			delayMsEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

			delayMsRow.addView(delayMsTextView);
			delayMsRow.addView(delayMsEdit);

			sequenceParametersTableLayout.addView(delayMsRow);
		}

		///////////////////////////////////////////////////////////////////////
		//bytes layout
		//////////////////////////////////////////////////////////////////////
		TableRow bytesRow = new TableRow(mContext);
		bytesRow.setId(sequenceControlsId + 12);

		TextView bytesTextView = new TextView(mContext);
		bytesTextView.setId(sequenceControlsId + 13);
		EditText bytesEdit = new EditText(mContext);
		bytesEdit.setId(sequenceControlsId + 14);

		{
			bytesTextView.setText("Bytes");
			bytesEdit.setEms(10);
			bytesEdit.setSingleLine(true);
			bytesEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

			bytesRow.addView(bytesTextView);
			bytesRow.addView(bytesEdit);

			sequenceParametersTableLayout.addView(bytesRow);
		}

		///////////////////////////////////////////////////////////////////////
		//repeat layout
		//////////////////////////////////////////////////////////////////////
		TableRow repeatRow = new TableRow(mContext);
		repeatRow.setId(sequenceControlsId + 15);

		TextView repeatView = new TextView(mContext);
		repeatView.setId(sequenceControlsId + 16);
		EditText repeatEdit = new EditText(mContext);
		repeatEdit.setId(sequenceControlsId + 17);

		{
			repeatView.setText("Repeat");
			repeatEdit.setEms(10);
			repeatEdit.setSingleLine(true);
			repeatEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

			repeatRow.addView(repeatView);
			repeatRow.addView(repeatEdit);

			sequenceParametersTableLayout.addView(repeatRow);
		}

		sequenceLinearLayout.addView(sequenceParametersTableLayout);

		threadLinearLayout.addView(sequenceLinearLayout);
	}

	OnClickListener	deleteSequenceButtonListener	= new OnClickListener()
													{
														public void onClick(View v)
														{
															int buttonId = v.getId();

															mDeleteThreadId = (buttonId / MAGIC_THREAD_ID) - 1;
															int basicControlsId = (mDeleteThreadId + 1) * MAGIC_THREAD_ID;

															mDeleteSequenceId = (buttonId - basicControlsId) / MAGIC_SEQ_ID - 1;

															AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

															// set title
															alertDialogBuilder.setTitle("Sequence deletion");

															// set dialog message
															if (mTestingThreads.get(mDeleteThreadId).mTestingSequences.size() == 1)
															{
																alertDialogBuilder.setMessage("This is the only sequence in the thread.\nDelete entire Thread "
																		+ (mDeleteThreadId + 1) + "?");

																alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
																{
																	public void onClick(DialogInterface dialog, int id)
																	{
																		removeThreadAt(mDeleteThreadId);
																	}
																});
																alertDialogBuilder.setNeutralButton("Sequence only", new DialogInterface.OnClickListener()
																{
																	public void onClick(DialogInterface dialog, int id)
																	{
																		removeSequenceAt(mDeleteThreadId, mDeleteSequenceId);
																	}
																});
															}
															else
															{
																alertDialogBuilder.setMessage("Delete Sequence " + (mDeleteSequenceId + 1) + "?");

																alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
																{
																	public void onClick(DialogInterface dialog, int id)
																	{
																		removeSequenceAt(mDeleteThreadId, mDeleteSequenceId);

																	}
																});
															}

															alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
															{
																public void onClick(DialogInterface dialog, int id)
																{
																	// if this button is clicked, just close
																	// the dialog box and do nothing
																	dialog.cancel();
																}
															});

															// create alert dialog
															AlertDialog alertDialog = alertDialogBuilder.create();

															// show it
															alertDialog.show();

														}
													};

	private void removeSequenceAt(int threadId, int seqId)
	{
		int basicControlsId = (threadId + 1) * MAGIC_THREAD_ID;

		Log.d(TAG, "Deleting sequence for thread with id " + threadId + " sequence id " + seqId);

		LinearLayout threadLinearLayout = (LinearLayout) mConfigurationLinearLayout.findViewById(basicControlsId + THREAD_LAYOUT_ID);
		int sequenceControlsId = basicControlsId + (seqId + 1) * MAGIC_SEQ_ID;

		for (int i = 1; i <= 17; i++)
			threadLinearLayout.removeView(threadLinearLayout.findViewById(sequenceControlsId + i));

		mTestingThreads.get(threadId).mTestingSequences.remove(seqId);

	}

	private void loadXmlConfiguration(String filePath)
	{
		Toast.makeText(mContext, "Loading configuration from the XML file", Toast.LENGTH_LONG).show();
	}
	
	private void saveXmlConfiguration(String filePath)
	{
		Toast.makeText(mContext, "Saving configuration to the XML file", Toast.LENGTH_LONG).show();
	}
	
	private int getPixels(float dipValue)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, getResources().getDisplayMetrics());
	}
}
