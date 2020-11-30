package com.kimhakjin.timecheck;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StopInfo extends AppCompatActivity {
    TextView memo, stop_start, stop_end, stop_do;
    Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop_info);
//        setTitle("정보 상세보기");
        memo = (TextView)findViewById(R.id.stop_memo);
        stop_start = (TextView)findViewById(R.id.stop_start);
        stop_end = (TextView)findViewById(R.id.stop_end);
        stop_do = (TextView)findViewById(R.id.stop_do);
        back = (Button)findViewById(R.id.btnback) ;

        Intent intent = getIntent();
        Info res = intent.getParcelableExtra("infodetail");

        memo.setText(res.getMemo());
        stop_start.setText(res.getStartTime());
        stop_end.setText(res.getEndTime());
        stop_do.setText(res.getDoTime());
    }

    public void onClick(View v)
    {
        Intent intent = getIntent();
        Info res = intent.getParcelableExtra("restinfo");
        switch (v.getId())
        {
            case R.id.btnback:
                finish();
                break;
        }
    }
}
