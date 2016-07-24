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
    private String eventDate;
    private String eventImage;
    private String previewUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventData eventData = (EventData) o;

        if (eventName != null ? !eventName.equals(eventData.eventName) : eventData.eventName != null) return false;
        if (eventPage != null ? !eventPage.equals(eventData.eventPage) : eventData.eventPage != null) return false;
        if (startTime != null ? !startTime.equals(eventData.startTime) : eventData.startTime != null) return false;
        if (endTime != null ? !endTime.equals(eventData.endTime) : eventData.endTime != null) return false;
        if (location != null ? !location.equals(eventData.location) : eventData.location != null) return false;
        if (eventDate != null ? !eventDate.equals(eventData.eventDate) : eventData.eventDate != null) return false;
        if (eventImage != null ? !eventImage.equals(eventData.eventImage) : eventData.eventImage != null) return false;
        return previewUrl != null ? previewUrl.equals(eventData.previewUrl) : eventData.previewUrl == null;

    }

    @Override
    public int hashCode() {
        int result = eventName != null ? eventName.hashCode() : 0;
        result = 31 * result + (eventPage != null ? eventPage.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (eventDate != null ? eventDate.hashCode() : 0);
        result = 31 * result + (eventImage != null ? eventImage.hashCode() : 0);
        result = 31 * result + (previewUrl != null ? previewUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventData{" +
                "eventName='" + eventName + '\'' +
                ", eventPage='" + eventPage + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", location='" + location + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", eventImage='" + eventImage + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                '}';
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
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

}
