package com.esy.jaha.osmdroid.nuevo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.SessionManager;
import com.esy.jaha.osmdroid.cuenta.MarkerActivity;
import com.esy.jaha.osmdroid.cuenta.MoverActivity;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ActivityPaginaEditable extends AppCompatActivity {

    private ImageLoader imageLoader;
    private NetworkImageView networkImageView;
    int id;//no privatizar
    String icono, marcador;
    private Double  latitud, longitud;
    private LayoutInflater inflater;
    private String nombre, descripcion, info, ciudad;
    private Intent intent;
    private EditText editTextComent;
    String urlPortada;
    String[] arrayFoto;
    private NestedScrollView scrollView;
    private boolean esDesdePagina;
    private LinearLayout contentComentarios;
    private LinearLayout linearLayoutFotos;
    private final int MOVER_UBICACION = 0;
    private final int EDITAR_INFO = 1;
    private final int CAMBIAR_PORTADA = 2;
    private final int EDITAR_GALERIA = 3;
    private final int CAMBIAR_ICONO = 4;
    private final int CAMBIAR_MARKER = 5;
    private final int ELIMINAR = 6;
    private final int BORRAR_COMENTARIOS = 7;
    final int FUE_EDITADO = 9;
    String horario = "";
    String hora_evento;
    String fecha_evento;
    String[] cuentasArray = new String[3];
    int[] categoriasArray = new int[2];
    String tipo;
    String[] telefs = new String[2];
    String carpetaPortada = "";
    int height;
    private TextView tvTitDescrip;
    private String foto_portada;
    private boolean cancelarTodo , vaciarComentarios, vaciarFotosyComentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_pagina);
        setSupportActionBar(toolbar);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        height = dm.widthPixels;
        intent = getIntent();
        latitud = intent.getDoubleExtra("latitud", 0);
        longitud = intent.getDoubleExtra("longitud", 0);
        nombre = intent.getStringExtra("nombre");
        descripcion = intent.getStringExtra("descripcion");
        id = intent.getIntExtra("id", 0);
        tipo = intent.getStringExtra("tipo");
        icono = intent.getStringExtra("icono");
        marcador = intent.getStringExtra("marcador");
        foto_portada = intent.getStringExtra("foto_portada");
        esDesdePagina = intent.getBooleanExtra("desde_pagina", false);
        initCollapsingToolbar(nombre);
        linearLayoutFotos = (LinearLayout) findViewById(R.id.linearLayoutFotos);
        contentComentarios = findViewById(R.id.comentarios);
        imageLoader = MySingleton.getInstance(getApplicationContext()).getImageLoader();
        networkImageView = (NetworkImageView) findViewById(R.id.netImgView_port);
        networkImageView.setErrorImageResId(R.drawable.fondo_main);
        scrollView = findViewById(R.id.scrollView_pag);
        inflater = LayoutInflater.from(this);

        if (foto_portada.equals("null")){
            urlPortada = Constantes.URL_IMAGENES + tipo + "/portadaDefault/thumbs/portadaDefault.png";
            carpetaPortada = Constantes.URL_IMAGENES + tipo + "/portadaDefault/";
            foto_portada = "portadaDefault.png";
        }else {
            urlPortada = Constantes.URL_IMAGENES + tipo + "/" + id + "/portada/thumbs/" + foto_portada;
            carpetaPortada = Constantes.URL_IMAGENES + tipo + "/" + id + "/portada/";
        }
        arrayFoto = new String[]{foto_portada};
        networkImageView.setImageUrl(urlPortada, imageLoader);
        networkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), ImagenDetalleActivity.class);
                intent.putExtra("carpeta", carpetaPortada);
                intent.putExtra("nombre", nombre);
                intent.putExtra("num_fotos", 1);//portada
                intent.putExtra("nom_fotos",  arrayFoto);
                intent.putExtra("id_foto", id );
                intent.putExtra("extra_image", 0);//indice
                intent.putExtra("marcador", marcador );
                startActivity(intent);
            }
        });

        cargarInfo();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            menu.add(0, MOVER_UBICACION, 0, "Cambiar Ubicación");
            menu.add(0, EDITAR_INFO, 1, "Editar Información");
            menu.add(0, CAMBIAR_PORTADA, 2, "Cambiar Portada");
            menu.add(0, EDITAR_GALERIA, 3, "Editar Galería");
            menu.add(0, CAMBIAR_ICONO, 4, "Cambiar Icono");
            menu.add(0, CAMBIAR_MARKER, 5, "Cambiar Marker");
            if (!esDesdePagina) {
                menu.add(0, BORRAR_COMENTARIOS, 6, "Borrar comentarios");
                menu.add(0, ELIMINAR, 7, "Eliminar Página");
            }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Intent intent = null;
        int requestCode = 0;
        switch (itemId){

            case MOVER_UBICACION:
                intent = new Intent(this, MoverActivity.class);
                intent.putExtra("marcador", marcador);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                requestCode = MOVER_UBICACION;
                break;

            case EDITAR_INFO:
                intent = obtenerIntent();
                final int EDITAR = 33;
                requestCode = EDITAR;
                break;

            case CAMBIAR_PORTADA:
                final int CAMBIO_PORTADA = 66;
                intent = new Intent(this, ActivityPortada.class);
                intent.putExtra("marcador", marcador);
                intent.putExtra("nombre", nombre);
                intent.putExtra("descripcion", descripcion);
                intent.putExtra("modoEdicion", true);
                intent.putExtra("urlPortada", urlPortada);
                requestCode = CAMBIO_PORTADA;
                break;

            case CAMBIAR_ICONO:
                final int CAMBIO_ICONO = 88;
                intent = new Intent(this, Activity_iconos.class);
                intent.putExtra("icono", icono);
                intent.putExtra("nombre", nombre);
                intent.putExtra("descripcion", descripcion);
                intent.putExtra("modoEdicion", true);
                requestCode = CAMBIO_ICONO;
                break;

            case EDITAR_GALERIA:
                final int EDIT_GALERIA = 45;
                intent = new Intent(this, ActivityGaleriaEditable.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("marcador", marcador);
                requestCode = EDIT_GALERIA;
                break;

            case CAMBIAR_MARKER:
                intent = new Intent(this, MarkerActivity.class);
                intent.putExtra("marcador", marcador);
                break;

            case ELIMINAR:
                eliminar();
                break;

            case BORRAR_COMENTARIOS:
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setMessage("Se borrarán todos los comentarios\n¿Desea continuar?")
                        .setCancelable(true)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                borrarComentarios();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();
                break;

        }
        if (itemId != ELIMINAR && itemId != BORRAR_COMENTARIOS) {
            intent.putExtra("id", id);
            intent.putExtra("tipo", tipo);
            startActivityForResult(intent, requestCode);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int EDITADO = 44;
        final int CAMBIO_PORTADA = 8;
        final int CAMBIO_UBICACION = 17;
        final int CAMBIO_ICONO = 19;
        final int CAMBIO_MARKER = 13;
        final int CAMBIO_GALERIA = 31;
        Intent intent = new Intent();
        intent.putExtra("tipo", tipo);
        switch (resultCode){
            case EDITADO:
                vaciarComentarios = true;
                nombre = data.getStringExtra("nombre");
                descripcion = data.getStringExtra("descripcion");
                setTitle(nombre);
                cargarInfo();
                setResult(FUE_EDITADO, intent);
                break;
            case CAMBIO_PORTADA:
                String foto_portada = data.getStringExtra("foto_portada");
                networkImageView.setImageUrl(Constantes.URL_IMAGENES + tipo + "/" + id + "/portada/thumbs/" + foto_portada, imageLoader);
                actualizarUrlPortada(foto_portada);
                setResult(FUE_EDITADO, intent);
                break;
            case CAMBIO_UBICACION:
                 setResult(FUE_EDITADO, intent);
                break;
            case CAMBIO_ICONO:
                setResult(FUE_EDITADO, intent);
                icono = data.getStringExtra("icono_nuevo");
                break;
            case CAMBIO_MARKER:
                setResult(FUE_EDITADO, intent);
                marcador = data.getStringExtra("marcador_nuevo");
                break;
            case CAMBIO_GALERIA:
//                ArrayList<RecyclerGaleriaItem> fotos =  data.getParcelableArrayListExtra("galeriaItems");
             vaciarFotosyComentarios = true;
             cargarInfo();
            break;
        }
    }

    private void initCollapsingToolbar(final String title) {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(title);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_pagina);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
       appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                   if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                 if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(title);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private String obtenerRuta(String tipo ){

        String url = "";

        switch (tipo){

            case "locales":
                url = Constantes.URL_BASE + "locales/obtenerLocalEditable.php";
                break;
            case "eventos":
                url = Constantes.URL_BASE + "eventos/obtenerEventoEditable.php";
                break;
            case "promos":
                url = Constantes.URL_BASE + "promos/obtenerPromoEditable.php";
                break;
            case "especiales":
                url = Constantes.URL_BASE + "especiales/obtenerEspecialEditable.php";
                break;
        }
            return url;
    }


     String[] meses = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO",
            "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
    private String convertirFecha(String fechaMysql){
        String[] array = fechaMysql.split("-");
        return array[2] + " - " +meses[Integer.valueOf(array[1]) - 1] + " - " + array[0];
    }

private Intent obtenerIntent(){

    Intent intent = null;

    switch (tipo){

        case "locales":
            intent = new Intent(this, NuevoLocal.class);
            intent.putExtra("horario", horario);
            break;
        case "eventos":
            intent = new Intent(this, NuevoEvento.class);
            intent.putExtra("fechaEvento", fecha_evento);
            intent.putExtra("horaEvento", hora_evento);
            break;
        case "promos":
            intent = new Intent(this, NuevaPromo.class);
            break;
        case "especiales":
            intent = new Intent(this, NuevoEspecial.class);
            break;
    }
    intent.putExtra("nombre", nombre);
    intent.putExtra("descripcion", descripcion);
    intent.putExtra("informacion", info);
    intent.putExtra("ciudad", ciudad);
    intent.putExtra("categorias", categoriasArray);
    intent.putExtra("telefonos", telefs);
    intent.putExtra("cuentas", cuentasArray);
    intent.putExtra("modoEdicion", true);
    return intent;
}

private void cargarInfo(){
    if (vaciarFotosyComentarios){
        linearLayoutFotos.removeAllViews();
        contentComentarios.removeAllViews();
    }else if (vaciarComentarios){
        contentComentarios.removeAllViews();
        if (linearLayoutFotos.getVisibility() == View.VISIBLE){
            linearLayoutFotos.removeAllViews();
        }
    }
    tvTitDescrip = (TextView)findViewById(R.id.tvTitleDescrip);
    tvTitDescrip.setText(nombre);
    TextView tvDescrip = (TextView) findViewById(R.id.tv_descripcion);
    if(!descripcion.isEmpty()) {
        tvDescrip.setVisibility(View.VISIBLE);
        tvDescrip.setText(descripcion);
    }else{
        if(tvTitDescrip.getVisibility() == View.VISIBLE){
            tvDescrip.setVisibility(View.GONE);
        }
    }
    obtenerInfo();
 }

private void obtenerInfo(){

    JSONObject jsonObject = new JSONObject();
    String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    try {
        jsonObject.put("id", id);
        jsonObject.put("android_id", android_id);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    final ProgressDialog pDialog = new ProgressDialog(this);
    pDialog.setMessage("Un momento..." );
    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    pDialog.setCancelable(true);
    pDialog.show();
    String url = obtenerRuta(tipo);
    JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(!cancelarTodo) {
                        pDialog.dismiss();
                        procesar_respuesta(response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(!cancelarTodo) {
                        pDialog.dismiss();
                        Toast.makeText(ActivityPaginaEditable.this, getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            );
    MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
}

    private void cargarFotos(JSONArray jsonArrayFotos){

     if (jsonArrayFotos.length() > 0) {
         linearLayoutFotos.setVisibility(View.VISIBLE);
         final String[] nombresFotos = new String[jsonArrayFotos.length()];
         String carpeta_imagenes = Constantes.URL_IMAGENES  + tipo +"/" + id + "/";
         for (int i = 0; i < jsonArrayFotos.length(); i++) {
             String nombreFoto = null;
             try {
                 nombreFoto = jsonArrayFotos.getJSONObject(i).getString("nombre_foto");
                 nombresFotos[i] = nombreFoto;
             } catch (JSONException e) {
                 e.printStackTrace();
             }

         }
        for (int i = 0; i < jsonArrayFotos.length(); i++) {
            try {
                String nombreFoto = jsonArrayFotos.getJSONObject(i).getString("nombre_foto");

                if (i >= 3){
                    break;//solo mostrar 5 fotos
                }
                View view = inflater.inflate(R.layout.card_foto_pagina, linearLayoutFotos, false);
                NetworkImageView n = (NetworkImageView) view.findViewById(R.id.niv_card_pagina);
                n.setErrorImageResId(R.drawable.fondo_main);
                n.setMaxHeight(height);
                TextView tvDescripFoto = (TextView) view.findViewById(R.id.tv_descr_pagina);
                final Intent intent = new Intent(getApplicationContext(), ImagenDetalleActivity.class);
                intent.putExtra("carpeta", carpeta_imagenes);
                intent.putExtra("nombre", nombre);
                intent.putExtra("marcador", marcador);
                final int finalI = i;
                n.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    intent.putExtra("extra_image", finalI);
                    intent.putExtra("num_fotos", nombresFotos.length);
                    intent.putExtra("nom_fotos", nombresFotos);

                    startActivity(intent);
                     }
            });

                n.setImageUrl(Constantes.URL_IMAGENES + tipo + "/" + id + "/thumbs/" + jsonArrayFotos.getJSONObject(i).getString("nombre_foto"), imageLoader);
                String descripcionFoto = jsonArrayFotos.getJSONObject(i).getString("descripcion");

 /*               if (descripcionFoto.contains("euro_simbol")) {
                    descripcionFoto = descripcionFoto.replace("euro_simbol", "€");
                }
                if (descripcionFoto.contains("libra_simbol")) {
                    descripcionFoto = descripcionFoto.replace("libra_simbol", "£");
                }
                if (!descripcionFoto.equals("null")) {//el json fuerza la conversion a string

                }*/
                tvDescripFoto.setText(descripcionFoto);
                linearLayoutFotos.addView(view);

            }catch (JSONException e)     {
                e.printStackTrace();
            }
        }

        if (jsonArrayFotos.length() > 3) {
            Button verTodasLasFotos = new Button(this);
            verTodasLasFotos.setText("Ver todas las fotos");
            verTodasLasFotos.setAllCaps(false);
            verTodasLasFotos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Intent intent = new Intent(getApplicationContext(), ActivityGaleriaEditable.class);
                    intent.putExtra("tipo", tipo);
                    intent.putExtra("id", id);
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("marcador", marcador);
                    startActivityForResult(intent, 1);
                }
            });
            linearLayoutFotos.addView(verTodasLasFotos);
//            numViews = jsonArrayFotos.length() + 1 ; //button incluido
        }

    }

}

private void eliminar(){

    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
    builder.setMessage("¿Desea eliminar esta página?")
            .setCancelable(true)
            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                    eliminarPagina();
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                    dialog.cancel();
                }
            });
    android.app.AlertDialog alert = builder.create();
    alert.show();
}

private void eliminarPagina(){

    JSONObject jsonObject = new JSONObject();
    try {
        jsonObject.put("token", SessionManager.getInstancia(this).getToken());
        jsonObject.put("id_user", SessionManager.getInstancia(this).getUserId());
        jsonObject.put("id", id);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    final ProgressDialog pDialog = new ProgressDialog(this);
    pDialog.setMessage("Un momento..." );
    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    pDialog.setCancelable(true);
    pDialog.show();
    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
            obtenerUrlEliminar(tipo), jsonObject, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            if(!cancelarTodo) {
                pDialog.dismiss();
                try {
                    int estado = jsonObject.getInt("estado");
                    if (estado == 1) {
                        Toast.makeText(ActivityPaginaEditable.this, "La página ha sido eliminada", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("tipo", tipo);
                        setResult(FUE_EDITADO, intent);
                        finish();
                    } else {
                        Toast.makeText(ActivityPaginaEditable.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if(!cancelarTodo) {
                pDialog.dismiss();
                Toast.makeText(getBaseContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    );
    MySingleton.getInstance(this).addToRequestQueue(objectRequest);
}

    private void borrarComentarios(){
    if (contentComentarios.getChildCount() == 0){
        return;
    }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", SessionManager.getInstancia(this).getToken());
            jsonObject.put("id_user", SessionManager.getInstancia(this).getUserId());
            jsonObject.put("id", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Un momento..." );
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.show();
        String url = Constantes.URL_BASE + tipo + "/borrar_comentarios.php";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(!cancelarTodo) {
                    pDialog.dismiss();
                    try {
                        int estado = jsonObject.getInt("estado");
                        if (estado == 1) {
                            Toast.makeText(ActivityPaginaEditable.this, "Los comentarios han sido borrados", Toast.LENGTH_SHORT).show();
                            contentComentarios.removeAllViews();
                        } else {
                            Toast.makeText(ActivityPaginaEditable.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(!cancelarTodo) {
                    pDialog.dismiss();
                    Toast.makeText(getBaseContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        MySingleton.getInstance(this).addToRequestQueue(objectRequest);
    }


    private String obtenerUrlEliminar(String tipo ){

        String url = "";

        switch (tipo){

            case "locales":
                url = Constantes.URL_BASE + "locales/eliminar_local.php";
                break;
            case "eventos":
                url = Constantes.URL_BASE + "eventos/eliminar_evento.php";
                break;
            case "promos":
                url = Constantes.URL_BASE + "promos/eliminar_promo.php";
                break;
            case "especiales":
                url = Constantes.URL_BASE + "especiales/eliminar_especial.php";
                break;
        }
        return url;
    }

    private void actualizarUrlPortada(String nom_portada){
        urlPortada = Constantes.URL_IMAGENES + tipo + "/" + id + "/portada/thumbs/" + nom_portada;
        carpetaPortada = Constantes.URL_IMAGENES + tipo + "/" + id + "/portada/";
        arrayFoto[0] = nom_portada;
    }


    private void procesar_respuesta(JSONObject response){
        String telefono1 = null;

        try {
            int resultJson = response.getInt("estado");

            if (resultJson == 1) {
                JSONObject info_json = response.getJSONObject("info");
                info = info_json.getString("informacion");
                String fecha_creacion = info_json.getString("fecha_creacion");
                String fecha_modicacion = info_json.getString("fecha_modificacion");
//                String foto_portada = info_json.getString("foto_portada");
                int veces_vista = info_json.getInt("veces_vista");
                ciudad = info_json.getString("ciudad");

                JSONArray telefonos = response.getJSONArray("telefonos");
                JSONArray cuentas = response.getJSONArray("cuentas");
                JSONArray categorias = response.getJSONArray("categorias");
                JSONArray comentarios = response.getJSONArray("comentarios");


                if (tipo.equals("locales")) {

                    horario = info_json.getString("horario");
                    CardView cuadroHorario = findViewById(R.id.cuadroHorario);
                    if (!horario.isEmpty()) {
                        cuadroHorario.setVisibility(View.VISIBLE);
                        TextView tv_horario = findViewById(R.id.tv_horario);
                        tv_horario.setText(horario);
                    }else{
                        cuadroHorario.setVisibility(View.GONE);
                    }
/*                    LinearLayout layout_ciudad = findViewById(R.id.layoutCiudad);
                    if (!ciudad.isEmpty()){
                        layout_ciudad.setVisibility(View.VISIBLE);
                        TextView ciudadLocal = findViewById(R.id.tv_ciudadLocal);
                        ciudadLocal.setText(ciudad);
                    }else{
                        layout_ciudad.setVisibility(View.GONE);
                    }*/
                }

                if (tipo.equals("eventos")) {
                    fecha_evento = info_json.getString("fecha_evento");
                    hora_evento = info_json.getString("hora_evento");
                    CardView cuadroFecha = findViewById(R.id.cuadroFechaEvento);
                    cuadroFecha.setVisibility(View.VISIBLE);
                    TextView fechaEvento = findViewById(R.id.tv_fechaEvento);
                    fechaEvento.setText(convertirFecha(fecha_evento));

                    TextView horaEvento = findViewById(R.id.tv_horaEvento);
                    hora_evento = hora_evento.substring(0, 5) + " hs.";
                    horaEvento.setText(hora_evento);
/*                    LinearLayout ciudadEvento = findViewById(R.id.layout_ciudadEvento);
                    if (!ciudad.isEmpty()) {
                        ciudadEvento .setVisibility(View.VISIBLE);
                        TextView ciudad_evento = findViewById(R.id.tv_ciudadEvento);
                        ciudad_evento.setText(ciudad);
                    }else{
                        ciudadEvento.setVisibility(View.GONE);
                    }*/
                }
                CardView cuadroInfo = (CardView) findViewById(R.id.cuadroInfo);
                if (!info.equals("")) {
                    cuadroInfo.setVisibility(View.VISIBLE);
                    TextView tvInfo = (TextView) findViewById(R.id.tv_informacion);
                    tvInfo.setText(info);
                }else {
                    cuadroInfo.setVisibility(View.GONE);
                }

                CardView cardViewTel = (CardView) findViewById(R.id.cuadroTelefonos);
                if (telefonos.length() > 0 ) {
                    cardViewTel.setVisibility(View.VISIBLE);
                    TextView tvTitleTelefonos = (TextView) findViewById(R.id.titleTelef);
                    tvTitleTelefonos.setText("Teléfonos");
                    TextView tvTelefonos = (TextView) findViewById(R.id.tv_telefono);
                    tvTelefonos.setLineSpacing(5.0f, 1);
                    String tels = "";
                    String html2 = "";

                    for (int i = 0; i < telefonos.length(); i++) {
                        if(i > 1) break;
                        telefono1 = telefonos.getJSONObject(i).getString("telefono");
                        tels = tels + (telefono1 + "\n");
                        html2 = html2 + "<a href=tel:" + telefono1 + ">" + telefono1 + "</a>";
                        telefs[i] = telefono1;
                        if (!(i == telefonos.length() - 1)){
                            html2 = html2 + "<br>";
                        }
                    }
                    tvTelefonos.setText(Html.fromHtml(html2));
//                   tvTelefonos.setText(tels);
                    tvTelefonos.setMovementMethod(LinkMovementMethod.getInstance());
//                    Linkify.addLinks(tvTelefonos, Linkify.PHONE_NUMBERS);
                }else {
                    cardViewTel.setVisibility(View.GONE);
                }

                String cuenta = null;

                CardView cardViewCuentas = (CardView) findViewById(R.id.card_cuentas);
                if (cuentas.length() > 0) {
                    cardViewCuentas.setVisibility(View.VISIBLE);
                    TextView textViewCuentas = (TextView) findViewById(R.id.tv_cuentas);
                    textViewCuentas.setLineSpacing(10.0f, 1);
                    textViewCuentas.setAutoLinkMask(Linkify.ALL);
                    String cuentass = "";
                    for (int i = 0; i < cuentas.length(); i++) {

                        cuenta = cuentas.getJSONObject(i).getString("cuenta");
                        cuentass = cuentass + cuenta ;
                        cuentasArray[i] = cuenta;
                        if (!(i == cuentas.length() - 1)){
                            cuentass = cuentass + "\n";
                        }
                    }
                    textViewCuentas.setText(cuentass);
                }else {
                    cardViewCuentas.setVisibility(View.GONE);
                }


                JSONArray jsonArrayFotos = response.getJSONArray("fotos");
                cargarFotos(jsonArrayFotos);

                if (!fecha_creacion.equals("")) {
                    findViewById(R.id.cuadrofechaCreacion).setVisibility(View.VISIBLE);
                    TextView tvFechaCreacion = findViewById(R.id.tv_fechaCreacion);
                    tvFechaCreacion.setText(convertirFecha(fecha_creacion));
                    if (!fecha_creacion.equals(fecha_modicacion)) {
                        LinearLayout cuadroFechaModificacion = findViewById(R.id.cuadroFechaModificacion);
                        cuadroFechaModificacion.setVisibility(View.VISIBLE);
                        TextView tvFechaModificacion = findViewById(R.id.tv_fechaModificacion);
                        tvFechaModificacion.setText(convertirFecha(fecha_modicacion));
                    }
                    TextView vecesVista = findViewById(R.id.tv_vecesVista);
                    String sufijo = " personas";
                    if (veces_vista == 1) {
                        sufijo = " persona";
                    }
                    String text = veces_vista + sufijo;
                    vecesVista.setText(text);
                }

            if (categorias.length() > 0) {
                CardView cardViewcat = findViewById(R.id.cuadroCategorias);
                cardViewcat.setVisibility(View.VISIBLE);
                TextView tv_categorias = findViewById(R.id.tv_categorias);
                String cats = "";
                for (int i = 0; i < categorias.length(); i++) {
                    categoriasArray[i] = categorias.getJSONObject(i).getInt("id_categoria");
                    cats = cats + categorias.getJSONObject(i).getString("categoria");
                    if (!(i == categorias.length() - 1)) {
                        cats = cats + " - ";
                    }
                }
                tv_categorias.setText(cats);
            }
                //======================================================================================
                CardView layout_ciudad = findViewById(R.id.cuadroCiudad);
                if (!ciudad.equals("")){
                    layout_ciudad.setVisibility(View.VISIBLE);
                    TextView ciudadLocal = findViewById(R.id.txtCiudad);
                    ciudadLocal.setText(ciudad);
                }else{
                    layout_ciudad.setVisibility(View.GONE);
                }

                Button btnSubmitComent = findViewById(R.id.submitComentario);
                btnSubmitComent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        enviarComentario();

                    }
                });
                if (comentarios.length() > 0){

                    for (int i = 0; i < comentarios.length(); i++){

                        JSONObject jsonObject = comentarios.getJSONObject(i);
                        agregarComentario(jsonObject, false);
                    }
                }
            }
        } catch(JSONException e){
            e.printStackTrace();
        }

    }

    private void agregarComentario(JSONObject jsonObject, boolean isNuevo){
        String nombreUser = null;
        String fechaHora = "", comentario = "", id_user = "";
        try {
            nombreUser = jsonObject.getString("nombre");
            fechaHora = jsonObject.getString("fecha_hora");
            comentario = jsonObject.getString("comentario");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        View view = inflater.inflate(R.layout.item_coment, contentComentarios, false);
        TextView tvNnombreUser = view.findViewById(R.id.comentUser);
        TextView tvfechaComent = view.findViewById(R.id.fechaComent);
        TextView tvComent = view.findViewById(R.id.textComentario);

//       DateFormat simpleDateFormat =  DateFormat.getDateTimeInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
        simpleDateFormat.setTimeZone(utcZone);
        try {
            Date myDate = simpleDateFormat.parse(fechaHora);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            String formattedDate = simpleDateFormat.format(myDate);
            tvfechaComent.setText(formattedDate.substring(0, 16));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvNnombreUser.setText(nombreUser +  ":");

        tvComent.setText(comentario);
        if (isNuevo){
            contentComentarios.addView(view, 0);
        }else {
            contentComentarios.addView(view);
        }

    }

    private void procesarNuevoComent(JSONObject response){

        try {
            int estado = response.getInt("estado");
            if (estado == 1){
                JSONObject comentNuevo = response.getJSONObject("comentario");
                agregarComentario(comentNuevo, true);
                editTextComent.setText("");
            }else{
                Toast.makeText(this, "Ha ocurrido un error\nVuelve a intentarlo luego", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void enviarComentario(){

        editTextComent = findViewById(R.id.edtxComent);
        String coment = editTextComent.getText().toString().trim();
        if(coment.equals("")){
            return;
        }
        final Snackbar snackbar = Snackbar.make(scrollView, "Un momento...", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_user", SessionManager.getInstancia(this).getUserId());
            jsonObject.put("token", SessionManager.getInstancia(this).getToken());
            jsonObject.put("comentario", coment);
            jsonObject.put("id", id);
            String url = Constantes.URL_BASE + tipo + "/insertar_comentario.php";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (!cancelarTodo) {
                        snackbar.dismiss();
                        procesarNuevoComent(response);

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(!cancelarTodo) {
                        snackbar.dismiss();
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}
