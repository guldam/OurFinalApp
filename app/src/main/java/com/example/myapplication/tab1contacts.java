package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.simple.parser.JSONParser;
import android.content.Context;
import android.content.res.AssetManager;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.widget.AdapterView;

import android.database.Cursor;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import static android.os.Build.ID;


/**
 * Created by q on 2016-12-28.
 */



public class tab1contacts extends Fragment{
    ArrayList<HotelData> datas= new ArrayList<HotelData>();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 200;
    private ArrayList<AddrBean> contacts_name_number = new ArrayList<>();

    ListView listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1contacts, null);

        // data procesing ////

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else{

            AddrBean bean;
            ArrayList<AddrBean> list = new ArrayList<>();
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String [] ad = new String[] {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            Cursor cursor = getContext().getContentResolver().query(uri, ad, null, null, null);
//            cursor.moveToFirst();
            if(cursor.getCount()>0){
                Log.d("TEST", "********************************** get contacts!");
                while (cursor.moveToNext()){
                    String name = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME ));
                    Log.d("TEST", "********************************** name : " + name);
                    String number = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER  ));
                    Log.d("TEST", "********************************** number : " + number);
                    bean = new AddrBean();
                    bean.setName(name);
                    bean.setNumber(number);
                    list.add(bean);
                }
                contacts_name_number = list;
            } cursor.close();
        }


        listview = (ListView) rootView.findViewById(R.id.list);

        HotelDataAdapter adapter = new HotelDataAdapter( getLayoutInflater(null) , datas );
        PhoneNumberAdapter adapter2 = new PhoneNumberAdapter (getLayoutInflater(null), contacts_name_number);
        listview.setAdapter(adapter2);
        return rootView;
    }



    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getActivity().getAssets().open("hotel.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    void doJSONParser()  {
        try {
            JSONArray obj = new JSONArray( loadJSONFromAsset() );
           /* String str = obj.toString();*/

           // JSONObject obj = new JSONObject(loadJSONFromAsset());
           // JSONArray jarray = new JSONArray(obj);   /////////// 여기를 고쳐야 함
            //jarray = loadJSONFromAsset();

            for (int i = 0; i < obj.length(); i++) {

               JSONObject jObject = obj.getJSONObject(i);
                String name = jObject.getString("name");
                String number = jObject.getString("number");
                datas.add(new HotelData(name,number));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class AddrBean{
        String name;
        String number;
        public void setName(String name){
            this.name = name;
        }
        public void setNumber(String number){
            this.number = number;
        }
        public String getName() {
            return name;
        }
        public String getNumber() {
            return number;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Cursor cursor = null;
                    AddrBean bean = null;
                    ArrayList<AddrBean> list = null;
                    Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String[] ad = new String[]{
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    };
                    cursor = getContext().getContentResolver().query(uri, ad, null, null, null);
                    cursor.moveToFirst();
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            String name = cursor.getString(cursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String number = cursor.getString(cursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            bean = new AddrBean();
                            bean.setName(name);
                            bean.setNumber(number);
                            list.add(bean);
                        }
                        contacts_name_number = list;
                    } cursor.close();
                }
                else {
                    Toast.makeText(getActivity(), "Permission Denied",Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check fo
            // permissions this app might request
        }
    }

}
