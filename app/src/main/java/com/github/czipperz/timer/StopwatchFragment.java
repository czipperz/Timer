package com.github.czipperz.timer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StopwatchFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StopwatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StopwatchFragment extends Fragment {
    private StopwatchFragmentInteractionListener mListener = null;
    private int index;

    public StopwatchFragment() {
        // Required empty public constructor
    }

    public static final String ARG_INDEX = "index";

    public static StopwatchFragment newInstance(int index) {
        StopwatchFragment fragment = new StopwatchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        View play = view.findViewById(R.id.fragment_stopwatch_play_button);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View ignore) {
                mListener.playStopwatch(StopwatchFragment.this);
            }
        });

        View pause = view.findViewById(R.id.fragment_stopwatch_pause_button);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View ignore) {
                mListener.pauseStopwatch(StopwatchFragment.this);
            }
        });

        View stop = view.findViewById(R.id.fragment_stopwatch_stop_button);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View ignore) {
                mListener.stopStopwatch(StopwatchFragment.this);
            }
        });

        View delete = view.findViewById(R.id.fragment_stopwatch_delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View ignore) {
                mListener.deleteStopwatch(StopwatchFragment.this);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StopwatchFragmentInteractionListener) {
            mListener = (StopwatchFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement StopwatchFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setText(String text) {
        View view = getView();
        if (view != null) {
            TextView timeView = view.findViewById(R.id.fragment_stopwatch_time);
            timeView.setText(text);
        }
    }

    public void setIndex(int i) {
        index = i;
    }
    public int getIndex() { return index; }

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
    public interface StopwatchFragmentInteractionListener {
        void playStopwatch(StopwatchFragment fragment);
        void pauseStopwatch(StopwatchFragment fragment);
        void stopStopwatch(StopwatchFragment fragment);
        void deleteStopwatch(StopwatchFragment fragment);
    }
}
