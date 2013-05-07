package ca.jvsh.pulmonary;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class BreathlessnessListAdapter extends SimpleAdapter
{
	private int[]	colors	= new int[] {
							0xF0396592,
							0xF02D7C9D,
							0xF01391A0,
							0xF0046846,
							0xF0065420,
							0xF036752E,
							0xF0868006,
							0xF09F8C02,
							0xF09B7311,
							0xF09E4620,
							0xF0942722,
							0xF06B140A
							};

	BreathlessnessListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
	{
		super(context, data, resource, from, to);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = super.getView(position, convertView, parent);
		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);
		
		return view;
	}
}
