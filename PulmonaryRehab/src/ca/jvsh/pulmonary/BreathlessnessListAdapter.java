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
							0xF0A0D799,
							0xF095C0AD,
							0xF0BFCFB4,
							0xF0D9DABB,
							0xF0F3DCBD,
							0xF0FADDBF,
							0xF0F8C5B4,
							0xF0F6ACA9,
							0xF0F59798,
							0xF0EF5E6D,
							0xF0EC3E4F,
							0xF0D1182B
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
