package com.example.myapplication;

/**
 * Created by q on 2017-01-01.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PhoneNumberAdapter extends BaseAdapter{

    ArrayList<tab1contacts.AddrBean> datas =null ;
    LayoutInflater inflater = null  ;

    public PhoneNumberAdapter(LayoutInflater inflater, ArrayList<tab1contacts.AddrBean> datas){

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
