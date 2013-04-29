package ca.jvsh.pulmonary;

import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class BreathlessnessListAdapter<T> extends ArrayAdapter<T>
{
	private int[]	colors	= new int[] { 0x30FF0000, 0x300000FF };

	
	BreathlessnessListAdapter(Context context, int textViewResourceId)
	{
		super(context, textViewResourceId);
	}
	
	BreathlessnessListAdapter(Context context, int resource, int textViewResourceId)
	{
		super(context, resource, textViewResourceId );
	}
	
	BreathlessnessListAdapter(Context context, int textViewResourceId, T[] objects)
	{
		super(context, textViewResourceId, objects);
	}
	
	 BreathlessnessListAdapter(Context context, int resource, int textViewResourceId, T[] objects)
	{
		super(context, resource, textViewResourceId, objects);
	}
	
	 BreathlessnessListAdapter(Context context, int textViewResourceId, List<T> objects)
	{
		super(context, textViewResourceId, objects);
	}
	
	 BreathlessnessListAdapter(Context context, int resource, int textViewResourceId, List<T> objects)
	{
		super(context, resource, textViewResourceId, objects);
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
