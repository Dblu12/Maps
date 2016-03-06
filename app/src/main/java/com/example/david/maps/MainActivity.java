package com.example.david.maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.david.maps.pojo.Posicion;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Db4O bd;
    private GoogleMap mMap;
    private static final int CTEPLAY = 1;
    private LocationRequest peticionLocalizaciones;
    private GoogleApiClient cliente;
    private PolylineOptions ruta;
    private int diaactual= Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    private int diavisual= diaactual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (status == ConnectionResult.SUCCESS) {
            cliente = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            cliente.connect();
            //tv.setText("Conecta" + "\n");
        } else {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                GooglePlayServicesUtil.getErrorDialog(status, this, CTEPLAY).show();
            } else {
                Toast.makeText(this, "No", Toast.LENGTH_LONG).show();
            }
        }

        Log.v("xxx", "oncreate");
        peticionLocalizaciones = new LocationRequest();
        peticionLocalizaciones.setInterval(10000);
        peticionLocalizaciones.setFastestInterval(5000);
        peticionLocalizaciones.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        ruta= new PolylineOptions();

        bd = new Db4O(this);
        for(Posicion p:bd.getConsulta()){
            if(p.getDia()==diavisual){
                ruta.add(p.getCoords());
            }
        }

        Log.v("xxx", "cerrada");
        bd.close();

        Intent intent = new Intent(this, Service.class);


        startService(intent);


    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.v("xxx", "onConected");
        peticionLocalizaciones = new LocationRequest();
        //peticionLocalizaciones.setInterval(10000);
        peticionLocalizaciones.setSmallestDisplacement(1);
        peticionLocalizaciones.setFastestInterval(5000);
        peticionLocalizaciones.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(cliente, peticionLocalizaciones, this);
        /*ultimaLocalizacion= LocationServices.
                FusedLocationApi.getLastLocation(cliente);
        if(ultimaLocalizacion!= null) {

        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("xxx", "onConectedSuspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        if(diavisual==diaactual) {
            Log.v("xxx", "cambiado");
            Toast.makeText(this, "Cambiado", Toast.LENGTH_SHORT).show();
            double la = location.getLatitude();
            double lo = location.getLongitude();
            LatLng sydney = new LatLng(la, lo);
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            ruta.add(sydney);
            mMap.clear();
            mMap.addPolyline(ruta);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera


        LatLng sydney = new LatLng(37.35, -122.0);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Instantiates a new Polyline object and adds points to define a rectangle



// Get back the mutable Polyline

        Polyline polyline = mMap.addPolyline(ruta);



        /*Query q = bd.query();
        q.constrain(Posicion.class);
        q.descend("dia").constrain(diavisual).like();
        ObjectSet<Posicion> p= q.execute();
        for(Posicion a: p) {
            ruta.add(a.getCoords());
        }

*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_mon) {
            diavisual=2;
            ruta= new PolylineOptions();
            bd = new Db4O(this);
            for(Posicion p:bd.getConsulta()){
                if(p.getDia()==diavisual){
                    ruta.add(p.getCoords());
                }
            }
            bd.close();
            mMap.clear();
            mMap.addPolyline(ruta);
            return true;
        }
        if (id == R.id.action_tues) {
            diavisual=3;
            ruta= new PolylineOptions();
            bd = new Db4O(this);
            for(Posicion p:bd.getConsulta()){
                if(p.getDia()==diavisual){
                    ruta.add(p.getCoords());
                }
            }
            bd.close();
            mMap.clear();
            mMap.addPolyline(ruta);
            return true;
        }

        if (id == R.id.action_wed) {
            diavisual=4;
            ruta= new PolylineOptions();
            bd = new Db4O(this);
            for(Posicion p:bd.getConsulta()){
                if(p.getDia()==diavisual){
                    ruta.add(p.getCoords());
                }
            }
            bd.close();
            mMap.clear();
            mMap.addPolyline(ruta);
            return true;
        }
        if (id == R.id.action_thurs) {
            diavisual=5;
            ruta= new PolylineOptions();
            bd = new Db4O(this);
            for(Posicion p:bd.getConsulta()){
                if(p.getDia()==diavisual){
                    ruta.add(p.getCoords());
                }
            }
            bd.close();
            mMap.clear();
            mMap.addPolyline(ruta);
            return true;
        }
        if (id == R.id.action_fri) {
            diavisual=6;
            ruta= new PolylineOptions();
            bd = new Db4O(this);
            for(Posicion p:bd.getConsulta()){
                if(p.getDia()==diavisual){
                    ruta.add(p.getCoords());
                }
            }
            bd.close();
            mMap.clear();
            mMap.addPolyline(ruta);
            return true;
        }
        if (id == R.id.action_satur) {
            diavisual=7;
            ruta= new PolylineOptions();
            bd = new Db4O(this);
            for(Posicion p:bd.getConsulta()){
                if(p.getDia()==diavisual){
                    ruta.add(p.getCoords());
                }
            }
            bd.close();
            mMap.clear();
            mMap.addPolyline(ruta);
            return true;
        }
        if (id == R.id.action_sund) {
            diavisual=1;
            ruta= new PolylineOptions();
            bd = new Db4O(this);
            for(Posicion p:bd.getConsulta()){
                if(p.getDia()==diavisual){
                    ruta.add(p.getCoords());
                }
            }
            bd.close();
            mMap.clear();
            mMap.addPolyline(ruta);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
