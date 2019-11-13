package com.esy.jaha.osmdroid.nuevo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SubirIcono extends AsyncTask<String, String, JSONObject> {
    private String nom_img, nom_carpeta;
    private ProgressDialog pDialog;
    private String tipo;
    private JSONObject result;
    private File file;
    private Context context;

     SubirIcono( File file, Context ctx, String tipo, String nom_carpeta){ //indice_foto
        this.file = file;
        context = ctx;
        nom_img = String.valueOf(System.currentTimeMillis());
        this.nom_carpeta = nom_carpeta;
        this.tipo = tipo;
    }

    @Override
    protected JSONObject doInBackground(String... args) {

        String url = obtenerUrl();

        HttpFileUploader uploader = new HttpFileUploader(url, nom_img, nom_carpeta, context );
        try {
            result =  uploader.doStart(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Actualizando Servidor, espere..." );
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();
    }

    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        pDialog.dismiss();
        int estado = 0;
        try {
            estado = result.getInt("estado");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (estado == 1){
            Activity_iconos.fijarIcono(nom_img);
        } else {
            Toast.makeText(context, "Operación fallida ", Toast.LENGTH_SHORT).show();
        }

    }

    private String obtenerUrl(){

        String url = "";

        switch (tipo){

            case "locales":
                url = Constantes.URL_BASE + "locales/subirIconoLocal.php";
                break;
            case "eventos":
                url = Constantes.URL_BASE + "eventos/subirIconoEvento.php";
                  break;
            case "promos":
                url = Constantes.URL_BASE + "promos/subirIconoPromo.php";
                break;
            case "especiales":
                url = Constantes.URL_BASE + "especiales/subirIconoEspecial.php";
                break;

        }

        return url;
    }
}

