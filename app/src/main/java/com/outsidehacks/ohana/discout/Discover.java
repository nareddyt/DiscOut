package com.outsidehacks.ohana.discout;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jerry on 7/23/16.
 */
public class Discover {
    private static String[] locations = {"Panhandle", "Lands End", "The Barbary", "Sutro"
            , "Twin Peaks", "The House by Heineken"};
    private List<EventData> events;

    private final OkHttpClient client = new OkHttpClient();

    private final JsonParser parser = new JsonParser();

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

    public List<String> getPlayList(String userId, String authToken) throws IOException {
        String playListUrl = "https://api.spotify.com/v1/me/playlists";
        String response = getRequestSync(playListUrl, authToken);
        JsonObject json = parser.parse(jsonStr).getAsJsonObject();
        List<String> playListIds = new ArrayList<>();
        for (JsonElement e: json.get("items").getAsJsonArray()) {
            String playListId = e.getAsJsonObject().get("snapshot_id").getAsString();
            playListIds.add(playListId);
        }
        return playListIds;
    }

    public String getUserId(String authToken) throws IOException {
        String userIdUrl = "https://api.spotify.com/v1/me";
        String response = getRequestSync(userIdUrl, authToken);
        JsonObject json = parser.parse(response).getAsJsonObject();
        return json.get("id").getAsString();
    }
    public Map<String, List<String>> getGenreMap() {


        return genreMap;
    }

    private Map<String, List<String>> genreMap = new HashMap<>();
    private Context context;

    public Discover(Context context) {
        this.context = context;
        events = readEvents();
    }

    public List<EventData> getEvents() {
        return events;
    }


    public Map<String, String> getGenresFromPlayList() {
        OKHttpClient okHttpClient = new OKHttpClient();

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
}
