package com.esy.jaha.osmdroid;

public class SpinnerItem {

    private String icono;
    private String name;
    private String descripcion;

    SpinnerItem(String icono, String name, String descripcion) {
        super();

        this.icono = icono;
        this.name = name;
        this.descripcion = descripcion;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}