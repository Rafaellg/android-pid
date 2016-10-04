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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static android.app.Activity.RESULT_OK;

public class OperationFragment extends Fragment {

    public static int RESULT_LOAD_IMAGE_1 = 1;
    public static int RESULT_LOAD_IMAGE_2 = 2;

    private Bitmap bitmapImage1, bitmapImage2;
    private Mat matImage1BW = new Mat(), matImage2BW = new Mat(), matImage1Normal = new Mat(), matImage2Normal = new Mat(), matResult = new Mat();

    private ImageView imgImage1, imgImage2, imgImageResult;
    private TextView txtResult;

    public OperationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
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

        imgImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE_1);
            }
        });

        imgImage2.setOnClickListener(new View.OnClickListener() {
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
                Utils.bitmapToMat(bitmapImage1, matImage1BW);
                Utils.bitmapToMat(bitmapImage1, matImage1Normal);

                // Transforma a imagem em escala de cinza
                Imgproc.cvtColor(matImage1BW, matImage1BW, Imgproc.COLOR_RGB2GRAY);
                Imgproc.cvtColor(matImage1Normal, matImage1Normal, Imgproc.COLOR_RGB2GRAY);

                // Transforma a matriz para binaria
                Imgproc.threshold(matImage1BW, matImage1BW, 127, 255, Imgproc.THRESH_BINARY);

                // Exibe o bitmap colorido
                imgImage1.setImageBitmap(bitmapImage1);

            } else if (requestCode == RESULT_LOAD_IMAGE_2) {
                // Cria o bitmap com a imagem selecionada
                bitmapImage2 = BitmapFactory.decodeFile(picturePath);

                // Cria a matriz do bitmap criado
                Utils.bitmapToMat(bitmapImage2, matImage2BW);
                Utils.bitmapToMat(bitmapImage2, matImage2Normal);

                // Transforma a imagem em escala de cinza
                Imgproc.cvtColor(matImage2BW, matImage2BW, Imgproc.COLOR_RGB2GRAY);
                Imgproc.cvtColor(matImage2Normal, matImage2Normal, Imgproc.COLOR_RGB2GRAY);

                // Transforma a matriz para binaria
                Imgproc.threshold(matImage2BW, matImage2BW, 127, 255, Imgproc.THRESH_BINARY);

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_operations, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_op_logical:
                showLogicalOperations();
                break;
            case R.id.action_op_math:
                showMathOperations();
                break;
        }

        return true;
    }

    public void showLogicalOperations() {
        final String items[] = new String[] {"AND", "OR", "XOR", "NOT"};

        new AlertDialog.Builder(getContext())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (bitmapImage1 == null || bitmapImage2 == null ||
                                matImage1BW.size().height != matImage2BW.size().height ||
                                matImage1BW.size().width != matImage2BW.size().width) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Atenção")
                                    .setMessage("Você precisa selecionar duas imagens de mesma resolucao para executar a operação")
                                    .setNeutralButton("OK", null)
                                    .show();
                        } else {
                            // Copia a matriz da imagem1 para copiar as proporcoes (linhas x colunas)
                            matImage1BW.copyTo(matResult);

                            // Executa a operacao selecionada
                            if (which == 0) {
                                Core.bitwise_and(matImage1BW, matImage2BW, matResult);
                            } else if (which == 1) {
                                Core.bitwise_or(matImage1BW, matImage2BW, matResult);
                            } else if (which == 2) {
                                Core.bitwise_xor(matImage1BW, matImage2BW, matResult);
                            } else if (which == 3) {
                                Core.bitwise_not(matImage1BW, matResult);
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
                                matImage1Normal.size().height != matImage2Normal.size().height ||
                                matImage1Normal.size().width != matImage2Normal.size().width) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Atenção")
                                    .setMessage("Você precisa selecionar duas imagens de mesma resolucao para executar a operação")
                                    .setNeutralButton("OK", null)
                                    .show();
                        } else {
                            // Copia a matriz da imagem1 para copiar as proporcoes (linhas x colunas)
                            matImage1Normal.copyTo(matResult);

                            // Executa a operacao selecionada
                            if (which == 0) {
                                Core.addWeighted(matImage1Normal, 0.5, matImage2Normal, 0.5, 0.0,matResult);
                            } else if (which == 1) {
                                Core.subtract(matImage1Normal, matImage2Normal, matResult);
                            } else if (which == 2) {
                                Core.multiply(matImage1Normal, matImage2Normal, matResult);
                            } else if (which == 3) {
                                Core.divide(matImage1Normal, matImage2Normal, matResult);
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
