package com.outsidehacks.ohana.discout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

public class DiscoverFragment extends Fragment implements View.OnClickListener {

    private boolean isPlaying = false;

    private TextView textView;

    private LinearLayout layout;
    private View yesButton;
    private View noButton;
    private View maybeButton;
    private TextView titleView;
    private CardView cardView;
    private ImageView playButton;
    private ImageView eventImage;
    private List<EventData> eventData;
    private TextView timeTextView;
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
        eventData = ((MainActivity) getActivity()).getEventDatasForQueue();
        Log.v("Event stuff", eventData.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_discover, container, false);

        textView = (TextView) v.findViewById(R.id.done_text);

        yesButton = v.findViewById(R.id.yes_button);
        noButton = v.findViewById(R.id.no_button);
        maybeButton = v.findViewById(R.id.maybe_button);
        titleView = (TextView) v.findViewById(R.id.title);
        playButton = (ImageView) v.findViewById(R.id.play_button);
        eventImage = (ImageView) v.findViewById(R.id.event_image);
        cardView = (CardView) v.findViewById(R.id.card_view);
        timeTextView = (TextView) v.findViewById(R.id.time_text);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {

                    isPlaying = false;
                    if (Build.VERSION.SDK_INT >= 21) {
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp,
                                null));
                    } else {
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                    }
                    mp.stop();
                    mp.reset();
                } else {
                    EventData d = eventData.get(index);

                    try {
                        Log.v("Preview url", d.getPreviewUrl());
                        mp.setDataSource(d.getPreviewUrl());
                        mp.prepare();
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                isPlaying = false;
                                if (Build.VERSION.SDK_INT >= 21) {
                                    playButton.setImageDrawable(getResources().getDrawable(R.drawable
                                            .ic_play_arrow_black_24dp, null));
                                } else {
                                    playButton.setImageDrawable(getResources().getDrawable(R.drawable
                                            .ic_play_arrow_black_24dp));
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception exc) {
                        playButton.setVisibility(View.INVISIBLE);
                    }
                    mp.start();
                    if (Build.VERSION.SDK_INT >= 21) {
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_black_24dp, null));
                    } else {
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_black_24dp));
                    }
                    isPlaying = true;
                }

            }
        });
        if (!eventData.isEmpty()) {
            index = 0;
            EventData event = eventData.get(index);
            titleView.setText(eventData.get(index).getEventName() + "\n" + eventData.get(index).getLocation());
            Picasso.with(getActivity()).load(event.getEventImage()).into(eventImage);
            if (eventData.get(index).getPreviewUrl().isEmpty()) {
                playButton.setVisibility(View.GONE);
            } else {
                playButton.setVisibility(View.VISIBLE);
            }
            timeTextView.setText("August " + eventData.get(index).getStartTime().substring(0, 1) + "th at " + eventData
                    .get(index).getStartTime().substring(5) + " - " + eventData.get(index).getEndTime().substring(5));
            eventImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(eventData.get(index).getEventPage()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.android.chrome");
                    try {
                        DiscoverFragment.this.startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        // Chrome browser presumably not installed so allow user to choose instead
                        intent.setPackage(null);
                        DiscoverFragment.this.startActivity(intent);
                    }
                }
            });
        } else {
            cardView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText("No More Suggestions");
        }
        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);
        maybeButton.setOnClickListener(this);


        return v;
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        yesButton.setClickable(false);
        if (id == R.id.yes_button) {

            EventData currEvent = eventData.get(index);
            eventData.remove(index);

            EventBus.getDefault().post(currEvent);

        } else if (id == R.id.no_button) {
            eventData.remove(index);
        } else if (id == R.id.maybe_button) {
            index++;
        }
        mp.stop();
        mp.reset();
        isPlaying = false;
        if (Build.VERSION.SDK_INT >= 21) {
            playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp, null));
        } else {
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
                if ((index) >= eventData.size()) {
                    index = 0;
                }
                if (index < eventData.size()) {
                    cardView.setVisibility(View.VISIBLE);
                    cardView.setScaleX(0);
                    cardView.setScaleY(0);
                    titleView.setText(eventData.get(index).getEventName() + "\n" + eventData.get(index).getLocation());
                    timeTextView.setText("August " + eventData.get(index).getStartTime().substring(0, 1) + "th at " +
                            eventData
                            .get(index).getStartTime().substring(5) + " - " + eventData.get(index).getEndTime()
                            .substring(5));
                    Picasso.with(getActivity()).load(eventData.get(index).getEventImage()).into(eventImage);

                    if (eventData.get(index).getPreviewUrl().isEmpty()) {
                        playButton.setVisibility(View.GONE);
                    } else {
                        playButton.setVisibility(View.VISIBLE);
                    }
                    eventImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(eventData.get(index)
                                    .getEventPage()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setPackage("com.android.chrome");
                            try {
                                DiscoverFragment.this.startActivity(intent);
                            } catch (ActivityNotFoundException ex) {
                                // Chrome browser presumably not installed so allow user to choose instead
                                intent.setPackage(null);
                                DiscoverFragment.this.startActivity(intent);
                            }
                        }
                    });
                    cardView.animate().scaleY(1f).scaleX(1f).setStartDelay(1000).setInterpolator(new
                            OvershootInterpolator()).setDuration(300).start();
                    yesButton.setClickable(true);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("No More Suggestions");
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        cardView.startAnimation(slideAnim);
    }
}