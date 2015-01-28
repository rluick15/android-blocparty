package com.bloc.blocparty.ui.fragments;


import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bloc.blocparty.R;
import com.bloc.blocparty.objects.Collection;
import com.bloc.blocparty.objects.FeedItem;
import com.bloc.blocparty.ui.adapters.CollectionListItemAdapter;
import com.bloc.blocparty.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddToCollectionFragment extends DialogFragment {

    private Context mContext;
    private FeedItem mFeedItem;
    private ListView mCollectionList;

    public AddToCollectionFragment() {} // Required empty public constructor

    public AddToCollectionFragment(Context context, FeedItem feedItem) {
        this.mContext = context;
        this.mFeedItem = feedItem;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        TextView dialogTitle = (TextView) view.findViewById(R.id.dialogTitle);
        dialogTitle.setText(mContext.getString(R.string.title_dialog_add_to_collection));

        //get Collection Array
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(Constants.PREFS, 0);
        String json = sharedPrefs.getString(Constants.COLLECTION_ARRAY, null);
        Type type = new TypeToken<ArrayList<Collection>>(){}.getType();
        final ArrayList<Collection> collections = new Gson().fromJson(json, type);

        mCollectionList = (ListView) view.findViewById(R.id.collectionList);
        mCollectionList.setEmptyView(view.findViewById(android.R.id.empty));

        if(collections != null) {
            CollectionListItemAdapter adapter = new CollectionListItemAdapter(mContext, collections);
            mCollectionList.setAdapter(adapter);
        }

        mCollectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
            }
        });

        Button selectCollectionButton = (Button) view.findViewById(R.id.selectCollectionButton);
        selectCollectionButton.setText(mContext.getString(R.string.title_dialog_add_to_collection));

        return view;
    }


}
