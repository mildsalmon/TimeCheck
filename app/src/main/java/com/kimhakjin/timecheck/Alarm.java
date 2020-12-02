package com.kimhakjin.timecheck;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Alarm extends AppCompatActivity {
    AlarmManager alarmManager;
    //    TimePicker alarm_timepicker;
    Context context;
    PendingIntent pendingIntent;

    private TextView nowTime, alarmTimer, rate;
    private Thread timeThread = null;
    private Thread nowTh = null;
    private Boolean NowisRunning = true;
    private Boolean AlarmisRunning = true;
    String startTime, endTime, doTime;


    int i = 0;
    int j = 0;

    int buttonCount = 0; // 0 - 시작, 1 - 정지

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        this.context = this;

        nowTime = (TextView)findViewById(R.id.nowTime);
        alarmTimer = (TextView)findViewById(R.id.alarmTimer);
        rate = (TextView)findViewById(R.id.rate);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        nowTh = new Thread(new Alarm.nowThread());
        nowTh.start();

        Button alarmStart = findViewById(R.id.alarmButton);
        alarmStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonCount == 0) {
                    startTime = currentTime();
                    AlarmisRunning = true;
                    timeThread = new Thread(new Alarm.timeThread());
                    timeThread.start();
                    alarmStart.setText("정지");
                    buttonCount = buttonCount + 1;

                }else{
                    AlarmisRunning = false;
                    alarmStart.setText("시작");
                    buttonCount = buttonCount - 1;
                    endTime = currentTime();
//                    doTime = timeText.getText().toString();

                    i = 0;
                    alarmTimer.setText("00:30:00");

                }
            }
        });

    }

//    public class AlaramReciver extends BroadcastReceiver{
//        Context context;
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            this.context = context;
//
//            String get_your
//        }
//    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg){
//            int mSec = msg.arg1 % 100;
            int sec = (msg.arg1 / 100) % 60;
            int min = ((msg.arg1 / 100) / 60) % 60;
            int hour = ((msg.arg1 / 100) / 60) / 60;
            int hour_out = 0;

            sec = (59 - sec) % 60;
            min = (29 - min) % 30;

            String result = String.format("%02d:%02d:%02d", hour_out, min, sec);
            String hour_rate = Integer.toString(hour);
            rate.setText(hour_rate);
            alarmTimer.setText(result);
        }
    };

    public class nowThread implements Runnable{
        public void run(){
            while(NowisRunning){
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
//                j++;
                nowTime.setText(currentTime());
            }
        }
    }

    public class timeThread implements Runnable {
        public void run() {
            while (AlarmisRunning) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.arg1 = i++;
                handler.sendMessage(msg);
            }
        }
    }

    public String currentTime() {
        long mNow = System.currentTimeMillis();
        Date mReDate = new Date(mNow);
        SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");
        String formatDate = mFormat.format(mReDate);

        return formatDate;
    }
}
