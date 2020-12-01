package com.kimhakjin.timecheck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // 스탑워치
    private TextView timeText;
    private Thread timeThread = null;
    private Boolean isRunning = true;
    String startTime, endTime, doTime;


    int i = 0;

    int buttonCount = 0; // 0 - 시작, 1 - 정지

    // 리스트
    ArrayList<String> items = new ArrayList<String>();
    ArrayList<String> items_info = new ArrayList<String>();
    ArrayAdapter<String> adapter, adapter2;
    ArrayList<Info> stopInfo = new ArrayList<Info>();
    ListView listView, infoView;

    final int NEW_INFO = 22;

    // notification
    NotificationManager manager;
    NotificationCompat.Builder builder;

    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = (Button)findViewById(R.id.startButton);
//        Button resetButton = (Button)findViewById(R.id.resetButton);

        timeText = (TextView)findViewById(R.id.timeText);

//        final ArrayList<String> items = new ArrayList<String>();
//        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items);
//        final ListView listView = (ListView) findViewById(R.id.timeList);
//        listView.setAdapter(adapter);

        setListView();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonCount == 0) {
                    startTime = currentTime();
                    isRunning = true;
                    timeThread = new Thread(new timeThread());
                    timeThread.start();
                    startButton.setText("정지");
                    buttonCount = buttonCount + 1;

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
                    startActivityForResult(intent, NEW_INFO);

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
            int sec = (msg.arg1 / 100) % 60;
            int min = ((msg.arg1 / 100) / 60) % 60;
            int hour = ((msg.arg1 / 100) / 60) / 60;

            String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);

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
                stopInfo.add(info);
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String currentTime(){
        long mNow = System.currentTimeMillis();
        Date mReDate = new Date(mNow);
        SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");
        String formatDate = mFormat.format(mReDate);

        return formatDate;

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
    }

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
        builder.setContentText("메");
        builder.setSmallIcon(R.drawable.jo3);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
//        builder.addAction(NotiService.makeButtonInNotification("Pause"));

        Notification notification = builder.build();

        manager.notify(1, notification);
    }




}