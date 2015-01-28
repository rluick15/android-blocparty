package com.bloc.blocparty.ui.fragments;


import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bloc.blocparty.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateCollectionDialogFragment extends DialogFragment {

    public CreateCollectionDialogFragment() {} // Required empty public constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_collection_dialog, container, false);

        return view;
    }


}
