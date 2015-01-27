package com.bloc.blocparty.ui.fragments;


import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bloc.blocparty.R;
import com.bloc.blocparty.facebook.FacebookRequest;
import com.bloc.blocparty.twitter.TwitterRequest;
import com.bloc.blocparty.utils.ConnectionDetector;
import com.bloc.blocparty.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadPhotoDialogFragment extends DialogFragment {

    private Bitmap mImage;
    private Context mContext;
    private String mNetwork;
    private EditText mMessage;

    public UploadPhotoDialogFragment() {} // Required empty public constructor

    public UploadPhotoDialogFragment(Context context, Bitmap image) {
        this.mImage = image;
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_upload_photo_dialog, container, false);
        getDialog().setTitle(getString(R.string.title_upload_photo));
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.thin_border);

        ImageView photo = (ImageView) view.findViewById(R.id.imageView);
        photo.setImageBitmap(mImage);

        mMessage = (EditText) view.findViewById(R.id.messageText);

        Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final Button submitButton = (Button) view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionDetector detector = new ConnectionDetector(mContext);
                if(!detector.isConnectingToInternet()) {
                    Toast.makeText(mContext, getString(R.string.toast_no_internet), Toast.LENGTH_LONG).show();
                }
                else {
                    uploadImage();
                }
            }
        });

        RadioGroup networkSelector = (RadioGroup) view.findViewById(R.id.radioGroup);
        networkSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                submitButton.setEnabled(true);
                switch (checkedId) {
                    case R.id.facebookButton:
                        mNetwork = Constants.FACEBOOK;
                        break;
                    case R.id.twitterButton:
                        mNetwork = Constants.TWITTER;
                        break;
                }
            }
        });
        
        return view;
    }

    private void uploadImage() {
        if(mNetwork.equals(Constants.FACEBOOK)) {
            FacebookRequest request = new FacebookRequest(mContext);

            if(request.isLoggedIn()) {
                String message = mMessage.getText().toString();
                request.uploadPhoto(mImage, message);
                dismiss();
            }
            else {
                Toast.makeText(mContext, mContext.getString(R.string.toast_no_facebook_session), Toast.LENGTH_LONG).show();
            }
        }
        else if(mNetwork.equals(Constants.TWITTER)) {
            TwitterRequest request = new TwitterRequest(mContext);

            if(request.isLoggedIn()) {
                String message = mMessage.getText().toString();
            }
            else {
                Toast.makeText(mContext, mContext.getString(R.string.toast_no_twitter_session), Toast.LENGTH_LONG).show();
            }
         }
    }


}
