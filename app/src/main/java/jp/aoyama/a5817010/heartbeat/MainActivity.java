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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
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
    private ArrayList<String> tmp_data = new ArrayList<String>();
    //private ArrayList<ArrayList<Float>> tmp_data = new ArrayList<ArrayList<Float>>();
    private boolean flag = false;
    //private String current_date;

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

        //ボタン設定
        ToggleButton status_btn = (ToggleButton) findViewById(R.id.status_btn);

        status_btn.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if(isChecked){
                            flag = true;
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
            hb = event.values[0];
            heartTextView.setText(String.valueOf(hb));

            if(flag){
                tmp_data.add(getToday() + "," + (int)hb);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void exportCsv(ArrayList data){
        try{
            statusView.setText("Saving...");
            FileWriter f = new FileWriter("/data/data/jp.aoyama.a5817010.heartbeat/heartbeat_log.csv", false);
            PrintWriter p = new PrintWriter(new BufferedWriter(f));

            //ヘッダーを指定する
            p.print("Time");
            p.print(",");
            p.print("heartbeat");
            p.println();

            //内容をセットする
            for(int i = 0; i < data.size(); i++){
                p.print(data.get(i));
                p.println();
            }

            p.close();

            statusView.setText("Save Complete!");

        } catch (IOException e){
            statusView.setText("Faild to save");
            e.printStackTrace();
        }
    }

    public String getToday() {
        LocalTime current_time = LocalTime.now();
        return current_time.toString();
    }
}
