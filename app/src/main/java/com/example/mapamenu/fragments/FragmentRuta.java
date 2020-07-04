package com.example.mapamenu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mapamenu.MapsActivity;
import com.example.mapamenu.R;

public class FragmentRuta extends Fragment {
    Button btn;
    EditText la, lo;
    EditText la2, lo2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final View view= inflater.inflate(R.layout.ruta_fragment,container,false);

        btn = (Button) view.findViewById(R.id.btn_puntos);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),MapsActivity.class);
                la = (EditText) view.findViewById(R.id.latitud);
                lo = (EditText) view.findViewById(R.id.longitud);

                la2 = (EditText) view.findViewById(R.id.latitud2);
                lo2 = (EditText) view.findViewById(R.id.longitud2);

                i.putExtra("opcion","ruta");
                i.putExtra("latitud", la.getText().toString());
                i.putExtra("longitud", lo.getText().toString());

                i.putExtra("latitud2", la2.getText().toString());
                i.putExtra("longitud2", lo2.getText().toString());
                startActivity(i);

            }
        });
        return view;

    }



}
