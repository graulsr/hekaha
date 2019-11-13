package com.esy.jaha.osmdroid.cuenta;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.SessionManager;
import com.esy.jaha.osmdroid.nuevo.ImageAdapterGridview1;
import com.esy.jaha.osmdroid.nuevo.ImageAdapterGridview2;
import com.esy.jaha.osmdroid.nuevo.ImageAdapterGridview3;
import com.esy.jaha.osmdroid.nuevo.ImageAdapterGridview4;
import com.esy.jaha.osmdroid.nuevo.ImageAdapterGridview5;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MarkerActivity extends AppCompatActivity {
    String marcadorNuevo;
    String marcador;
    LinearLayout layout;
    private boolean cancelarTodo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("Cambiar Marcador");
        marcador = getIntent().getStringExtra("marcador");
        marcadorNuevo = marcador;
        layout = findViewById(R.id.layoutMarkers);
        final String tipo = getIntent().getStringExtra("tipo");
        final ImageView imageView = findViewById(R.id.iv_marker);
        try {
            imageView.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(marcador + ".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GridView gridView1 = (GridView) findViewById(R.id.gridview1_markers);
        GridView gridView2 = (GridView) findViewById(R.id.gridview2_markers);
        GridView gridView3 = (GridView) findViewById(R.id.gridview3_markers);
        GridView gridView4 = (GridView) findViewById(R.id.gridview4_markers);
        GridView gridView5 = (GridView) findViewById(R.id.gridview5_markers);
        TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
        tabs.setup();
        TabHost.TabSpec spec = tabs.newTabSpec("mitab1");
        spec.setContent(R.id.tab1_markers);
        spec.setIndicator(null, getResources().getDrawable(R.drawable.restaurant_marker));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2_markers);
        spec.setIndicator(null, getResources().getDrawable(R.drawable.mk_pers_male));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab3");
        spec.setContent(R.id.tab3_markers);
        spec.setIndicator("", getResources().getDrawable(R.drawable.cons_home));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab4");
        spec.setContent(R.id.tab4_markers);
        spec.setIndicator(null, getResources().getDrawable(R.drawable.work_industry));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab5");
        spec.setContent(R.id.tab5_markers);
        spec.setIndicator("", getResources().getDrawable(R.drawable.veh_car));
        tabs.addTab(spec);

        gridView1.setAdapter(new ImageAdapterGridview1(this));
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    marcadorNuevo = (String)parent.getItemAtPosition(position) ;

                try {
                    imageView.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(marcadorNuevo + ".png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        gridView2.setAdapter(new ImageAdapterGridview2(this));
        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                marcadorNuevo = (String)parent.getItemAtPosition(position) ;

                try {
                    imageView.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(marcadorNuevo + ".png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        gridView3.setAdapter(new ImageAdapterGridview3(this));
        gridView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                marcadorNuevo = (String)parent.getItemAtPosition(position) ;

                try {
                    imageView.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(marcadorNuevo + ".png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        gridView4.setAdapter(new ImageAdapterGridview4(this));
        gridView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                marcadorNuevo = (String)parent.getItemAtPosition(position) ;
                try {
                    imageView.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(marcadorNuevo + ".png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        gridView5.setAdapter(new ImageAdapterGridview5(this));
        gridView5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                marcadorNuevo = (String)parent.getItemAtPosition(position) ;
                try {
                    imageView.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(marcadorNuevo + ".png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        Button button = findViewById(R.id.btn_markers);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marcadorNuevo.equals(marcador)) {
                    finish();
                } else {
                    JSONObject jsonObject = new JSONObject();
                    int id_user = SessionManager.getInstancia(getBaseContext()).getUserId();
                    String token = SessionManager.getInstancia(getBaseContext()).getToken();
                    int id = getIntent().getIntExtra("id", 0);
                    try {
                        jsonObject.put("id", id);
                        jsonObject.put("id_user", id_user);
                        jsonObject.put("token", token);
                        jsonObject.put("marcador", marcadorNuevo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String url = Constantes.URL_BASE + tipo + "/cambiarMarker.php";

                    final Snackbar snackbar = Snackbar.make(layout, "Un momento...", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (!cancelarTodo) {
                                snackbar.dismiss();
                                procesarRespuesta(response);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (!cancelarTodo) {
                                snackbar.dismiss();
                                Toast.makeText(MarkerActivity.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                }
            }
            });
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

    private void procesarRespuesta(JSONObject respuesta){

        try {
            int estado = respuesta.getInt("estado");
            if (estado == 1){
                final int CAMBIO_MARCADOR = 13;
                Intent intent = new Intent();
                intent.putExtra("marcador_nuevo", marcadorNuevo);
                setResult(CAMBIO_MARCADOR, intent);

                Snackbar.make(layout, "El marcador se ha cambiado exitosamente", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Cerrar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();

            }else{
                Toast.makeText(this, "Ha ocurrido un error. Inténtelo de nuevo", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
