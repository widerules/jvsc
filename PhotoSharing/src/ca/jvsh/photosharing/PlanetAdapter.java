package ca.jvsh.photosharing;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class PlanetAdapter extends ArrayAdapter<Planet> {
	 
private List<Planet> planetList;
private EditorFragment editorFragment;
 
public PlanetAdapter(List<Planet> planetList, EditorFragment editorFragment) {
    super(editorFragment.mContext, R.layout.computer_list_item, planetList);
    this.planetList = planetList;
    this.editorFragment = editorFragment;
}
 
public View getView(int position, View convertView, ViewGroup parent) {
     
	View v = convertView;
    
    PlanetHolder holder = new PlanetHolder();
     
    // First let's verify the convertView is not null
    if (convertView == null) {
        // This a new view we inflate the new layout
        LayoutInflater inflater = (LayoutInflater) editorFragment.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.computer_list_item, null);
        // Now we can fill the layout with the right values
        CheckBox chk = (CheckBox) v.findViewById(R.id.chk);
        TextView tv = (TextView) v.findViewById(R.id.name);
        TextView distView = (TextView) v.findViewById(R.id.dist);
 
         
        holder.planetNameView = tv;
        holder.distView = distView;
        holder.chk = chk;
        holder.chk.setOnCheckedChangeListener(editorFragment);
        v.setTag(holder);
    }
    else
        holder = (PlanetHolder) v.getTag();
     
    Planet p = planetList.get(position);
    holder.planetNameView.setText(p.getName());
    holder.distView.setText("" + p.getDistance());
     
     
    return v;
}

private static class PlanetHolder {
    public TextView planetNameView;
    public TextView distView;
    public CheckBox chk;
}

}
