package com.learning_app.user.chathamkulam.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.learning_app.user.chathamkulam.Model.MyBounceInterpolator;
import com.learning_app.user.chathamkulam.R;


public class FMFeedBack extends Fragment {

    EditText feedEdt;
    RelativeLayout lay_required,lay_letters_rq;

    public FMFeedBack() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fm_feedback, container, false);

        feedEdt = (EditText)view.findViewById(R.id.feedEdt);
        lay_required = (RelativeLayout)view.findViewById(R.id.lay_required);
        lay_letters_rq =(RelativeLayout)view.findViewById(R.id.lay_letters_rq);

        return view;
    }

    public void sendFeedback(){

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"info@theempiresoft.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Feedback About Learning App");
        i.putExtra(Intent.EXTRA_TEXT,feedEdt.getText().toString().trim());

        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.menu_share).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_submit : {

                String feedback = feedEdt.getText().toString();

                if (feedback.length() != 0){

                    if (feedback.length() <= 200){

                        sendFeedback();

                    } else {

//                        Use bounce interpolator with amplitude 0.2 and frequency 20
                        Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
                        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                        myAnim.setInterpolator(interpolator);

                        lay_letters_rq.startAnimation(myAnim);
                    }

                } else {

//                    Use bounce interpolator with amplitude 0.2 and frequency 20
                    Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                    myAnim.setInterpolator(interpolator);

                    lay_required.startAnimation(myAnim);

                }

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Feedback");
    }
}
