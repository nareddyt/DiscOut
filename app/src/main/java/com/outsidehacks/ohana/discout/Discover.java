package com.outsidehacks.ohana.discout;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry on 7/23/16.
 */
public class Discover {
    private static String[] locations = {"Panhandle", "Lands End", "The Barbary", "Sutro"
            , "Twin Peaks", "The House by Heineken"};
    private List<EventData> events;
    private Context context;

    public Discover(Context context) {
        this.context = context;
        events = readEvents();
    }

    public List<EventData> getEvents() {
        return events;
    }

    public List<EventData> readEvents() {
        InputStream is = null;
        List<EventData> results = new ArrayList<>();
        try {
            Resources res = context.getResources();
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
                        results.add(mapper.readValue(String.valueOf(events.getJSONObject(j)), EventData.class));
                        Log.v("event", String.valueOf(mapper.readValue(String.valueOf(events.getJSONObject(j)),
                                EventData.class)));
                    }
                }
            }
        } catch (FileNotFoundException | JSONException | JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }
}
