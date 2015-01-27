package com.bloc.blocparty.ui.fragments;


import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bloc.blocparty.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadPhotoDialogFragment extends DialogFragment {

    private Bitmap mImage;

    public UploadPhotoDialogFragment() {} // Required empty public constructor

    public UploadPhotoDialogFragment(Bitmap image) {
        this.mImage = image;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_photo_dialog, container, false);
        return view;
    }


}
