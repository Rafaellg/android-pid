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

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static android.app.Activity.RESULT_OK;
import static com.rafaelguimas.pid.OperationFragment.RESULT_LOAD_IMAGE_1;

public class FilterFragment extends Fragment {

    private Bitmap imgImage1;
    private Mat matImage1, matImage2, matResult = new Mat();

    ImageView img1, img2, imgResult;
    private static int RESULT_LOAD_IMG = 1;


    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        //link dos objetos da tela
        Button btnImage = (Button) view.findViewById(R.id.btnImage);
        Button btnFiltro = (Button) view.findViewById(R.id.btnFiltro);
        img1 = (ImageView) view.findViewById(R.id.img1);
        img2 = (ImageView) view.findViewById(R.id.img2);
        imgResult = (ImageView) view.findViewById(R.id.imgResult);
        final TextView txtResult = (TextView) view.findViewById(R.id.txtResult);

        //click dos botoes mais funcionalidades
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // pega imagem da galeria
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String items[] = new String[]{"Media", "Mediana", "Gaussiano", "Moda", "Maximo", "Minimo"};

                new AlertDialog.Builder(getContext())
                        .setTitle("Filtros")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    //metodo 1
                                } else if (which == 1 ){
                                    //metodo 2
                                } else if (which == 2){
                                    //metoto
                                } else if (which == 3){
                                    //metodo 4
                                } else if (which == 4){
                                    //metodo 5
                                } else if (which == 5) {
                                    //metodo 6
                                }

                                // Exibe o nome do filtro
                                String resultText = "Resultado - " + items[which];
                                txtResult.setText(resultText);


                            } //on click

                        })
                        .show();
            }

        });

        return view;
    }// on create

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
            imgImage1 = BitmapFactory.decodeFile(picturePath);
            Mat mat = new Mat(), matResult = new Mat();
            Utils.bitmapToMat(imgImage1,mat);
            Imgproc.GaussianBlur(mat, matResult, new Size(45,45), 0);
            Utils.matToBitmap(matResult, imgImage1);
            img1.setImageBitmap(imgImage1);
            img2.setImageBitmap(imgImage1);
        }


    }

//
//    public void filtros(int filtro){
//        // Cria as matrizes com os drawables
//        try {
//            matImage1 = Utils.loadResource(getContext(), R.drawable.lena_gray);
//            matImage2 = Utils.loadResource(getContext(), R.drawable.lena_gray_dot);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // Converte mat para bitmap
//        Bitmap bitmapImage1 = Bitmap.createBitmap(matImage1.cols(), matImage1.rows(), Bitmap.Config.ARGB_8888);
//        Bitmap bitmapImage2 = Bitmap.createBitmap(matImage2.cols(), matImage2.rows(), Bitmap.Config.ARGB_8888);
//        Bitmap bitmapImageResult = Bitmap.createBitmap(matResult.cols(), matResult.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(matImage1, bitmapImage1);
//        Utils.matToBitmap(matImage2, bitmapImage2);
//        Utils.matToBitmap(matResult, bitmapImageResult);
//
//        if(filtro == 3 ){ //gaussiano
//            Mat matResult = new Mat(matImage1.rows(),matImage1.cols(),matImage1.type());
//            matImage1.copyTo(matResult);
//            Imgproc.GaussianBlur(matImage1, matResult,new Size(45,45), 0);
//
//            imgResult.setImageBitmap(bitmapImageResult);
//
//        } //if
//
//    }



}


