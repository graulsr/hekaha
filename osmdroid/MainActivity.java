package com.esy.jaha.osmdroid;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
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
import com.esy.jaha.osmdroid.constants.OpenStreetMapConstants;
import com.esy.jaha.osmdroid.cuenta.ActivityCuenta;
import com.esy.jaha.osmdroid.nuevo.Iconos_fragment;
import com.esy.jaha.osmdroid.nuevo.NuevaPromo;
import com.esy.jaha.osmdroid.nuevo.NuevoEspecial;
import com.esy.jaha.osmdroid.nuevo.NuevoEvento;
import com.esy.jaha.osmdroid.nuevo.NuevoLocal;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.sync.SyncAdapter;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Utilidades;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.overlays.MarkerLabeled;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.esy.jaha.osmdroid.MapFragment.id_categoria;
import static com.esy.jaha.osmdroid.MapFragment.latNor;
import static com.esy.jaha.osmdroid.MapFragment.latSur;
import static com.esy.jaha.osmdroid.MapFragment.localesBoxJSON;
import static com.esy.jaha.osmdroid.MapFragment.lonEst;
import static com.esy.jaha.osmdroid.MapFragment.lonOes;
import static com.esy.jaha.osmdroid.MapFragment.mMapView;
import static com.esy.jaha.osmdroid.MapFragment.obtenerZoomMinimo;
import static com.esy.jaha.osmdroid.MapFragment.zoomLevel;

public class MainActivity extends AppCompatActivity implements OpenStreetMapConstants, Iconos_fragment.OnFragmentInteractionListener, CardFragment.OnFragmentInteraction {

    private static final String MAP_FRAGMENT_TAG = "MAP_FRAGMENT_TAG";
    private static final String FRAGMENT_ICONOS = "FRAGMENT_ICONOS";
    private static final String FRAGMENT_CARD = "FRAGMENT_CARD";
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 11;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 22;
    FragmentManager fm;
    private SharedPreferences mPrefs;
    private boolean sesion_iniciada;
    MapFragment mapFragment;
    CardFragment fragmentCard;
    MarkerLabeled mark;
    Iconos_fragment fragment;
    private DrawerLayout mDrawerLayout;
    static ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private int currentFragment;
    public static int openedFragmentCard;
    private boolean primeraVez;
    private CharSequence
            mTitle;
    public static CustomDrawerAdapter adapter;
    List<DrawerItem> dataList;
    AlertDialog alert;
    public static SQLiteDatabase db;
    SessionManager sesionManager;
    private SearchView searchView;
    private Context context;
    static boolean procesandoNuevo;
    final int MAP_FRAGMENT = 0;
    final int CARD_FRAGMENT = 1;
    final int ICONOS_FRAGMENT = 2;
    private RelativeLayout contenedor;
    static boolean procesando_busqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);// Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
        Toolbar toolbar = findViewById(R.id.toolbar_mainActivity);
        setSupportActionBar (toolbar);
        contenedor = (RelativeLayout) findViewById(R.id.contenedor_mapa);
        fm = this.getSupportFragmentManager();
/*
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchManager.setOnDismissListener(new SearchManager.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });*/


//        handleIntent(getIntent());  // Assumes current activity is the searchable activity
        dataList = new ArrayList<DrawerItem>();
        mTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        mDrawerList = (ListView) findViewById(R.id.nav_view);
        sesionManager = SessionManager.getInstancia(this);
        primeraVez = sesionManager.isFirstTime();
        long lastSync = sesionManager.getUltimaSicronizacion();

        db = BdSingleton.getInstance(getApplicationContext()).getSqLiteBD().getReadableDatabase();

        boolean sicronizar = (System.currentTimeMillis() - lastSync) > 259200000; //mayor a tres dias
        if (sicronizar || primeraVez) {
            SyncAdapter.sincronizarAhora(this, false);
            sesionManager.setUltimaSicronizacion(System.currentTimeMillis());
        }

        // ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
//                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {

//                getSupportActionBar().setTitle(mDrawerTitle);
//                invalidateOptionsMenu(); // creates call to
//                // onPrepareOptionsMenu()
            }
        };


        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mPrefs = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        dataList.add(new DrawerItem(sesionManager.getUserName(), sesionManager.getEmail()));   //adding image header to
        dataList.add(new DrawerItem(true));  // adding a spinner to the list
        dataList.add(new DrawerItem("Categorías"));    // adding a header to the list

        adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, dataList);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        context = this.getApplicationContext();
        sesion_iniciada = sesionManager.isLoggedIn();

    }// fin onCreate

    @Override
    public void onRequestPermissionsResult(int requestCode, final String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was grantedm
//                  mostrarMapa();
                    mMapView.invalidate();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (!primeraVez) {
                        Snackbar.make(contenedor, "Debe conceder el permiso de almacenamiento para cargar el mapa", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Permisos", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        goToSettings(); // o solicitar permiso de nuevo
//                                    solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                    }
                                }).show();
                    }
                }
            break;

            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    mostrar_ubicacion();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(contenedor, "Debe conceder el permiso de Ubicación para ver su posición", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Permisos", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToSettings(); // o solicitar permiso de nuevo
//                                    solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                }
                            }).show();
                }
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(procesandoNuevo){
            if (mMapView != null){
                mMapView.removeAllViews();
                List<Overlay> list = mMapView.getOverlays();
                for (Overlay o:list){
                    if (! (o instanceof MyLocationNewOverlay)){
                        list.remove(o);
                    }
                }
                invalidateOptionsMenu();
                procesandoNuevo = false;
            }
        }
        if (sesion_iniciada != sesionManager.isLoggedIn())//cambio de usuario
        {
            CustomDrawerAdapter.drawerItemList.remove(0);
            CustomDrawerAdapter.drawerItemList.add(0, new DrawerItem(sesionManager.getUserName(), sesionManager.getEmail()));
            MainActivity.adapter.notifyDataSetChanged();
            sesion_iniciada = sesionManager.isLoggedIn();
        }
        if (sesionManager.isNewAdded()){
            id_categoria = sesionManager.getIdCatOfNewAdded();
            CustomDrawerAdapter.currentSelected = sesionManager.getTipeOfNewAdded();
            mapFragment.getMarkersByBox(this, true, false);
            sesionManager.setIsNewAddedToFalse();
            setTitle("Hekahá");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mostrarMapa();
        if (!Utilidades.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)) {
            solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }
/*
    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();

        appData.putBoolean("extraData", true);
        startSearch(null, false, appData, false);
        return true;
    }
*/

     void handleIntent(Intent intent) {                                                                                                              //---------
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
//            searchView.setQuery(query, false);
            setTitle(query);
            if (query != null) {
                searchView.setQuery(query, true);
            }
        }
    }

    int countBackPressed = 0;
    @Override
    public void onBackPressed() {
        boolean cerrar = true;

        if (procesandoNuevo && countBackPressed == 1) {
//
            mMapView.removeAllViews();
            mMapView.getOverlays().remove(mark);
            procesandoNuevo = false;
            countBackPressed = 0;
            cerrar = false;
            invalidateOptionsMenu();
        }
        countBackPressed++;
/*
        if(countBackPressed >= 2){
            mMapView.removeViewAt(mMapView.getChildCount() - 1);
            mMapView.getOverlays().remove(mark);
            procesando_nuevo = false;
            countBackPressed = 0;
            cerrar = false;
            procesandoNuevo = false;
            invalidateOptionsMenu();
        }*/
/*        if (procesando_busqueda){
            cerrar = true;
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == CARD_FRAGMENT) {
                
                currentFragment = MAP_FRAGMENT;
                invalidateOptionsMenu();
            }else {
                if (currentFragment == ICONOS_FRAGMENT) {
                    currentFragment = MAP_FRAGMENT;
                    invalidateOptionsMenu();
                }
            }
            if(cerrar) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {

          if(menu != null){
            menu.clear();
        }

        if (currentFragment == MAP_FRAGMENT) {

            assert menu != null;


            if (!sesionManager.getSuperCAt4Name().equals("")) {

                menu.add(Menu.NONE, Constantes.NUEVO_LOC_ESP, 4, sesionManager.getSuperCAt4Name());

            }
            getMenuInflater().inflate(R.menu.main, menu);
            menu.add(Menu.NONE, Constantes.ACERCA_DE, 5, "Información");
            menu.add(Menu.NONE, Constantes.SINCRONIZAR, 6, "Sincronizar");

        } else {

           if (currentFragment == CARD_FRAGMENT) {
                getMenuInflater().inflate(R.menu.menu_card, menu);

               final MenuItem searchItem = menu.findItem(R.id.buscar);
//               searchItem.setTitle("Buscar " + obtenerTipo());
               searchView = (SearchView) searchItem.getActionView();

                   SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//                   searchView = (SearchView) menu.findItem(R.id.buscar).getActionView();
                   // Assumes current activity is the searchable activity
                   searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                   searchView.setIconifiedByDefault(true); //
                   searchView.setQueryRefinementEnabled(true);
                   searchView.setSubmitButtonEnabled(true);
                   searchView.setQueryHint("Buscar " + obtenerTipo());
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                   @Override
                   public boolean onQueryTextSubmit(String query) {
//                       Toast.makeText(MainActivity.this, "onQueryTextSubmit", Toast.LENGTH_SHORT).show();
                       searchView.clearFocus();
                       procesando_busqueda = true;
                       setTitle(query);
                       JSONObject json_request = new JSONObject();
                       if (zoomLevel < obtenerZoomMinimo()){
                           Toast.makeText(context, "Enfoque y aumente el zoom del mapa en la zona de su interés para realizar la búsqueda", Toast.LENGTH_LONG).show();
                           return true;
                       }
                       try {
                           json_request.put("palabras_clave", query);
                           json_request.put("latNort", latNor);
                           json_request.put("latSur", latSur);
                           json_request.put("longEst", lonEst);
                           json_request.put("longOest", lonOes);
                           if(MapFragment.tipo.equals("eventos")){
                               json_request.put("fecha", MapFragment.obtener_fecha());
                           }

                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                       final String BUSQUEDA_URL = obtenerUrl();
                       MySingleton.getInstance(context).addToRequestQueue(
                               new JsonObjectRequest(
                                       Request.Method.POST,
                                       BUSQUEDA_URL, json_request,
                                               new Response.Listener<JSONObject>() {
                                                   @Override
                                                   public void onResponse(JSONObject response) {
                                               mMapView.removeAllViews();
                                               List<Overlay> list = mMapView.getOverlays();
                                               for (Overlay o:list){
                                                   if (! (o instanceof MyLocationNewOverlay)){
                                                       list.remove(o);
                                                   }
                                               }
                                               localesBoxJSON = null;
                                               CardFragment.adaptador.actualizarDataset();
                                               MapFragment.procesar_respuesta(response, true, false);
                                           }
                                       },
                                       new Response.ErrorListener() {
                                           @Override
                                           public void onErrorResponse(VolleyError error) {
                                               Toast.makeText(context, "No se puede conectar con el servidor", Toast.LENGTH_SHORT).show();
                                           }
                                       }
                               )
                       );
                       //doMySearch(query);
                       SearchRecentSuggestions suggestions = new SearchRecentSuggestions(context, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
                       suggestions.saveRecentQuery(query, null);

                       return true;
                   }

                   @Override
                   public boolean onQueryTextChange(String newText) {
                    //   Toast.makeText(MainActivity.this, "onQueryTextChange", Toast.LENGTH_SHORT).show();

                       return false;

                   }
               });

                searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                   @Override
                   public boolean onMenuItemActionExpand(MenuItem item) {
//                       Toast.makeText(MainActivity.this, "onMenuItemActionExpand", Toast.LENGTH_SHORT).show();
                       menu.removeItem(R.id.eliminarHistorial);
                       searchView.clearFocus();
                       return true;
                   }

                   @Override
                   public boolean onMenuItemActionCollapse(MenuItem item) {
//                       Toast.makeText(MainActivity.this, "onMenuItemActionCollapse", Toast.LENGTH_SHORT).show();
                        menu.add(0, R.id.eliminarHistorial, 2, "Borrar historial de busqueda");
                       return true;
                   }
               });

           }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (currentFragment == MAP_FRAGMENT) {
            if (id == R.id.modo_tarjetas) {

                if (fm.findFragmentByTag(FRAGMENT_CARD) == null) {
                    //Toast.makeText(getApplicationContext(), "iniciando fragmentCard", Toast.LENGTH_SHORT).show();
                    fragmentCard = CardFragment.newInstance("param1", "param2");
                    fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack("fragmentCard").add(R.id.tarjetas, fragmentCard, FRAGMENT_CARD).commit();
                    openedFragmentCard = 1;
                }

                //frameLayout.setVisibility(View.VISIBLE);
                currentFragment = CARD_FRAGMENT;
                invalidateOptionsMenu();
            }
            if (id == R.id.nuevo_local) {

                if (sesion_iniciada) {

                    showMarkers("nuevo_local");

                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("origen", "nuevo_local");
                    startActivityForResult(intent, Constantes.NUEVO_LOCAL);
                }
            }

            if (id == R.id.nuevo_evento) {

                if (sesion_iniciada) {

                    showMarkers("nuevo_evento");

                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("origen", "nuevo_evento");
                    startActivityForResult(intent, Constantes.NUEVO_EVENTO);
                }
            }
            if (id == R.id.nueva_promo) {

                if (sesion_iniciada) {

                    showMarkers("nueva_promo");
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("origen", "nueva_promo");
                    startActivityForResult(intent, Constantes.NUEVA_PROMO);

                }
            }
            if (id == Constantes.NUEVO_LOC_ESP) {

                if (sesion_iniciada) {

                    showMarkers("nuevo_especial");
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("origen", "nuevo_especial");
                    startActivityForResult(intent, Constantes.NUEVO_LOC_ESP);
                }
            }

            if (id == R.id.mi_posicion) {
                if (Utilidades.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, this)) {
                    mostrar_ubicacion();
                }else {
                    solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
            }
            if (id == Constantes.ACERCA_DE){
                startActivity(new Intent(this, Acerca.class));
            }
            if(id == Constantes.SINCRONIZAR){
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("Estimado usuario")
                        .setMessage("Esta acción actualizará los elementos del menú lateral. Los cambios se verán al reiniciar la aplicación");
// Add the buttons
                builder.setPositiveButton("Sincronizar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    SyncAdapter.sincronizarAhora(getApplicationContext(), false);
                        Toast.makeText(MainActivity.this, "Sincronizando...", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
// Set other dialog properties


// Create the AlertDialog
                androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.show();
            }
        } else {
            switch (id) {

                case R.id.buscar:
//                    aqui tambien se puede buscar
//                    Toast.makeText(context, "itemSelectedBuscar", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.eliminarHistorial:
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                            MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
                    suggestions.clearHistory();
                    Toast.makeText(context, "El historial ha sido borrado", Toast.LENGTH_SHORT).show();
                    break;
            }

            if (mDrawerToggle.onOptionsItemSelected(item)) {
                   return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMarkers(String origen) {
        if (fm.findFragmentByTag(FRAGMENT_ICONOS) == null) {
            fragment = Iconos_fragment.newInstance(origen, "main_activity");
            fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack("fragment").add(R.id.frame, fragment, FRAGMENT_ICONOS).commit();
}
        currentFragment = ICONOS_FRAGMENT;
        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            final int FUE_EDITADO = 9;
            switch (resultCode) {
                case Constantes.NUEVO_LOCAL:
                    showMarkers("nuevo_local");
                    break;
                case Constantes.NUEVO_EVENTO:
                    showMarkers("nuevo_evento");
                    break;
                case Constantes.NUEVA_PROMO:
                    showMarkers("nueva_promo");
                    break;
                case Constantes.NUEVO_LOC_ESP:
                    showMarkers("nuevo_especial");
                    break;
                case Constantes.ONCLICK_DRAWER_HEADER:
                    Intent intent = new Intent(this, ActivityCuenta.class);
                    startActivity(intent);
                    break;
                case Constantes.FIN_NUEVO:
                    mMapView.removeAllViews();
                    mMapView.getOverlays().clear();
                    break;
                case FUE_EDITADO:
                    mapFragment.getMarkersByBox(this, false, true);
                    break;
            }
        }

    public void onPause() {
        super.onPause();// Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();

           // GeoPoint geoPoint = (GeoPoint) MapFragment.mMapView.getProjection().fromPixels(MapFragment.mMapView.getWidth() / 2, MapFragment.mMapView.getHeight() / 2, punto_cero);
        if (Utilidades.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MainActivity.this)){
            GeoPoint geoPoint = (GeoPoint) mMapView.getMapCenter();
                final SharedPreferences.Editor edit = mPrefs.edit();
                edit.putString(PREFS_MAP_CENTER, String.valueOf(geoPoint.getLatitude() + "," + String.valueOf(geoPoint.getLongitude())));
                edit.putFloat(PREFS_ZOOM_LEVEL, (float) mMapView.getZoomLevelDouble());
                edit.apply();

        }
        sesionManager.setSpinerItemSelected(CustomDrawerAdapter.currentSelected);
        //edit.putInt(PREFS_CURRENT_TAB, tabs.getCurrentTab());
        //this.mLocationOverlay.disableMyLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alert != null) {
            alert.dismiss();
        }

    }

    @Override
    public void onFragmentInteraction(final String origen, int pos, final String icono, final String activity_origen) {

        currentFragment = MAP_FRAGMENT;
        mMapView.removeAllViews();
        mMapView.getOverlays().clear();
        if (openedFragmentCard == 1){
            localesBoxJSON = null;
            CardFragment.adaptador.actualizarDataset();
        }
        procesandoNuevo = true;
        countBackPressed = 0;
        final Intent intent;
        Bitmap marker = null;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open(icono + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Matrix matrix = new Matrix();
        matrix.postScale(1.2f, 1.2f);
        marker = Bitmap.createBitmap(bitmap, 0, 0, 32, 37, matrix, false);
//        MapFragment.mMapView = (MapView) fm.findFragmentByTag(MAP_FRAGMENT_TAG).getView();
        //  MapFragment.mMapView = mapFragment.getMapView();
        mark = new MarkerLabeled(mMapView);
        String Titulo = "";
        switch (origen) {
            case "nuevo_local":
                Titulo = "Nuevo Local";
                intent = new Intent(getBaseContext(), NuevoLocal.class);
                break;
            case "nuevo_evento":
                Titulo = "Nuevo Evento";
                intent = new Intent(getBaseContext(), NuevoEvento.class);
                break;
            case "nueva_promo":
                Titulo = "Nueva Oferta";
                intent = new Intent(getBaseContext(), NuevaPromo.class);
                break;
            default:
                Titulo = sesionManager.getSuperCAt4Name();
                intent = new Intent(getBaseContext(), NuevoEspecial.class);
                break;
        }

        final String finalDescripcion = "Mantenga pulsado el marcador\npara arrastrarlo a la posición\ndeseada. Luego toque esta ventana\npara continuar";
        final String finalTitulo = Titulo;
        InfoWindow infoWindow = new InfoWindow(R.layout.mi_bonuspack_bubble, mMapView) {
            @Override
            public void onOpen(Object item) {
                LinearLayout layout = (LinearLayout) mView.findViewById(R.id.mi_bubble_layout);
                ImageView imagen = (ImageView) mView.findViewById(R.id.bubble_image);

                TextView txtTitle = (TextView) mView.findViewById(R.id.bubble_title);
                TextView txtDescription = (TextView) mView.findViewById(R.id.bubble_description);
                //TextView txtSubdescription = (TextView) mView.findViewById(R.id.bubble_subdescription);

                imagen.setImageResource(R.drawable.icono);
                txtTitle.setText(finalTitulo);
                txtDescription.setText(finalDescripcion);
                layout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Override Marker's onClick behaviour here

                        intent.putExtra("latitud", mark.getPosition().getLatitude());
                        intent.putExtra("longitud", mark.getPosition().getLongitude());
                        intent.putExtra("marcador", icono);
                        intent.putExtra("modoEdicion", false);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onClose() {

            }
        };

        mark.setInfoWindow(infoWindow);
        mark.setPosition((GeoPoint) mMapView.getMapCenter());
        mark.setDraggable(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//             mark.setInfoWindowAnchor(Marker.ANCHOR_RIGHT, Marker.ANCHOR_TOP);
        mark.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.showInfoWindow();
            }

            @Override
            public void onMarkerDragStart(Marker marker) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);
            }
        });

        // mark.setIcon(getResources().getDrawable(R.drawable.presence_online));
        //  Bitmap icono2=BitmapFactory.decodeFile("http://192.168.1.9/proyecto/iconos/" +nom_icon);
        mark.setIcon(marker);
        mark.setLabelFontSize(0);
        mark.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView. getOverlays().add(mark);
        mark.showInfoWindow();

        mMapView.invalidate();
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title; // mTitle es usado cada vez que se cierra el Drawer
        getSupportActionBar().setTitle(mTitle);

//        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
//        TextView textview = new TextView(MainActivity.this);
//        RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        textview.setLayoutParams(layoutparams);
//        textview.setText("titulo");
//        textview.setTextColor(Color.WHITE);
//        textview.setTextSize(12);
//        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionbar.setCustomView(textview);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onFragmentCardInteraction(double latitud, double longitud, int id) {

//        ArrayList<MarkerLabeled> markwindows = new ArrayList<>();
//        int count = mMapView.getChildCount();

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
//            Toast.makeText(getApplicationContext(), "drawer click", Toast.LENGTH_SHORT).show();
            if (position == 2 && !MapFragment.tipo.equals("eventos")){
                setTitle(adapter.getTitle());
            }
            if (position != 1 ) {
                if (Utilidades.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MainActivity.this)){

                    if (position == 2){
                        if (MapFragment.tipo.equals("eventos")){
                            SelectItem(position);
                        }
                     }else {
                        if (!(position == 4 && MapFragment.tipo.equals("eventos"))){
                            SelectItem(position);
                        }else {
                            setTitle(adapter.getTitle());
                        }

                }

            }
        }

     }
    }

    public void SelectItem(int possition) {

        MapFragment.id_categoria = CustomDrawerAdapter.drawerItemList.get(possition).getId_remota();
        if (fragment != null){
            if (fragment.isVisible()) {
                fm.popBackStack();
                currentFragment = MAP_FRAGMENT;
            }
        }
        switch (possition) {
            case 0: //

                if (sesion_iniciada) {
                    Intent intent = new Intent(this, ActivityCuenta.class);
                    startActivity(intent);
                } else {
                    Intent inten = new Intent(this, LoginActivity.class);
                    inten.putExtra("origen", "onClickDrawerHeader");
                    startActivityForResult(inten, Constantes.ONCLICK_DRAWER_HEADER);
                }
                break;

            default:

                //MapFragment.mMapView.getOverlays().removeAll(new ArrayList<Object>());

                if (procesandoNuevo) {
                    mMapView.removeAllViews();
                    mMapView.getOverlays().remove(mark);
                    countBackPressed = 0;
                    procesandoNuevo = false;

                }
                procesando_busqueda = false;

                mDrawerList.setItemChecked(possition, true);
                mTitle  = CustomDrawerAdapter.drawerItemList.get(possition).getItemName();
//                mSubTitle = CustomDrawerAdapter.mTitle;
                setTitleAndSubTitle(mTitle);
                if (fm.findFragmentByTag(MAP_FRAGMENT_TAG) == null) {
                    mapFragment = MapFragment.newInstance();
                    fm.beginTransaction().add(R.id.mapa, mapFragment, MAP_FRAGMENT_TAG).commit();
                    mMapView = mapFragment.getMapView();
                    assert mMapView != null;
                    mMapView.getController().setCenter(GeoPoint.fromDoubleString(mPrefs.getString(PREFS_MAP_CENTER, "-25.388400,-57.136800"), ','));
                    mMapView.getController().setZoom(mPrefs.getFloat(PREFS_ZOOM_LEVEL, 5.0f));
                } else {

                    mMapView = (MapView) fm.findFragmentByTag(MAP_FRAGMENT_TAG).getView();
                }
                if (mMapView.getZoomLevelDouble() > MapFragment.obtenerZoomMinimo() ) {// || currentFragment == CARD_FRAGMENT

                    mapFragment.getMarkersByBox(this, false, false);
                }else  {

                Toast.makeText(context, "Aumente el zoom para ver los elementos", Toast.LENGTH_SHORT).show();

            }
        }

        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void setTitleAndSubTitle(CharSequence title){

//        mSubTitle = subTitle;
        setTitle(title);
//        getSupportActionBar().setSubtitle(mSubTitle);

    }

    private String obtenerUrl(){

        String url = "";
       final int LOCALES = 0, EVENTOS = 1, PROMOS = 2 , ESPECIALES = 3;
        switch (CustomDrawerAdapter.currentSelected){

            case LOCALES:
                url = Constantes.URL_BASE + "locales/buscar_locales.php";
                break;
            case EVENTOS:
                url = Constantes.URL_BASE + "eventos/buscar_eventos.php";
                break;
            case PROMOS:
                url = Constantes.URL_BASE + "promos/buscar_promos.php";
                break;
            case ESPECIALES:
                url = Constantes.URL_BASE + "especiales/buscar_especiales.php";
                break;

        }
        return url;
    }
    private String obtenerTipo(){

        String tipo = "";
        final int LOCALES = 0, EVENTOS = 1, PROMOS = 2 , ESPECIALES = 3;
        switch (CustomDrawerAdapter.currentSelected){

            case LOCALES:
                tipo = "locales";
                break;
            case EVENTOS:
                tipo = "eventos";
                break;
            case PROMOS:
                tipo = "ofertas";
                break;
            case ESPECIALES:
                tipo = "...";//sufijo de buscar
                break;
        }
        return tipo;
    }

    private void solicitarPermiso(final String permission, final int requestCode){

/*            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {
                if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                // functionality that depends on this permission.
                Snackbar.make(contenedor, "Debe conceder el permiso de almacenamiento para cargar el mapa", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Solicitar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                goToSettings(); // o solicitar permiso de nuevo
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{permission},
                                        requestCode);                            }
                        }).show();

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                }else{
                    if (permission == Manifest.permission.ACCESS_FINE_LOCATION ) {
                        Snackbar.make(contenedor, "Debe conceder el permiso de Ubicación para ver su posición", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Solicitar", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                goToSettings(); // o solicitar permiso de nuevo
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{permission},
                                                requestCode);
                                    }
                                }).show();
                    }
                }

            } else {*/

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        requestCode);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
//            }
    }

    private void mostrarMapa(){


            if (fm.findFragmentByTag(MAP_FRAGMENT_TAG) == null) {
                mapFragment = MapFragment.newInstance();
                fm.beginTransaction().add(R.id.mapa, mapFragment, MAP_FRAGMENT_TAG).commit();
                currentFragment = MAP_FRAGMENT;

            } else {

                mapFragment = (MapFragment) fm.findFragmentByTag(MAP_FRAGMENT_TAG);

            }
    }


    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
//        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, 0);
    }
private void mostrar_ubicacion(){

    GeoPoint p;
    if (mapFragment != null) {
        p = mapFragment.getmyPosition();
    } else {
        mapFragment = (MapFragment) fm.findFragmentByTag(MAP_FRAGMENT_TAG);
        p = mapFragment.getmyPosition();
    }
    if (p != null) {

        mMapView.getController().animateTo(p);

    } else {
        alert = null;
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager == null) throw new AssertionError();
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("El sistema GPS está desactivado, ¿Desea activarlo?")
                    .setCancelable(true)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            alert = builder.create();
            alert.show();
        }else {
            Toast.makeText(context, "Esperando ubicación...", Toast.LENGTH_SHORT).show();
        }

    }
}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}





