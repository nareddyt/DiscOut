package com.outsidehacks.ohana.discout;

/**
 * Created by jerry on 7/23/16.
 */
public class EventData {
    private String eventName;
    private String eventPage;
    private String startTime;
    private String endTime;
    private String location;

    public String getEventPage() {
        return eventPage;
    }

    public void setEventPage(String eventPage) {
        this.eventPage = eventPage;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
