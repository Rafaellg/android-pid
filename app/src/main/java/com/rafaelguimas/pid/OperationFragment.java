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
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static android.app.Activity.RESULT_OK;

public class OperationFragment extends Fragment {

    public static int RESULT_LOAD_IMAGE_1 = 1;
    public static int RESULT_LOAD_IMAGE_2 = 2;

    private Bitmap bitmapImage1, bitmapImage2;
    private Mat matImage1 = new Mat(), matImage2 = new Mat(), matResult = new Mat();

    private ImageView imgImage1, imgImage2, imgImageResult;
    private TextView txtResult;

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
        imgImageResult = (ImageView) view.findViewById(R.id.imgResult);
        Button btnSelect1 = (Button) view.findViewById(R.id.btnSelect1);
        Button btnSelect2 = (Button) view.findViewById(R.id.btnSelect2);
        Button btnOperationLogical = (Button) view.findViewById(R.id.btnOperationLogical);
        Button btnOperationMath = (Button) view.findViewById(R.id.btnOperationMath);
        txtResult = (TextView) view.findViewById(R.id.txtResult);

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

        // Clique do botao de operacao logica
        btnOperationLogical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogicalOperations();
            }
        });

        // Clique do botao de operacao aritmetica
        btnOperationMath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMathOperations();
            }
        });
        
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

            // Verifica qual das duas imagem foi selecionada
            if (requestCode == RESULT_LOAD_IMAGE_1) {
                // Cria e exibe o bitmap com a imagem selecionada
                bitmapImage1 = BitmapFactory.decodeFile(picturePath);

                // Cria a matriz do bitmap criado
                Utils.bitmapToMat(bitmapImage1, matImage1);

                // Transforma a imagem em escala de cinza
                Imgproc.cvtColor(matImage1, matImage1, Imgproc.COLOR_RGB2GRAY);

                // Reconverte a matriz binaria para bitmap
                Utils.matToBitmap(matImage1, bitmapImage1);

                // Exibe o bitmap binario
                imgImage1.setImageBitmap(bitmapImage1);

            } else if (requestCode == RESULT_LOAD_IMAGE_2) {
                // Cria o bitmap com a imagem selecionada
                bitmapImage2 = BitmapFactory.decodeFile(picturePath);

                // Cria a matriz do bitmap criado
                Utils.bitmapToMat(bitmapImage2, matImage2);

                // Transforma a imagem em escala de cinza
                Imgproc.cvtColor(matImage2, matImage2, Imgproc.COLOR_RGB2GRAY);

                // Reconverte a matriz binaria para bitmap
                Utils.matToBitmap(matImage2, bitmapImage2);

                // Exibe o bitmap binario
                imgImage2.setImageBitmap(bitmapImage2);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Operações");
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void showLogicalOperations() {
        final String items[] = new String[] {"AND", "OR", "XOR", "NOT"};

        new AlertDialog.Builder(getContext())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (bitmapImage1 == null || bitmapImage2 == null ||
                                matImage1.size().height != matImage2.size().height ||
                                matImage1.size().width != matImage2.size().width) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Atenção")
                                    .setMessage("Você precisa selecionar duas imagens de mesma resolucao para executar a operação")
                                    .setNeutralButton("OK", null)
                                    .show();
                        } else {
                            // Copia a matriz da imagem1 para copiar as proporcoes (linhas x colunas)
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
                            imgImageResult.setImageBitmap(bitmapImageResult);
                        }
                    }
                })
                .setTitle("Operações")
                .show();
    }

    public void showMathOperations() {
        final String items[] = new String[] {"ADIÇÃO", "SUBTRAÇÃO", "MULTIPLICAÇÃO", "DIVISÃO"};

        new AlertDialog.Builder(getContext())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (bitmapImage1 == null || bitmapImage2 == null ||
                                matImage1.size().height != matImage2.size().height ||
                                matImage1.size().width != matImage2.size().width) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Atenção")
                                    .setMessage("Você precisa selecionar duas imagens de mesma resolucao para executar a operação")
                                    .setNeutralButton("OK", null)
                                    .show();
                        } else {
                            // Copia a matriz da imagem1 para copiar as proporcoes (linhas x colunas)
                            matImage1.copyTo(matResult);

                            // Executa a operacao selecionada
                            if (which == 0) {
                                Core.addWeighted(matImage1, 0.5,matImage2, 0.5, 0.0,matResult);
                            } else if (which == 1) {
                                Core.subtract(matImage1, matImage2, matResult);
                            } else if (which == 2) {
                                Core.multiply(matImage1, matImage2, matResult);
                            } else if (which == 3) {
                                Core.divide(matImage1, matImage2, matResult);
                            }

                            // Exibe o nome da operacao
                            String resultText = "Resultado - " + items[which];
                            txtResult.setText(resultText);

                            // Converte o resultado para bm
                            Bitmap bitmapImageResult = Bitmap.createBitmap(matResult.cols(), matResult.rows(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(matResult, bitmapImageResult);

                            // Exibe a imagem do resultado
                            imgImageResult.setImageBitmap(bitmapImageResult);
                        }
                    }
                })
                .setTitle("Operações")
                .show();
    }

}
