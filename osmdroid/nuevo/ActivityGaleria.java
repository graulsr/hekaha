package com.esy.jaha.osmdroid.nuevo;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ActivityGaleria extends AppCompatActivity  {

    private GaleriaAdaptador adaptador;
    List<RecyclerGaleriaItem> data;
    private boolean cancelarTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toobar_galeria);
        setSupportActionBar(toolbar);
        String nombre = getIntent().getStringExtra("nombre");
        String marcador = getIntent().getStringExtra("marcador");
        setTitle(nombre);
        int id = getIntent().getIntExtra("id", 0);
        String tipo = getIntent().getStringExtra("tipo");
        obtenerFotos(id, tipo);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = (int) (width*0.70);
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_galeria);
        String carpeta_imagenes = Constantes.URL_IMAGENES  + tipo +"/" + id + "/";
        data = new ArrayList<>();
        adaptador = new GaleriaAdaptador(this, carpeta_imagenes, data, tipo, nombre, height, marcador);
        recycler.setAdapter(adaptador);
        recycler.setLayoutManager(new LinearLayoutManager(this)); // Vertical Orientation By Default
    }

    @Override
    protected void onStart() {
        super.onStart();
        cancelarTodo = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelarTodo = true;
    }


    private void obtenerFotos(final int id, String tipo){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Constantes.URL_BASE + tipo + "/obtenerFotos.php";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Un momento..." );
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(!cancelarTodo) {
                    pDialog.dismiss();
                    procesar_respuesta(jsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(!cancelarTodo) {
                    pDialog.dismiss();
                    Toast.makeText(ActivityGaleria.this, getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();}
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void procesar_respuesta(JSONObject response){
        try {
            int estado = response.getInt("estado");
            if(estado == 1){
                Gson gson = new Gson();
                JSONArray array_fotos = response.getJSONArray("nom_fotos");
                RecyclerGaleriaItem[] res = gson.fromJson(array_fotos != null ? array_fotos.toString() : null, RecyclerGaleriaItem[].class);   // Parsear con Gson
                Collection<RecyclerGaleriaItem> items = Arrays.asList(res);
                data.addAll(items);
                adaptador.actualizarData(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    }

