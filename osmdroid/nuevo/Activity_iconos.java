package com.esy.jaha.osmdroid.nuevo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.esy.jaha.osmdroid.Adapter_iconos_locales;
import com.esy.jaha.osmdroid.Adapter_iconos_objetos;
import com.esy.jaha.osmdroid.Adapter_iconos_personas;
import com.esy.jaha.osmdroid.Adapter_iconos_simbolos;
import com.esy.jaha.osmdroid.Adapter_iconos_transportes;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.SessionManager;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Utilidades;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

public class Activity_iconos extends AppCompatActivity {

    private static boolean iconoPropio;
    private Context ctx;
    private final int SELECT_PICTURE = 300;
    private File file;
    static Bitmap redim;
    public static ImageView iv_ic_bub;
    private String tipo, id_nombre;
    private int id;
    private Intent intent2;
    static String icono;
    private LinearLayout layoutIconos;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 11;
    private boolean modoEdicion, cancelarTodo;
    String mIconoAnterior;
    NetworkImageView networkImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iconos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setSupportActionBar(toolbar);
        ctx = this;
        layoutIconos = findViewById(R.id.layoutIconos);
        iv_ic_bub = findViewById(R.id.iv_ic_bubble);
        modoEdicion = getIntent().getBooleanExtra("modoEdicion", false);

        Intent intent = getIntent();
        Double latitud = 0.0;
        Double longitud = 0.0;
        String nombre = intent.getStringExtra("nombre");
        String descripcion = intent.getStringExtra("descripcion");
        tipo = getIntent().getStringExtra("tipo");

        if (modoEdicion){

            id = getIntent().getIntExtra("id", 0);
            setTitle("Cambiar Icono");
            mIconoAnterior = getIntent().getStringExtra("icono");
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(ctx.getAssets().open(mIconoAnterior + ".png"));

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null){
                iv_ic_bub.setImageBitmap(bitmap);
            }else{
                iv_ic_bub.setVisibility(View.GONE);
                networkImageView = findViewById(R.id.niv_icono);
                networkImageView.setDefaultImageResId(R.drawable.logo_24x26);
                networkImageView.setErrorImageResId(R.drawable.logo_24x26);
                networkImageView.setVisibility(View.VISIBLE);
                networkImageView.setImageUrl(Constantes.URL_IMAGENES + tipo + "/" + id + "/logo/" + mIconoAnterior, MySingleton.getInstance(this).getImageLoader());
            }
            icono = mIconoAnterior;
        }else {
            latitud = intent.getDoubleExtra("latitud", 0);
            longitud = intent.getDoubleExtra("longitud", 0);
            id = getIntent().getIntExtra("lastId", 0);
            String marcador = intent.getStringExtra("marcador");
            intent2 = new Intent(getBaseContext(), ActivityPortada.class);
            intent2.putExtra("latitud", latitud);
            intent2.putExtra("longitud", longitud);
            intent2.putExtra("marcador", marcador);
            intent2.putExtra("icono", icono);
            intent2.putExtra("nombre", nombre);
            intent2.putExtra("descripcion", descripcion);
            intent2.putExtra("tipo", tipo);
            intent2.putExtra("id", id);

            setTitle(obtenerTitulo(tipo));
            obtenerIcono();
        }
        final Button boton_logo = (Button) findViewById(R.id.btn_logotipo) ;
        TextView tv_titulo = (TextView) findViewById(R.id.txt_title_bubble);
        TextView tv_descripcion = (TextView) findViewById(R.id.txt_bubble_description);
        assert boton_logo != null;
        boton_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilidades.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Activity_iconos.this)) {
                    showOptions();
                }else {
                    solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }            }
        });

        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab1_iconos");
        tabSpec.setContent(R.id.tab1_iconos);
        tabSpec.setIndicator(null, getResources().getDrawable(R.drawable.bar_restaurant));
        tabHost.addTab(tabSpec);
        GridView gv1 = (GridView)findViewById(R.id.gridview1_iconos);
        Adapter_iconos_objetos adapter = new Adapter_iconos_objetos(ctx);
        assert gv1 != null;
        gv1.setAdapter(adapter);
        gv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                icono = (String) adapterView.getItemAtPosition(i);
                chequearImageView();
                    try {
                        iv_ic_bub.setImageBitmap(BitmapFactory.decodeStream(ctx.getAssets().open(icono + ".png")));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

            }
        });


        tabSpec = tabHost.newTabSpec("tab2_iconos");
        tabSpec.setContent(R.id.tab2_iconos);
        tabSpec.setIndicator(null,getResources().getDrawable(R.drawable.trans_bus2));
        tabHost.addTab(tabSpec);
        GridView gv2 = (GridView)findViewById(R.id.gridview2_iconos);
        Adapter_iconos_transportes adapter2 = new Adapter_iconos_transportes(ctx);
        assert gv2 != null;
        gv2.setAdapter(adapter2);
        gv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                icono = (String) adapterView.getItemAtPosition(i);
                chequearImageView();
                    try {
                        iv_ic_bub.setImageBitmap(BitmapFactory.decodeStream(ctx.getAssets().open(icono + ".png")));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

            }
        });

        tabSpec = tabHost.newTabSpec("tab3_iconos");
        tabSpec.setContent(R.id.tab3_iconos);
        tabSpec.setIndicator(null,getResources().getDrawable(R.drawable.per5_bicicleta));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab4_iconos");
        tabSpec.setContent(R.id.tab4_iconos);
        tabSpec.setIndicator(null,getResources().getDrawable(R.drawable.obj_bolsa2));
        tabHost.addTab(tabSpec);

        GridView gv3 = (GridView)findViewById(R.id.gridview3_iconos);
        Adapter_iconos_personas adapter3 = new Adapter_iconos_personas(ctx);
        assert gv3 != null;
        gv3.setAdapter(adapter3);
        gv3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                icono = (String) adapterView.getItemAtPosition(i);
                chequearImageView();
                    try {
                        iv_ic_bub.setImageBitmap(BitmapFactory.decodeStream(ctx.getAssets().open(icono + ".png")));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

            }
        });


        GridView gv4 = (GridView)findViewById(R.id.gridview4_iconos);
        Adapter_iconos_locales adapter4 = new Adapter_iconos_locales(ctx);
        assert gv4 != null;
        gv4.setAdapter(adapter4);
        gv4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                icono = (String) adapterView.getItemAtPosition(i);
                chequearImageView();
                    try {
                        iv_ic_bub.setImageBitmap(BitmapFactory.decodeStream(ctx.getAssets().open(icono + ".png")));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
            }
        });

        tabSpec = tabHost.newTabSpec("tab5_iconos");
        tabSpec.setContent(R.id.tab5_iconos);
        tabSpec.setIndicator(null,getResources().getDrawable(R.drawable.sim_award_symbol));
        tabHost.addTab(tabSpec);
        GridView gv5 = (GridView)findViewById(R.id.gridview5_iconos);
        Adapter_iconos_simbolos adapter5 = new Adapter_iconos_simbolos(ctx);
        assert gv5 != null;
        gv5.setAdapter(adapter5);
        gv5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                icono = (String) adapterView.getItemAtPosition(i);
                chequearImageView();
                    try {
                        iv_ic_bub.setImageBitmap(BitmapFactory.decodeStream(ctx.getAssets().open(icono + ".png")));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
            }
        });

        tv_titulo.setText(nombre);
        if (!descripcion.isEmpty()) {
            tv_descripcion.setText(descripcion);
        }else {
            tv_descripcion.setVisibility(View.GONE);
        }
        Button btn_sgt = (Button) findViewById(R.id.btn_ic);
        assert btn_sgt != null;

        btn_sgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!iconoPropio) {
                    JSONObject jsonObject = new JSONObject();
                    String url = obtenerUrl(tipo);
                        try {
                            SessionManager sesionManager = SessionManager.getInstancia(ctx);
                            String token = sesionManager.getToken();
                            int id_user = sesionManager.getUserId();
                            jsonObject.put(id_nombre, id);
                            jsonObject.put("icono", icono);
                            jsonObject.put("token", token);
                            jsonObject.put("id_user", id_user);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    final ProgressDialog pDialog = new ProgressDialog(ctx);
                    pDialog.setMessage("Un momento..." );
                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pDialog.setCancelable(true);
                    pDialog.show();
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            pDialog.dismiss();
                            if (!cancelarTodo) {
                                procesarRespuesta(jsonObject);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            pDialog.dismiss();
                            if (!cancelarTodo) {
                                Toast.makeText(Activity_iconos.this, getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    MySingleton.getInstance(ctx).addToRequestQueue(jsonObjectRequest);

                } else{
                    if(!modoEdicion) {
                        startActivity(intent2);
                    }else{
                        final int CAMBIO_ICONO = 19;
                        Intent intent = new Intent();
                        intent.putExtra("icono_nuevo", icono);
                        setResult(CAMBIO_ICONO, intent);
                        finish();
                    }
                }
            }
        });
    }

    private void showOptions() {

        final CharSequence[] option = { "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(Activity_iconos.this);
        builder.setTitle("Tamaño: 48x48 Formato: png");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              if(option[which] == "Elegir de galeria"){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                }else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    String imageName;
    int  Code;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // ContentResolver cr = this.getContentResolver();

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case SELECT_PICTURE:
                    Uri path = data.getData();
                    String galeria = getRealPathFromURI(path);
                    file = new File(galeria);
                    imageName = file.getName();
                    //file = getApplicationContext().getFileStreamPath(galeria);
                    //bit = android.provider.MediaStore.Images.Media.getBitmap(cr, path);
                    if (path != null) {
                        // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                        //bit = BitmapFactory.decodeStream(imageStream);
                        if(verificarDimensiones(galeria)){
                            redim = BitmapFactory.decodeFile(galeria);
                            int mWidth = redim.getWidth();
                            int mHeight = redim.getHeight();
                            String[] orientationColumn2 = {MediaStore.Images.Media.ORIENTATION};
                            Cursor cur = getContentResolver().query(path, orientationColumn2, null, null, null);

                            int orientation = -1;
                            if (cur != null && cur.moveToFirst()) {
                                orientation = cur.getInt(cur.getColumnIndex(orientationColumn2[0]));
                                cur.close();
                            }

                            if (orientation != -1) {
                                Matrix matrix = new Matrix();
                                matrix.postRotate(orientation);
                                try {
                                    redim = Bitmap.createBitmap(redim, 0, 0, mWidth, mHeight, matrix, true);//
                                }catch (InvalidParameterException e){
                                    Toast.makeText(Activity_iconos.this, "EL icono debe tener 48x48 píxeles como mínimo(formato: png)", Toast.LENGTH_SHORT).show();
                                }
                            }
                            subirIcono();

                        }else{
                            Toast.makeText(Activity_iconos.this, "El tamaño máximo debe ser de 64x64 (formato: png) ", Toast.LENGTH_SHORT).show();
                        }
                    }
            }
        }
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        CursorLoader cursor = new CursorLoader(getApplicationContext(), uri, projection, null, null,null);
        Cursor c = cursor.loadInBackground();
        int column_index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        c.moveToFirst();
        return c.getString(column_index);
    }

    private void subirIcono(){
        if (file.exists()) {
             //tipo : Local, Evento ,etc.
            if(id != 0) {
                String nom_carpeta = id + "/logo";
                new SubirIcono(file, ctx, tipo, nom_carpeta).execute();
            }
        }
    }

    private boolean verificarDimensiones(String path){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int height = options.outHeight;
        int width = options.outWidth;
        if( width > 64 || width < 40 || height > 64 || height < 40){
            return false;
        }else{
            return true;
        }

    }

    public static void fijarIcono(String icon) {
        iv_ic_bub.setImageBitmap(redim);
        iconoPropio = true;
    }

    CharSequence obtenerTitulo(String tipo){

        CharSequence titulo = "";
        switch (tipo){
            case "locales":
                titulo = "Nuevo Local";
                break;
            case "eventos":
                titulo = "Nuevo Evento";
                break;
            case "promos":
                titulo = "Nueva Oferta";
                break;
            case "especiales":
                titulo = "Iconos";
                break;
        }
        return titulo;
    }


    private String obtenerUrl(String tipo){

        String url = "";
        switch (tipo){

            case "locales":
                url = Constantes.URL_BASE + "locales/actualizarIconoLocal.php";
                id_nombre = "id_local";
                break;
            case "eventos":
                url = Constantes.URL_BASE + "eventos/actualizarIconoEvento.php";
                id_nombre = "id_evento";
                break;
            case "promos":
                url = Constantes.URL_BASE + "promos/actualizarIconoPromo.php";
                id_nombre = "id_promo";
                break;
            case "especiales":
                url = Constantes.URL_BASE + "especiales/actualizarIconoEspecial.php";
                id_nombre = "id_especial";
                break;
        }
        return url;
    }

    private void procesarRespuesta(JSONObject response){
        try {
            int estado = response.getInt("estado");
            if(estado == 1){
                if(modoEdicion){
                    final int CAMBIO_ICONO = 19;
                    Intent intent = new Intent();
                    intent.putExtra("icono_nuevo", icono);
                    setResult(CAMBIO_ICONO, intent);
                    Snackbar.make(layoutIconos, "El icono se ha cambiado exitosamente", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Cerrar", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            }).show();
                }else{
                    startActivity(intent2);
                }
            }else if(estado == 2){

                Toast.makeText(Activity_iconos.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(Activity_iconos.this, "Ha ocurrido un error de autenticación", Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, final String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    showOptions();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(layoutIconos, "Debe conceder el permiso de almacenamiento para acceder a la Galería", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Permisos", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToSettings(); // o solicitar permiso de nuevo
//                                    solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                }
                            }).show();

                }

                break;

           /* case MY_PERMISSIONS_REQUEST_CAMERA:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    openCamera();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(linearLayout, "Debe conceder el permiso para acceder a la camara", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Permisos", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToSettings(); // o solicitar permiso de nuevo
//                                    solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                }
                            }).show();


                }
                break;*/
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, 0);
    }


    private void solicitarPermiso(final String permission, final int requestCode){

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
        }


    private void obtenerIcono(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Constantes.URL_BASE + tipo + "/obtenerIcono.php";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Un momento..." );
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        if (!cancelarTodo) {
                            obtenerRespuesta(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                if (!cancelarTodo) {
                    ponerIconosPorDefecto();
                }
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    private void obtenerRespuesta(JSONObject respuesta){
        try {
            int estado = respuesta.getInt("estado");
            if (estado == 1) {
                String iconoNet = respuesta.getString("icono");
                if (!iconoNet.equals("null")) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getAssets().open(iconoNet + ".png"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        iv_ic_bub.setImageBitmap(bitmap);

                    } else {
                        iv_ic_bub.setVisibility(View.GONE);
                        networkImageView = findViewById(R.id.niv_icono);
                        networkImageView.setDefaultImageResId(R.drawable.fondo_main);
                        networkImageView.setErrorImageResId(R.drawable.fondo_main);
                        networkImageView.setVisibility(View.VISIBLE);
                        networkImageView.setImageUrl(Constantes.URL_IMAGENES + tipo + "/" + id + "/logo/" + iconoNet, MySingleton.getInstance(this).getImageLoader());
                    }
                }else {
                    ponerIconosPorDefecto();
                }
            }else {
                ponerIconosPorDefecto();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void ponerIconosPorDefecto(){

        iv_ic_bub.setImageResource(R.drawable.logo_24x26);
        icono = "icono";
    }

    private void chequearImageView(){
        if(iv_ic_bub.getVisibility() == View.GONE){
            networkImageView.setVisibility(View.GONE);
            iv_ic_bub.setVisibility(View.VISIBLE);
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
