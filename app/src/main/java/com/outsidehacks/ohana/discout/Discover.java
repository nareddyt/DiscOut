package com.outsidehacks.ohana.discout;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    public Discover() {
        events = readEvents();
    }

    public List<EventData> readEvents() {
        InputStream is = null;
        List<EventData> results = new ArrayList<>();
        try {
            is = new FileInputStream("schedule.json");
            String jsonTxt = IOUtils.toString(is);
            JSONArray jsonArray = new JSONArray(jsonTxt);
            Log.v("JSON Array: ", String.valueOf(jsonArray));
            ObjectMapper mapper = new ObjectMapper();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject oneDaySchedule = jsonArray.getJSONObject(i);
                for (String location : locations) {
                    JSONArray events = oneDaySchedule.getJSONArray(location);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        results.add(mapper.readValue(events.getJSONObject(j), Event.class));
                        Log.v("event", mapper.readValue(events.getJSONObject(j), Event.class));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<EventData> getEvents() {
        return events;
    }
}
