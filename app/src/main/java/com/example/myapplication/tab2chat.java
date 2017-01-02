package com.example.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

// Fragment class for B tab (Gallery)
public class tab2chat extends Fragment {

    public static tab2chat newInstance() {
        return new tab2chat();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

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
        return rootView;
    }
}