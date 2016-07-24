package com.outsidehacks.ohana.discout;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "6c6016da831348e18df08257e074c9c4";


    private final OkHttpClient client = new OkHttpClient();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String authToken;
    private Map<String, Integer> myGenreMap = new HashMap<>();
    private ProgressBar progressBar;
    private List<EventData> eventDatasForQueue = new ArrayList<EventData>();

    public List<EventData> getEventDatasForQueue() {
        return eventDatasForQueue;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AuthenticationClient.clearCookies(this);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Discover";
                case 1:
                    return "Schedule";
            }
            return null;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                Bundle args = new Bundle();
                args.putString("token", getIntent().getStringExtra("AUTH_TOKEN"));
                DiscoverFragment fragment = DiscoverFragment.newInstance();
                fragment.setArguments(args);
                return fragment;
            } else {
                return ScheduleFragment.newInstance();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);


        this.authToken = this.getIntent().getStringExtra("AUTH_TOKEN");

        Log.v("Auth", authToken);

        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/following?type=artist&after=0000000000000000000000")
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            Handler handler = new Handler(context.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String json = response.body().string();

                Log.v("Response", json);

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    Log.v("JSON object", jsonObject.toString());

                    JSONObject artistsObject = jsonObject.getJSONObject("artists");
                    Log.v("Artists object", artistsObject.toString());

                    JSONArray itemsArray = artistsObject.getJSONArray("items");
                    Log.v("Items array", itemsArray.toString());

                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject item = itemsArray.getJSONObject(i);
                        JSONArray genres = item.getJSONArray("genres");
                        Log.v("Genres List", genres.toString());

                        for (int j = 0; j < genres.length(); j++) {
                            String genre = genres.getString(j);
                            String[] splitGenres = genre.split(" ");
                            Log.v("Genre", Arrays.toString(splitGenres));

                            for (String splitGenre : splitGenres) {
                                Integer genreCount = myGenreMap.get(splitGenre);
                                if (genreCount == null || genreCount == 0) {
                                    myGenreMap.put(splitGenre, 1);
                                } else {
                                    myGenreMap.put(splitGenre, 1 + genreCount);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.v("Genres Map", myGenreMap.toString());
                Discover discover = new Discover(context);

                List<EventData> eventList = discover.getEvents();

                final Map<String, EventData> eventArtistMap = new HashMap<String, EventData>();
                for (EventData eventData : eventList) {
                    eventArtistMap.put(eventData.getEventName(), eventData);
                }
                Log.v("eventArtistMap", eventArtistMap.toString());
                Map<String, List<String>> artistGenreMap = discover.getGenreMap();

                List<Map.Entry<String, Integer>> genrePriority = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : myGenreMap.entrySet()) {
                    genrePriority.add(entry);
                }

                Collections.sort(genrePriority, new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                        return entry2.getValue().compareTo(entry1.getValue());
                    }
                });

                Log.v("Genre Priority List", genrePriority.toString());

                for (Map.Entry<String, Integer> genre : genrePriority) {
                    List<String> artists = artistGenreMap.get(genre.getKey());
                    if (artists != null) {
                        for (String artist : artists) {
                            EventData eventData = eventArtistMap.get(artist);
                            if (eventData != null && !eventDatasForQueue.contains(eventData)) {
                                eventDatasForQueue.add(eventData);
                            }
                        }
                    }
                }

                Log.v("Event queue", eventDatasForQueue.toString());

                discover.getArtists(authToken, new Discover.ArtistsAction() {
                    @Override
                    public void execute(List<String> artists) {
                        Log.v("Playlist Artists", artists.toString());

                        for (String artist : artists) {
                            EventData eventData = eventArtistMap.get(artist.toUpperCase());
                            if (eventData != null && !eventDatasForQueue.contains(eventData)) {
                                eventDatasForQueue.add(eventData);
                            }
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Create the adapter that will return a fragment for each of the three
                                // primary sections of the activity.
                                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                                // Set up the ViewPager with the sections adapter.
                                mViewPager = (ViewPager) findViewById(R.id.container);
                                mViewPager.setAdapter(mSectionsPagerAdapter);

                                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                                setSupportActionBar(toolbar);

                                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                                tabLayout.setupWithViewPager(mViewPager);

                                progressBar.animate().alpha(0).setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                }).start();
                            }
                        });
                    }
                });
            }
        });
    }
}
