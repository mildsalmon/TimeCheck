package com.kimhakjin.timecheck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import java.util.HashMap;

public class voice  extends AppCompatActivity {

    private TextToSpeechClient ttsClient;
    private Button button;
    private EditText editText;
//    private RadioButton rSpeechMode1, rSpeechMode2, rSpeechMode3, rSpeechMode4;
    private SeekBar sb;
    private TextView tv;

    String speechMode = TextToSpeechClient.VOICE_WOMAN_READ_CALM;
    Double speechSpeed = 1.0;

    HashMap<String, String> h = new HashMap<String, String>();

    public static Context voContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice);

        voContext = this;

        h.put("TextToSpeechClient.VOICE_WOMAN_READ_CALM", "여성 차분한 낭독체");
        h.put("TextToSpeechClient.VOICE_MAN_READ_CALM", "남성 차분한 낭독체");
        h.put("TextToSpeechClient.VOICE_WOMAN_DIALOG_BRIGHT", "여성 밝은 대화체");
        h.put("TextToSpeechClient.VOICE_MAN_DIALOG_BRIGHT", "남성 밝은 대화체");

        voice_init();

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
                TTS(speechMode, speechSpeed, editText.getText().toString());

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

        clickGetBt();

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


    public void changeSpeechMode(View view){
        boolean checked = ((RadioButton)view).isChecked();

        switch (view.getId()){
            case R.id.speechMode1:
                if(checked){
                    speechMode = TextToSpeechClient.VOICE_WOMAN_READ_CALM;
//                    Toast.makeText(voice.this, "라디오 그룹 버튼1 눌렸습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.speechMode2:
                if(checked){
                    speechMode = TextToSpeechClient.VOICE_MAN_READ_CALM;
//                    Toast.makeText(voice.this, "라디오 그룹 버튼2 눌렸습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.speechMode3:
                if(checked){
                    speechMode = TextToSpeechClient.VOICE_WOMAN_DIALOG_BRIGHT;
//                    Toast.makeText(voice.this, "라디오 그룹 버튼3 눌렸습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.speechMode4:
                if(checked){
                    speechMode = TextToSpeechClient.VOICE_MAN_DIALOG_BRIGHT;
//                    Toast.makeText(voice.this, "라디오 그룹 버튼4 눌렸습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.voice_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.saveSetting:
//                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(this, voice.class);
//                startActivity(intent);
                clickSetBt();
                return true;
            case R.id.loadSetting:
//                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                clickGetBt();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void clickSetBt(){
//        String h_kor = h.get(speechMode);
//        Toast.makeText(this, h.get(speechMode), Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SpeechVoice", speechMode);
        String S_speechSpeed = Double.toString(speechSpeed);
        editor.putString("SpeechSpeed", S_speechSpeed);
        editor.commit();
        Toast.makeText(this, "설정 저장\nSpeechVoice : " + speechMode + "\n"
                + "SpeechSpeed : " + speechSpeed + "\n"
                + "위 내용을 저장합니다.", Toast.LENGTH_SHORT).show();
    }

    public void clickGetBt(){
        SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
        speechMode = sharedPreferences.getString("SpeechVoice", "");
        String S_speechSpeed = sharedPreferences.getString("SpeechSpeed", "");
        speechSpeed = Double.parseDouble(S_speechSpeed);
        Toast.makeText(this, "설정 불러오기\nSpeechVoice : " + speechMode + "\n"
                + "SpeechSpeed : " + speechSpeed + "\n"
                + "위 내용을 불러왔습니다.", Toast.LENGTH_SHORT).show();
        double sekb = speechSpeed * 2;
        int isekb = (int)sekb;
        sb.setProgress(isekb);

        switch (speechMode){
            case TextToSpeechClient.VOICE_WOMAN_READ_CALM:
                RadioButton rSpeechMode1 = (RadioButton) findViewById(R.id.speechMode1);
                rSpeechMode1.setChecked(true);
                break;
            case TextToSpeechClient.VOICE_MAN_READ_CALM:
                RadioButton rSpeechMode2 = (RadioButton) findViewById(R.id.speechMode2);
                rSpeechMode2.setChecked(true);
                break;
            case TextToSpeechClient.VOICE_WOMAN_DIALOG_BRIGHT:
                RadioButton rSpeechMode3 = (RadioButton) findViewById(R.id.speechMode3);
                rSpeechMode3.setChecked(true);
                break;
            case TextToSpeechClient.VOICE_MAN_DIALOG_BRIGHT:
                RadioButton rSpeechMode4 = (RadioButton) findViewById(R.id.speechMode4);
                rSpeechMode4.setChecked(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        clickSetBt();

        Intent intent = getIntent();
        intent.putExtra("speechMode", speechMode);
        intent.putExtra("speechSpeed", speechSpeed);
        setResult(RESULT_OK,intent);
//        Toast.makeText(this, "W", Toast.LENGTH_SHORT).show();
        finish();
    }
}
