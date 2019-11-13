package com.esy.jaha.osmdroid.nuevo;

import android.os.Parcel;
import android.os.Parcelable;

public class RecyclerGaleriaItem implements Parcelable {

    private int id_foto;
    private String nombre_foto;
    private String  descripcion;


     RecyclerGaleriaItem(int id, String descripcion, String nombre_foto){

        this.id_foto = id;
        this.nombre_foto = nombre_foto;
        this.descripcion = descripcion;


    }
    private RecyclerGaleriaItem (Parcel in){
        readFromParcel(in);
    }
    String getDescripcion_foto() {
        if (descripcion != null) {
            return descripcion;
        }else {
            return "";
        }
    }
    void setDescripcion_foto(String descripcion) {
        this. descripcion = descripcion;
    }

    int getId_foto() {
        return id_foto;
    }

    String getNombre_foto() {
        return nombre_foto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id_foto);
        dest.writeString(nombre_foto);
        dest.writeString(descripcion);

    }
    private void readFromParcel(Parcel in) {
        id_foto = in.readInt();
        nombre_foto = in.readString();
        descripcion = in.readString();

    }
    public static final Parcelable.Creator<RecyclerGaleriaItem> CREATOR
            = new Parcelable.Creator<RecyclerGaleriaItem>() {
        public RecyclerGaleriaItem createFromParcel(Parcel in) {
            return new RecyclerGaleriaItem(in);
        }

        public RecyclerGaleriaItem[] newArray(int size) {
            return new RecyclerGaleriaItem[size];
        }
    };
}
