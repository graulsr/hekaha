package com.esy.jaha.osmdroid.nuevo;

import android.graphics.Bitmap;

/**
 * Created by Romi on 08/03/2018.
 */

 class FotoItem{
   public Bitmap foto;
    public String descripcion;

   FotoItem(Bitmap foto, String descripcion){
        this.foto = foto;
        this.descripcion = descripcion;
    }
}
