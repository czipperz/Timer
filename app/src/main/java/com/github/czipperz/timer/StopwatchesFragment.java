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
 * {@link StopwatchesFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StopwatchesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StopwatchesFragment extends Fragment {
    private StopwatchesFragmentInteractionListener mListener;

    public StopwatchesFragment() {
        // Required empty public constructor
    }

    public static StopwatchesFragment newInstance() {
        StopwatchesFragment fragment = new StopwatchesFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stopwatches, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StopwatchesFragmentInteractionListener) {
            mListener = (StopwatchesFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement StopwatchesFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public synchronized void addStopwatch() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) {
            fragmentManager = getActivity().getSupportFragmentManager();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LinearLayout list = getView().findViewById(R.id.fragment_stopwatches_list);
        StopwatchFragment fragment = StopwatchFragment.newInstance(list.getChildCount());
        fragmentTransaction.add(R.id.fragment_stopwatches_list, fragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();

        mListener.addStopwatch(fragment);

        final ScrollView scrollView = getView().findViewById(R.id.fragment_stopwatches_scroll_view);
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
    public interface StopwatchesFragmentInteractionListener {
        void addStopwatch(StopwatchFragment fragment);
    }
}
