package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

import static com.facebook.FacebookSdk.getApplicationContext;

// Fragment class for B tab (Gallery)
public class tab2chat extends Fragment {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 200;
    public static tab2chat newInstance() {
        return new tab2chat();
    }
    public static String[] projection = {MediaStore.Images.Media.DATA};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);






        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else {
            Cursor imageCursor = getContext().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 이미지 컨텐트 테이블
                    projection, // DATA를 출력
                    null,       // 모든 개체 출력
                    null,
                    null);      // 정렬 안 함

            ArrayList<Uri> result = new ArrayList<>(imageCursor.getCount());
            int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);

            if (imageCursor == null) {
                Toast.makeText(getApplicationContext(), "No images in Gallery", Toast.LENGTH_LONG).show();
            } else if (imageCursor.moveToFirst()) {
                do {
                    String filePath = imageCursor.getString(dataColumnIndex);
                    Uri imageUri = Uri.parse(filePath);
                    result.add(imageUri);
                } while (imageCursor.moveToNext());
            } else {
                // imageCursor가 비었습니다.
            }
            imageCursor.close();


            for (int i = 1; i < result.size(); i++) {

                Bitmap bitmap = BitmapFactory.decodeFile(result.get(i).toString());
                new UploadAsyncTask().execute(bitmap);
            }
        }





        final GalleryAdapter adapter = new GalleryAdapter(getContext());
        for (int i = 1; i < 21; i++) {
            adapter.add(getResources().getIdentifier("t" + i, "drawable", getActivity().getPackageName()));
        }

        final ImageView iv1 = (ImageView) rootView.findViewById(R.id.imageView1);
        final PhotoViewAttacher attacher = new PhotoViewAttacher(iv1);

        Gallery g = (Gallery) rootView.findViewById(R.id.gallery1);
        g.setAdapter(adapter);
        g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                iv1.setImageResource(adapter.get(position));
                attacher.update();
            }
        });

        for (int i = 1; i < 21; i++) {

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("t" + i, "drawable", getActivity().getPackageName()));
            new UploadAsyncTask().execute(bitmap);
        }

        return rootView;
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {




        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Cursor imageCursor = getContext().getContentResolver().query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 이미지 컨텐트 테이블
                            projection, // DATA를 출력
                            null,       // 모든 개체 출력
                            null,
                            null);      // 정렬 안 함

                    ArrayList<Uri> result = new ArrayList<>(imageCursor.getCount());
                    int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);

                    if (imageCursor == null) {
                        Toast.makeText(getApplicationContext(), "No images in Gallery", Toast.LENGTH_LONG).show();
                    } else if (imageCursor.moveToFirst()) {
                        do {
                            String filePath = imageCursor.getString(dataColumnIndex);
                            Uri imageUri = Uri.parse(filePath);
                            result.add(imageUri);
                        } while (imageCursor.moveToNext());
                    } else {
                        // imageCursor가 비었습니다.
                    }
                    imageCursor.close();


                    for (int i = 1; i < result.size(); i++) {

                        Bitmap bitmap = BitmapFactory.decodeFile(result.get(i).toString());
                        new UploadAsyncTask().execute(bitmap);
                    }

                }else {
                    Toast.makeText(getActivity(), "Permission Denied",Toast.LENGTH_SHORT).show();
                }
                return;


        }
    }







   private class UploadAsyncTask extends AsyncTask<Bitmap, Void, String> {
        String server_url = "http://52.78.19.20:8080/api/pics";

        String boundary = "qPo$^%@#bvER";

        @Override
        protected String doInBackground(Bitmap... param) {
            HttpURLConnection conn = null;
            String id = null;
            try {
                URL url = new URL(server_url);
                Log.d("Port:", "Port " + url.getPort());
                conn = (HttpURLConnection) url.openConnection();
                Log.d("Connection", conn.toString());
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                conn.setRequestMethod("POST");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bm=param[0];

                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                byte[] image = baos.toByteArray();
                String encodedImage = Base64.encodeToString(image, Base64.DEFAULT);
                JSONObject j = new JSONObject();

                j.put("image", encodedImage);
                Log.d("CS496", "Image sent");
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(j.toString());


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
                return id;
            }

        }

    }
}