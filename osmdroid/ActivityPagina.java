package com.esy.jaha.osmdroid;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
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
import com.esy.jaha.osmdroid.nuevo.ActivityGaleria;
import com.esy.jaha.osmdroid.nuevo.ActivityPaginaEditable;
import com.esy.jaha.osmdroid.nuevo.ImagenDetalleActivity;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ActivityPagina extends AppCompatActivity {

    private ImageLoader imageLoader;
    private NetworkImageView networkImageView;
    int id;//no privatizar
    private String icono, marcador;
    private LayoutInflater inflater;
    private String nombre, descripcion;
    String foto_portada;
    private int[] categoriasArray = new int[2];
    private LinearLayout layout;
    private String tipo;
//    private String[] telefs = new String[2];
    private String carpetaPortada = "";
    private int height, recomendados;
    String urlPortada;
    private boolean cancelarTodo, isNuevo;
    private LinearLayout contentComentarios, contenedorPagina;
    private EditText editTextComent;
    int id_userLocal;
    private final int MEJORAR_PAGINA = 1;
    private final int DENUNCIAR_PAGINA = 2;
    private final int LOCAL_YA_NO_EXISTE = 3;
    private final int ENCARGARSE_DEL_LOCAL = 4;
    private final int RECOMENDAR = 5;
    private boolean habilitarMenu;
    private Double  latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_pagina);
        setSupportActionBar(toolbar);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        height = dm.widthPixels;
        layout = findViewById(R.id.linearLayoutPag);
        Intent intent;
        contentComentarios = findViewById(R.id.comentarios);
        contenedorPagina = findViewById(R.id.linearLayoutPag);
        intent = getIntent();
        latitud = intent.getDoubleExtra("latitud", 0);
        longitud = intent.getDoubleExtra("longitud", 0);
        nombre = intent.getStringExtra("nombre");
        descripcion = intent.getStringExtra("descripcion");
        id = intent.getIntExtra("id", 0);
        tipo = intent.getStringExtra("tipo");
        icono = intent.getStringExtra("icono");
        marcador = intent.getStringExtra("marcador");
        isNuevo = intent.getBooleanExtra("isNuevo", false);
        mostrarTodo();
        if(isNuevo) {
            Button button = (Button) findViewById(R.id.boton_submit_pagina);
            button.setVisibility(View.VISIBLE);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Excelente!")
                    .setMessage("Todo ha salido bien.");
// Add the buttons
            builder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SessionManager.getInstancia(getBaseContext()).setNewAddedToTrue();
                    Intent intent;
                    intent = new Intent(getApplicationContext(), MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            });
            final AlertDialog dialog = builder.create();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                }
            });
        }

        foto_portada = intent.getStringExtra("foto_portada");

        if (foto_portada.equals("null")){
            urlPortada = Constantes.URL_IMAGENES + tipo + "/portadaDefault/thumbs/portadaDefault.png";
            carpetaPortada = Constantes.URL_IMAGENES + tipo + "/portadaDefault/";
            foto_portada = "portadaDefault.png";
        }else {
            urlPortada = Constantes.URL_IMAGENES + tipo + "/" + id + "/portada/thumbs/" + foto_portada;
            carpetaPortada = Constantes.URL_IMAGENES + tipo + "/" + id + "/portada/";
        }
        final String[] arrayFoto = new String[]{foto_portada};
        networkImageView.setImageUrl(urlPortada, imageLoader);
        networkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ImagenDetalleActivity.class);
                intent.putExtra("carpeta", carpetaPortada);
                intent.putExtra("nombre", nombre);
                intent.putExtra("num_fotos", 1 );//foto de portada
                intent.putExtra("nom_fotos",  arrayFoto);
                intent.putExtra("id_foto", id );
                intent.putExtra("extra_image", 0);//indice
                intent.putExtra("marcador", marcador );
                startActivity(intent);
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

    private void procesar_respuesta(JSONObject response){
        String telefono1, info, ciudad;
        LinearLayout linearLayout;
        String horario = "";
        String hora_evento;
        String fecha_evento;
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
                id_userLocal = info_json.getInt("id_user");


                JSONArray telefonos = response.getJSONArray("telefonos");
                JSONArray cuentas = response.getJSONArray("cuentas");
                JSONArray categorias = response.getJSONArray("categorias");
                JSONArray comentarios = response.getJSONArray("comentarios");

                if (tipo.equals("locales")) {

                    horario = info_json.getString("horario");
                    if (!horario.isEmpty()) {
                        CardView cuadroHorario = findViewById(R.id.cuadroHorario);
                        cuadroHorario.setVisibility(View.VISIBLE);
                        TextView tv_horario = findViewById(R.id.tv_horario);
                        tv_horario.setText(horario);
                    }

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
  /*                  if (!ciudad.isEmpty()) {
                        findViewById(R.id.layout_ciudadEvento).setVisibility(View.VISIBLE);
                        TextView ciudad_evento = findViewById(R.id.tv_ciudadEvento);
                        ciudad_evento.setText(ciudad);
                    }*/
                }

                if (!info.equals("")) {

                    CardView cuadroInfo = (CardView) findViewById(R.id.cuadroInfo);
                    cuadroInfo.setVisibility(View.VISIBLE);
                    TextView tvInfo = (TextView) findViewById(R.id.tv_informacion);
                    tvInfo.setText(info);

                }

                if (telefonos.length() > 0 ) {

                    CardView cardViewTel = (CardView) findViewById(R.id.cuadroTelefonos);
                    cardViewTel.setVisibility(View.VISIBLE);
                    TextView tvTitleTelefonos = (TextView) findViewById(R.id.titleTelef);
                    tvTitleTelefonos.setText("Teléfonos");
                    TextView tvTelefonos = (TextView) findViewById(R.id.tv_telefono);
//                    tvTelefonos.setLineSpacing(5.0f, 1);
//                    String tels = "";
                    String html2 = "";

                    for (int i = 0; i < telefonos.length(); i++) {
                        if (i > 1) break;
                      telefono1 = telefonos.getJSONObject(i).getString("telefono");
//                      tels = tels + (telefono1 + "\n");
                      html2 = html2 + "<a href=tel:" + telefono1 + ">" + telefono1 + "</a>";
                        if (!(i == telefonos.length() - 1)){
                            html2 = html2 + "<br>";
                        }
                    }
                    tvTelefonos.setText(Html.fromHtml(html2));
//                   tvTelefonos.setText(tels);
                    tvTelefonos.setMovementMethod(LinkMovementMethod.getInstance());
//                    Linkify.addLinks(tvTelefonos, Linkify.PHONE_NUMBERS);
                }

                String cuenta = null;

                if (cuentas.length() > 0) {

                    CardView cardViewCuentas = (CardView) findViewById(R.id.card_cuentas);
                    cardViewCuentas.setVisibility(View.VISIBLE);
                    TextView textViewCuentas = (TextView) findViewById(R.id.tv_cuentas);
                    textViewCuentas.setLineSpacing(10.0f, 1);
                    textViewCuentas.setAutoLinkMask(Linkify.ALL);
                    String cuentass = "";
                    for (int i = 0; i < cuentas.length(); i++) {

                        cuenta = cuentas.getJSONObject(i).getString("cuenta");
                        cuentass = cuentass + cuenta ;
                        if (!(i == cuentas.length() - 1)){
                            cuentass = cuentass + "\n";
                        }

                    }
                    textViewCuentas.setText(cuentass);
                }

                JSONArray jsonArrayFotos = response.getJSONArray("fotos");
                if (jsonArrayFotos.length() > 0) {
                    linearLayout = findViewById(R.id.linearLayoutFotos);
                    linearLayout.setVisibility(View.VISIBLE);
                    final String[] nombresFotos = new String[jsonArrayFotos.length()];
                    String carpeta_imagenes = Constantes.URL_IMAGENES  + tipo +"/" + id + "/";
                    for (int i = 0; i < jsonArrayFotos.length(); i++) {
                        String nombreFoto = jsonArrayFotos.getJSONObject(i).getString("nombre_foto");
                        nombresFotos[i] = nombreFoto;
                    }
                    for (int i = 0; i < jsonArrayFotos.length(); i++) {
                        String nombreFoto = jsonArrayFotos.getJSONObject(i).getString("nombre_foto");

                        if (i >= 3){
                            break;//solo mostrar 3 fotos
                        }
                        View view = inflater.inflate(R.layout.card_foto_pagina, linearLayout, false);
                        NetworkImageView n = (NetworkImageView) view.findViewById(R.id.niv_card_pagina);
                        n.setErrorImageResId(R.drawable.fondo_main);
                        n.setMaxHeight(height);
                        TextView tvDescrip = (TextView) view.findViewById(R.id.tv_descr_pagina);
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

                        n.setImageUrl(Constantes.URL_IMAGENES + tipo + "/" + id + "/thumbs/" + nombreFoto, imageLoader);
                        String descripcionFoto = jsonArrayFotos.getJSONObject(i).getString("descripcion");
                        /*if (descripcionFoto.contains("euro_simbol")){
                           descripcionFoto = descripcionFoto.replace("euro_simbol", "€");
                        }
                        if (descripcionFoto.contains("libra_simbol")){
                            descripcionFoto = descripcionFoto.replace("libra_simbol", "£");
                        }
                        if (!descripcionFoto.equals("null")) {
                            tvDescrip.setText(descripcionFoto);
                        }*/
                        tvDescrip.setText(descripcionFoto);
                        linearLayout.addView(view);
                    }
                    if (jsonArrayFotos.length() > 3) {
                        Button verTodasLasFotos = new Button(this);
                        verTodasLasFotos.setText("Ver todas las fotos");

                        verTodasLasFotos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), ActivityGaleria.class);
                                intent.putExtra("tipo", tipo);
                                intent.putExtra("id", id);
                                intent.putExtra("nombre", nombre);
                                intent.putExtra("marcador", marcador);
                                startActivity(intent);
                            }
                        });

                        linearLayout.addView(verTodasLasFotos);
                    }
                }
                Button btnSubmitComent = findViewById(R.id.submitComentario);
                btnSubmitComent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (SessionManager.getInstancia(getBaseContext()).isLoggedIn()){
                            enviarComentario();
                         }else{
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.putExtra("origen", "comentar");
                            startActivityForResult(intent, Constantes.COMENTAR);
                        }
                    }
                });
                if (comentarios.length() > 0){

                    for (int i = 0; i < comentarios.length(); i++){

                        JSONObject jsonObject = comentarios.getJSONObject(i);
                        agregarComentario(jsonObject, false);
                    }
                }


                //  Fechas, categorias y veces vista
                //=================================================================================================
            if (!fecha_creacion.equals("") && !isNuevo) {
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
                        cats = cats + categorias.getJSONObject(i).getString("categoria");
                        if (!(i == categorias.length() - 1)) {
                            cats = cats + " - ";
                        }
                    }
                    tv_categorias.setText(cats);
                }


                recomendados = info_json.getInt("recomendado");
                if (recomendados != 0) {
                    actualizarRecomendados(recomendados);
                }
                //======================================================================================
                    if (!ciudad.isEmpty()){
                        CardView layout_ciudad = findViewById(R.id.cuadroCiudad);
                        layout_ciudad.setVisibility(View.VISIBLE);
                        TextView ciudadLocal = findViewById(R.id.txtCiudad);
                        ciudadLocal.setText(ciudad);
                    }
             }
//             else{
//                Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
//            }
            } catch(JSONException e){
                e.printStackTrace();
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
        if (tipo == null)return "";
        switch (tipo){

            case "locales":
                url = Constantes.URL_BASE + "locales/obtenerLocalPorId.php";
                break;
            case "eventos":
                url = Constantes.URL_BASE + "eventos/obtenerEventoPorId.php";
                break;
            case "promos":
                url = Constantes.URL_BASE + "promos/obtenerPromoPorId.php";
                break;
            case "especiales":
                url = Constantes.URL_BASE + "especiales/obtenerEspecialPorId.php";
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

private void mostrarTodo(){

    initCollapsingToolbar(nombre);
    TextView tvTitDescrip = (TextView)findViewById(R.id.tvTitleDescrip);
    tvTitDescrip.setText(nombre);

    if(!descripcion.isEmpty()) {

        TextView tvDescrip = (TextView) findViewById(R.id.tv_descripcion);
        tvDescrip.setVisibility(View.VISIBLE);
        tvDescrip.setText(descripcion);
    }

    String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    JSONObject jsonObject = new JSONObject();
    try {
        jsonObject.put("id", id);
        jsonObject.put("android_id", android_id);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    String url = obtenerRuta(tipo);

    final Snackbar snackbar = Snackbar.make(contenedorPagina, "Un momento...", Snackbar.LENGTH_INDEFINITE);
    snackbar.show();

    JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(!cancelarTodo) {
                        snackbar.dismiss();
                        procesar_respuesta(response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(!cancelarTodo) {
                        snackbar.dismiss();
                        Toast.makeText(ActivityPagina.this, getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            );
    MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

    imageLoader = MySingleton.getInstance(getApplicationContext()).getImageLoader();
    networkImageView = (NetworkImageView) findViewById(R.id.netImgView_port);
    networkImageView.setErrorImageResId(R.drawable.fondo_main);
    inflater = LayoutInflater.from(this);
}

private void enviarComentario(){

    editTextComent = findViewById(R.id.edtxComent);
    String coment = editTextComent.getText().toString().trim();
    if(coment.equals("")){
        return;
    }
    final Snackbar snackbar = Snackbar.make(layout, "Un momento...", Snackbar.LENGTH_INDEFINITE);
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
                    Toast.makeText(ActivityPagina.this, getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                }
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    } catch (JSONException e) {
        e.printStackTrace();
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

private void agregarComentario(JSONObject jsonObject, boolean isNuevo){
    String nombreUser = null;
    String fechaHora = "", comentario = "";
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

//    SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance();
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
    tvNnombreUser.setText(nombreUser + ":");
//    tvNnombreUser.setText(nombreUser + " " + id_user + ":");

    tvComent.setText(comentario);
    if (isNuevo){
        contentComentarios.addView(view, 0);
    }else {
        contentComentarios.addView(view);
    }
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constantes.COMENTAR) {
            enviarComentario();
        }
        int FUE_EDITADO = 9;
        final int DESDE_PAGINA = 88;
        if (requestCode == DESDE_PAGINA){
            if (resultCode == FUE_EDITADO) {
                setResult(FUE_EDITADO);
                finish();
            }else{
                 finish();
             }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

    if (isNuevo){
        return  false;
    }
            menu.clear();
            menu.add(0, DENUNCIAR_PAGINA, 1, "Denunciar Página");

            if (tipo.equals("locales")) {
                menu.add(0, LOCAL_YA_NO_EXISTE, 2, "Este local ya no existe");
                menu.add(0, RECOMENDAR, 3, "Recomendar Local");

                if (SessionManager.getInstancia(this).isLoggedIn() && id_userLocal == 1) {
                    menu.add(0, MEJORAR_PAGINA, 4, "Mejorar Página");
                    menu.add(0, ENCARGARSE_DEL_LOCAL, 5, "Encargarse de este local");
                }
            }
            if (tipo.equals("eventos")){
                menu.add(0, RECOMENDAR, 2, "Recomendar Evento");
            }
            if (tipo.equals("promos")){
                menu.add(0, RECOMENDAR, 2, "Recomendar Oferta");
            }
            if (tipo.equals("especiales")){
                menu.add(0, RECOMENDAR, 2, "Recomendar");
            }
            return true;
    }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == MEJORAR_PAGINA){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final int id_local = id;
            final int DESDE_PAGINA = 88;
            builder.setTitle("Estimado usuario")
            .setMessage("Agradecemos su ayuda para mejorar esta página. Para abrir la página en modo edición haga click en Editar");
// Add the buttons
            builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Intent intent = new Intent(getApplicationContext(), ActivityPaginaEditable.class);
                    intent.putExtra("id", id_local);
                    intent.putExtra("latitud", latitud);
                    intent.putExtra("longitud", longitud);
                    intent.putExtra("marcador", marcador);
                    intent.putExtra("icono", icono);
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("descripcion", descripcion);
                    intent.putExtra("isNuevo", false);
                    intent.putExtra("foto_portada", foto_portada);
                    intent.putExtra("tipo", tipo);
                    intent.putExtra("desde_pagina", true);
                    startActivityForResult(intent, DESDE_PAGINA);

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
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (itemId == DENUNCIAR_PAGINA){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final int id_local = id;
            builder.setTitle("Estimado usuario")
                    .setMessage("Si hay algo inadecuado en esta página haga click en Denunciar, para que los moderadores de la aplicación puedan identificarla. Nadie más lo notará.");
// Add the buttons
            builder.setPositiveButton("Denunciar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    enviar_denuncia();
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
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (itemId == LOCAL_YA_NO_EXISTE){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final int id_local = id;
            builder
                    .setMessage("¿Está seguro de que este local ya no existe?");
// Add the buttons
            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    enviar_local_no_existe();
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
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (itemId == ENCARGARSE_DEL_LOCAL){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Encargarse del local")
                    .setMessage("Si acepta, este local se vinculará a su cuenta y solo usted podrá editarla nuevamente.\n" +
                            "La encontrará en la sección Mis Locales del Panel de su Cuenta." );
// Add the buttons
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                 vincularLocalUsuario();
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
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (itemId == RECOMENDAR){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setMessage("¿Desea recomendar " + obtenerFrase() + "?");
// Add the buttons
            builder.setPositiveButton("Recomendar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    recomendar();
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
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }


// Suma o resta las horas recibidos a la fecha

    public Date sumarRestarHorasFecha(Date fecha, int horas){

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha); // Configuramos la fecha que se recibe

        calendar.add(Calendar.HOUR, horas);  // numero de horas a añadir, o restar en caso de horas<0

        return calendar.getTime(); // Devuelve el objeto Date con las nuevas horas añadidas

    }

    public String getDiferencia(Date fechaInicial, Date fechaFinal){

        long diferencia = fechaFinal.getTime() - fechaInicial.getTime();

        long segsMilli = 1000;
        long minsMilli = segsMilli * 60;
        long horasMilli = minsMilli * 60;
        long diasMilli = horasMilli * 24;

        long diasTranscurridos = diferencia / diasMilli;
        diferencia = diferencia % diasMilli;

        long horasTranscurridos = diferencia / horasMilli;
        diferencia = diferencia % horasMilli;

        long minutosTranscurridos = diferencia / minsMilli;
        diferencia = diferencia % minsMilli;

        long segsTranscurridos = diferencia / segsMilli;

        return "diasTranscurridos: " + diasTranscurridos + " , horasTranscurridos: " + horasTranscurridos +
                " , minutosTranscurridos: " + minutosTranscurridos + " , segsTranscurridos: " + segsTranscurridos;

    }

    private void enviar_denuncia(){

        final String DENUNCIA_URL = Constantes.URL_BASE + tipo + "/denunciar.php";
        JSONObject json = new JSONObject();
        String token = SessionManager.getInstancia(this).getToken();
        int id_user = SessionManager.getInstancia(this).getUserId();
        final ProgressDialog dialog;
        try {
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            json.put("android_id", androidId);
            json.put("id_user", id_user);
            json.put("token", token);
            json.put("id", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog = ProgressDialog.show(this, null, "Por favor espere...", true, true);
        MySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        DENUNCIA_URL, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(!cancelarTodo) {
                                    dialog.dismiss();
                                    procesar_respuesta_denuncia(response);
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

    private void procesar_respuesta_denuncia(JSONObject response){

        try {
            int estado = response.getInt("estado");
            if(estado == 1){
                Toast.makeText(this, "La denuncia ha sido recibida. Esta página será revisada lo antes posible. Agradecemos su colaboración.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Ha ocurrido un problema.\nVuelva a intentarlo más tarde.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void enviar_local_no_existe(){

        final String URL = Constantes.URL_BASE + tipo + "/marcar_clausurado.php";
        JSONObject json = new JSONObject();
        String token = SessionManager.getInstancia(this).getToken();
        int id_user = SessionManager.getInstancia(this).getUserId();
        final ProgressDialog dialog;
        try {
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            json.put("android_id", androidId);
            json.put("id_user", id_user);
            json.put("token", token);
            json.put("id", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog = ProgressDialog.show(this, null, "Por favor espere...", true, true);
        MySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        URL, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(!cancelarTodo) {
                                    dialog.dismiss();
                                    respuesta_local_no_existe(response);
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

    private void respuesta_local_no_existe(JSONObject response){

        try {
            int estado = response.getInt("estado");
            if(estado == 1){
                Toast.makeText(this, "El local ha sido identificado. Será revisado en breve. Gracias por su colaboración", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Ha ocurrido un problema.\nVuelva a intentarlo más tarde.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void vincularLocalUsuario(){

        final String DENUNCIA_URL = Constantes.URL_BASE + tipo + "/vincularConUsuario.php";
        JSONObject json = new JSONObject();
        String token = SessionManager.getInstancia(this).getToken();
        int id_user = SessionManager.getInstancia(this).getUserId();
        final ProgressDialog dialog;
        try {
            json.put("id_user", id_user);
            json.put("token", token);
            json.put("id", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog = ProgressDialog.show(this, null, "Por favor espere...", true, true);
        MySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        DENUNCIA_URL, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(!cancelarTodo) {
                                    dialog.dismiss();
                                    respVincularUserLocal(response);
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

    private void respVincularUserLocal(JSONObject response){

        try {
            int estado = response.getInt("estado");
            if(estado == 1){
                Toast.makeText(this, "El local ha sido vinculado a su cuenta.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Ha ocurrido un problema.\nVuelva a intentarlo más tarde.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void recomendar(){

        final String DENUNCIA_URL = Constantes.URL_BASE + tipo + "/recomendar.php";
        JSONObject json = new JSONObject();
        final ProgressDialog dialog;
        try {
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            json.put("android_id", androidId);
            json.put("id", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog = ProgressDialog.show(this, null, "Por favor espere...", true, true);
        MySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        DENUNCIA_URL, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(!cancelarTodo) {
                                    dialog.dismiss();
                                    procesar_recomendar(response);
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

    private void procesar_recomendar(JSONObject response){

        try {
            int estado = response.getInt("estado");
            if(estado == 1){
                Toast.makeText(this, "Operación exitosa.\nEl número de las recomendaciones a aumentado a " + String.valueOf(recomendados + 1) , Toast.LENGTH_LONG).show();
                actualizarRecomendados(recomendados + 1);
            }else{
                if (estado == 2){
                    Toast.makeText(this, "Ya ha recomendado " + obtenerFrase(), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void actualizarRecomendados(int recomendados){

        LinearLayout cuadroRecomendados = findViewById(R.id.cuadroRecomendados);
        cuadroRecomendados.setVisibility(View.VISIBLE);
        TextView recomendaciones = findViewById(R.id.recomendaciones);
        String sufijo = " personas";
        if (recomendados == 1) {
            sufijo = " persona";
        }
        recomendaciones.setText(recomendados + sufijo);
    }


    private String obtenerFrase( ){

        String frase;

        switch (tipo){

            case "locales":
                frase = "este local";
                break;
            case "eventos":
                frase = "este evento";
                break;
            case "promos":
                frase = "esta oferta";
                break;
            case "especiales":
                frase = "este sitio";
                break;
            default:
                frase = "esta página";
                break;
        }
        return frase;
    }

}

