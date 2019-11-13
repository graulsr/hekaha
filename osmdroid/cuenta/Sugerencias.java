package com.esy.jaha.osmdroid.cuenta;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class Sugerencias extends AppCompatActivity {

    EditText edtx_sugerencia;
    private boolean cancelarTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerencias);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sugerencias);
        setSupportActionBar(toolbar);
        setTitle("Sugerencia");
        edtx_sugerencia = (EditText)findViewById(R.id.edtx_sugerencia);
        Button btn_enviar = (Button) findViewById(R.id.btn_sugerencia);
        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviar_sugerencia();
            }
        });
    }


    private void enviar_sugerencia(){
        String sugerencia = edtx_sugerencia.getText().toString();
        if (sugerencia.isEmpty()){
            return;
        }
        final String LOGIN_URL = Constantes.URL_BASE + "usuarios/insertar_sugerencia.php";
        JSONObject json = new JSONObject();
        String token = SessionManager.getInstancia(this).getToken();
        int id_user = SessionManager.getInstancia(this).getUserId();
        final ProgressDialog dialog;
        try {
            json.put("id_user", id_user);
            json.put("token", token);
            json.put("sugerencia", sugerencia);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog = ProgressDialog.show(this, "Conectando con el servidor", "Por favor espere...", true, true);
        MySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        LOGIN_URL, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(!cancelarTodo) {
                                    dialog.dismiss();
                                    procesar_respuesta(response);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if(!cancelarTodo) {
                                    dialog.dismiss();
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                )
        );
    }

    private void procesar_respuesta(JSONObject response){
        try {
            int estado = response.getInt("estado");
            if(estado == 1){
                Toast.makeText(this, "La sugerencia ha sido envíada exitosamente.\nMuchas Gracias.", Toast.LENGTH_SHORT).show();
                edtx_sugerencia.setText("");
            }else{
                Toast.makeText(this, getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
}
