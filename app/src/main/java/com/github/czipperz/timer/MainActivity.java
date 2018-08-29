package com.github.czipperz.timer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements
        TimerFragment.TimerFragmentInteractionListener,
        TimersFragment.TimersFragmentInteractionListener,
        StopwatchesFragment.StopwatchesFragmentInteractionListener,
        StopwatchFragment.StopwatchFragmentInteractionListener {
    private TimersFragment timersFragment;
    private StopwatchesFragment stopwatchesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        timersFragment = TimersFragment.newInstance();
        stopwatchesFragment = StopwatchesFragment.newInstance();

        getSupportActionBar().hide();

        workerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        workerRun.set(false);
    }

    private static String timeToString(long time) {
        time /= 10;
        long milliseconds = time % 100;
        time /= 100;
        long seconds = time % 60;
        time /= 60;
        long minutes = time % 60;
        time /= 60;
        long hour = time % 24;
        return String.format(Locale.US, "%02d:%02d:%02d.%02d", hour, minutes, seconds, milliseconds);
    }

    private AtomicBoolean workerRun = new AtomicBoolean(true);
    private Thread workerThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (workerRun.get()) {
                try {
                    stopwatchesLock.acquire();
                    try {
                        for (int i = 0; i < stopwatches.size(); ++i) {
                            StopwatchData s = stopwatches.get(i);
                            long time;
                            if (s.startTime != -1) {
                                time = s.startSeconds + (System.currentTimeMillis() - s.startTime);
                            } else {
                                time = s.startSeconds;
                            }
                            final String text = timeToString(time);
                            final StopwatchFragment fragment = s.fragment;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fragment.setText(text);
                                }
                            });
                        }
                    } finally {
                        stopwatchesLock.release();
                    }
                    timersLock.acquire();
                    try {
                        for (int i = 0; i < timers.size(); ++i) {
                            TimerData t = timers.get(i);
                            long time;
                            if (t.startTime != -1) {
                                time = t.startSeconds - (System.currentTimeMillis() - t.startTime);
                                if (time <= 0) {
                                    t.startSeconds = 0;
                                    t.startTime = -1;
                                    // TODO play alarm sound
                                }
                            } else {
                                time = t.startSeconds;
                            }
                            final String text = timeToString(time);
                            final TimerFragment fragment = t.fragment;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fragment.setText(text);
                                }
                            });
                        }
                    } finally {
                        timersLock.release();
                    }
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private static class TimerData {
        public long startSeconds = 0;
        public long startTime = -1;
        public TimerFragment fragment;

        public TimerData(TimerFragment fragment) {
            this.fragment = fragment;
        }
    }

    private ArrayList<TimerData> timers = new ArrayList<>();
    private Semaphore timersLock = new Semaphore(1);

    @Override
    public void addTimer(TimerFragment fragment) {
        try {
            timersLock.acquire();
            try {
                timers.add(new TimerData(fragment));
            } finally {
                timersLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // TODO
        fragment.setText(timeToString(0));
    }

    @Override
    public void playTimer(TimerFragment fragment) {
        try {
            timersLock.acquire();
            try {
                TimerData t = timers.get(fragment.getIndex());
                if (t.startTime == -1) {
                    t.startTime = System.currentTimeMillis();
                }
            } finally {
                timersLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pauseTimer(TimerFragment fragment) {
        try {
            timersLock.acquire();
            try {
                TimerData t = timers.get(fragment.getIndex());
                if (t.startTime != -1) {
                    t.startSeconds -= System.currentTimeMillis() - t.startTime;
                    t.startTime = -1;
                }
            } finally {
                timersLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopTimer(TimerFragment fragment) {
        try {
            timersLock.acquire();
            try {
                TimerData t = timers.get(fragment.getIndex());
                t.startSeconds = 0;
                t.startTime = -1;
            } finally {
                timersLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTimer(TimerFragment fragment) {
        try {
            timersLock.acquire();
            try {
                TimerData t = timers.remove(fragment.getIndex());
                for (int i = fragment.getIndex(); i < timers.size(); ++i) {
                    timers.get(i).fragment.setIndex(i);
                }
                timersFragment.removeFragment(t.fragment);
            } finally {
                timersLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTime(TimerFragment fragment, long time) {
        try {
            timersLock.acquire();
            try {
                TimerData t = timers.get(fragment.getIndex());
                t.startTime = -1;
                t.startSeconds = time;
            } finally {
                timersLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class StopwatchData {
        public long startSeconds = 0;
        public long startTime = -1;
        public StopwatchFragment fragment;

        public StopwatchData(StopwatchFragment fragment) {
            this.fragment = fragment;
        }
    }

    private ArrayList<StopwatchData> stopwatches = new ArrayList<>();
    private Semaphore stopwatchesLock = new Semaphore(1);

    @Override
    public void addStopwatch(StopwatchFragment fragment) {
        try {
            stopwatchesLock.acquire();
            try {
                stopwatches.add(new StopwatchData(fragment));
            } finally {
                stopwatchesLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fragment.setText(timeToString(0));
    }

    @Override
    public void playStopwatch(StopwatchFragment fragment) {
        try {
            stopwatchesLock.acquire();
            try {
                StopwatchData s = stopwatches.get(fragment.getIndex());
                if (s.startTime == -1) {
                    s.startTime = System.currentTimeMillis();
                }
            } finally {
                stopwatchesLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pauseStopwatch(StopwatchFragment fragment) {
        try {
            stopwatchesLock.acquire();
            try {
                StopwatchData s = stopwatches.get(fragment.getIndex());
                if (s.startTime != -1) {
                    s.startSeconds += System.currentTimeMillis() - s.startTime;
                    s.startTime = -1;
                }
            } finally {
                stopwatchesLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopStopwatch(StopwatchFragment fragment) {
        try {
            stopwatchesLock.acquire();
            try {
                StopwatchData s = stopwatches.get(fragment.getIndex());
                s.startSeconds = 0;
                s.startTime = -1;
            } finally {
                stopwatchesLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteStopwatch(StopwatchFragment fragment) {
        try {
            stopwatchesLock.acquire();
            try {
                stopwatches.remove(fragment.getIndex());
                for (int i = fragment.getIndex(); i < stopwatches.size(); ++i) {
                    stopwatches.get(i).fragment.setIndex(i);
                }
                stopwatchesFragment.removeFragment(fragment);
            } finally {
                stopwatchesLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return timersFragment;
                case 1:
                    return stopwatchesFragment;
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.timers_title);
                case 1:
                    return getString(R.string.stop_watches_title);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public void addButton(View view) {
        ViewPager viewPager = findViewById(R.id.view_pager);
        switch (viewPager.getCurrentItem()) {
            case 0:
                timersFragment.addTimer();
                break;
            case 1:
                stopwatchesFragment.addStopwatch();
                break;
        }
    }
}
