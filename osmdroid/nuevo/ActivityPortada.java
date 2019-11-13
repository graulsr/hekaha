package com.esy.jaha.osmdroid.nuevo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.esy.jaha.osmdroid.BuildConfig;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Utilidades;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class ActivityPortada extends AppCompatActivity {

    private double latitud, longitud;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private String camara, marcador;
    static String urlPortada;
    static File file;
    private static String tipo;
    private static Context ctx;
    Activity activity;
    Bitmap bit;
    private LinearLayout linearLayout;
    private static int id;
    private  static boolean modoEdicion;
    private static NetworkImageView networkImageView;
    private static boolean actualizado;
    private Context context;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 9;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 22;
    private boolean cancelarTodo;
    private static String foto_portada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portada);
        Intent intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_portada);
        setSupportActionBar(toolbar);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        foto_portada = "null";
        activity = this;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = (int) ((width - 40) * 0.75);
        float densidad = dm.density;
//        int height = (int) ((width - 40*dm.density)*0.75 );
        ctx = this;
        final TextView tvNombre = (TextView)findViewById(R.id.tvNombre);
        TextView tvDescripcion = (TextView)findViewById(R.id.tvDescripcion);
        tipo = getIntent().getStringExtra("tipo");

        linearLayout = findViewById(R.id.layoutPortada);
        id = getIntent().getIntExtra("id", 0);
        latitud = getIntent().getDoubleExtra("latitud", 0);
        longitud = getIntent().getDoubleExtra("longitud", 0);
        marcador = getIntent().getStringExtra("marcador");
        final String nombre = intent.getStringExtra("nombre");
        final String descripcion = intent.getStringExtra("descripcion");
        modoEdicion = getIntent().getBooleanExtra("modoEdicion", false);
        if (modoEdicion) {
            setTitle("Cambiar Portada");
        }else{
            setTitle(obtenerTitulo(tipo));
        }

        Button btn_sig = (Button)findViewById(R.id.btn_sig_ptd);

        networkImageView = findViewById(R.id.nivPortada);
        networkImageView.setDefaultImageResId(R.drawable.fondo_main);
        networkImageView.setErrorImageResId(R.drawable.fondo_main);
        networkImageView.getLayoutParams().height = height;

        networkImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fondo_main));
        networkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilidades.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ActivityPortada.this)) {
                    showOptions();
                }else {
                    solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        if (modoEdicion){
            urlPortada = getIntent().getStringExtra("urlPortada");
            networkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            networkImageView.setImageUrl(urlPortada, MySingleton.getInstance(this).getImageLoader());

        }else {
            networkImageView.setScaleType(ImageView.ScaleType.CENTER);
            obtenerPortada();
        }

        btn_sig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             if (modoEdicion){
                finalizar();
             }else{
                 Intent intent = new Intent(getApplicationContext(), ActivityFotos.class);
                 intent.putExtra("tipo", tipo);
                 intent.putExtra("id", id);
                 intent.putExtra("latitud", latitud);
                 intent.putExtra("longitud", longitud);
                 intent.putExtra("marcador", marcador);
                 intent.putExtra("nombre", nombre);
                 intent.putExtra("descripcion", descripcion);
                 intent.putExtra("foto_portada", foto_portada);
                 startActivity(intent);
             }
            }
        });

        tvNombre.setText(nombre);
        if (!descripcion.isEmpty()) {
            tvDescripcion.setText(descripcion);
        }else {
            tvDescripcion.setVisibility(View.GONE);
        }
        ImageView imageView = findViewById(R.id.iv_marker);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open(marcador + ".png"));
            if (densidad > 1) {
                Matrix matrix = new Matrix();
                matrix.postScale(1.4f, 1.4f);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, 32, 37, matrix, false);
            }
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
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

    private void showOptions() {

        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPortada.this);

        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option[which] == "Tomar foto"){
                    if (Utilidades.hasPermission(Manifest.permission.CAMERA, ActivityPortada.this)){

                        openCamera();

                    }else   {

                        solicitarPermiso(Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA);
                    }

                }else if(option[which] == "Elegir de galeria"){

                    abrirGaleria();

                }else {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void abrirGaleria(){

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);

    }

    String imageName;
    Uri output;
    private void openCamera() {

        Long timestamp = System.currentTimeMillis() / 1000;
        imageName = timestamp.toString() + ".jpg";
        camara = Environment.getExternalStorageDirectory() + File.separator + imageName;
        file = new File(camara);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //output = Uri.fromFile(file);

        if (Utilidades.isV23orLower()){
            output = Uri.fromFile(file);
        }else {
            output = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        startActivityForResult(intent, PHOTO_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        ContentResolver cr = this.getContentResolver();
        bit = null;
        if (resultCode == RESULT_OK) {

            final int CROP_CODE = 3535;

            switch (requestCode) {
                case PHOTO_CODE:

                        MediaScannerConnection.scanFile(this,
                                new String[]{camara}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        cropCapturedImage(uri);
                                    }
                                });

                    break;

                case SELECT_PICTURE:
                    Uri path = data.getData();
                    String galeria = getRealPathFromURI(path);
                    file = new File(galeria);
                    imageName = file.getName();
                if (path != null) {

                        cropCapturedImage(path);
                    }
                    break;

                case CROP_CODE:
                   Uri uri = data.getData();
                    String pathFromURI = getRealPathFromURI(uri);
                    file = new File(pathFromURI);
                    imageName = file.getName();
                    subirFoto();
                    break;
            }

        }
    }
    private void subirFoto(){
        if (file.exists())
        {
            String nom_carpeta =  id + "/portada";
            new SubirFoto(file, ctx, tipo, nom_carpeta, 400, 300, "", "portada").execute();
        }
    }

    private void solicitarPermiso(final String permission, final int requestCode){


            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
    }


    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        CursorLoader cursor = new CursorLoader(getApplicationContext(), uri, projection, null, null,null);
        Cursor c = cursor.loadInBackground();
        int column_index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        c.moveToFirst();
        return c.getString(column_index);
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
                titulo = "Foto de Portada";
                break;
        }
        return titulo;
    }

    public void cropCapturedImage(Uri urlImagen){

        //inicializamos nuestro intent
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(urlImagen, "image/*");

        //Habilitamos el crop en este intent
        cropIntent.putExtra("crop", "true");

        cropIntent.putExtra("aspectX", 4);
        cropIntent.putExtra("aspectY", 3);

        //indicamos los limites de nuestra imagen a cortar
        cropIntent.putExtra("outputX", 800);
        cropIntent.putExtra("outputY", 600);

        //True: retornara la imagen como un bitmap, False: retornara la url de la imagen la guardada.
        cropIntent.putExtra("return-data", false);

        //iniciamos nuestra activity y pasamos un codigo de respuesta.
         startActivityForResult(cropIntent, 3535);
    }

    static void removerImagen(){

            file.delete();
    }

    static void actualizarImagen(String nomImagen){
        if (modoEdicion){
            actualizado = true;
        }
        actualizarNetwortImageView(nomImagen);
        foto_portada = nomImagen;
        file.delete();
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
                    Snackbar.make(linearLayout, "Debe conceder el permiso de almacenamiento para obtener una imagen de Galería", Snackbar.LENGTH_INDEFINITE)
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
                    Snackbar.make(linearLayout, "Debe conceder el permiso para acceder a la camara", Snackbar.LENGTH_INDEFINITE)
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
        finalizar();
        super.onBackPressed();
    }

    private void finalizar(){
        if (actualizado){
            final int CAMBIO_PORTADA = 8;
            Intent data = new Intent();
/*            if (imageName.contains(" ")){
                imageName = imageName.replace(" ", "_");
            }
            if (imageName.contains("ñ")){
                imageName = imageName.replace("ñ", "n");
            }*/
            data.putExtra("foto_portada", imageName);
            setResult(CAMBIO_PORTADA, data);
            actualizado = false;
        }
        finish();
    }

    private void obtenerPortada(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Snackbar snackbar = Snackbar.make(linearLayout, "Un momento...",Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        String url = Constantes.URL_BASE + tipo + "/obtenerPortada.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(!cancelarTodo) {
                            snackbar.dismiss();
                            try {
                                int estado = response.getInt("estado");
                                if (estado == 1) {
                                    String nombrePortada = response.getString("portada");
                                    if (!nombrePortada.equals("null")) {
                                        actualizarNetwortImageView(nombrePortada);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(!cancelarTodo) {
                    snackbar.dismiss();
                }
//                inicializarImageView();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private static void actualizarNetwortImageView(String nombrePortada){

        networkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        networkImageView.setImageUrl(Constantes.URL_IMAGENES + tipo + "/" + id + "/portada/thumbs/" + nombrePortada, MySingleton.getInstance(ctx).getImageLoader());

    }
}
