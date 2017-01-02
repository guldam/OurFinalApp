package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by q on 2016-12-28.
 */

public class HotelDataAdapter extends BaseAdapter {

    ArrayList<HotelData> datas;
    LayoutInflater inflater;

    public HotelDataAdapter(LayoutInflater inflater, ArrayList<HotelData> datas){

        this.datas = datas;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if( convertView == null){
            convertView = inflater.inflate(R.layout.list_layout,null);

        }
        TextView text_name = (TextView)convertView.findViewById(R.id.text_name);
        TextView text_number = (TextView)convertView.findViewById(R.id.text_number);

        text_name.setText(datas.get(position).getName());
        text_number.setText(datas.get(position).getNumber());

        return convertView;
    }
}