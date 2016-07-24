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
    private String previewUrl;

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public EventData(String eventName, String eventPage, String startTime, String endTime, String location, String previewUrl) {
        this.eventName = eventName;
        this.eventPage = eventPage;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.previewUrl = previewUrl;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventPage() {
        return eventPage;
    }

    public void setEventPage(String eventPage) {
        this.eventPage = eventPage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "EventData{" +
                "eventName='" + eventName + '\'' +
                ", eventPage='" + eventPage + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
