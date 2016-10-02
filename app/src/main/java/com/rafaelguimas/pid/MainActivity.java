package com.rafaelguimas.pid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV success");
        } else {
            Log.d(TAG, "OpenCV failed");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Abre o fragment principal
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new MainFragment())
                .addToBackStack(null)
                .commit();
    }
}
