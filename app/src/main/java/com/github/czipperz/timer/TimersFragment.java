package com.github.czipperz.timer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimersFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimersFragment extends Fragment {
    private TimersFragmentInteractionListener mListener;

    public TimersFragment() {
        // Required empty public constructor
    }

    public static TimersFragment newInstance() {
        TimersFragment fragment = new TimersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timers, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TimersFragmentInteractionListener) {
            mListener = (TimersFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TimersFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public synchronized void addTimer() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) {
            fragmentManager = getActivity().getSupportFragmentManager();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LinearLayout list = getView().findViewById(R.id.fragment_timers_list);
        TimerFragment fragment = TimerFragment.newInstance(list.getChildCount());
        fragmentTransaction.add(R.id.fragment_timers_list, fragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();

        mListener.addTimer(fragment);

        final ScrollView scrollView = getView().findViewById(R.id.fragment_timers_scroll_view);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) {
            fragmentManager = getActivity().getSupportFragmentManager();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
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
    public interface TimersFragmentInteractionListener {
        void addTimer(TimerFragment fragment);
    }
}
