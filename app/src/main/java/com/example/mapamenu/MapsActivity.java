package com.example.mapamenu;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mapamenu.Clases.DirectionsParser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISIONS_FINE_LOCATION = 1000;
    private GoogleMap mMap;
    String latitud, longitud, opcion;
    String latitud2, longitud2;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        bundle = getIntent().getExtras();
        opcion = bundle.getString("opcion");
        if (opcion.equals("usandogps")) {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(30000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
            actualizarGPS();
        }

    }

    private void actualizarGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    ActualizarUbicacion(location);
                }
            });

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISIONS_FINE_LOCATION);
            }
        }

    }

    private void ActualizarUbicacion(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        LatLng GpsPoints = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(GpsPoints).title("TU"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(GpsPoints));

        latitud=bundle.getString("latitud");
        longitud=bundle.getString("longitud");

        LatLng dest = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
        mMap.addMarker(new MarkerOptions().position(dest).title("Destino"));

        String url = getRequestUrl(GpsPoints, dest);
        TaskResquestDirections taskRequestDirection = new TaskResquestDirections();
        taskRequestDirection.execute(url);


        //nose por que me manda hasta san fransisco pero no muere la app
    }


     /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);



        latitud = bundle.getString("latitud");
        longitud = bundle.getString("longitud");

        latitud2 = bundle.getString("latitud2");
        longitud2 = bundle.getString("longitud2");

        // Add a marker in Sydney and move the camera

        switch (opcion) {
            case "marca":
                    LatLng marca = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
                    mMap.addMarker(new MarkerOptions().position(marca).title("Lugar Indicado"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marca, 16));
                break;
            case "ruta":
                    LatLng origen = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
                    mMap.addMarker(new MarkerOptions().position(origen).title("Punto Origen"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, 16));

                    LatLng destino = new LatLng(Double.parseDouble(latitud2), Double.parseDouble(longitud2));
                    mMap.addMarker(new MarkerOptions().position(destino).title("Punto Destino"));

                    String url = getRequestUrl(origen, destino);
                    TaskResquestDirections taskRequestDirection = new TaskResquestDirections();
                    taskRequestDirection.execute(url);
                break;
        }

    }


    private String getRequestUrl(LatLng origen, LatLng destino) {
        String resultado = "";

        String string_origen = "origin="+origen.latitude+","+origen.longitude;
        String string_destino = "destination="+destino.latitude+","+destino.longitude;

        String sensor = "sensor=false";
        String modo = "mode=driving";

        String param = string_origen+"&"+string_destino+"&"+sensor+"&"+modo;
        String salida = "json";

        resultado = "https://maps.googleapis.com/maps/api/directions/"+salida+"?"+param;

        return resultado;
    }
    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String linea = "";

            while ((linea = bufferedReader.readLine())!=null){
                stringBuffer.append(linea);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.getMessage();
        } finally {
            if(inputStream != null)
                inputStream.close();
            httpURLConnection.disconnect();
        }

        return responseString;
    }
    public class TaskResquestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";

            try{
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }
    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser=new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for(List<HashMap<String, String>> path : lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for(HashMap<String, String> point : path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if(polylineOptions!= null){
                mMap.addPolyline(polylineOptions);
            } else{
                Toast.makeText(getApplicationContext(), "Dirección no encontrada", Toast.LENGTH_SHORT).show();
            }
        }
    }


}