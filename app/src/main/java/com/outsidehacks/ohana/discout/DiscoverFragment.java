package com.outsidehacks.ohana.discout;

import android.animation.Animator;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spotify.sdk.android.player.Config;

import java.io.IOException;
import java.util.ArrayList;

public class DiscoverFragment extends Fragment implements View.OnClickListener {

    private boolean isPlaying = false;

    private LinearLayout layout;
    private View yesButton;
    private View noButton;
    private View maybeButton;
    private TextView titleView;
    private CardView cardView;
    private ImageView playButton;
    private ArrayList<EventData> eventData;
    private int index;
    private MediaPlayer mp;
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
        mp = new MediaPlayer();
        eventData = new ArrayList<EventData>();
        eventData.add(new EventData("Cool stuff", "Idk", "2pm","5pm", "Cool street", "http://d318706lgtcm8e.cloudfront.net/mp3-preview/f454c8224828e21fa146af84916fd22cb89cedc6"));
        eventData.add(new EventData("Cool stuffeee", "Idk", "2pm","5pm", "Cool street", "https://p.scdn.co/mp3-preview/12b8cee72118f995f5494e1b34251e4ac997445e"));
        eventData.add(new EventData("Cool stuffs", "Idk", "2pm","5pm", "Cool street", "http://d318706lgtcm8e.cloudfront.net/mp3-preview/f454c8224828e21fa146af84916fd22cb89cedc6"));
        eventData.add(new EventData("Cool stuff dude", "Idk", "2pm","5pm", "Cool street", "http://d318706lgtcm8e.cloudfront.net/mp3-preview/f454c8224828e21fa146af84916fd22cb89cedc6"));
        eventData.add(new EventData("Cool stuff :D", "Idk", "2pm","5pm", "Cool street", "http://d318706lgtcm8e.cloudfront.net/mp3-preview/f454c8224828e21fa146af84916fd22cb89cedc6"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recommend, container, false);
        yesButton = v.findViewById(R.id.yes_button);
        noButton = v.findViewById(R.id.no_button);
        maybeButton = v.findViewById(R.id.maybe_button);
        titleView = (TextView) v.findViewById(R.id.title);
        playButton = (ImageView) v.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View view) {
                if (isPlaying){

                    isPlaying = false;
                    if (Build.VERSION.SDK_INT >= 21){
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp, null));
                    }else{
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                    }
                    mp.stop();
                    mp.reset();
                }else {
                    EventData d = eventData.get(index);

                    try {
                        mp.setDataSource(d.getPreviewUrl());
                        mp.prepare();
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                isPlaying = false;
                                if (Build.VERSION.SDK_INT >= 21){
                                    playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp, null));
                                }else{
                                    playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.start();
                    if (Build.VERSION.SDK_INT >= 21){
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_black_24dp, null));
                    }else{
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_black_24dp));
                    }
                    isPlaying = true;
                }

            }
        });
        if (!eventData.isEmpty()){
            index = 0;
            EventData event = eventData.get(index);
            titleView.setText(event.getEventName());
        }
        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);
        maybeButton.setOnClickListener(this);
        cardView = (CardView) v.findViewById(R.id.card_view);


        return v;
    }


    @Override
    public void onClick(View view) {
        final int id = view.getId();
        yesButton.setClickable(false);
        if (id == R.id.yes_button){

            eventData.remove(index);

        }else if( id == R.id.no_button){
            eventData.remove(index);
        }else if ( id == R.id.maybe_button){
            index++;
        }
        mp.stop();
        mp.reset();
        isPlaying = false;
        if (Build.VERSION.SDK_INT >= 21){
            playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp, null));
        }else{
            playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
        }
        Animation slideAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setVisibility(View.GONE);
//                    // Process discovery
                if ((index) >= eventData.size()){
                    index = 0;
                }
                if (index < eventData.size()) {
                    cardView.setVisibility(View.VISIBLE);
                    cardView.setScaleX(0);
                    cardView.setScaleY(0);
                    titleView.setText(eventData.get(index).getEventName());
                    cardView.animate().scaleY(1f).scaleX(1f).setStartDelay(1000).setInterpolator(new OvershootInterpolator()).setDuration(300).start();
                    yesButton.setClickable(true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        cardView.startAnimation(slideAnim);
    }
}