package com.rafaelguimas.pid;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";
    public static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        // Abre o fragment principal
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new MainFragment())
                .addToBackStack(null)
                .commit();
    }
}
