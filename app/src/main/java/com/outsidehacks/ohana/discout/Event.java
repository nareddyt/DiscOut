package com.outsidehacks.ohana.discout;

import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;

import java.util.Calendar;

/**
 * Created by tnareddy on 7/24/16.
 */
public class Event extends BaseCalendarEvent {

    public Event(String title, String description, String location, int color, Calendar startTime, Calendar endTime,
                 boolean allDay) {
        super(title, description, location, color, startTime, endTime, allDay);
    }
}
