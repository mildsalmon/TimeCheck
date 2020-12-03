package com.kimhakjin.timecheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



public class MainActivity extends AppCompatActivity {

    // 스탑워치
    private TextView timeText;
    private Thread timeThread = null;
    private Boolean isRunning = true;
    String startTime, endTime, doTime, now;


    int i = 0;

    int buttonCount = 0; // 0 - 시작, 1 - 정지

    // 리스트
    ArrayList<String> items = new ArrayList<String>();
    ArrayList<String> items_info = new ArrayList<String>();
    ArrayAdapter<String> adapter, adapter2;
    ArrayList<Info> stopInfo = new ArrayList<Info>();
    ListView listView, infoView;

    final int NEW_INFO = 22;
    final int VOICE = 23;
    final int ALARM = 24;

    // notification
    NotificationManager manager;
    NotificationCompat.Builder builder;

    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";

    // voice
    String speechMode = TextToSpeechClient.VOICE_WOMAN_READ_CALM;
    Double speechSpeed = 1.0;
    private TextToSpeechClient ttsClient;
    String [] comment = new String[]{
            "스트레칭을 할 시간이에요 !",
            "우와, 집중력이 대단한데요?.",
            "가볍게 산책하고 다시해요.",
            "30분이 지났어요."};

    //alarm
    int TIME_30 = 1800;
//    int min = 30;
//    int sec=180000;
    int sec=TIME_30;
    int buttonCount_2;
    String S_count;
    int count;

    int min = 30;
    String result;
//    private TextView alarmTimer, rate;
    private Boolean AlarmisRunning = true;
    private Thread timeThread2 = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getAppKeyHash();
        Button startButton = (Button)findViewById(R.id.startButton);
//        Button resetButton = (Button)findViewById(R.id.resetButton);

        timeText = (TextView)findViewById(R.id.timeText);

//        final ArrayList<String> items = new ArrayList<String>();
//        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items);
//        final ListView listView = (ListView) findViewById(R.id.timeList);
//        listView.setAdapter(adapter);

//        // SDK 초기화
//        KakaoSDK.init(new KakaoAdapter() {
//
//            @Override
//            public IApplicationConfig getApplicationConfig() {
//                return new IApplicationConfig() {
//                    @Override
//                    public Context getApplicationContext() {
//                        return MyApplication.this;
//                    }
//                };
//            }
//        });

        setListView();

        voice_init();

        clickGetBt();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "스탑워치 " + startButton.getText(), Toast.LENGTH_SHORT).show();

                if (buttonCount == 0) {
                    now = currentDate();

                    startTime = currentTime();
                    isRunning = true;
                    timeThread = new Thread(new timeThread());
                    timeThread.start();
                    startButton.setText("정지");
                    buttonCount = buttonCount + 1;

//                    TTS(speechMode, speechSpeed, "스탑워치를 시작합니다.");

                    showNoti();
                }else{
                    isRunning = false;
                    startButton.setText("시작");
                    buttonCount = buttonCount - 1;
                    endTime = currentTime();
                    doTime = timeText.getText().toString();

                    Intent intent = new Intent(MainActivity.this, AddInfo.class);
//                    intent.putExtra("restlist", items);
                    intent.putExtra("start_time", startTime);
                    intent.putExtra("end_time", endTime);
                    intent.putExtra("do_time", doTime);
                    intent.putExtra("now", now);
                    startActivityForResult(intent, NEW_INFO);

//                    TTS(speechMode, speechSpeed, "스탑워치를 중지하였습니다.");

//                    items.add((String) timeText.getText());
//                    adapter.notifyDataSetChanged();

                    i = 0;
                    timeText.setText("00:00:00:00");

                }

            }
        });

//        Button add = (Button)findViewById(R.id.button);
//        add.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                int count;
//                count = adapter.getCount();
//
//                // 아이템 추가.
//                items.add("LIST" + Integer.toString(count + 1));
//
//                // listview 갱신
//                adapter.notifyDataSetChanged();
//            }
//        });

//        resetButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(buttonCount == 0){
//                    i = 0;
//                    timeText.setText("00:00:00:00");
//                }
//            }
//        });

    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            int mSec = msg.arg1 % 100;
            int sec2 = (msg.arg1 / 100) % 60;
            int min = ((msg.arg1 / 100) / 60) % 60;
            int hour = ((msg.arg1 / 100) / 60) / 60;
            int hour_v = 0;

            if (hour_v != hour){
                TTS(speechMode, speechSpeed, "시작한지 1시간이 지났습니다~ 조금 쉬다가 하세요.");
                hour_v = hour;
            }

            String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec2, mSec);

            timeText.setText(result);
        }
    };

    public class timeThread implements Runnable{
        public void run(){
            while(isRunning){
                try{
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

    Handler handler2 = new Handler(){
        public void handleMessage(Message msg){
            int sec1 = (msg.arg1) % 60;
            min = ((msg.arg1) / 60) % 30;
            int hour_out = 0;

//            sec = (59 - sec) % 60;
//            min = (29 - min) % 30;

            if(sec == 0){
                sec = TIME_30;
                count = count + 1;
                String use_comm = comment[count%(comment.length)];
                TTS(speechMode, speechSpeed, use_comm);
            }
            result = String.format("%02d:%02d:%02d", hour_out, min, sec1);
            String S_count = Integer.toString(count);
//
//            if(!AlarmisRunning){
////                alarmTimer.setText("00:30:00");
//            }
        }
    };

    public class timeThread2 implements Runnable {
        public void run() {
//            min = intent.getIntExtra("min", 0);
//            sec = intent.getIntExtra("sec", 0);
            while (AlarmisRunning) {

                Message msg = new Message();
                msg.arg1 = sec--;

                System.out.println("main I : " + sec);

                handler2.sendMessage(msg);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setListView(){
        // 리스트
        listView = (ListView)findViewById(R.id.timeList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

//        infoView = (ListView)findViewById(R.id.infoMemo);
//        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items_info);
//        infoView.setAdapter(adapter2);

        // 삭제
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //정보를 삭제하는지 묻는 대화상자 나타남
                AlertDialog.Builder dlg = new AlertDialog.Builder(view.getContext());
                dlg.setTitle("삭제확인")
                        .setIcon(R.drawable.jo2)
                        .setMessage("선택한 기록을 정말 삭제하시겠습니까?")
                        .setNegativeButton("취소",null)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //삭제 클릭시 아래꺼
                                items.remove(position);
                                stopInfo.remove(position);
                                adapter.notifyDataSetChanged();
//                                tv.setText("맛집 리스트("+restdata.size()+"개)");
                                Snackbar.make(view,"삭제되었습니다.",2000).show();
                            }
                        })
                        .show();
                return true;
            }
        });

        //클릭시 상세정보가 나타남
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(MainActivity.this, StopInfo.class);
                Info info = stopInfo.get(position);
                intent.putExtra("infodetail", info);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == NEW_INFO)
        {
            if(resultCode == RESULT_OK)
            {
                Info info = data.getParcelableExtra("newinfo"); //새 기록 받아옴
                items.add(info.getDoTime());
//                Toast.makeText(this, info.getNow(), Toast.LENGTH_SHORT).show();

                stopInfo.add(info);
                adapter.notifyDataSetChanged();
//                Toast.makeText(this, "S", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == VOICE){
            if(resultCode == RESULT_OK){
                speechMode = data.getStringExtra("speechMode");
                speechSpeed = data.getDoubleExtra("speechSpeed", 0);
//                Toast.makeText(this, "A", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "SpeechVoice : " + speechMode + "\n"
                        + "SpeechSpeed : " + speechSpeed + "\n"
                        + "위 내용을 저장합니다.", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == ALARM){
            if(resultCode == RESULT_OK){
//                min = data.getIntExtra("min",0);
//                sec = data.getIntExtra("sec", 0);
                sec = data.getIntExtra("i", TIME_30);
                buttonCount_2 = data.getIntExtra("buttonCount", 0);
                count = data.getIntExtra("count", 0);
                Toast.makeText(this, "2. " + sec + "\n" + count +  "\n" + buttonCount_2, Toast.LENGTH_SHORT).show();
                if(buttonCount_2==1) {
                    timeThread2 = new Thread(new timeThread2());
                    timeThread2.start();
                    AlarmisRunning = true;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String currentTime() {
        long mNow = System.currentTimeMillis();
        Date mReDate = new Date(mNow);
        SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");
        String formatDate = mFormat.format(mReDate);

        return formatDate;
    }

    public String currentDate(){
        long mNow1 = System.currentTimeMillis();
        Date mReDate2 = new Date(mNow1);
        SimpleDateFormat mFormat2 = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일");
        String formatDate2 = mFormat2.format(mReDate2);
//        Toast.makeText(this, formatDate2, Toast.LENGTH_SHORT).show();
        return formatDate2;

    }
//        Calendar _now = Calendar.getInstance();
//        int Hour = _now.get(Calendar.HOUR_OF_DAY);
//        int minute = _now.get(Calendar.MINUTE);
//        int second = _now.get(Calendar.SECOND);
//
//        String S_Hour = Integer.toString(Hour);
//        String S_minute = Integer.toString(minute);
//        String S_second = Integer.toString(second);
//        String now = S_Hour + ":" + S_minute + ":" + S_second;

//        return now;


    // Notification
    public void showNoti(){

//        Intent intent = new Intent(this, NotiService.class);
//
//        startService(intent);

        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        name = "";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        }else{
            builder = new NotificationCompat.Builder(this);
        }
//        Intent intent = new Intent(getBaseContext(), InfoNoti.class);
////        Intent intent = getIntent();
//        intent.setAction(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                101,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("name", name);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 101,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        intent.setAction(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.addCategory(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 101,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle("알림");
        builder.setContentText("지금 스탑워치가 동작중입니다.\n중지하시려면 터치해주세요.");
        builder.setSmallIcon(R.drawable.jo3);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
//        builder.addAction(NotiService.makeButtonInNotification("Pause"));

        Notification notification = builder.build();

        manager.notify(1, notification);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.alarm:
//                Toast.makeText(this, "알람", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Alarm.class);
                intent.putExtra("i", sec);
                intent.putExtra("count", count);
                intent.putExtra("buttonCount", buttonCount_2);
                intent.putExtra("speechMode", speechMode);
                intent.putExtra("speechSpeed", speechSpeed);
//                Toast.makeText(this, "3. " + sec + "\n" + count +  "\n" + buttonCount_2, Toast.LENGTH_SHORT).show();
                System.out.println(timeThread2);
                if(timeThread2 != null) {
                    timeThread2.interrupt();
                    AlarmisRunning = false;
                }
                startActivityForResult(intent, ALARM);
                return true;
            case R.id.voice:
//                Toast.makeText(this, "음성합성", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(MainActivity.this, voice.class);
//                startActivity(intent2);
//                intent2.putExtra("min", min);
////                intent2.putExtra("sec", sec);
//                intent2.putExtra("i", sec);
//                intent2.putExtra("S_count", S_count);
//                intent2.putExtra("buttonCount", buttonCount_2);
                startActivityForResult(intent2, VOICE);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    public void voice_init(){
//음성인식 초기화
        SpeechRecognizerManager.getInstance().initializeLibrary(getApplicationContext());
//        TextToSpeechManager.getInstance().initializeLibrary(this);
        TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());

    }

    public void TTS(String speechMode, Double speechSpeed, String voice){
        ttsClient = new TextToSpeechClient.Builder()
                .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)     // 음성합성방식
                .setSpeechSpeed(speechSpeed)            // 발음 속도(0.5~4.0)
                .setSpeechVoice(speechMode)  //TTS 음색 모드 설정(여성 차분한 낭독체)
                .setListener(ttsListener)
                .build();

//                String voice = "안녕";

//                ttsClient.play(voice);
        ttsClient.play(voice);

        System.out.println("main TTS Do ");


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

    public void clickGetBt(){
        SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
        speechMode = sharedPreferences.getString("SpeechVoice", "");
        String S_speechSpeed = sharedPreferences.getString("SpeechSpeed", "");
        speechSpeed = Double.parseDouble(S_speechSpeed);
//        Toast.makeText(this, "설정 불러오기\nSpeechVoice : " + speechMode + "\n"
//                + "SpeechSpeed : " + speechSpeed + "\n"
//                + "위 내용을 불러왔습니다.", Toast.LENGTH_SHORT).show();


    }
}