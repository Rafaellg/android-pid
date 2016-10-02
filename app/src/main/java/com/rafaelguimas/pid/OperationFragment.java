package com.rafaelguimas.pid;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class OperationFragment extends Fragment {

    public static int RESULT_LOAD_IMAGE_1 = 1;
    public static int RESULT_LOAD_IMAGE_2 = 2;

    private Bitmap img1, img2;
    private Mat mat1, mat2, mat3;

    private ImageView iv, iv2, iv3;

    public OperationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for getContext() fragment
        View view = inflater.inflate(R.layout.fragment_operation, container, false);

        // Objetos da tela
        iv = (ImageView) view.findViewById(R.id.img1);
        iv2 = (ImageView) view.findViewById(R.id.img2);
        iv3 = (ImageView) view.findViewById(R.id.img3);
        Button btnSelect1 = (Button) view.findViewById(R.id.btnSelect1);
        Button btnSelect2 = (Button) view.findViewById(R.id.btnSelect2);
        Button btnOperation = (Button) view.findViewById(R.id.btnOperation);

        btnSelect1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE_1);
            }
        });

        btnSelect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE_2);
            }
        });

        // Exemplo de criacao de matriz
//        Mat m = new Mat(100, 400, CvType.CV_8UC3);

        // Cria as matrizes com os drawables
        try {
            mat1 = Utils.loadResource(getContext(), R.drawable.lena_gray);
            mat2 = Utils.loadResource(getContext(), R.drawable.lena_gray_dot);
            mat3 = Utils.loadResource(getContext(), R.drawable.lena_gray);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converte mat para bitmap
        Bitmap bm1 = Bitmap.createBitmap(mat1.cols(), mat1.rows(), Bitmap.Config.ARGB_8888);
        Bitmap bm2 = Bitmap.createBitmap(mat2.cols(), mat2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat1, bm1);
        Utils.matToBitmap(mat2, bm2);

        // Exibe as imagens de entrada
        iv.setImageBitmap(bm1);
        iv2.setImageBitmap(bm2);

        // Clique do botao de operacao
        btnOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Executa operacoes com as matrizes
//                Imgproc.cvtColor(mat1, mat1, Imgproc.COLOR_BGR2GRAY);
                Core.bitwise_xor(mat3, mat2, mat3);

                // Converte o resultado para bm
                Bitmap bm3 = Bitmap.createBitmap(mat3.cols(), mat3.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat3, bm3);

                // Exibe a imagem do resultado
                iv3.setImageBitmap(bm3);

                // Alerta do que deve ser desenvolvido
                Toast.makeText(getContext(), "Abre o dialog e seleciona operacao (padrão: xor)", Toast.LENGTH_LONG).show();
            }
        });
        
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Codigo comum pras duas imagens
        String picturePath = "";
        if (resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
        }

        if (requestCode == RESULT_LOAD_IMAGE_1) {
            img1 = BitmapFactory.decodeFile(picturePath);
            iv.setImageBitmap(img1);
        } else if (requestCode == RESULT_LOAD_IMAGE_2) {
            img2 = BitmapFactory.decodeFile(picturePath);
            iv2.setImageBitmap(img2);
        }
    }

}
