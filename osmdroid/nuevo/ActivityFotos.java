package com.esy.jaha.osmdroid.nuevo;

import android.Manifest;
import android.app.ActivityManager;
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
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esy.jaha.osmdroid.ActivityPagina;
import com.esy.jaha.osmdroid.BuildConfig;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.SessionManager;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Utilidades;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ActivityFotos extends AppCompatActivity implements EditDescripcion.OnFragmentInteractionListener, Fragment_descripcion_foto.OnFragmentFotoInteractionListener  {


    private Button btn_aceptar;
        private int id;
        private double latitud, longitud;
        private String marcador, url_eliminar;
        private int mWidth;
        static RecyclerView recycler;
        private ActivityManager.MemoryInfo memoryInfo;
        private int mHeight;
        private final int PHOTO_CODE = 200;
        private final int SELECT_PICTURE = 300;
        private Uri output;
        private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 9;
        static String camara;
        private String galeria;
        static File file;
        private String tipo;
        static String descripcion;
        Bitmap bitmap_redim, bitmapRotado;
        private boolean rotado = false;
        public static ArrayList<FotoItem> fotoItems;
        private final int MY_PERMISSIONS_REQUEST_CAMERA = 22;
        static Context ctx;
        private LinearLayout layoutFotos;
        AppBarLayout appBarLayout;
        static GaleriaFotosAdaptador adaptador;
        String carpeta;
        private List<RecyclerGaleriaItem> data;
        private FragmentManager fm;
        private final int ELIMINAR = 1;
        private final int EDITAR = 2;
        private boolean cancelarTodo;
        private Button btnAñadirFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_fotos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_fotos);
        setSupportActionBar(toolbar);
        Intent intent1 = getIntent();
        ctx = this;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = (int) (displayMetrics.widthPixels * 0.75);
        layoutFotos = findViewById(R.id.layoutFotos);
        appBarLayout = findViewById(R.id.app_bar_fotos);
        fm = getSupportFragmentManager();
        tipo = intent1.getStringExtra("tipo"); //tipo: locales, eventos, promos, etc
        id = getIntent().getIntExtra("id" , 0);
        latitud = getIntent().getDoubleExtra("latitud",0);
        longitud = getIntent().getDoubleExtra("longitud",0);
        marcador = getIntent().getStringExtra("marcador");
        final String descrip = getIntent().getStringExtra("descripcion");
        final String nombre = getIntent().getStringExtra("nombre");
        final String foto_portada = getIntent().getStringExtra("foto_portada");
        carpeta = Constantes.URL_IMAGENES  + tipo +"/" + id + "/";
        toolbar.setTitle("Galería de fotos");
        btnAñadirFoto = findViewById(R.id.btnAñadirFoto);
        btnAñadirFoto.setVisibility(View.VISIBLE);
        btnAñadirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilidades.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getApplicationContext())) {
                    showOptions();
                }else {
                    solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }
        });
        btn_aceptar = (Button)findViewById(R.id.btnActFot) ;
        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), ActivityPagina.class);
                intent.putExtra("id", id);
                intent.putExtra("tipo", tipo);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                intent.putExtra("marcador", marcador);
                intent.putExtra("nombre", nombre);
                intent.putExtra("descripcion", descrip);
                intent.putExtra("foto_portada", foto_portada);
                intent.putExtra("isNuevo", true);

                startActivity(intent);
            }
        });
        recycler = findViewById(R.id.recycler_fotos);
        data = new ArrayList<>();
        adaptador = new GaleriaFotosAdaptador(this, carpeta, data, nombre, height, marcador);
        recycler.setAdapter(adaptador);
        recycler.setLayoutManager(new LinearLayoutManager(this)); // Vertical Orientation By Default
        obtenerFotos();
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
    public boolean onContextItemSelected(MenuItem item) {

        final int id_imagen = item.getIntent().getIntExtra("id_imagen", 0);
        final int posicion = item.getIntent().getIntExtra("position", 0);
        final String nombre_imagen = item.getIntent().getStringExtra("nombre_imagen");
        inicializarUrls();
        switch (item.getItemId()) {

            case ELIMINAR:
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setMessage("¿Desea eliminar esta imagen?")
                        .setCancelable(true)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                eliminarImagen(id_imagen, nombre_imagen, posicion);
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

            case EDITAR:
                if (fm.findFragmentByTag("fragment_descripcion") == null) {
                    String des = item.getIntent().getStringExtra("descripcion");
                    String url = Constantes.URL_IMAGENES + tipo + "/" + id + "/thumbs/" + nombre_imagen;
                    EditDescripcion fragment = EditDescripcion.newInstance(id_imagen, posicion, des, url, tipo);
                    fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack("fragment_descripcion").add(R.id.descripcion_foto, fragment, "fragment_descripcion").commit();
                    appBarLayout.setVisibility(View.INVISIBLE);
                }
                break;
        }
        return true;
    }

    private void eliminarImagen(int id_imagen, String nombre_imagen, final int posicion){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", SessionManager.getInstancia(this).getToken());
            jsonObject.put("id_user", SessionManager.getInstancia(this).getUserId());
            jsonObject.put("id_foto", id_imagen);
            jsonObject.put("id", id);
            jsonObject.put("nombre_foto", nombre_imagen);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Actualizando..." );
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.show();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                url_eliminar, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (!cancelarTodo) {
                    pDialog.dismiss();
                    procesar_eliminacion(jsonObject, posicion);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (!cancelarTodo) {
                    pDialog.dismiss();
                    Toast.makeText(getBaseContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        MySingleton.getInstance(this).addToRequestQueue(objectRequest);
    }

    private void inicializarUrls(){

        switch (tipo){

            case "locales":
                url_eliminar = Constantes.URL_BASE + "locales/borrarUnaFotoLocal.php"  ;
                break;
            case "eventos":
                url_eliminar = Constantes.URL_BASE + "eventos/borrarUnaFotoEvento.php" ;
                break;
            case "promos":
                url_eliminar = Constantes.URL_BASE + "promos/borrarUnaFotoPromo.php" ;
                break;
            case "especiales":
                url_eliminar = Constantes.URL_BASE + "especiales/borrarUnaFotoEspecial.php";
                break;
        }
    }

    public void showOptions() {

        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//        builder.setTitle("Elige una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option[which] == "Tomar foto"){
                    if (Utilidades.hasPermission(Manifest.permission.CAMERA, ActivityFotos.this)) {

                        openCamera();
                    }else {
                        solicitarPermiso(Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA);
                    }

                }else if(option[which] == "Elegir de galeria"){
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

    private void openCamera() {

        Long timestamp = System.currentTimeMillis() / 1000;
        String imageName = timestamp.toString() + ".jpg";
        camara = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + imageName;

        file = new File(camara);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        output = Uri.fromFile(file);

        if (Utilidades.isV23orLower()){
            output = Uri.fromFile(file);
        }else {
            output = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        startActivityForResult(intent, PHOTO_CODE);
        }

       static Bitmap bit;
        @Override
        public boolean onPrepareOptionsMenu (Menu menu) {
            return false;
        }
        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString("file_path2", galeria);
            outState.putString("file_path", camara);
        }

        @Override
        protected void onRestoreInstanceState(Bundle savedInstanceState) {
            super.onRestoreInstanceState(savedInstanceState);
            galeria = savedInstanceState.getString("file_path2");
            camara = savedInstanceState.getString("file_path");
        }
    
        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            //ContentResolver cr = this.getContentResolver();

            if (resultCode == RESULT_OK) {
                rotado = false;

                switch (requestCode) {
                    case PHOTO_CODE:

                        //bit = android.provider.MediaStore.Images.Media.getBitmap(cr, output);
                        MediaScannerConnection.scanFile(this,
                                new String[]{camara}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                    }
                                });

                        bitmap_redim = decodedBitmap(camara, 300, 300);
                        mWidth = bitmap_redim.getWidth();
                        mHeight = bitmap_redim.getHeight();

                       /* String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                        Cursor cur = getContentResolver().query(output, orientationColumn, null, null, null);
                        int orientation = -1;
                        if (cur != null && cur.moveToFirst()) {
                            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                        }*/
                        int rotate = 0;
                        try {

                            ExifInterface exif = new ExifInterface(
                                    file.getAbsolutePath());
                            int orientation = exif.getAttributeInt(
                                    ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_NORMAL);

                            switch (orientation) {
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotate = 270;
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotate = 180;
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotate = 90;
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Matrix matrix;
                        if(rotate != 0) {
                          matrix = new Matrix();
                          matrix.postRotate(rotate);
                          bitmap_redim =  Bitmap.createBitmap(bitmap_redim, 0, 0, mWidth, mHeight, matrix, false);
                          bitmapRotado = decodedBitmap(camara, 400, 400) ;
                            memoryInfo = getAvailableMemory();
                            if (!memoryInfo.lowMemory) {
                                bitmapRotado = Bitmap.createBitmap(bitmapRotado, 0, 0, bitmapRotado.getWidth(), bitmapRotado.getHeight(), matrix, false);
                            }else {
                                finish();
                            }
                          rotado = true;
                          if (rotate != 180)
                            conmutarDimensiones();
                      }
                        mostrarFragment();
                        break;

                    case SELECT_PICTURE:
                        Uri path = data.getData();
                        galeria =  getRealPathFromURI(path);
                        file = new File(galeria);
                        String imageName = file.getName();
                        //file = getApplicationContext().getFileStreamPath(galeria);

                        //bit = android.provider.MediaStore.Images.Media.getBitmap(cr, path);
                        if (path != null) {
//                               InputStream imageStream = null;
                        /*    try {
                                //imageStream = getContentResolver().openInputStream(path);
                                //bit = BitmapFactory.decodeStream(imageStream);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }*/

                                bitmap_redim = decodedBitmap(galeria, 300, 300);
                                mWidth = bitmap_redim.getWidth();
                                mHeight = bitmap_redim.getHeight();

                            // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                            //orientation
                            String[] orientationColumn2 = {MediaStore.Images.Media.ORIENTATION};
                            Cursor cur = getContentResolver().query(path, orientationColumn2, null, null, null);
                            //String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};

                            int orientation = 0;
                            if (cur != null && cur.moveToFirst()) {
                                orientation = cur.getInt(cur.getColumnIndex(orientationColumn2[0]));
                                cur.close();
                            }

                            if (orientation != 0) {

                               matrix = new Matrix();
                               matrix.postRotate(orientation);
                               bitmap_redim = Bitmap.createBitmap(bitmap_redim, 0, 0, mWidth, mHeight, matrix, false);
                               bitmapRotado = decodedBitmap(galeria, 400, 400) ;
                                memoryInfo = getAvailableMemory();
                                if (!memoryInfo.lowMemory) {
                                    bitmapRotado = Bitmap.createBitmap(bitmapRotado, 0, 0, bitmapRotado.getWidth(), bitmapRotado.getHeight(), matrix, false);
                                }else {
                                    finish();
                                }
                               rotado = true;
                               if (orientation != 180)
                                 conmutarDimensiones(); //para mandar los parametros de la miniatura al servidor
                            }

                            mostrarFragment();
                        }
                        break;
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

        private void mostrarFragment(){
            final String FOTO_FRAGMENT_TAG = "FOTO_FRAGMENT_TAG";
            FragmentManager fm = this.getSupportFragmentManager();
            if (fm.findFragmentByTag(FOTO_FRAGMENT_TAG) == null) {
                Fragment_descripcion_foto fragment = Fragment_descripcion_foto.newInstance(null, null, bitmap_redim);
                fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack("fotoFragment").add(R.id.descripcion_foto, fragment, FOTO_FRAGMENT_TAG).commit();
                appBarLayout.setVisibility(View.INVISIBLE);

            }

        }
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }
        private void subirFoto(){
            String descripcion_mod = descripcion;

            if (file.exists()) {

                if (id != 0) {
                    String nom_carpeta = String.valueOf(id);
                    if (rotado) {

                        try {

                            File temp;
                            FileOutputStream os;
                            String[] ext = file.getName().split( "\\.");
                            String extension = ext[ext.length - 1];
                      if (extension.equals("png") || extension.equals("PNG")){
                                temp = File.createTempFile("temp", ".png");
                                os = new FileOutputStream(temp);
                                // Bitmap bitmap = BitmapFactory.decodeFile(getRealPathFromURI(Uri.fromFile(temp)));
                                bitmapRotado.compress(Bitmap.CompressFormat.PNG, 100, os);
                                os.flush();
                                os.close();
                                new SubirFoto(temp, this, tipo, nom_carpeta, mWidth, mHeight, descripcion_mod, "fotos").execute();
                            }else if(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("JPG") || extension.equals("JPEG")){
                                temp = File.createTempFile("temp", ".jpg");
                                os = new FileOutputStream(temp);
                                bitmapRotado.compress(Bitmap.CompressFormat.JPEG, 100, os);
                                os.flush();
                                os.close();
                                new SubirFoto(temp, this, tipo, nom_carpeta, mWidth, mHeight, descripcion_mod, "fotos").execute();
                            }else{
                                Toast.makeText(ActivityFotos.this, "Hay un problema con esta imagen.\nPruebe con otra por favor ", Toast.LENGTH_SHORT).show();
                            }
                            bitmap_redim = null;
                            bitmapRotado = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{

                        new SubirFoto(file, this, tipo, nom_carpeta, mWidth, mHeight, descripcion_mod, "fotos").execute();

                    }
                }
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
          //  temp.delete();
        }

     static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfWidth / inSampleSize) >= reqWidth && (halfHeight / inSampleSize) >= reqHeight) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodedBitmap(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }


    private void conmutarDimensiones(){

        int aux = mWidth;
        mWidth = mHeight;
        mHeight = aux;

            }

    private void solicitarPermiso(final String permission, final int requestCode){


            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }



    @Override
    public void onRequestPermissionsResult(int requestCode, final String permissions[], int[] grantResults) {

        final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 9;

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    showOptions();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(layoutFotos, "Debe conceder el permiso de almacenamiento para obtener una imagen de Galería", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Permisos", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToSettings(); // o solicitar permiso de nuevo
//                                    solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                }
                            }).show();


                }

                break;

            case MY_PERMISSIONS_REQUEST_CAMERA:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    openCamera();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(layoutFotos, "Debe conceder el permiso para acceder a la camara", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Permisos", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToSettings(); // o solicitar permiso de nuevo
//                                    solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                }
                            }).show();


                }
                break;
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

    @Override
    public void onBackPressed() {
        appBarLayout.setVisibility(View.VISIBLE);
        super.onBackPressed();
    }

    @Override
    public void onFragmentFotoInteraction(String descrip) {

        descripcion = descrip;
        subirFoto();
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }

    static void agregarItem(int id, String nombre, String descripcion) {

        adaptador.addItem(new RecyclerGaleriaItem(id, descripcion, nombre));
        recycler.scrollToPosition(0);
    }

    @Override
    public void onFragmentDescripcionInteraction(int posicion, String desc) {

        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
        adaptador.actualizarItem(posicion, desc);
    }

    private void obtenerFotos(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Actualizando..." );
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.show();

        String url = Constantes.URL_BASE + tipo + "/obtenerFotos.php";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        if (!cancelarTodo) {
                            procesarFotos(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                if (!cancelarTodo) {
                    actualizarAdaptador();
                    Toast.makeText(ActivityFotos.this, "No se puede conectar con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(objectRequest);
    }

    private void procesarFotos(JSONObject response){

        try {
            int estado = response.getInt("estado");
            if (estado == 1){
                Gson gson = new Gson();
                JSONArray array_fotos = response.getJSONArray("nom_fotos");
                RecyclerGaleriaItem[] res = gson.fromJson(array_fotos != null ? array_fotos.toString() : null, RecyclerGaleriaItem[].class);   // Parsear con Gson
                Collection<RecyclerGaleriaItem> items = Arrays.asList(res);
                data.addAll(items);
                actualizarAdaptador();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void actualizarAdaptador(){

        adaptador.actualizarData(data);//con o sin resultados
    }


    private void procesar_eliminacion(JSONObject response, int posicion){

     try {
            int estado = response.getInt("estado");
            if(estado == 1){
                adaptador.remover(posicion);
            }else {
                Toast.makeText(this, "No se pudo realizar la operación", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}











