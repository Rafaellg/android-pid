package com.rafaelguimas.pid;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
    private Mat matImage1, matImage2, matResult = new Mat();

    private ImageView imgImage1, imgImage2, iv3;

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
        imgImage1 = (ImageView) view.findViewById(R.id.img1);
        imgImage2 = (ImageView) view.findViewById(R.id.img2);
        iv3 = (ImageView) view.findViewById(R.id.imgResult);
        Button btnSelect1 = (Button) view.findViewById(R.id.btnSelect1);
        Button btnSelect2 = (Button) view.findViewById(R.id.btnSelect2);
        Button btnOperation = (Button) view.findViewById(R.id.btnOperation);
        final TextView txtResult = (TextView) view.findViewById(R.id.txtResult);

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
            matImage1 = Utils.loadResource(getContext(), R.drawable.lena_gray);
            matImage2 = Utils.loadResource(getContext(), R.drawable.lena_gray_dot);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converte mat para bitmap
        Bitmap bitmapImage1 = Bitmap.createBitmap(matImage1.cols(), matImage1.rows(), Bitmap.Config.ARGB_8888);
        Bitmap bitmapImage2 = Bitmap.createBitmap(matImage2.cols(), matImage2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matImage1, bitmapImage1);
        Utils.matToBitmap(matImage2, bitmapImage2);

        // Exibe as imagens de entrada
        imgImage1.setImageBitmap(bitmapImage1);
        imgImage2.setImageBitmap(bitmapImage2);

        // Clique do botao de operacao
        btnOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String items[] = new String[] {"AND", "OR", "XOR", "NOT"};

                new AlertDialog.Builder(getContext())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Copia a matriz da imagem um
                                matImage1.copyTo(matResult);

                                // Executa a operacao selecionada
                                if (which == 0) {
                                    Core.bitwise_and(matImage1, matImage2, matResult);
                                } else if (which == 1) {
                                    Core.bitwise_or(matImage1, matImage2, matResult);
                                } else if (which == 2) {
                                    Core.bitwise_xor(matImage1, matImage2, matResult);
                                } else if (which == 3) {
                                    Core.bitwise_not(matImage1, matResult);
                                }

                                // Exibe o nome da operacao
                                String resultText = "Resultado - " + items[which];
                                txtResult.setText(resultText);

                                // Converte o resultado para bm
                                Bitmap bitmapImageResult = Bitmap.createBitmap(matResult.cols(), matResult.rows(), Bitmap.Config.ARGB_8888);
                                Utils.matToBitmap(matResult, bitmapImageResult);

                                // Exibe a imagem do resultado
                                iv3.setImageBitmap(bitmapImageResult);
                            }
                        })
                        .setTitle("Operações")
                        .show();
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
            imgImage1.setImageBitmap(img1);
        } else if (requestCode == RESULT_LOAD_IMAGE_2) {
            img2 = BitmapFactory.decodeFile(picturePath);
            imgImage2.setImageBitmap(img2);
        }
    }

}
