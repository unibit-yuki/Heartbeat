package jp.aoyama.a5817010.heartbeat;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private TextView heartTextView;
    private TextView statusView;
    private SensorManager mSensorManager;
    public float hb=0.0f;
    public long startTime = 0;
    public long tmpTime = 0;
    private ArrayList<String> tmp_data = new ArrayList<String>();
    private boolean flag = false;

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        heartTextView = (TextView) findViewById(R.id.text_heart);
        statusView = (TextView) findViewById(R.id.status_view);

        //計測開始時刻の保存
        startTime = System.currentTimeMillis();

        //ボタン設定
        ToggleButton status_btn = (ToggleButton) findViewById(R.id.status_btn);

        status_btn.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if(isChecked){
                            flag = true;
                            tmp_data.clear();
                            statusView.setText("Recording...");
                        }else{
                            flag = false;
                            exportCsv(tmp_data);
                        }
                    }
                }
        );


    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {

            //極小期間(100ms以下)で心拍データが2つ取得されるのを防ぐ
            if(System.currentTimeMillis() - tmpTime > 100) {
                hb = event.values[0];
                tmpTime = System.currentTimeMillis();
                heartTextView.setText(String.valueOf(hb));

                if(flag){
                    tmp_data.add((int)hb + "," + tmpTime);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //[心拍数,計測経過時間]をcsvファイルに書き込む関数
    public void exportCsv(ArrayList data){
        try{
            statusView.setText("Saving...");

            LocalDateTime end_time = LocalDateTime.now();
            DateTimeFormatter dtformat = DateTimeFormatter.ofPattern("MMddHHmmss");
            String fname = dtformat.format(end_time);

            FileWriter f = new FileWriter("/data/data/jp.aoyama.a5817010.heartbeat/" + fname + ".csv", false);
            PrintWriter p = new PrintWriter(new BufferedWriter(f));

            //ヘッダーを指定する
            p.print("Heartbeat");
            p.print(",");
            p.print("Time");
            p.println();

            //内容をセットする
            for(int i = 0; i < data.size(); i++){
                p.print(data.get(i));
                p.println();
            }

            p.close();

            tmp_data.clear();
            statusView.setText("Save Complete!");

        } catch (IOException e){
            statusView.setText("Faild to save");
            e.printStackTrace();
        }
    }

    /*
    //現在の時刻を取得する関数
    public String getToday() {
        LocalTime current_time = LocalTime.now();
        return current_time.toString();
    }
    */
}
