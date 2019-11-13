package com.esy.jaha.osmdroid;

public class RecyclerCardItem {
    private int id; // id local evento etc
    private double latitud;
    private double longitud;
    private String marcador;
    private String icono;
    private String nombre;
    private String descripcion;
    private String foto_portada;

    public RecyclerCardItem(int id, double latitud, double longitud, String marcador, String icono, String nombre, String descripcion, String foto_portada){
        this.id = id;
        this.latitud = latitud;
        this.longitud = longitud;
        this.marcador = marcador;
        this.icono = icono;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.foto_portada = foto_portada;

    }

    public String getPortada() {
        if (foto_portada == null){
            return "null";
        }
        return foto_portada;    }

    public String getdescripcion() {
        if (descripcion.isEmpty()){
            return "****************";
        }else {
            return descripcion;
        }
    }

    public String getNombreLocal() {
        return nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getMarcador(){ return marcador;}

    public String getIcono(){return icono;}

    public int getId(){return id;}

}
