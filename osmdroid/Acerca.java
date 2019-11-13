package com.esy.jaha.osmdroid;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

public class Acerca extends AppCompatActivity {

    private boolean cancelarTodo;
    TextView txt_acercaDe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_acerca);
        setSupportActionBar(toolbar);
        setTitle("Información");
        txt_acercaDe = findViewById(R.id.tvAcercaDe);
        obtener_info();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cancelarTodo = false;
    }

    private void procesar_response(JSONObject response){
        String info;
        try {
            int estado = response.getInt("estado");
            if(estado == 1){
                info = response.getString("acercaDe");
                    txt_acercaDe.setText(info);
            }else{
                Toast.makeText(this, "No se ha obtenido respuesta del servidor", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtener_info(){

        String url = Constantes.URL_BASE + "acercaDe/acerca.php";
        final ProgressDialog progress;
        progress = ProgressDialog.show(this, "", "Obteniendo información...", false, true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(!cancelarTodo){
                    progress.dismiss();
                    procesar_response(jsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(!cancelarTodo){
                    progress.dismiss();
                  Toast.makeText(getBaseContext(), getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                }
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelarTodo = true;
    }
}
