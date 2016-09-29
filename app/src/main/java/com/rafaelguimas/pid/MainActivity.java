package com.rafaelguimas.pid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

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

        TextView txt1 = (TextView) findViewById(R.id.txt1);
        TextView txt2 = (TextView) findViewById(R.id.txt2);
        TextView txt3 = (TextView) findViewById(R.id.txt3);

        // make a mat and draw something
//        Mat m = new Mat(100, 400, CvType.CV_8UC3);

        Mat mat1 = null;
        Mat mat2 = null;
        try {
            mat1 = Utils.loadResource(this, R.drawable.lena_gray);
            mat2 = Utils.loadResource(this, R.drawable.lena_gray_dot);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Imgproc.cvtColor(mat1, mat1, Imgproc.COLOR_BGR2GRAY);
        Mat mat3 = new Mat();
        Core.bitwise_xor(mat1, mat2, mat1);

        // Converte mat para bitmap
        Bitmap bm = Bitmap.createBitmap(mat1.cols(), mat1.rows(), Bitmap.Config.ARGB_8888);
        Bitmap bm2 = Bitmap.createBitmap(mat2.cols(), mat2.rows(), Bitmap.Config.ARGB_8888);
//        Bitmap bm3 = Bitmap.createBitmap(mat3.cols(), mat3.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat1, bm);
        Utils.matToBitmap(mat2, bm2);

        // Coloca no imageview
        ImageView iv = (ImageView) findViewById(R.id.img1);
        ImageView iv2 = (ImageView) findViewById(R.id.img2);
        ImageView iv3 = (ImageView) findViewById(R.id.img2);
        iv.setImageBitmap(bm);
        iv2.setImageBitmap(bm2);
//        iv3.setImageBitmap(bm3);

    }
}
