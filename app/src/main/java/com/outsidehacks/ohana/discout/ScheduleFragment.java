package com.outsidehacks.ohana.discout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment implements CalendarPickerController {
    private OnFragmentInteractionListener mListener;
    private List<CalendarEvent> eventList = new ArrayList<>();
    private boolean[][] availableHours = new boolean[24][3];
    private AgendaCalendarView mAgendaCalendarView;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance() {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public boolean addEvent(String artistName, String location, Calendar startTime, Calendar endTime) {
        int day = startTime.get(Calendar.DAY_OF_MONTH) - 5;
        int startHour = startTime.get(Calendar.HOUR_OF_DAY);
        int endHour = endTime.get(Calendar.HOUR_OF_DAY);

        boolean taken = false;
        for (int i = startHour; i <= endHour && !taken; i++) {
            if (availableHours[i][day]) {
                taken = true;
            }
        }

        if (taken) {
            return false;
        }

        for (int i = startHour; i <= endHour; i++) {
            availableHours[i][day] = true;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm", Locale.US);
        String startTimeString = simpleDateFormat.format(startTime.getTime());
        String endTimeString = simpleDateFormat.format(endTime.getTime());

        Event event = new Event(artistName, "dummy", location + "\t\t" + startTimeString + " - " + endTimeString,
                ContextCompat.getColor(this.getContext(), R.color.colorAccent), startTime, endTime, false);
        eventList.add(event);

        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        mAgendaCalendarView = (AgendaCalendarView) view.findViewById(R.id.agenda_calendar_view);

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.set(2016, 8, 5);
        maxDate.set(2016, 8, 7);

        this.mockList();

        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
        mAgendaCalendarView.onStickyHeaderChanged(null, null, 0, 0);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDaySelected(DayItem dayItem) {
    }

    @Override
    public void onEventSelected(CalendarEvent event) {
    }

    @Override
    public void onScrollToDate(Calendar calendar) {
    }

    private void mockList() {
        Calendar startTime1 = Calendar.getInstance();
        Calendar endTime1 = Calendar.getInstance();
        startTime1.set(2016, 8, 5, 3, 48);
        endTime1.set(2016, 8, 5, 12, 15);
        this.addEvent("Test 1", "Twin Peaks", startTime1, endTime1);

        Calendar startTime2 = Calendar.getInstance();
        Calendar endTime2 = Calendar.getInstance();
        startTime2.set(2016, 8, 6, 3, 48);
        endTime2.set(2016, 8, 6, 12, 15);
        this.addEvent("Test 2", "Sutro", startTime2, endTime2);

        Calendar startTime3 = Calendar.getInstance();
        Calendar endTime3 = Calendar.getInstance();
        startTime3.set(2016, 8, 6, 7, 21);
        endTime3.set(2016, 8, 6, 8, 15);
        this.addEvent("Test 3", "SF", startTime3, endTime3);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
