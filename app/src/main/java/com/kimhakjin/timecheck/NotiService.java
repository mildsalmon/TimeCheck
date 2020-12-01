package com.kimhakjin.timecheck;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.kimhakjin.timecheck.R;

public class NotiService extends Service {
    public static final int FLAG = 17465;

    public NotiService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setNotification();

        // Notification 버튼 Action 처리
        switch (intent.getAction()) {
            case "Start":
                Toast.makeText(this, "Start Clicked", Toast.LENGTH_SHORT).show();
                break;

            case "Pause":
                Toast.makeText(this, "Pause Clicked", Toast.LENGTH_SHORT).show();
                break;

            case "Stop":
                Toast.makeText(this, "Stop Clicked", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // ...
    private final int TEST_FLAG = 111;

    private void setNotification() {
        // 알람을 관리하는 알람 매니저 설정
        NotificationManager notificationManager
                = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        // 알람 알리기
        notificationManager.notify(TEST_FLAG, makeNotification());
    }

    private Notification makeNotification() {
        // 알람바 변수
        Notification notification=null;

        // 알람바를 생성하기 위한 빌더 변수, 빌더 패턴
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.jo);

        // 빌더에 설정
        notifBuilder.setContentTitle("Content Title")
                .setContentInfo("Content Info")
                .setContentText("Content Text")
                .setProgress(100, 20, false)
                .setCategory("Category")
                .setSmallIcon(R.drawable.jo)
                .setLargeIcon(largeIcon);

        // 버튼 등록
        notifBuilder.addAction(makeButtonInNotification("Start"));
        notifBuilder.addAction(makeButtonInNotification("Pause"));
        notifBuilder.addAction(makeButtonInNotification("Stop"));

        // 빌더를 통해 알람바 생성
        notification = notifBuilder.build();

        return notification;
    }

    private NotificationCompat.Action makeButtonInNotification(String action) {
        // PendingIntent로 등록될 Intent 생성
        Intent intent = new Intent(getBaseContext(), NotiService.class);
        // Intent로 전달될 액션 설정
        intent.setAction(action);

        // PendingIntent 생성
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 1, intent, 0);

        // 임의의 버튼 아이콘 등록
        int iconId = android.R.drawable.ic_media_pause;

        // 버튼 타이틀 등록
        String btnTitle = action;

        // 해당 버튼 액션 설정
        NotificationCompat.Action notifAction
                = new NotificationCompat.Action.Builder(iconId, btnTitle, pendingIntent).build();

        return notifAction;
    }
}
