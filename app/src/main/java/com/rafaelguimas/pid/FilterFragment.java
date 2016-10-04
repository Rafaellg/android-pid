package com.rafaelguimas.pid;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static android.app.Activity.RESULT_OK;
import static com.rafaelguimas.pid.OperationFragment.RESULT_LOAD_IMAGE_1;
import static org.opencv.core.Core.randn;

public class FilterFragment extends Fragment {

    private static int RESULT_LOAD_IMG = 1;

    private Mat matImageOriginal = new Mat(), matImageGaussian = new Mat(), matImageSalt = new Mat(), matResultGaussian = new Mat(), matResultSalt = new Mat();

    private ImageView imgImageGaussian, imgImageSalt, imgImageResult1, imgImageResult2;
    private TextView txtResult;

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        //link dos objetos da tela
        Button btnSelectImage = (Button) view.findViewById(R.id.btnSelectImage);
        Button btnFiltro = (Button) view.findViewById(R.id.btnFilter);
        imgImageGaussian = (ImageView) view.findViewById(R.id.img1);
        imgImageSalt = (ImageView) view.findViewById(R.id.img2);
        imgImageResult1 = (ImageView) view.findViewById(R.id.imgResult1);
        imgImageResult2 = (ImageView) view.findViewById(R.id.imgResult2);
        txtResult = (TextView) view.findViewById(R.id.txtResult);

        //click dos botoes mais funcionalidades
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // pega imagem da galeria
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFiltersDialog();
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
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // Verifica qual das duas imagem foi selecionada
            if (requestCode == RESULT_LOAD_IMAGE_1) {
                // Cria e exibe o bitmap com a imagem selecionada
                final Bitmap bitmapImageGaussian = BitmapFactory.decodeFile(picturePath);
                final Bitmap bitmapImageSalt = BitmapFactory.decodeFile(picturePath);

                // Cria a matriz do bitmap criado
                Utils.bitmapToMat(bitmapImageGaussian, matImageOriginal);

                // Cria dialog de processando
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Processando ruídos");
                progressDialog.setCancelable(false);

                // Task para calcular os ruidos no background
                AsyncTask taskNoise = new AsyncTask() {
                    @Override
                    protected void onPreExecute() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Exibe dialog
                                progressDialog.show();
                            }
                        });
                    }

                    @Override
                    protected Object doInBackground(Object[] params) {
                        // Aplica os ruidos
                        noiseGaussian();
                        noiseSaltAndPepper();

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        // Reconverte a matriz binaria para bitmap
                        Utils.matToBitmap(matImageGaussian, bitmapImageGaussian);
                        Utils.matToBitmap(matImageSalt, bitmapImageSalt);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Exibe os bitmaps dos ruidos
                                imgImageGaussian.setImageBitmap(bitmapImageGaussian);
                                imgImageSalt.setImageBitmap(bitmapImageSalt);

                                // Esconde dialog
                                progressDialog.dismiss();
                            }
                        });
                    }
                };
                taskNoise.execute();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Filtros");
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_filters, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_filter:
                showFiltersDialog();
                break;
        }

        return true;
    }

    public void noiseGaussian() {
        // Copia a matriz original para a matriz do ruido gaussiano
        matImageGaussian = matImageOriginal.clone();

        // Cria matriz de ruido gaussiano
        Mat matNoiseGaussian = new Mat(matImageGaussian.size(), matImageGaussian.type());

        // randn(matriz destino, valor medio, desvio padrao)
        randn(matNoiseGaussian,0,50);

        // Aplica o ruido na matriz principal
        for(int m = 0; m < matImageGaussian.rows(); m++){
            for(int n = 0; n < matImageGaussian.cols(); n++){
                double[] val = new double[matImageGaussian.get(m,n).length];
                for(int i = 0; i < matImageGaussian.get(m,n).length; i++){
                    val[i] = matImageGaussian.get(m,n)[i] + matNoiseGaussian.get(m, n)[i];
                }
                matImageGaussian.put(m, n, val);
            }
        }
    }

    public void noiseSaltAndPepper() {
        // Copia a matriz original para a matriz do ruido gaussiano
        matImageSalt = matImageOriginal.clone();

        // Cria matriz de ruido sal e pimenta
        Mat matNoiseSalt = new Mat(matImageGaussian.size(), matImageGaussian.type());
        randn(matNoiseSalt,0,255);

        // Aplica o ruido na matriz principal
        // Substitui valores pequenos por 0 e altos por 255
        for(int m = 0; m < matImageSalt.rows(); m++){
            for(int n = 0; n < matImageSalt.cols(); n++){
                double[] val = new double[matImageSalt.get(m,n).length];
                if(matNoiseSalt.get(m,n)[0] < 15 && matNoiseSalt.get(m,n)[1] < 15 && matNoiseSalt.get(m,n)[2] < 15){
                    for(int i = 0; i < matImageSalt.get(m,n).length; i++){
                        val[i] = 0;
                    }
                    matImageSalt.put(m, n, val);
                }
                if(matNoiseSalt.get(m,n)[0] > 230 && matNoiseSalt.get(m,n)[1] > 230 && matNoiseSalt.get(m,n)[2] > 230){
                    for(int i = 0; i < matImageSalt.get(m,n).length; i++){
                        val[i] = 255;
                    }
                    matImageSalt.put(m, n, val);
                }
            }
        }
    }

    public void showFiltersDialog(){
        final String items[] = new String[]{"Media", "Mediana", "Gaussiano", "Moda", "Maximo", "Minimo"};

        new AlertDialog.Builder(getContext())
                .setTitle("Filtros")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Mat kernel = new Mat(3,3, CvType.CV_32F){
                            {
                                put(0,0,-1);
                                put(0,1,0);
                                put(0,2,1);

                                put(1,0-2);
                                put(1,1,0);
                                put(1,2,2);

                                put(2,0,-1);
                                put(2,1,0);
                                put(2,2,1);
                            }
                        };

                        if(which == 0){
                            Imgproc.blur(matImageGaussian, matResultGaussian, new Size(5,5));
                            Imgproc.blur(matImageSalt, matResultSalt, new Size(5,5));
                        } else if (which == 1 ){
                            Imgproc.medianBlur(matImageGaussian, matResultGaussian, 5);
                            Imgproc.medianBlur(matImageSalt, matResultSalt, 5);
                        } else if (which == 2){
                            Imgproc.GaussianBlur(matImageGaussian, matResultGaussian, new Size(5,5), 0);
                            Imgproc.GaussianBlur(matImageSalt, matResultSalt, new Size(5,5), 0);
                        } else if (which == 3){
                            Imgproc.filter2D(matImageGaussian, matResultGaussian, -1, kernel);
                            Imgproc.filter2D(matImageSalt, matResultSalt, -1, kernel);
                        } else if (which == 4){
                            Imgproc.dilate(matImageGaussian, matResultGaussian, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5)));
                            Imgproc.dilate(matImageSalt, matResultSalt, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5)));
                        } else if (which == 5) {
                            Imgproc.erode(matImageGaussian, matResultGaussian, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5)));
                            Imgproc.erode(matImageSalt, matResultSalt, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5)));
                        }

                        // Reconverte a matriz binaria para bitmap
                        Bitmap bitmapImageResult1 = Bitmap.createBitmap(matResultGaussian.cols(), matResultGaussian.rows(), Bitmap.Config.ARGB_8888);
                        Bitmap bitmapImageResult2 = Bitmap.createBitmap(matResultSalt.cols(), matResultSalt.rows(), Bitmap.Config.ARGB_8888);

                        // Transforma matrizes em bitmaps
                        Utils.matToBitmap(matResultGaussian, bitmapImageResult1);
                        Utils.matToBitmap(matResultSalt, bitmapImageResult2);

                        // Exibe os bitmaps com filtro
                        imgImageResult1.setImageBitmap(bitmapImageResult1);
                        imgImageResult2.setImageBitmap(bitmapImageResult2);

                        // Exibe o nome do filtro
                        String resultText = "Resultados - " + items[which];
                        txtResult.setText(resultText);
                    }

                })
                .show();
    }

}



