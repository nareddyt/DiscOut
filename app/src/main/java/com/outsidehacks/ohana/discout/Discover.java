package com.outsidehacks.ohana.discout;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jerry on 7/23/16.
 */
public class Discover {

    private static String[] locations = {"Panhandle", "Lands End", "The Barbary", "Sutro"
            , "Twin Peaks", "The House by Heineken"};
    private final OkHttpClient client = new OkHttpClient();
    private final JsonParser parser = new JsonParser();
    private List<EventData> events;
    private Map<String, List<String>> genreMap = new HashMap<>();
    private Context context;

    public Discover(Context context) {
        this.context = context;
        events = readEvents();
    }

    public void getArtists(String authToken, ArtistsAction onDone) throws IOException {
        String playlistUrl = "https://api.spotify.com/v1/me/playlists";
        String json = getRequestSync(playlistUrl, authToken);
        JsonArray playlists = parser.parse(json).getAsJsonObject().get("items").getAsJsonArray();
        List<String> artists = new ArrayList<>();
        AtomicInteger numLeft = new AtomicInteger(playlists.size());
        for (JsonElement e : playlists) {
            String playlistId = e.getAsJsonObject().get("id").getAsString();
            Log.v("Playlist id", playlistId);
            String userId = getUserId(authToken);
            getArtistsInPlaylist(artists, numLeft, userId, playlistId, authToken, onDone);
        }
    }

    public List<EventData> getEvents() {
        return events;
    }

    public Map<String, List<String>> getGenreMap() {
        return genreMap;
    }

    public void getRequest(String url, String authToken, Callback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public String getRequestSync(String url, String authToken) throws IOException {
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String getUserId(String authToken) throws IOException {
        String userIdUrl = "https://api.spotify.com/v1/me";
        String response = getRequestSync(userIdUrl, authToken);
        JsonObject json = parser.parse(response).getAsJsonObject();
        return json.get("id").getAsString();
    }

    public List<EventData> readEvents() {
        Resources res = context.getResources();
        InputStream is = null;
        List<EventData> results = new ArrayList<>();

        try {
            is = res.openRawResource(R.raw.schedule);
            Log.v("Discover", "Called");
            String jsonTxt = IOUtils.toString(is);
            JSONArray jsonArray = new JSONArray(jsonTxt);
            Log.v("JSON Array: ", String.valueOf(jsonArray));
            ObjectMapper mapper = new ObjectMapper();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject oneDaySchedule = jsonArray.getJSONObject(i);
                for (String location : locations) {
                    JSONArray events = oneDaySchedule.getJSONArray(location);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        EventData eventData = mapper.readValue(String.valueOf(events.getJSONObject(j)), EventData
                                .class);
                        eventData.setStartTime((i + 5) + " -- " + eventData.getStartTime());
                        eventData.setEndTime((i + 5) + " -- " + eventData.getEndTime());
                        results.add(eventData);

                        Log.v("event", eventData.toString());
                    }
                }
            }

            is = res.openRawResource(R.raw.genres);
            String jsonText2 = IOUtils.toString(is);

            ObjectMapper mapper2 = new ObjectMapper();
            genreMap = mapper2.readValue(jsonText2, Map.class);
            Log.v("Genre Map", genreMap.toString());

        } catch (FileNotFoundException | JSONException | JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    private void getArtistsInPlaylist(final List<String> artists, final AtomicInteger numLeft, String userId, String
            playlistId, String authToken, final ArtistsAction onDone) {
        String playlistTracksUrl = "https://api.spotify.com/v1/users/" + userId + "/playlists/" + playlistId +
                "/tracks";
        getRequest(playlistTracksUrl, authToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.v("Json", json);
                Log.v("Json Object", parser.parse(json).getAsJsonObject().toString());
                JsonArray tracks = parser.parse(json).getAsJsonObject().get("items").getAsJsonArray();
                for (JsonElement e : tracks) {
                    JsonObject track = e.getAsJsonObject();
                    Log.v("Track", track.toString());
                    String artist = track.get("track").getAsJsonObject().get("artists").getAsJsonArray().get(0)
                            .getAsJsonObject().get("name").getAsString();
                    if (!artists.contains(artist)) {
                        artists.add(artist);
                    }
                }
                Integer left = numLeft.decrementAndGet();
                if (left.equals(0)) {
                    onDone.execute(artists);
                }
            }
        });
    }

    static abstract class ArtistsAction {
        abstract public void execute(List<String> artists);
    }
}
