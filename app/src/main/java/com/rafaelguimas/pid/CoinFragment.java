package com.rafaelguimas.pid;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static android.app.Activity.RESULT_OK;
import static org.opencv.imgproc.Imgproc.CV_HOUGH_GRADIENT;
import static org.opencv.imgproc.Imgproc.CV_HOUGH_STANDARD;

public class CoinFragment extends Fragment {

    public static int RESULT_LOAD_IMAGE = 1;

    private ImageView imgOriginal, imgResult;
    private TextView txtTotalValue;

    public CoinFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coin, container, false);

        // Instancia os elementos da tela
        imgOriginal = (ImageView) view.findViewById(R.id.imgOriginal);
        imgResult = (ImageView) view.findViewById(R.id.imgResult);
        txtTotalValue = (TextView) view.findViewById(R.id.txtTotalValue);

        imgOriginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        test();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {
            // Recupera o caminho da imagem selecionada
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // Cria e exibe o bitmap com a imagem selecionada
            if (requestCode == RESULT_LOAD_IMAGE) {
                Bitmap bitmapImageOriginal = BitmapFactory.decodeFile(picturePath);
                Bitmap bitmapImageResult = BitmapFactory.decodeFile(picturePath);
                Mat matImage = new Mat();

                // Exibe a imagem original
                imgOriginal.setImageBitmap(bitmapImageOriginal);

                // Cria a matriz do bitmap criado
                Utils.bitmapToMat(bitmapImageOriginal, matImage);

                // Transforma a imagem em escala de cinza
                Imgproc.cvtColor(matImage, matImage, Imgproc.COLOR_RGB2GRAY);

                // Aplica o filtro gaussiano
                Imgproc.GaussianBlur(matImage, matImage, new Size(5,5), 0);

                // Transforma a matriz para binaria
                Imgproc.threshold(matImage, matImage, 127, 255, Imgproc.THRESH_BINARY);

                Mat cannyEdges = new Mat();
                Imgproc.Canny(matImage, cannyEdges, 40, 100);















                // Reconverte a imagem binaria para bitmap
                Utils.matToBitmap(cannyEdges, bitmapImageResult);

                // Exibe o bitmap final
                imgResult.setImageBitmap(bitmapImageResult);
            }
        }
    }

    public void test() {
        Bitmap bitmapImageOriginal = BitmapFactory.decodeFile("/storage/emulated/0/Download/photo_2016-11-02_13-05-57.jpg");
        Bitmap bitmapImageResult = BitmapFactory.decodeFile("/storage/emulated/0/Download/photo_2016-11-02_13-05-57.jpg");
        Mat matImage = new Mat();
        Mat matImageGray = new Mat();

        // Exibe a imagem original
        imgOriginal.setImageBitmap(bitmapImageOriginal);

        // Cria a matriz do bitmap criado
        Utils.bitmapToMat(bitmapImageOriginal, matImage);

        // Transforma a imagem em escala de cinza
        Imgproc.cvtColor(matImage, matImageGray, Imgproc.COLOR_RGB2GRAY);

        // Aplica o filtro gaussiano
        Imgproc.GaussianBlur(matImageGray, matImageGray, new Size(5,5), 0);

        // Transforma a matriz para binaria
        Imgproc.threshold(matImageGray, matImageGray, 155, 255, Imgproc.THRESH_BINARY);

        // Canny
        Mat cannyEdges = new Mat();
        Imgproc.Canny(matImageGray, cannyEdges, 200, 100);

        // Hough
        Mat circles = new Mat();
        int sensibilidade = 4;
        int minDist = 10;
        int minRadius = 0;
        int maxRadius = 40;
        Imgproc.HoughCircles(cannyEdges, circles, CV_HOUGH_GRADIENT, sensibilidade, minDist, 20, 100, minRadius, maxRadius);
        Toast.makeText(getContext(), "Circles: " + circles.cols(), Toast.LENGTH_LONG).show();

        // Coloca os circulos na imagem original
        for (int i = 0; i < circles.cols(); i++) {
            double[] parameters = circles.get(0, i);
            double x, y;
            int r;

            x = Math.round(parameters[0]);
            y = Math.round(parameters[1]);
            r = (int) Math.round(parameters[2]);

            Point center = new Point(x, y);

//            Imgproc.circle(houghCircles, center, 3, new Scalar(0, 255, 0), -1, 8, 0);

            Imgproc.circle(matImage, center, r, new Scalar(255, 0, 0), 1);
        }

        // Reconverte a imagem binaria para bitmap
        Utils.matToBitmap(matImage, bitmapImageResult);

        // Exibe o bitmap final
        imgResult.setImageBitmap(bitmapImageResult);



        // teste ------------------------------------
        // Cria a matriz do bitmap criado
        Utils.matToBitmap(matImageGray, bitmapImageOriginal);

        // Exibe a imagem original
        imgOriginal.setImageBitmap(bitmapImageOriginal);
    }

}
