package com.kimhakjin.timecheck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddInfo extends AppCompatActivity {
    EditText memo, add_start, add_end, add_do;
    Info info;
    String startTime, endTime, doTime, now;

    final int NEW_INFO = 22;

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        if(requestCode == NEW_INFO)
//        {
//            if(resultCode == RESULT_OK)
//            {
//                startTime = data.getStringExtra("start_time");
//                endTime = data.getStringExtra("end_time");
//                doTime = data.getStringExtra("do_time");
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_info);

        memo = (EditText)findViewById(R.id.add_memo);
        add_start = (EditText)findViewById(R.id.add_start);
        add_end = (EditText)findViewById(R.id.add_end);
        add_do = (EditText)findViewById(R.id.add_do);
//        setTitle("새 기록");

        Intent intent = getIntent();

        startTime = intent.getStringExtra("start_time");
        endTime = intent.getStringExtra("end_time");
        doTime = intent.getStringExtra("do_time");
        now = intent.getStringExtra("now");
        Toast.makeText(this, now, Toast.LENGTH_SHORT).show();

        add_start.setText(startTime);
        add_end.setText(endTime);
        add_do.setText(doTime);

        memo.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(memo.getWindowToken(), 0);
                }
                return false;
            }
        });
        //ArrayList<String> dt = Intent.getStringExtra("restlist");

    }


    public void onClick(View v)
    {
        if (v.getId() == R.id.btnCancel)
        {
            finish();
        }
        else
        {
            addInfo();
            info.setMemo(memo.getText().toString());
            info.setStartTime(add_start.getText().toString());
            info.setEndTime(add_end.getText().toString());
            info.setDoTime(add_do.getText().toString());
            info.setNow(now);
//            info.setMemo(finddate());
            Intent intent = getIntent();
            intent.putExtra("newinfo", info);  //Parcelable한 info를 첨부
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    public void addInfo(){
        info = new Info(memo.getText().toString(),
                add_start.getText().toString(),
                add_end.getText().toString(),
                add_do.getText().toString(),
                now);
    }


//    public void setcategorynum(int n)
//    {
//        res = new Info(etname.getText().toString(),
//                ettel.getText().toString(),
//                etaddr.getText().toString(),
//                n);
//    }

    public String finddate()
    {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String fmdate = sdf.format(date);
        return fmdate;
    }

}
