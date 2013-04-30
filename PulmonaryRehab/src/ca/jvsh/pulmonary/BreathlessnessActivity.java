package ca.jvsh.pulmonary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BreathlessnessActivity extends Activity
{
	ListView	lv;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.breathlessness_activity);

		this.getActionBar().hide();
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		final ArrayList<HashMap<String, Object>> m_data = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> status_0 = new HashMap<String, Object>();
		status_0.put("score_text", "0");
		status_0.put("description_text", "I feel myself like I can breathe underwater.");
		m_data.add(status_0);

		HashMap<String, Object> status_05 = new HashMap<String, Object>();
		status_05.put("score_text", "0.5");
		status_05.put("description_text", "Can breathe with my full chest. Never felt better.");
		m_data.add(status_05);

		HashMap<String, Object> status_1 = new HashMap<String, Object>();
		status_1.put("score_text", "1");
		status_1.put("description_text", "My breath is ok if you don't mind the coffee aroma.");
		m_data.add(status_1);

		HashMap<String, Object> status_2 = new HashMap<String, Object>();
		status_2.put("score_text", "2");
		status_2.put("description_text", "I feel myself like I am a chain-smoker.");
		m_data.add(status_2);

		HashMap<String, Object> status_3 = new HashMap<String, Object>();
		status_3.put("score_text", "3");
		status_3.put("description_text", "I am experiencing severe shortness of breath.");
		m_data.add(status_3);

		HashMap<String, Object> status_4 = new HashMap<String, Object>();
		status_4.put("score_text", "4");
		status_4.put("description_text", "Feels like somebody is sitting on my chest. Breathing is very hard.");
		m_data.add(status_4);

		HashMap<String, Object> status_5 = new HashMap<String, Object>();
		status_5.put("score_text", "5");
		status_5.put("description_text", "Feels like I've just climbed the Everest.");
		m_data.add(status_5);

		HashMap<String, Object> status_6 = new HashMap<String, Object>();
		status_6.put("score_text", "6");
		status_6.put("description_text", "Quick! Throw me an inhalator!");
		m_data.add(status_6);

		HashMap<String, Object> status_7 = new HashMap<String, Object>();
		status_7.put("score_text", "7");
		status_7.put("description_text", "Feels like a thousand of thin needles in my chest. Breathing though pain.");
		m_data.add(status_7);

		HashMap<String, Object> status_8 = new HashMap<String, Object>();
		status_8.put("score_text", "8");
		status_8.put("description_text", "Breathing is very painful, feels like there is a slipknot on my neck");
		m_data.add(status_8);

		HashMap<String, Object> status_9 = new HashMap<String, Object>();
		status_9.put("score_text", "9");
		status_9.put("description_text", "I am about to suffocate. One more button press and I am done.");
		m_data.add(status_9);

		HashMap<String, Object> status_10 = new HashMap<String, Object>();
		status_10.put("score_text", "10");
		status_10.put("description_text", "Can't breathe. I am selecting this item using my last powers.");
		m_data.add(status_10);

		for (HashMap<String, Object> m : m_data)
			//make data of this view should not be null (hide )
			m.put("checked", false);
		//end init data

		final ListView lv = (ListView) findViewById(R.id.selection_listview);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		final BreathlessnessListAdapter adapter = new BreathlessnessListAdapter(this,
				m_data,
				R.layout.breathlessness_item,
				new String[] { "score_text", "description_text", "checked" },
				new int[] { R.id.tv_ScoreText, R.id.tv_DescriptionText, R.id.rb_Choice });

		adapter.setViewBinder(new ViewBinder()
		{
			public boolean setViewValue(View view, Object data, String textRepresentation)
			{
				if (data == null) //if 2nd line text is null, its textview should be hidden 
				{
					view.setVisibility(View.GONE);
					return true;
				}
				view.setVisibility(View.VISIBLE);
				return false;
			}

		});

		// Bind to our new adapter.
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3)
			{
				RadioButton rb = (RadioButton) v.findViewById(R.id.rb_Choice);
				if (!rb.isChecked()) //OFF->ON
				{
					for (HashMap<String, Object> m : m_data)
						//clean previous selected
						m.put("checked", false);

					m_data.get(arg2).put("checked", true);
					adapter.notifyDataSetChanged();
				}
			}
		});

		// find the button and add click method to it
		final Button nextButton = (Button) findViewById(R.id.next_button);
		nextButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				/*int p = lv.getCheckedItemPosition();
				String s = ((TextView) lv.getChildAt(p)).getText().toString();
				Toast.makeText(BreathlessnessActivity.this, "Selected item is " + s, Toast.LENGTH_LONG).show();
				*/
				int r = -1;
				for (int i = 0; i < m_data.size(); i++) //clean previous selected
				{
					HashMap<String, Object> m = m_data.get(i);
					Boolean x = (Boolean) m.get("checked");
					if (x == true)
					{
						r = i;
						break; //break, since it's a single choice list
					}
				}
				Intent hIntent = new Intent();
				hIntent.setClass(v.getContext(), OximeterConnectActivity.class);
				startActivity(hIntent);
			}
		});

	}

	private List<Car> getData()
	{
		List<Car> cars = new ArrayList<Car>();
		cars.add(new Car("Dodge", "Viper"));
		cars.add(new Car("Chevrolet", "Corvette"));
		cars.add(new Car("Aston Martin", "Vanquish"));
		cars.add(new Car("Lamborghini", "Diablo"));
		cars.add(new Car("Ford", "Pinto"));
		return cars;
	}

	public class Car extends HashMap
	{
		public String	make;
		public String	model;

		public Car(String make, String model)
		{
			this.make = make;
			this.model = model;
		}

		public static final String	KEY_MODEL	= "model";
		public static final String	KEY_MAKE	= "make";

		@Override
		public String get(Object k)
		{
			String key = (String) k;
			if (KEY_MAKE.equals(key))
				return make;
			else if (KEY_MODEL.equals(key))
				return model;
			return null;
		}
	}
}
