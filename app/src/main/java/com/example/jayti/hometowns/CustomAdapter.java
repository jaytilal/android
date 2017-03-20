package com.example.jayti.hometowns;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jayti on 3/18/2017.
 */

public class CustomAdapter extends ArrayAdapter<User> implements View.OnClickListener{
    private ArrayList<User> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView nickname;
        TextView country;
        TextView state;
        TextView city;
        TextView year;
    }

    public CustomAdapter(ArrayList<User> data, Context context) {
        super(context, R.layout.list_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.nickname = (TextView) convertView.findViewById(R.id.nick_name);
            viewHolder.country = (TextView) convertView.findViewById(R.id.country);
            viewHolder.city = (TextView) convertView.findViewById(R.id.city);
            viewHolder.state = (TextView) convertView.findViewById(R.id.state);
            viewHolder.year = (TextView) convertView.findViewById(R.id.year);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        viewHolder.nickname.setText(dataModel.getNickname());
        viewHolder.country.setText(dataModel.getCountry());
        viewHolder.state.setText(dataModel.getState());
        viewHolder.city.setText(dataModel.getCity());
        viewHolder.year.setText(String.valueOf(dataModel.getYear()));
        return convertView;
    }
}

