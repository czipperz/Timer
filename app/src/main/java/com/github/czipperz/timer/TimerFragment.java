package com.github.czipperz.timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimerFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment {
    private TimerFragmentInteractionListener mListener;
    private int index;

    public TimerFragment() {
        // Required empty public constructor
    }

    public static final String ARG_INDEX = "index";

    public static TimerFragment newInstance(int index) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            index = arguments.getInt(ARG_INDEX);
        }
        Intent intent = new Intent(getActivity(), PromptTimeActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                mListener.updateTime(this, data.getExtras().getLong(PromptTimeActivity.ARG_TIME));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mListener.deleteTimer(this);
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                mListener.updateTime(this, data.getExtras().getLong(PromptTimeActivity.ARG_TIME));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        View time = view.findViewById(R.id.fragment_timer_time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View ignore) {
                Intent intent = new Intent(getActivity(), PromptTimeActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        View play = view.findViewById(R.id.fragment_timer_play_button);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View ignore) {
                mListener.playTimer(TimerFragment.this);
            }
        });

        View pause = view.findViewById(R.id.fragment_timer_pause_button);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View ignore) {
                mListener.pauseTimer(TimerFragment.this);
            }
        });

        View stop = view.findViewById(R.id.fragment_timer_stop_button);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View ignore) {
                mListener.stopTimer(TimerFragment.this);
            }
        });

        View delete = view.findViewById(R.id.fragment_timer_delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View ignore) {
                mListener.deleteTimer(TimerFragment.this);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TimerFragmentInteractionListener) {
            mListener = (TimerFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TimerFragmentInteractionListener");
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
            TextView timeView = view.findViewById(R.id.fragment_timer_time);
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
    public interface TimerFragmentInteractionListener {
        void playTimer(TimerFragment fragment);
        void pauseTimer(TimerFragment fragment);
        void stopTimer(TimerFragment fragment);
        void deleteTimer(TimerFragment fragment);
        void updateTime(TimerFragment fragment, long time);
    }
}
