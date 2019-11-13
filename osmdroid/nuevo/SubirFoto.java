package com.esy.jaha.osmdroid.nuevo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.SessionManager;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SubirFoto extends AsyncTask<String, String, JSONObject> {
    private String nom_img, nom_carpeta, descripcion;
    private ProgressDialog pDialog;
    private JSONObject result;
    private String tipo;
    private File file;
    private int thumbWidth, thumbHeight;
    private Context context;
    private boolean isPortada;
    private String origen;

     SubirFoto( File file, Context ctx, String tipo, String nom_carpeta, int thumbWidth, int thumbHeight, String descripcion, String origen){ //indice_foto
        this.file = file;
        this.origen = origen;
        context = ctx;
        nom_img =  file.getName();
/*        if (nom_img.contains(" ")){
            nom_img = nom_img.replace(" ", "_");
        }
        if (nom_img.contains("ñ")){
            nom_img = nom_img.replace("ñ", "n");
        }*/
        this.tipo = tipo;
        this.nom_carpeta = nom_carpeta;
        this.thumbWidth = thumbWidth;
        this.thumbHeight = thumbHeight;
        this.descripcion = descripcion;
    }


    @Override
    protected JSONObject doInBackground(String... args) {

        HttpFileUploader uploader;
        String url;
        if(nom_carpeta.contains("portada")){
            url = obtenerUrl(true);
        }else{
            url = obtenerUrl(false);
        }
        uploader = new HttpFileUploader(url, nom_img, nom_carpeta, thumbWidth, thumbHeight, descripcion, context);
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
        pDialog.setMessage("Un momento..." );
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
//        pDialog.dismiss();
        int estado;
        String nombreFoto = "", descrip = "";
        try {
            if (result != null){
                estado = result.getInt("estado");

                if (estado == 1){
                    int lasId = 0;
                    nombreFoto = result.getString("filename");
                    if (!origen.equals("portada") && !descripcion.equals("")){
                        lasId = result.getInt("lastId");
                        actualizarDescripcion(lasId);
                    }else{
                        pDialog.dismiss();
                        switch (origen){
                            case "portada":
                                ActivityPortada.actualizarImagen(nom_img);
                                break;

                            case "fotos":
                                lasId = result.getInt("lastId");
                                ActivityFotos.agregarItem(lasId, nombreFoto, "");
                                break;

                            case "galeria":
                                lasId = result.getInt("lastId");
                                ActivityGaleriaEditable.agregarItem(lasId, nombreFoto, "");
                                break;
                        }
                    }

                } else {
                    pDialog.dismiss();
                        if (isPortada)    {
                            ActivityPortada.removerImagen();
                        }
                        Toast.makeText(context, "No se pudo subir la imagen", Toast.LENGTH_SHORT).show();
                    }
            }else {
                pDialog.dismiss();
                Toast.makeText(context, "No se puede conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            pDialog.dismiss();
            Toast.makeText(context, "No se puede conectar con el servidor", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    private String obtenerUrl(boolean isPortada){

        this.isPortada = isPortada;
        String url = "";
        String urlPortada = "";

        switch (tipo){

            case "locales":
                url = Constantes.URL_BASE + "locales/subirFotoLocal.php";
                urlPortada = Constantes.URL_BASE + "locales/subirFotoPortadaLocal.php";
                break;
            case "eventos":
                url = Constantes.URL_BASE +"eventos/subirFotoEvento.php";
                urlPortada = Constantes.URL_BASE + "eventos/subirFotoPortadaEvento.php";
                break;
            case "promos":
                url = Constantes.URL_BASE +"promos/subirFotoPromo.php";
                urlPortada = Constantes.URL_BASE + "promos/subirFotoPortadaPromo.php";
                break;
            case "especiales":
                url = Constantes.URL_BASE +"especiales/subirFotoEspecial.php";
                urlPortada = Constantes.URL_BASE + "especiales/subirFotoPortadaEspecial.php";
                break;

        }
        if(isPortada){
            return urlPortada;
        }else {
            return url;
        }
    }
private void actualizarImagen(){

    String nombreFoto = null;
    try {
        nombreFoto = result.getString("filename");
        int id_foto = result.getInt("lastId");
        switch (origen){

            case "fotos":
                ActivityFotos.agregarItem(id_foto, nombreFoto, descripcion);
                break;

            case "galeria":
                ActivityGaleriaEditable.agregarItem(id_foto, nombreFoto, descripcion);
                break;
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }


}

    private void actualizarDescripcion(int lastId){

        String ruta = Constantes.URL_BASE + tipo + "/actualizarDescripcion.php";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", SessionManager.getInstancia(context).getToken());
            jsonObject.put("id_user", SessionManager.getInstancia(context).getUserId());
            jsonObject.put("id_foto", lastId);
            jsonObject.put("descripcion", descripcion);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    ruta, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jsonObject) {

                        pDialog.dismiss();
                    try {
                        int estado = jsonObject.getInt("estado");
                        if (estado == 1){
                            actualizarImagen();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                        pDialog.dismiss();
                        Toast.makeText(context, "Hubo un problema con la descripción", Toast.LENGTH_SHORT).show();
                }
            });
            MySingleton.getInstance(context).addToRequestQueue(request);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

