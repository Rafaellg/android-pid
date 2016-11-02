package com.rafaelguimas.pid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CoinFragment extends Fragment {

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

        return view;
    }

}
