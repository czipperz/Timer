package com.github.czipperz.timer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PromptTimeActivity extends AppCompatActivity {
    public static final String ARG_TIME = "time";
    char[] nums;
    int numsIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nums = new char[6];
        for (int i = 0; i < nums.length; ++i) {
            nums[i] = '0';
        }
        numsIndex = 0;
        setContentView(R.layout.activity_prompt_time);
    }

    private void number(char n) {
        nums[numsIndex % nums.length] = n;
        ++numsIndex;
        TextView time = findViewById(R.id.prompt_time_time);
        time.setText(text());
    }

    private String text() {
        char[] buf = new char[6 + 2];
        int e = buf.length - 1;
        for (int i = nums.length + numsIndex - 1; i >= numsIndex; --i) {
            if (e == 2 || e == 5) {
                buf[e] = ':';
                --e;
            }
            buf[e] = nums[i % nums.length];
            --e;
        }
        return new String(buf);
    }

    public void cancel(View view) {
        finish();
    }

    public void apply(View view) {
        String text = text();
        String[] arr = text.split(":");
        long hours = Long.parseLong(arr[0]);
        long minutes = Long.parseLong(arr[1]);
        long seconds = Long.parseLong(arr[2]);
        long time = 0;
        time += hours;
        time *= 60;
        time += minutes;
        time *= 60;
        time += seconds;
        time *= 1000;
        Intent intent = new Intent();
        intent.putExtra(ARG_TIME, time);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void number1(View view) {
        number('1');
    }

    public void number2(View view) {
        number('2');
    }

    public void number3(View view) {
        number('3');
    }

    public void number4(View view) {
        number('4');
    }

    public void number5(View view) {
        number('5');
    }

    public void number6(View view) {
        number('6');
    }

    public void number7(View view) {
        number('7');
    }

    public void number8(View view) {
        number('8');
    }

    public void number9(View view) {
        number('9');
    }
}
