package com.bloc.blocparty.ui.fragments;


import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bloc.blocparty.R;
import com.bloc.blocparty.objects.Collection;
import com.bloc.blocparty.utils.Constants;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateCollectionDialogFragment extends DialogFragment {

    private FilterDialogFragment mFilterFragment;
    private EditText mTitleField;
    private Context mContext;
    private ArrayList<Collection> mCollections;

    public CreateCollectionDialogFragment() {} // Required empty public constructor

    public CreateCollectionDialogFragment(FilterDialogFragment filterDialogFragment, Context context,
                                          ArrayList<Collection> collections) {
        this.mFilterFragment = filterDialogFragment;
        this.mContext = context;
        this.mCollections = collections;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_collection_dialog, container, false);
        getDialog().setTitle(getString(R.string.dialog_create_collection));

        mTitleField = (EditText) view.findViewById(R.id.collectionTitle);

        final Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button createCollectionButton = (Button) view.findViewById(R.id.createButton);
        createCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((mTitleField.getText().toString()).equals("")) {
                    Toast.makeText(mContext, mContext.getString(R.string.toast_no_title), Toast.LENGTH_LONG).show();
                }
                else {
                    if(mCollections == null) {
                        mCollections = new ArrayList<>();
                    }

                    Collection collection = new Collection(mTitleField.getText().toString());
                    mCollections.add(collection);

                    String jsonCat = new Gson().toJson(mCollections);
                    SharedPreferences.Editor prefsEditor =
                            mContext.getSharedPreferences(Constants.PREFS, 0).edit();
                    prefsEditor.putString(Constants.COLLECTION_ARRAY, jsonCat);
                    prefsEditor.commit();

                    mFilterFragment.dismiss();
                    dismiss();
                    FilterDialogFragment fragment = new FilterDialogFragment(mContext);
                    fragment.show(getFragmentManager(), "dialog");
                }
            }
        });

        return view;
    }


}
