package com.example.david.maps;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.AndroidSupport;
import com.db4o.config.EmbeddedConfiguration;
import com.example.david.maps.pojo.Posicion;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by David on 05/03/2016.
 */
public class Service extends android.app.Service implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{


    private static final int CTEPLAY = 1;
    private LocationRequest peticionLocalizaciones;
    private GoogleApiClient cliente;
    private Db4O bd;

    public Service(){
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (status == ConnectionResult.SUCCESS) {
            cliente = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            cliente.connect();
            Toast.makeText(this, "Conecta", Toast.LENGTH_LONG).show();
            //tv.setText("Conecta" + "\n");
        } else {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                //GooglePlayServicesUtil.getErrorDialog(status, this, CTEPLAY).show();
            } else {
                Toast.makeText(this, "No", Toast.LENGTH_LONG).show();
            }
        }


        peticionLocalizaciones = new LocationRequest();
        //peticionLocalizaciones.setInterval(10000);
        peticionLocalizaciones.setSmallestDisplacement(1);
        peticionLocalizaciones.setFastestInterval(5000);
        peticionLocalizaciones.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        return START_REDELIVER_INTENT;
    }





    private Location ultimaLocalizacion;

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void onConnected(Bundle bundle) {
        peticionLocalizaciones = new LocationRequest();
        //peticionLocalizaciones.setInterval(30000);
        peticionLocalizaciones.setFastestInterval(10000);
        peticionLocalizaciones.setSmallestDisplacement(1);
        peticionLocalizaciones.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //LocationServices.FusedLocationApi.requestLocationUpdates(cliente, peticionLocalizaciones, this);
        /*ultimaLocalizacion= LocationServices.
                FusedLocationApi.getLastLocation(cliente);
        if(ultimaLocalizacion!= null) {

        }*/
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(cliente, peticionLocalizaciones, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        float la = (float)location.getLatitude();
        float lo = (float)location.getLongitude();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        Posicion po = new Posicion(new LatLng(la,lo),day);
        Log.v("xxxService", po.toString());
        bd = new Db4O(this);
        bd.insertar(po);
        ArrayList<Posicion> datos = bd.getConsulta();
        bd.close();
        for (Posicion p : datos){
            Log.v("INSERTOOOOO BDDD", p.toString());
        }
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}