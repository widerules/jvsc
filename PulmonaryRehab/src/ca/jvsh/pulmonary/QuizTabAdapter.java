package ca.jvsh.pulmonary;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class QuizTabAdapter extends ArrayAdapter<QuizTab>
{

	Context	context;
	int		layoutResourceId;
	QuizTab	data[]	= null;

	public QuizTabAdapter(Context context, int layoutResourceId, QuizTab[] data)
	{
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		WeatherHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new WeatherHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
			holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);

			row.setTag(holder);
		}
		else
		{
			holder = (WeatherHolder) row.getTag();
		}

		QuizTab weather = data[position];
		holder.txtTitle.setText(weather.name);
		holder.imgIcon.setImageResource(weather.icon);

		return row;
	}

	static class WeatherHolder
	{
		ImageView	imgIcon;
		TextView	txtTitle;
	}
}
