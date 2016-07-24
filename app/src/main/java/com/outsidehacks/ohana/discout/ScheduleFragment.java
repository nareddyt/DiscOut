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
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    public boolean addEvent(String artistName, String location, String startTimeString, String endTimeString) {
        int day = Integer.valueOf(startTimeString.substring(0, 1));
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 7, day);
        day -= 5;

        startTimeString = startTimeString.substring(5);
        int startHour;
        if (startTimeString.charAt(1) == ':') {
            startHour = Integer.valueOf(startTimeString.substring(0, 1));
        } else {
            startHour = Integer.valueOf(startTimeString.substring(0, 2));
        }

        endTimeString = endTimeString.substring(5);
        int endHour;
        if (endTimeString.charAt(1) == ':') {
            endHour = Integer.valueOf(endTimeString.substring(0, 1));
        } else {
            endHour = Integer.valueOf(endTimeString.substring(0, 2));
        }

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

        BaseCalendarEvent event = new BaseCalendarEvent(artistName, "dummy", location + "\t\t" + startTimeString + " " +
                "- " + endTimeString, ContextCompat.getColor(this.getContext(), R.color.colorAccent), calendar,
                calendar, false);
        eventList.add(event);

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.set(2016, 7, 5);
        maxDate.set(2016, 7, 7);
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);

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

        EventBus.getDefault().register(this);

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.set(2016, 7, 5);
        maxDate.set(2016, 7, 7);

        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventAdded(EventData eventData) {
        this.addEvent(eventData.getEventName(), eventData.getLocation(), eventData.getStartTime(), eventData
                .getEndTime());
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
