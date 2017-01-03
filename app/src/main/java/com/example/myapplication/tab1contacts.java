package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by q on 2016-12-28.
 */



public class tab1contacts extends Fragment{

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 200;
    private ArrayList<AddrBean> contacts_name_number = new ArrayList<>();
    private JSONArray forSubmit=new JSONArray();
    public CallbackManager callbackManager;
    ListView listview;

    public static tab1contacts newInstance() {
        return new tab1contacts();
    }

    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1contacts, container,false);

        // data procesing ////

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else{

            AddrBean bean;
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String [] ad = new String[] {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            Cursor cursor = getContext().getContentResolver().query(uri, ad, null, null, null);
            /*cursor.moveToFirst();*/
            if(cursor.getCount()>0){
                Log.d("TEST", "********************************** get contacts!");
                while (cursor.moveToNext()){
                    String name = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME ));
                    Log.d("TEST", "********************************** name : " + name);
                    String number = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER  ));
                    Log.d("TEST", "********************************** number : " + number);

                    JSONObject obj=new JSONObject();

                    try {
                        obj.put("name", name);
                        obj.put("number", number);
                    }catch(org.json.JSONException e){
                        e.printStackTrace();
                    }

                    forSubmit.put(obj);
                    bean = new AddrBean();
                    bean.setName(name);
                    bean.setNumber(number);
                    contacts_name_number.add(bean);
                }
            } cursor.close();
        }

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        listview = (ListView) rootView.findViewById(R.id.list);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("result",object.toString());
                        try {
                            JSONObject obj;
                            JSONArray arr;
                            JSONParser jsonParser=new JSONParser();

                            String user_id = object.getString("id");
                            Log.d("test", "****************************************** " + user_id);

                            new GraphRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    "/" + user_id + "/taggable_friends?limit=20",
                                    null,
                                    HttpMethod.GET,
                                    new GraphRequest.Callback() {
                                        public void onCompleted(GraphResponse response) {
                                            Log.d("RESPONSE", "********************************* friend list reponse : " + response.toString());

                                            try {
                                              /*  JSONObject object1 = response.getJSONObject();
                                                JSONArray array1 = object1.getJSONArray("data");

                                                */

                                                JSONObject object1 = response.getJSONObject();
                                                JSONArray arr=(JSONArray) object1.get("data");

                                                for(int i=0;i<arr.length();i++){

                                                    JSONObject object2=new JSONObject();
                                                    AddrBean b=new AddrBean();
                                                    JSONObject obj2=(JSONObject) arr.get(i);

                                                    object2.put("name",obj2.get("name").toString());
                                                    Log.d("name",obj2.get("name").toString());
                                                    b.setName(obj2.get("name").toString());

                                                    object2.put("number","facebook");

                                                    Log.d("number","facebook");
                                                    b.setNumber("facebook");

                                                    forSubmit.put(object2);
                                                    contacts_name_number.add(b);
                                                }

                                                HttpConnection http=new HttpConnection();
                                                http.execute(forSubmit);

                                                PhoneNumberAdapter adapter2 = new PhoneNumberAdapter (getLayoutInflater(null), contacts_name_number);
                                                listview.setAdapter(adapter2);
                                                adapter2.notifyDataSetChanged();

                                            }catch(org.json.JSONException e){
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                            ).executeAsync();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr",error.toString());
            }
        });

        return rootView;
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
                            contacts_name_number.add(bean);
                        }

                    } cursor.close();
                }
                else {
                    Toast.makeText(getActivity(), "Permission Denied",Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to chec
            // permissions this app might request
        }
    }

}
