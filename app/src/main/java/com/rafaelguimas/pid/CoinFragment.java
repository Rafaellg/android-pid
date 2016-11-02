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

        processBitmaps("/storage/emulated/0/Download/photo_2016-11-02_13-05-55 (2).jpg");

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE) {
                // Recupera o caminho da imagem selecionada
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                // Cria e exibe o bitmap com a imagem selecionada
                processBitmaps(picturePath); // "/storage/emulated/0/Download/photo_2016-11-02_13-05-56.jpg"
            }
        }
    }

    public void processBitmaps(String picturePath) {
        Bitmap bitmapImageOriginal = BitmapFactory.decodeFile(picturePath);
        Bitmap bitmapImageResult = BitmapFactory.decodeFile(picturePath);
        Mat matImage = new Mat();
        Mat matImageGray = new Mat();

        // Exibe a imagem original
        imgOriginal.setImageBitmap(bitmapImageOriginal);

        // Cria a matriz do bitmap criado
        Utils.bitmapToMat(bitmapImageOriginal, matImage);

        // Transforma a imagem em escala de cinza
        Imgproc.cvtColor(matImage, matImageGray, Imgproc.COLOR_RGB2GRAY);

        // Aplica o filtro gaussiano
        Imgproc.GaussianBlur(matImageGray, matImageGray, new Size(5,5), 5);

        // Transforma a matriz para binaria
        Imgproc.threshold(matImageGray, matImageGray, 145, 255, Imgproc.THRESH_BINARY);

        // Canny
        Mat cannyEdges = new Mat();
        Imgproc.Canny(matImageGray, cannyEdges, 200, 100);

        // Hough
        Mat circles = new Mat();
        double sensibilidade = 3;
        int minDist = 10;
        int minRadius = 15;
        int maxRadius = 40;
        Imgproc.HoughCircles(cannyEdges, circles, CV_HOUGH_GRADIENT, sensibilidade, cannyEdges.rows()/8, 200, 75, minRadius, maxRadius);
        Toast.makeText(getContext(), "Círculos: " + circles.cols(), Toast.LENGTH_LONG).show();

        // Coloca os circulos na imagem original
        for (int i = 0; i < circles.cols(); i++) {
            double[] parameters = circles.get(0, i);
            double x, y;
            int r;

            // Recupera os valores da circunferencia
            x = Math.round(parameters[0]);
            y = Math.round(parameters[1]);
            r = (int) Math.round(parameters[2]);
            Point center = new Point(x, y);

            // Desenha o centro
            Imgproc.circle(matImage, center, 3, new Scalar(0, 255, 0), -1, 8, 0);

            // Desenha o circulo
            Imgproc.circle(matImage, center, r, new Scalar(0, 0, 255), 3);
        }

        // Reconverte a imagem binaria para bitmap
        Utils.matToBitmap(matImage, bitmapImageResult);

        // Exibe o bitmap final
        imgResult.setImageBitmap(bitmapImageResult);

        // teste ------------------------------------
        // Cria a matriz do bitmap criado
        Utils.matToBitmap(cannyEdges, bitmapImageOriginal);

        // Exibe a imagem original
        imgOriginal.setImageBitmap(bitmapImageOriginal);
    }

}
