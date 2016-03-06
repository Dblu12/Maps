package com.example.david.maps.pojo;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by David on 05/03/2016.
 */
public class Posicion {
    private LatLng coords;
    private int dia;

    public Posicion(LatLng coords, int dia) {
        this.coords = coords;
        this.dia = dia;
    }

    public LatLng getCoords() {
        return coords;
    }

    public void setCoords(LatLng coords) {
        this.coords = coords;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    @Override
    public String toString() {
        return "Posicion{" +
                "coords=" + coords +
                ", dia=" + dia +
                '}';
    }
}
