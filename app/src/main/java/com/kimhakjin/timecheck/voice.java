package com.kimhakjin.timecheck;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

public class voice  extends AppCompatActivity {

    private TextToSpeechClient ttsClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice);

//음성인식 초기화
        SpeechRecognizerManager.getInstance().initializeLibrary(this);
//        TextToSpeechManager.getInstance().initializeLibrary(this);
        TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());

        Button button = findViewById(R.id.voiceBtn);
        final EditText editText = findViewById(R.id.voiceET);

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
                        .setSpeechSpeed(1.0)            // 발음 속도(0.5~4.0)
                        .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_READ_CALM)  //TTS 음색 모드 설정(여성 차분한 낭독체)
                        .setListener(ttsListener)
                        .build();

//                String voice = "안녕";

//                ttsClient.play(voice);
                ttsClient.play(editText.getText().toString());
            }
        });
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
