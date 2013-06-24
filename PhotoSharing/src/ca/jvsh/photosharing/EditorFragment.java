package ca.jvsh.photosharing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import ca.jvsh.photosharing.R;
import ca.jvsh.photosharing.TestingThread.TestingSequence;

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
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class EditorFragment extends SherlockFragment implements android.widget.CompoundButton.OnCheckedChangeListener
{

	private EditText					mConfigurationFilePathEdit;
	//private LinearLayout				mConfigurationLinearLayout;
	private Button						mOpenFileButton;
	ListView mComputersList; 
	//private Button						mAddNewThreadButton;
	ArrayAdapter mArrayAdapter;

	View								mView;
	Context						mContext;

	private static final LayoutParams	mBasicLinearLayoutParams	= new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	private static final LayoutParams	mWeightLayoutParams			= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
	private static final InputFilter[]	mFilterArray				= new InputFilter[1];
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

	//boolean								mConfigurationChanged		= false;
	//MenuItem							mSaveMenuItem;

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
		//mConfigurationFilePathEdit.addTextChangedListener(mEditConfigFileChangeTextWatcher);

		//mConfigurationLinearLayout = (LinearLayout) mView.findViewById(R.id.linearLayoutThreads);

		mFilterArray[0] = new InputFilter.LengthFilter(6);
		
		
		mOpenFileButton = (Button) mView.findViewById(R.id.buttonOpenFile);
		mOpenFileButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), FileDialog.class);
				intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/");

				//can user select directories or not
				intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
				intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);

				//alternatively you can set file filter
				intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "jpg" });

				startActivityForResult(intent, SelectionMode.MODE_OPEN);
			}
		});

		mComputersList = (ListView) mView.findViewById(R.id.list);
		 initList();
		 aAdpt = new PlanetAdapter(planetsList, this);
		 mComputersList.setAdapter(aAdpt);
	        
	        // React to user clicks on item
		 mComputersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
						long id) {


					TextView clickedView = (TextView) view.findViewById(R.id.name);

					Toast.makeText(mContext, "Item with id ["+id+"] - Position ["+position+"] - Planet ["+clickedView.getText()+"]", Toast.LENGTH_SHORT).show();

					
					
					
					CheckBox rb = (CheckBox) view.findViewById(R.id.chk);
					rb.setChecked(!rb.isChecked());
				}
			   });

		return mView;
	}
	
	List<Planet> planetsList = new ArrayList<Planet>();
	PlanetAdapter aAdpt;

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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
         int pos = mComputersList.getPositionForView(buttonView);
         System.out.println("Pos ["+pos+"]");
         if (pos != ListView.INVALID_POSITION) {
             Planet p = planetsList.get(pos);    
             
             p.setChecked(isChecked);
         }
         
    }
	
	private void initList() {
        // We populate the planets
       
        planetsList.add(new Planet("Mercury", 10));
        planetsList.add(new Planet("Venus", 20));
        planetsList.add(new Planet("Mars", 30));
        planetsList.add(new Planet("Jupiter", 40));
        planetsList.add(new Planet("Saturn", 50));
        planetsList.add(new Planet("Uranus", 60));
        planetsList.add(new Planet("Neptune", 70));
     
    	
    }
	
	/*public void addPlanet(View view) {
    	final Dialog d = new Dialog(this);
    	d.setContentView(R.layout.dialog);
    	d.setTitle("Add planet");
    	d.setCancelable(true);
    	
    	final EditText edit = (EditText) d.findViewById(R.id.editTextPlanet);
    	Button b = (Button) d.findViewById(R.id.button1);
    	b.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String planetName = edit.getText().toString();
				MainActivity.this.planetsList.add(new Planet(planetName, 0));
				MainActivity.this.aAdpt.notifyDataSetChanged(); // We notify the data model is changed
				d.dismiss();
			}
		});
    	
    	d.show();
    }*/

	public synchronized void onActivityResult(final int requestCode,
			int resultCode, final Intent data)
	{

		if (resultCode == Activity.RESULT_OK)
		{
			String filePath = data.getStringExtra(FileDialog.RESULT_PATH);

			mConfigurationFilePathEdit.setText(filePath);
			//mAddNewThreadButton.setEnabled(true);

			if (requestCode == SelectionMode.MODE_OPEN)
			{
				Log.d(TAG, "Opening old configuration...");

				//loadXmlConfiguration(filePath);
				//mConfigurationChanged = false;
				Log.d(TAG, "onActivityResult mSaveMenuItem.setVisible(false);");
				//mSaveMenuItem.setVisible(false);
			}

		}
		else if (resultCode == Activity.RESULT_CANCELED)
		{
			mConfigurationFilePathEdit.setText("");
			//mSaveMenuItem.setVisible(false);
			//mAddNewThreadButton.setEnabled(false);
		}

	}

	public boolean checkFileExt(String filepath, String checkExt)
	{
		String ext = filepath.substring((filepath.lastIndexOf(".") + 1), filepath.length());
		if (ext.compareToIgnoreCase(checkExt) != 0)
			return false;
		return true;
	}

	private int getPixels(float dipValue)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, getResources().getDisplayMetrics());
	}
}
