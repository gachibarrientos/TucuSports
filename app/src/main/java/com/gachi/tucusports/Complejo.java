package com.gachi.tucusports;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Gachi on 05/07/2015.
 */
public class Complejo {
    String titulo, descripcion, numero;
    LatLng coord;

    public Complejo(LatLng coord, String titulo, String descripcion, String numero) {
        this.coord = coord;
        this.descripcion = descripcion;
        this.numero = numero;
        this.titulo = titulo;
    }

    public Complejo() {
    }

    public LatLng getCoord() {
        return coord;
    }

    public void setCoord(LatLng coord) {
        this.coord = coord;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


}

