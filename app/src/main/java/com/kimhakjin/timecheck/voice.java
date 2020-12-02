package com.kimhakjin.timecheck;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

public class voice  extends AppCompatActivity {

    private TextToSpeechClient ttsClient;
    private Button button;
    private EditText editText;
//    private RadioButton rSpeechMode1, rSpeechMode2, rSpeechMode3, rSpeechMode4;
    private SeekBar sb;
    private TextView tv;

    String speechMode = TextToSpeechClient.VOICE_WOMAN_READ_CALM;
    Double speechSpeed = 1.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice);

//음성인식 초기화
        SpeechRecognizerManager.getInstance().initializeLibrary(this);
//        TextToSpeechManager.getInstance().initializeLibrary(this);
        TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());

        button = findViewById(R.id.voiceBtn);
        editText = findViewById(R.id.voiceET);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TextToSpeechClient.Builder builder = new TextToSpeechClient.Builder();
//                builder.setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1);
//                builder.setSpeechSpeed(2.5);
//                builder.setSpeechVoice(TextToSpeechClient.VOICE_MAN_READ_CALM);
//                builder.setListener(ttsListener);
//
//                ttsClient = builder.build();

                ttsClient = new TextToSpeechClient.Builder()
                        .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)     // 음성합성방식
                        .setSpeechSpeed(speechSpeed)            // 발음 속도(0.5~4.0)
                        .setSpeechVoice(speechMode)  //TTS 음색 모드 설정(여성 차분한 낭독체)
                        .setListener(ttsListener)
                        .build();

//                String voice = "안녕";

//                ttsClient.play(voice);
                ttsClient.play(editText.getText().toString());
            }
        });

//        rSpeechMode1 = (RadioButton) findViewById(R.id.speechMode1);
//        rSpeechMode2 = (RadioButton) findViewById(R.id.speechMode2);
//        rSpeechMode3 = (RadioButton) findViewById(R.id.speechMode3);
//        rSpeechMode4 = (RadioButton) findViewById(R.id.speechMode4);

        tv = (TextView) findViewById(R.id.sppechSpeedValue);
        sb = (SeekBar) findViewById(R.id.speechSpeed);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double value = progress/2.0;
                String Svalue = Double.toString(value);
                tv.setText(Svalue);

                speechSpeed = value;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void changeSpeechMode(View view){
        boolean checked = ((RadioButton)view).isChecked();

        switch (view.getId()){
            case R.id.speechMode1:
                if(checked){
                    speechMode = TextToSpeechClient.VOICE_WOMAN_READ_CALM;
                    Toast.makeText(voice.this, "라디오 그룹 버튼1 눌렸습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.speechMode2:
                if(checked){
                    speechMode = TextToSpeechClient.VOICE_MAN_READ_CALM;
                    Toast.makeText(voice.this, "라디오 그룹 버튼2 눌렸습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.speechMode3:
                if(checked){
                    speechMode = TextToSpeechClient.VOICE_WOMAN_DIALOG_BRIGHT;
                    Toast.makeText(voice.this, "라디오 그룹 버튼3 눌렸습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.speechMode4:
                if(checked){
                    speechMode = TextToSpeechClient.VOICE_MAN_DIALOG_BRIGHT;
                    Toast.makeText(voice.this, "라디오 그룹 버튼4 눌렸습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

        }
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
