package com.example.mapamenu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mapamenu.MapsActivity;
import com.example.mapamenu.R;

public class FragmentUsandoGPS extends Fragment {
    Button btn;
    EditText la, lo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final View view= inflater.inflate(R.layout.usandogps_fragments,container,false);

        btn = (Button) view.findViewById(R.id.btn_puntos);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MapsActivity.class);
                la = (EditText) view.findViewById(R.id.latitud);
                lo = (EditText) view.findViewById(R.id.longitud);

                i.putExtra("opcion","usandogps");

                i.putExtra("latitud", la.getText().toString());
                i.putExtra("longitud", lo.getText().toString());

                startActivity(i);

            }
        });
        return view;
    }
}
