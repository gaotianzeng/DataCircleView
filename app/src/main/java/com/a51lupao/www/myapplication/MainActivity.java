package com.a51lupao.www.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    DataRing dataRing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataRing = (DataRing) findViewById(R.id.dataRing);
        dataRing.setCurrentCount(4000,360, 900, 360, 360, 360, 180);
    }
}
