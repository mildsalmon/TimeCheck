package com.kimhakjin.timecheck;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.snackbar.Snackbar;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

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
//    private Thread nowTh = null;
    private Boolean NowisRunning = true;
    private Boolean AlarmisRunning = true;
    String startTime, endTime, doTime;

    int TIME_30 = 1800;

    int i = TIME_30;
    int j = 0;
    int count = 0;

    int buttonCount = 0; // 0 - 시작, 1 - 정지

    String result;
//    String S_count;

    int min = 30;
    int sec = 00;
    int buttonCount_2;
//    String S_count = "0";

    Intent intent;

    // voice
    String speechMode;
    Double speechSpeed;
    private TextToSpeechClient ttsClient;
    String [] comment = new String[]{
            "스트레칭을 할 시간이에요 !",
            "우와, 집중력이 대단한데요?.",
            "가볍게 산책하고 다시해요.",
            "30분이 지났어요."};

    // vibrator
    private Vibrator vibrator;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

//        clickGetBt();

        this.context = this;
        voice_init();
//        nowTime = (TextView)findViewById(R.id.nowTime);
        alarmTimer = (TextView)findViewById(R.id.alarmTimer);
        rate = (TextView)findViewById(R.id.rate);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

//        nowTh = new Thread(new Alarm.nowThread());
//        nowTh.start();

        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        intent = getIntent();

        speechMode = intent.getStringExtra("speechMode");
        speechSpeed = intent.getDoubleExtra("speechSpeed",0);

        buttonCount = intent.getIntExtra("buttonCount", 0);
        i = intent.getIntExtra("i", TIME_30);
//        i = 30000;
        count = intent.getIntExtra("count", 0);
        Toast.makeText(this, "4. " + i + "\n" + count +  "\n" + buttonCount, Toast.LENGTH_SHORT).show();
        // buttonCount if로 조건 들어가게, i는 왜 1800000이 안들어가는지
        Button alarmStart = findViewById(R.id.alarmButton);
        alarmStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmClicked();
            }
        });

        if (buttonCount == 1) {
            buttonCount = 0;
            alarmClicked();
        }

    }

    public void alarmClicked(){
        Button alarmStart = findViewById(R.id.alarmButton);

        if (buttonCount == 0) {
//                    startTime = currentTime();
            AlarmisRunning = true;
            timeThread = new Thread(new Alarm.timeThread());
            timeThread.start();
            alarmStart.setText("정지");

            buttonCount = buttonCount + 1;

        }else{
            AlarmisRunning = false;
            alarmStart.setText("시작");
            buttonCount = buttonCount - 1;
//                    endTime = currentTime();
//                    doTime = timeText.getText().toString();
            timeThread.interrupt();
//            i = 180000;
            i = TIME_30;
            count = 0;
            alarmTimer.setText("00:30:00");

        }
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
            sec = (msg.arg1) % 60;
            min = ((msg.arg1) / 60) % 30;
            int hour_out = 0;

//            sec = (59 - sec) % 60;
//            min = (29 - min) % 30;

            if(i == 0){
//                i = 180000;
                i = TIME_30;
                count = count + 1;
                String use_comm = comment[count%(comment.length)];
                TTS(speechMode, speechSpeed, use_comm);
            }

            result = String.format("%02d:%02d:%02d", hour_out, min, sec);
            String S_count = Integer.toString(count);
            rate.setText(S_count);
            alarmTimer.setText(result);

//            if(!AlarmisRunning){
//                System.out.println("AlaRun False in Handler");
//                alarmTimer.setText("00:30:00");
//            }
        }
    };

//    public class nowThread implements Runnable{
//        public void run(){
//
//            while(NowisRunning){
//                try{
//                    Thread.sleep(1000);
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
////                j++;
//                nowTime.setText(currentTime());
//            }
//        }
//    }

    public class timeThread implements Runnable {
        public void run() {
//            min = intent.getIntExtra("min", 0);
//            sec = intent.getIntExtra("sec", 0);
            while (AlarmisRunning) {

                Message msg = new Message();
                msg.arg1 = --i;
                handler.sendMessage(msg);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("alarm I : " + i);

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

//    public void clickSetBt(){
//        SharedPreferences sharedPreferences = getSharedPreferences("alarm", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt("min", min);
//        editor.putInt("sec", sec);
//        editor.putInt("buttonCount", buttonCount);
//        editor.putString("S_count", S_count);
//        editor.commit();
////        Toast.makeText(this, "설정 저장\nSpeechVoice : " + speechMode + "\n"
////                + "SpeechSpeed : " + speechSpeed + "\n"
////                + "위 내용을 저장합니다.", Toast.LENGTH_SHORT).show();
//    }
//
//    public void clickGetBt(){
//        SharedPreferences sharedPreferences = getSharedPreferences("alarm", MODE_PRIVATE);
//        min = sharedPreferences.getInt("min", 0);
//        sec = sharedPreferences.getInt("sec", 0);
//        buttonCount = sharedPreferences.getInt("buttonCount", 0);
//        S_count = sharedPreferences.getString("S_count", S_count);
////        Toast.makeText(this, "설정 불러오기\nSpeechVoice : " + speechMode + "\n"
////                + "SpeechSpeed : " + speechSpeed + "\n"
////                + "위 내용을 불러왔습니다.", Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onBackPressed() {
//        clickSetBt();

        Intent intent = getIntent();
//        intent.putExtra("min", min);
//        intent.putExtra("sec", sec);
        intent.putExtra("i", i);
        intent.putExtra("count", count);
        intent.putExtra("buttonCount", buttonCount);
        setResult(RESULT_OK,intent);
        AlarmisRunning = false;
//        Toast.makeText(this, "1. " + i + "\n" + count +  "\n" + buttonCount, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void voice_init(){
//음성인식 초기화
        SpeechRecognizerManager.getInstance().initializeLibrary(getApplicationContext());
//        TextToSpeechManager.getInstance().initializeLibrary(this);
        TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());

    }

    public void TTS(String speechMode, Double speechSpeed, String voice){
//        try {
            ttsClient = new TextToSpeechClient.Builder()
                    .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)     // 음성합성방식
                    .setSpeechSpeed(speechSpeed)            // 발음 속도(0.5~4.0)
                    .setSpeechVoice(speechMode)  //TTS 음색 모드 설정(여성 차분한 낭독체)
                    .setListener(ttsListener)
                    .build();

            //                String voice = "안녕";

            //                ttsClient.play(voice);
            ttsClient.play(voice);
            vibrator.vibrate(2000); // 1초간 진동

            System.out.println("Alarm TTS Do ");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    private TextToSpeechListener ttsListener = new TextToSpeechListener() {
        @Override
        public void onFinished() {

        }

        @Override
        public void onError(int code, String message) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        TextToSpeechManager.getInstance().finalizeLibrary();
    }
}
