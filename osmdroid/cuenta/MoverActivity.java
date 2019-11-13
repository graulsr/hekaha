package com.esy.jaha.osmdroid.cuenta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.SessionManager;
import com.esy.jaha.osmdroid.constants.OpenStreetMapConstants;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.overlays.MarkerLabeled;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.io.IOException;

public class MoverActivity extends AppCompatActivity implements OpenStreetMapConstants{
    MarkerLabeled markerLabeled;
    int id;
    String tipo;
    CoordinatorLayout coordinatorLayout;
    MapView mapView;
    private double latAnterior, longAnterior;
    private boolean cancelarTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mover);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Cambiar ubicación");
        coordinatorLayout = findViewById(R.id.coordinator_mover);
        final Intent intent = getIntent();
        SharedPreferences mPrefs =  getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        mapView = findViewById(R.id.mapViewMover);
        mapView.setTilesScaledToDpi(false);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.setMinZoomLevel(4.0);
        latAnterior = intent.getDoubleExtra("latitud", 0);
        longAnterior = intent.getDoubleExtra("longitud", 0);
        mapView.getController().setCenter(new GeoPoint(latAnterior, longAnterior));
        mapView.getController().setZoom(mPrefs.getFloat(PREFS_ZOOM_LEVEL, 10.0f));

        id = intent.getIntExtra("id", 0);
        tipo = intent.getStringExtra("tipo");
        String marcador = intent.getStringExtra("marcador");
        Bitmap iconoMarker = null;
        try {
            iconoMarker = BitmapFactory.decodeStream(this.getAssets().open(marcador + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        markerLabeled = new MarkerLabeled(mapView);
        markerLabeled.setIcon(iconoMarker);
        markerLabeled.setLabelFontSize(0);
        markerLabeled.setDraggable(true);
        markerLabeled.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                markerLabeled.showInfoWindow();
            }

            @Override
            public void onMarkerDragStart(Marker marker) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);
            }
        });
        InfoWindow infoWindow = new InfoWindow(R.layout.mi_bonuspack_bubble, mapView) {
            @Override
            public void onOpen(Object item) {
                LinearLayout layout = (LinearLayout) mView.findViewById(R.id.mi_bubble_layout);
                ImageView imagenView = (ImageView) mView.findViewById(R.id.bubble_image);
                imagenView.setVisibility(View.GONE);
                TextView txtTitle = (TextView) mView.findViewById(R.id.bubble_title);
                TextView txtDescription = (TextView) mView.findViewById(R.id.bubble_description);
                txtTitle.setText("Cambiar ubicación:");
                txtDescription.setText("Mantenga pulsado el marker para \narrastrarlo a la " +
                        "posición deseada.\nLuego toque esta ventana para confirmar");
                layout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        intentarMover();
                    }
                });
            }

            @Override
            public void onClose() {

            }
        };
        markerLabeled.setInfoWindow(infoWindow);
        markerLabeled.setPosition(new GeoPoint(latAnterior, longAnterior));
        markerLabeled.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(markerLabeled);
        markerLabeled.showInfoWindow();
    }

    private void intentarMover(){
        double latitud = markerLabeled.getPosition().getLatitude();
        double longitud = markerLabeled.getPosition().getLongitude();

        if (!(latitud == latAnterior && longitud == longAnterior)) {

            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Actualizando...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
            JSONObject jsonObject = new JSONObject();

            try {
                SessionManager sessionManager = SessionManager.getInstancia(getApplicationContext());
                int id_user = sessionManager.getUserId();
                String token = sessionManager.getToken();
                jsonObject.put("id_user", id_user);
                jsonObject.put("token", token);
                jsonObject.put("id", id);
                jsonObject.put("latitud", latitud);
                jsonObject.put("longitud", longitud);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url_mover = Constantes.URL_BASE + tipo + "/cambiarUbicacion.php";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_mover, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    pDialog.dismiss();
                    if (!cancelarTodo) {
                        procesarResponse(jsonObject);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    pDialog.dismiss();
                    if (!cancelarTodo) {
                        Toast.makeText(MoverActivity.this, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        }
    }

    private void procesarResponse(JSONObject jsonObject){
        try {
            if(jsonObject.getInt("estado") == 1){
                final int CAMBIO_UBICACION = 17;
                setResult(CAMBIO_UBICACION);
                Snackbar.make(coordinatorLayout, "Se ha movido correctamente", Snackbar.LENGTH_INDEFINITE )
                        .setAction("Aceptar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();

            }else {
                Snackbar.make(coordinatorLayout, "No se ha podido actualizar la nueva ubicación", Snackbar.LENGTH_INDEFINITE )
                        .setAction("Reintentar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                intentarMover();
                            }
                        }).show();
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
