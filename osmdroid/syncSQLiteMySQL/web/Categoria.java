package com.esy.jaha.osmdroid.syncSQLiteMySQL.web;

/**
 * Esta clase representa un registro individual de la base de datos
 */
public class Categoria {

    public String id_categoria;
    public String categoria;
    public String icono;


    public Categoria(String id_categoria, String categoria, String icono) {
        this.id_categoria = id_categoria;
        this.categoria = categoria;
        this.icono = icono;
    }
}
