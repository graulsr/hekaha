package com.esy.jaha.osmdroid.cuenta;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.SessionManager;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

public class Informacion extends AppCompatActivity {

    TextView txt_info;
    Button reintentar;
    private boolean cancelarTodo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_info);
        setSupportActionBar(toolbar);
        setTitle("Información");
        txt_info = (TextView) findViewById(R.id.txt_info);
        reintentar = (Button) findViewById(R.id.btn_reintentar);
    }


    @Override
    protected void onStart() {
        super.onStart();
        cancelarTodo = false;
        obtener_info();
    }

    private void procesar_response(JSONObject response){
        String info;
        try {
            int estado = response.getInt("estado");
            if(estado == 1){
                info = response.getString("info");
                if(!info.equals("")){
                    txt_info.setText(info);
                }else{
                    txt_info.setText("No hay información nueva");
                }
            }else{
                txt_info.setText("No hay información nueva");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtener_info(){

        String url = Constantes.URL_BASE +"usuarios/obtener_info.php";
        JSONObject jsonObject = new JSONObject();
        int id_user = SessionManager.getInstancia(this).getUserId();
        String token = SessionManager.getInstancia(this).getToken();
        try {
            jsonObject.put("id_user", id_user);
            jsonObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progress;
        progress = ProgressDialog.show(this, "", "Obteniendo información...", false, true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(!cancelarTodo){
                   progress.dismiss();
                    if(reintentar.isShown()){
                        reintentar.setVisibility(View.GONE);
                    }
                    procesar_response(jsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(!cancelarTodo){
                    progress.dismiss();
                    txt_info.setText(getResources().getString(R.string.error_ocurrido));
//                    Toast.makeText(getActivity(), getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                    if(!reintentar.isShown()) {
                        reintentar.setVisibility(View.VISIBLE);
                        reintentar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                obtener_info();
                            }
                        });
                    }
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
