package com.outsidehacks.ohana.discout;

import android.animation.Animator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class DiscoverFragment extends Fragment implements View.OnClickListener {

    private LinearLayout layout;
    private View yesButton;
    private View noButton;
    private View maybeButton;
    private CardView cardView;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recommend, container, false);
        yesButton = v.findViewById(R.id.yes_button);
        noButton = v.findViewById(R.id.no_button);
        maybeButton = v.findViewById(R.id.maybe_button);
        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);
        maybeButton.setOnClickListener(this);
        cardView = (CardView) v.findViewById(R.id.card_view);
        return v;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.yes_button){
            cardView.setVisibility(View.GONE);
//            cardView.animate().scaleX(0).scaleY(0).setDuration(300).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animator) {
//                    yesButton.setClickable(false);
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animator) {
//                    cardView.setVisibility(View.GONE);
//                    // Process discovery
//
//                    cardView.setVisibility(View.VISIBLE);
//                    cardView.animate().scaleY(1f).scaleX(1f).setStartDelay(1000).setInterpolator(new OvershootInterpolator()).setDuration(250).start();
//                    yesButton.setClickable(true);
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animator) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animator) {
//
//                }
//            }).start();
        }else if( id == R.id.no_button){

        }else if ( id == R.id.maybe_button){

        }
    }
}