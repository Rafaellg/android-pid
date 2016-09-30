package com.rafaelguimas.pid;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMG = 1 ;
    public static String TAG = "MainActivity";
    Button btnSelect1, btnSelect2 , btnAnd;

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV success");
        } else {
            Log.d(TAG, "OpenCV failed");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.img3);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "Selecione uma imagem",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSelect1 =  (Button) findViewById(R.id.btnSelect1);
        Button btnAnd =  (Button) findViewById(R.id.btnSelect1);
        Button btnSelect2 =  (Button) findViewById(R.id.btnSelect1);

        btnSelect1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });




        TextView txt1 = (TextView) findViewById(R.id.txt1);
     //   TextView txt2 = (TextView) findViewById(R.id.txt2);
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
