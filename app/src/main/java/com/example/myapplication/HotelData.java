package com.example.myapplication;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.ArrayList;
import com.example.myapplication.HotelData;


 /**
 * Created by q on 2016-12-28.
 */

public class HotelData {

    String name;
    String number;

    public HotelData(String name, String number){
    this.name = name;
    this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
