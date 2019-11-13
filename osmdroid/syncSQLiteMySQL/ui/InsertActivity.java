package com.esy.jaha.osmdroid.syncSQLiteMySQL.ui;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esy.jaha.osmdroid.Adapter_iconos_locales;
import com.esy.jaha.osmdroid.Adapter_iconos_objetos;
import com.esy.jaha.osmdroid.Adapter_iconos_personas;
import com.esy.jaha.osmdroid.Adapter_iconos_simbolos;
import com.esy.jaha.osmdroid.Adapter_iconos_transportes;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.SessionManager;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.sync.SyncAdapter;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Utilidades;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Actividad de inserción
 */
public class InsertActivity extends AppCompatActivity{
    private EditText categoria;
    private ImageView imgv_ic;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        setToolbar();
        setTitle("Crear nueva categoría");
        GridView gridView1, gridView2, gridView3, gridView4, gridView5;

        TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("mitab1");
        spec.setContent(R.id.tab1_newCat);
        spec.setIndicator(null, getResources().getDrawable(R.drawable.obj_bolsa2));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2_newCat);
        spec.setIndicator(null, getResources().getDrawable(R.drawable.bar_restaurant));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab3");
        spec.setContent(R.id.tab3_newCat);
        spec.setIndicator("", getResources().getDrawable(R.drawable.per5_bicicleta));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab4");
        spec.setContent(R.id.tab4_newCat);
        spec.setIndicator(null, getResources().getDrawable(R.drawable.sim_award_symbol));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab5");
        spec.setContent(R.id.tab5_newCat);
        spec.setIndicator("", getResources().getDrawable(R.drawable.trans_bus2));
        tabs.addTab(spec);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imgv_ic = (ImageView)findViewById(R.id.imgvw_ic_cat) ;
        imgv_ic.setTag("icono");

        categoria = (EditText) findViewById(R.id.editText_nueva_categoria);

        Adapter_iconos_locales adaptador1 = new Adapter_iconos_locales(this);
        Adapter_iconos_objetos adaptador2 = new Adapter_iconos_objetos(this);
        Adapter_iconos_personas adaptador3 = new Adapter_iconos_personas(this);
        Adapter_iconos_simbolos adaptador4 = new Adapter_iconos_simbolos(this);
        Adapter_iconos_transportes adaptador5 = new Adapter_iconos_transportes(this);

        gridView1 = (GridView)findViewById(R.id.gridview1_newCat);
        gridView2 = (GridView)findViewById(R.id.gridview2_newCat);
        gridView3 = (GridView)findViewById(R.id.gridview3_newCat);
        gridView4 = (GridView)findViewById(R.id.gridview4_newCat);
        gridView5 = (GridView)findViewById(R.id.gridview5_newCat);

        gridView1.setAdapter(adaptador1);
        gridView2.setAdapter(adaptador2);
        gridView3.setAdapter(adaptador3);
        gridView4.setAdapter(adaptador4);
        gridView5.setAdapter(adaptador5);

        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l) {

                    String icono = (String) adapterView.getItemAtPosition(posicion);

                if(!icono.equals("none")){
                        try {
                            imgv_ic.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(icono + ".png")));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imgv_ic.setTag(icono);

                 }
            }
        });

        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l) {

                String icono = (String) adapterView.getItemAtPosition(posicion);
                try {
                    imgv_ic.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(icono + ".png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgv_ic.setTag(icono);

            }
        });

        gridView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l) {

                String icono = (String) adapterView.getItemAtPosition(posicion);
                try {
                    imgv_ic.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(icono + ".png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgv_ic.setTag(icono);

            }
        });

        gridView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l) {

                String icono = (String) adapterView.getItemAtPosition(posicion);
                try {
                    imgv_ic.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(icono + ".png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgv_ic.setTag(icono);

            }
        });

        gridView5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l) {

                String icono = (String) adapterView.getItemAtPosition(posicion);
                try {
                    imgv_ic.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(icono + ".png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgv_ic.setTag(icono);

            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_insert);
        setSupportActionBar(toolbar);

    }
    public void alClickearBoton(View v) {

        SessionManager sessionManager = SessionManager.getInstancia(this);
        String url = obtenerUrl();

        String cat = categoria.getText().toString().trim();
        String icono = (String)imgv_ic.getTag();
        int id_user = sessionManager.getUserId();
        String token = sessionManager.getToken();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("categoria", cat);
            jsonObject.put("icono", icono);
            jsonObject.put("id_user", id_user);
            jsonObject.put("token", token);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(!cat.isEmpty()) {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Actualizando Servidor, espere..." );
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
          MySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                  new JsonObjectRequest(
                          Request.Method.POST,
                          url,
                          jsonObject,
                          new Response.Listener<JSONObject>() {
                              @Override
                              public void onResponse(JSONObject response) {
                                pDialog.dismiss();
                                procesarRespuestaInsert(response);

                              }
                          },
                          new Response.ErrorListener() {
                              @Override
                              public void onErrorResponse(VolleyError error) {
                                  pDialog.dismiss();
                                  Toast.makeText(InsertActivity.this, getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                              }
                          }

                  ) {
                      @Override
                      public Map<String, String> getHeaders() {
                          Map<String, String> headers = new HashMap<String, String>();
                          headers.put("Content-Type", "application/json; charset=utf-8");
                          headers.put("Accept", "application/json");
                          return headers;
                      }

                      @Override
                      public String getBodyContentType() {
                          return "application/json; charset=utf-8" + getParamsEncoding();
                      }
                  }
          );
         /* ContentValues values = new ContentValues();
          values.put(ContratoProvider.Columnas.CATEGORIA, cat);
          values.put(ContratoProvider.Columnas.ID_ICONO, id_ic);
          values.put(ContratoProvider.Columnas.ID_USER, id_user);
          values.put(ContratoProvider.Columnas.PENDIENTE_INSERCION, 1);
         /// getContentResolver().insert(ContratoProvider.CONTENT_URI, values);
         // SyncAdapter.sincronizarAhora(this, true);*/

      }else{
            categoria.setError("Este campo es requerido");
            categoria.requestFocus();
        }


    }

private void procesarRespuestaInsert(JSONObject json){
    final int EXITO = 2;

    try {
        int estado = json.getInt("estado");
        switch (estado){
            case 1:
                synchronized (this){
                    SyncAdapter.sincronizarAhora(getApplicationContext(), false);
                }
                Toast.makeText(InsertActivity.this, "Operación exitosa", Toast.LENGTH_SHORT).show();
                setResult(EXITO);
                if (Utilidades.materialDesign())
                    finishAfterTransition();
                else finish();
                break;
            case 2:
                Toast.makeText(InsertActivity.this, "Operación fallida, Inténtelo de nuevo", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(InsertActivity.this, "Error de autenticación", Toast.LENGTH_SHORT).show();
                break;
        }

    } catch (JSONException e) {
        e.printStackTrace();
    }

}

    private String obtenerUrl(){

        String tipo = getIntent().getStringExtra("tipoElemento");
        String url = "";
        switch (tipo){

            case "local":
                url = Constantes.URL_BASE + "locales/insertar_categoria.php";
                break;
            case "evento":
                url = Constantes.URL_BASE + "eventos/insertar_categoria_evento.php";
                break;
            case "promo":
                url = Constantes.URL_BASE + "promos/insertar_categoria_promo.php";
                break;
            case "especial":
                url = Constantes.URL_BASE + "especiales/insertar_categoria_especial.php";
                break;

        }

        return url;
    }

}
