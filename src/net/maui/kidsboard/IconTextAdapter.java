package net.maui.kidsboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IconTextAdapter extends ArrayAdapter<String> {

	public IconTextAdapter(Context context, int resource,
			int textViewResourceId, String[] objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// return super.getView(position, convertView, parent);

		LayoutInflater inflater = LayoutInflater.from(getContext());
		/*
		View row = inflater.inflate(R.layout.row, parent, false);
		TextView label = (TextView) row.findViewById(R.id.weekofday);
		label.setText(DayOfWeek[position]);

		ImageView icon = (ImageView) row.findViewById(R.id.icon);

		if (DayOfWeek[position] == "Sunday") {
			icon.setImageResource(R.drawable.icon);
		} else {
			icon.setImageResource(R.drawable.icongray);
		}
		return row;
		*/
		return null;
	}

}
