package com.esy.jaha.osmdroid.cuenta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.esy.jaha.osmdroid.DrawerItem;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.SessionManager;
import com.esy.jaha.osmdroid.constants.OpenStreetMapConstants;
import com.esy.jaha.osmdroid.nuevo.ActivityPaginaEditable;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.overlays.MarkerLabeled;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActivityCuenta extends AppCompatActivity
        implements OpenStreetMapConstants, CardFragmentCuenta.OnFragmentCardCuentaListener {

    List<DrawerItem> dataList;
    private int id_user;
    boolean fragmentCardIniciada;
    private String token;
    static MapView mMapView;
    CardFragmentCuenta fragmentCard;
    RelativeLayout relativeLayout;
    static String tipo_actual;
    FragmentManager fm;
    private CharSequence mTitle;
    CuentaDrawerAdapter adapter;
    ListView mDrawerList;
    private int seleccion_actual;
    private boolean cancelarTodo = false;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    static JSONArray mislocales;
    SessionManager sessionManager;
    private float densidad;
    final int MAP_FRAGMENT = 0;
    final int CARD_FRAGMENT = 1;
    int currentFragment;


    private final String MIS_LOCALES = "Mis Locales", MIS_EVENTOS = "Mis Eventos" , MIS_PROMOS = "Mis Ofertas", ESPECIALES = "Especiales";
    private final String TAG_CARD_FRAGM_CUETA = "fragCardCuenta";
    private final int FUE_EDITADO = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_cuenta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cuenta);
        setSupportActionBar(toolbar);
        mislocales = new JSONArray();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_cuenta);
         toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };

        drawer.addDrawerListener(toggle);
        sessionManager = SessionManager.getInstancia(this);

        dataList = new ArrayList<DrawerItem>();
        mTitle = getTitle();
        mDrawerList = (ListView) findViewById(R.id.drawerList_cuenta);


        dataList.add(new DrawerItem(sessionManager.getUserName(), sessionManager.getEmail()));   //adding image header to
        dataList.add(new DrawerItem("Elementos"));

        adapter = new CuentaDrawerAdapter(this, R.layout.custom_drawer_item, dataList);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        id_user = sessionManager.getUserId();
        token = sessionManager.getToken();
        mMapView = (MapView) findViewById(R.id.map_cuenta);
        assert mMapView != null;
        mMapView.setMultiTouchControls(true);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMinZoomLevel(4.0);
        fm = getSupportFragmentManager();
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_for_snack);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        densidad = dm.density;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_cuenta);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == CARD_FRAGMENT) {
                currentFragment = MAP_FRAGMENT;
                invalidateOptionsMenu();
            }
              super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(menu != null){
            menu.clear();
        }

        if (currentFragment == CARD_FRAGMENT) {

                getMenuInflater().inflate(R.menu.mn_cuenta_card2, menu);

        }else {
            getMenuInflater().inflate(R.menu.mn_cuenta, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

            if (id == R.id.cerrar_sesion) {

                cerrarSesion();

            } else if (id == R.id.modo_cards) {

                if (fm.findFragmentByTag(TAG_CARD_FRAGM_CUETA) == null) {
                    //Toast.makeText(getApplicationContext(), "iniciando fragmentCard", Toast.LENGTH_SHORT).show();
                    fragmentCard = CardFragmentCuenta.newInstance("param1", "param2");
                    fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(TAG_CARD_FRAGM_CUETA).add(R.id.frame_cuenta, fragmentCard, TAG_CARD_FRAGM_CUETA).commit();
                    fragmentCardIniciada = true;
                }
                currentFragment = Constantes.CARD_FRAGMENT;
                invalidateOptionsMenu();
            }

            if (id == R.id.info) {

                startActivity(new Intent(this, Informacion.class));

            } else if (id == R.id.sugerencia) {

                startActivity(new Intent(this, Sugerencias.class));
            }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "on destroy", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onResume() {
        super.onResume(); // Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mMapView.getController().setCenter(GeoPoint.fromDoubleString(mPrefs.getString(PREFS_MAP_CENTER, "-25.388400000000,-57.136800000000"), ','));
        mMapView.getController().setZoom(mPrefs.getFloat(PREFS_ZOOM_LEVEL, 5.0f));
        mMapView.invalidate();
        cancelarTodo = false;
    }

    public void onPause() {
        super.onPause();// Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
            GeoPoint geoPoint = (GeoPoint) mMapView.getMapCenter();     //mMapView.getProjection().fromPixels(mMapView.getWidth() / 2, mMapView.getHeight() / 2, punto_cero);
            final SharedPreferences.Editor edit = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            edit.putString(PREFS_MAP_CENTER, String.valueOf(geoPoint.getLatitude() + "," + String.valueOf(geoPoint.getLongitude())));
            edit.putFloat(PREFS_ZOOM_LEVEL, (float) mMapView.getZoomLevelDouble());
            edit.apply();
        cancelarTodo = true;
        sessionManager.setCuentaDrawerItemSelected(seleccion_actual);
    }


    private void cerrarSesion(){

        String url = Constantes.URL_BASE + "usuarios/cerrar_sesion.php";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_user", id_user);
            jsonObject.put("token", token);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progress;
        progress = ProgressDialog.show(this, "Conectando con el servidor", "Por favor espere...", true, true);

        MySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(!cancelarTodo) {
                            progress.dismiss();
                            procesar_respuesta(response);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(!cancelarTodo) {
                            progress.dismiss();
                            Toast.makeText(ActivityCuenta.this, getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    private void procesar_respuesta(JSONObject respuesta){
        try {
            int estado = respuesta.getInt("estado");
            switch (estado){
                case 1:
                    Toast.makeText(ActivityCuenta.this, "Se ha cerrado sesión correctamente", Toast.LENGTH_SHORT).show();
                    SessionManager sesionManager = SessionManager.getInstancia(this);
                    sesionManager.setUserEmail("Correo electrónico");
                    sesionManager.setUserName("Nombre de usuario");
                    sesionManager.setLogin(false);
                    finish();
                    break;
                case 2:
                    Snackbar.make(relativeLayout, "Ha ocurrido un error", Snackbar.LENGTH_LONG);
                    break;
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtenerMarkers(final String tipo){
        mMapView.removeAllViews();
        mMapView.getOverlays().clear();
        mislocales = null;
        if (fragmentCardIniciada) { //&& MainActivity.currentFragment == MainActivity.CARD_FRAGMENT

                RecyclerCardCuentaAdaptador.data = null;
                CardFragmentCuenta.adaptador.notifyDataSetChanged();
        }

        String url = Constantes.URL_BASE  + tipo + "/obtener_" + tipo + "_PorUsuario.php";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_user", id_user);
            jsonObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Snackbar snackbar = Snackbar.make(relativeLayout, "Un momento...", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                snackbar.dismiss();
                if(tipo_actual.equals(tipo) && !cancelarTodo) {
                    procesarMisLocales(jsonObject, tipo);
                }
            }
         }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                snackbar.dismiss();
                if(tipo_actual.equals(tipo) && !cancelarTodo) {
                    Toast.makeText(ActivityCuenta.this, getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                }
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == FUE_EDITADO){
            obtenerMarkers(data.getStringExtra("tipo"));
        }
    }

    private void procesarMisLocales(JSONObject json, final String tipo){

     int estado = 0;
        try {
            estado = json.getInt("estado");
            if(estado == 1) {

                mislocales = json.getJSONArray("mis_" + tipo);
                    for (int i = 0; i < mislocales.length(); i++) {
                        final int id = mislocales.getJSONObject(i).getInt("id");
                        final double latitud = mislocales.getJSONObject(i).getDouble("latitud");
                        final double longitud = mislocales.getJSONObject(i).getDouble("longitud");
                        final String marcador = mislocales.getJSONObject(i).getString("marcador");
                        final String icono = mislocales.getJSONObject(i).getString("icono");

                        final String nombre = mislocales.getJSONObject(i).getString("nombre");
                        final String descripcion = mislocales.getJSONObject(i).getString("descripcion");
                        final String foto_portada = mislocales.getJSONObject(i).getString("foto_portada");
                        final MarkerLabeled marker = new MarkerLabeled(mMapView);
                        Bitmap markerBitmap = null;
                        Bitmap iconoBitmap = null;
                        try {
                            markerBitmap = BitmapFactory.decodeStream(this.getAssets().open(marcador + ".png"));
                            Matrix matrix = new Matrix();
                            double zoomLevel = mMapView.getZoomLevelDouble();
                            if (densidad > 1) {
                                if (zoomLevel > 16) {
                                    matrix.postScale(1.6f, 1.6f);
                                } else if (zoomLevel > 12) {
                                    matrix.postScale(1.4f, 1.4f);
                                }else {
                                    matrix.postScale(1.2f, 1.2f);
                                }
                            }else {
                                matrix.postScale(1.2f, 1.2f);
                            }
                            markerBitmap = Bitmap.createBitmap(markerBitmap, 0, 0, 32, 37, matrix, false);

                            if (!icono.contains(".png") && !icono.contains("null")) {
                                    iconoBitmap = BitmapFactory.decodeStream(getAssets().open(icono + ".png"));
                                }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        marker.setIcon(markerBitmap);
                        marker.setLabelFontSize(0);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        marker.setPosition(new GeoPoint(latitud, longitud));
                        //  final Bitmap icono64 = BitmapFactory.decodeResource(getResources(), id_icono);
                        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker, MapView mapView) {

                                if (marker.isInfoWindowOpen()) {
                                    marker.closeInfoWindow();

                                } else {
                                    marker.showInfoWindow();
                                }
                                return false;
                            }
                        });

                        final Bitmap finalIconoBitmap = iconoBitmap;
                        final String finalIcono = icono;
                        InfoWindow infoWindow = new InfoWindow(R.layout.mi_bonuspack_bubble, mMapView) {

                            @Override
                            public void onOpen(Object item) { //aqui item es un MarkerLabeled

                                LinearLayout layout = (LinearLayout) mView.findViewById(R.id.mi_bubble_layout);

                                ImageView imagenView = (ImageView) mView.findViewById(R.id.bubble_image);
//                                Button btnMoreInfo = (Button) mView.findViewById(R.id.bubble_moreinfo);
                                TextView txtTitle = (TextView) mView.findViewById(R.id.bubble_title);
                                TextView txtDescription = (TextView) mView.findViewById(R.id.bubble_description);

                                if (finalIconoBitmap != null) {
                                    imagenView.setImageBitmap(finalIconoBitmap);

                                } else {
                                    if (icono.equals("null")){
                                        try {
                                            imagenView.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("icono.png")));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }else {
                                        imagenView.setVisibility(View.GONE);
                                        NetworkImageView networkImageView = (NetworkImageView) mView.findViewById(R.id.bubble_netImage);
                                        networkImageView.setDefaultImageResId(R.drawable.logo_24x26);
                                        networkImageView.setErrorImageResId(R.drawable.logo_24x26);
                                        networkImageView.setVisibility(View.VISIBLE);
                                        networkImageView.setImageUrl(Constantes.URL_IMAGENES + tipo + "/" + id + "/logo/" + finalIcono, MySingleton.getInstance(getBaseContext()).getImageLoader());
                                    }
                                }
                                txtTitle.setText(nombre);
                                if (!descripcion.isEmpty()) {
                                    txtDescription.setText(descripcion);
                                } else {
                                    txtDescription.setVisibility(View.GONE);
                                }

                                layout.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), ActivityPaginaEditable.class);
                                        intent.putExtra("id", id);
                                        intent.putExtra("latitud", latitud);
                                        intent.putExtra("longitud", longitud);
                                        intent.putExtra("marcador", marcador);
                                        intent.putExtra("icono", finalIcono);
                                        intent.putExtra("nombre", nombre);
                                        intent.putExtra("descripcion", descripcion);
                                        intent.putExtra("tipo", tipo);
                                        intent.putExtra("foto_portada", foto_portada);
                                        startActivityForResult(intent, FUE_EDITADO);
                                    }
                                });

                            }

                            @Override
                            public void onClose() {

                            }
                        };
//                        infoWindow.getView().setTag(marker);
                        marker.setInfoWindow(infoWindow);
                        marker.setInfoWindowAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
                        marker.setRelatedObject(id);
                        mMapView.getOverlays().add(marker);
                        mMapView.invalidate();
                    }

                if (fragmentCardIniciada) {
                    CardFragmentCuenta.adaptador.actualizarDataset();
                }
            }else{
                Toast.makeText(this, "No tiene elementos en esta categoría", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }


    @Override
    public void onFragmentCardInteraction(double latitud, double longitud, int id) {

        List<Overlay> markerLabeleds =  mMapView.getOverlays();

        for (Overlay m: markerLabeleds){
            if (m instanceof MarkerLabeled) {
                MarkerLabeled markerLabeled = (MarkerLabeled) m;
                if (!markerLabeled.getRelatedObject().equals(id)) {

                    markerLabeled.closeInfoWindow();
                } else {
                    markerLabeled.showInfoWindow();
                }
            }
        }
        mMapView.getController().animateTo(new GeoPoint(latitud, longitud));

        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position != 0 && !CuentaDrawerAdapter.drawerItemList.get(position).isTitle){
                selectItem(position);
            }
        }
    }

    public void selectItem(int possition) {
 /*       if (procesandoNuevo) {
            mMapView.removeViewAt(mMapView.getChildCount() - 1);
            mMapView.getOverlays().remove(mark);
            countBackPressed = 0;
            procesandoNuevo = false;

        }*/
            mDrawerList.setItemChecked(possition, true);
            seleccion_actual = possition;
            String itemName = CuentaDrawerAdapter.drawerItemList.get(possition).getItemName();
            setTitle(itemName);

            switch (itemName){
                case MIS_LOCALES:

                    tipo_actual = "locales";
                    obtenerMarkers(tipo_actual);
                    break;

                case MIS_EVENTOS:

                    tipo_actual = "eventos";
                    obtenerMarkers(tipo_actual);
                    break;

                case MIS_PROMOS:

                    tipo_actual = "promos";
                    obtenerMarkers(tipo_actual);
                    break;

                default:
                    if (!sessionManager.getSuperCAt4Name().equals("")) {
                        tipo_actual = "especiales";
                        obtenerMarkers(tipo_actual);
                    }
                    break;
        }

        drawer.closeDrawer(mDrawerList);

    }
}




