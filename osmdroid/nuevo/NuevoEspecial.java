package com.esy.jaha.osmdroid.nuevo;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esy.jaha.osmdroid.BdSingleton;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.SessionManager;
import com.esy.jaha.osmdroid.spinner.Categorias;
import com.esy.jaha.osmdroid.spinner.CategoriasSpinnerAdapter;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.ui.ActivityNuevaCategoria;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NuevoEspecial extends AppCompatActivity {

    private EditText nombre_espec;
    private EditText descripcion;
    private EditText edtx_ciudad;
    private EditText telefono;
    private EditText telefono2;
    private EditText cuenta1;
    private EditText cuenta2;
    private EditText cuenta3;
    private EditText informacion;
    private String marcador, url;
    private String activity_origen;
    private String nombre, descrip, categoria1, categoria2;
    private Context context;
    private int cat1;
    private int cat2;
    Spinner spinner1, spinner2;
    TextView view_error;
    CoordinatorLayout layout;
    SQLiteDatabase bd;
    Intent intent1;
    SessionManager sessionManager;
    boolean isModoEdicion;
    private int id_nuevo;
    private boolean cancelarTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_especial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_nuevo_espec);
        setSupportActionBar(toolbar);
        intent1 = getIntent();
        isModoEdicion = intent1.getBooleanExtra("modoEdicion", false);

        view_error = (TextView) findViewById(R.id.txtv_error_espec);
        context = this;
        inicializar_spinners();
        final Button nueva_categoria = (Button) findViewById(R.id.btn_new_cat_esp);
        assert nueva_categoria != null;
        nueva_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityNuevaCategoria.class);
                intent.putExtra("tipoElemento", "especial");
                int REQUEST_CODE = 1;
                startActivityForResult(intent, REQUEST_CODE);            }
        });
        layout = (CoordinatorLayout) findViewById(R.id.layout_espec);
        nombre_espec = (EditText) findViewById(R.id.edtx_nom_especial);
        descripcion = (EditText) findViewById(R.id.edtx_descrip_espec);
        informacion = (EditText) findViewById(R.id.edtx_info_especial);
        telefono = (EditText) findViewById(R.id.espec_telef1);
        telefono2 = (EditText) findViewById(R.id.espec_telef2);
        cuenta1 = (EditText) findViewById(R.id.espec_cuenta1);
        cuenta2 = (EditText) findViewById(R.id.espec_cuenta2);
        cuenta3 = (EditText) findViewById(R.id.espec_cuenta3);
        edtx_ciudad = findViewById(R.id.edtx_ciudad_especial);
        if (isModoEdicion){
            setTitle("Editar información");
            nombre_espec.setText(intent1.getStringExtra("nombre"));
            descripcion.setText(intent1.getStringExtra("descripcion"));
            informacion.setText(intent1.getStringExtra("informacion"));
            edtx_ciudad.setText(intent1.getStringExtra("ciudad"));
            String[] tels;
            tels = intent1.getStringArrayExtra("telefonos");



            if (tels[0] != null){
                telefono.setText(tels[0]);
            }
            if (tels[1] != null){
                telefono2.setText(tels[1]);
            }

            String[] cuentas;
            cuentas = intent1.getStringArrayExtra("cuentas");

            cuenta1.setText(cuentas[0]);
            cuenta2.setText(cuentas[1]);
            cuenta3.setText(cuentas[2]);
            url = Constantes.URL_BASE + "especiales/actualizar_especial.php";
        }else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Formulario");
            url = Constantes.URL_BASE + "especiales/insertar_especial.php";
        }
        Button btn_aceptar = (Button) findViewById(R.id.esp_btn_submit);

        assert btn_aceptar != null;
        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sessionManager = SessionManager.getInstancia(context);
                String token = sessionManager.getToken();
                int id_user = sessionManager.getUserId();
                nombre = nombre_espec.getText().toString().trim();
                descrip = descripcion.getText().toString().trim();
                String info = informacion.getText().toString().trim();
                String telef1 = telefono.getText().toString().trim();
                String telef2 = telefono2.getText().toString().trim();
                String cuenta_1 = cuenta1.getText().toString().trim();
                String cuenta_2 = cuenta2.getText().toString().trim();
                String cuenta_3 = cuenta3.getText().toString().trim();
                String ciudad = edtx_ciudad.getText().toString().trim();
                marcador = getIntent().getStringExtra("marcador");
                boolean cancel = false;
                View focusView = null;
                nombre_espec.setError(null);
                view_error.setError(null);
                if (cat1 == cat2) {
                    cat2 = 0;
                }
                if (TextUtils.isEmpty(nombre)) {
                    nombre_espec.setError("Este campo es requerido");
                    cancel = true;
                    focusView = nombre_espec;
                }
                if( cat1 == 0 && cat2 == 0) {

                    Snackbar.make(layout,"Debes elegir al menos una categoría", Snackbar.LENGTH_LONG).show();
                    view_error.setVisibility(View.VISIBLE);
                    cancel = true;
                    view_error.setError("");
                    focusView = view_error;
                }
                if(cancel){
                    focusView.requestFocus();
                }else{
                    //Creo el Objeto JSON
                    JSONObject jsonForm = new JSONObject();
                    if (descrip.isEmpty()){
                        if (!categoria1.equals("Categoría 1")) {
                            descrip = categoria1;
                            if (!categoria2.equals("Categoría 2") && !categoria2.equals(categoria1)) {
                                descrip = descrip + " - " + categoria2;
                            }
                        }else {
                            if (!categoria2.equals("Categoría 2")) {
                                descrip = categoria2;
                            }
                        }
                    }
                    try {
                        jsonForm.put("marcador", marcador);
                        jsonForm.put("nombre", nombre);
                        jsonForm.put("descripcion", descrip);
                        jsonForm.put("informacion", info);
                        jsonForm.put("id_categoria1", cat1);
                        jsonForm.put("id_categoria2", cat2);

                        jsonForm.put("telefono1", telef1);
                        jsonForm.put("telefono2", telef2);

                        jsonForm.put("cuenta1", cuenta_1);
                        jsonForm.put("cuenta2", cuenta_2);
                        jsonForm.put("cuenta3", cuenta_3);
                        jsonForm.put("ciudad", ciudad);
                        jsonForm.put("token", token);
                        jsonForm.put("id_user", id_user);
                        if (id_nuevo != 0){
                            jsonForm.put("id_especial", id_nuevo); //se actualizarán
                        }
                        if (isModoEdicion){
                            jsonForm.put("id_especial", intent1.getIntExtra("id", 0));
                        }else{
                            final double latitud = getIntent().getDoubleExtra("latitud", 0);
                            final double longitud = getIntent().getDoubleExtra("longitud", 0);
                            jsonForm.put("latitud", latitud);
                            jsonForm.put("longitud", longitud);
                        }

                    } catch (JSONException e) {
                        // Toast.makeText(getBaseContext(),"Operacion exitosa",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    // Envio los parámetros post.
                    final ProgressDialog progress;
                    progress = ProgressDialog.show(context, "Subiendo datos", "Por favor espere...", false, true);

                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.POST, url, jsonForm, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    // mTxtDisplay.setText("Response: " + response.toString());
                                    progress.dismiss();
                                    if (!cancelarTodo) {
                                        procesar_response(response);
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progress.dismiss();
                                    if (!cancelarTodo) {
                                        Toast.makeText(getBaseContext(), "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    MySingleton.getInstance(getApplication()).addToRequestQueue(jsObjRequest);


                }
            }});

    }
    /*

    /**
     if (currentFragment==0) {
     getMenuInflater().inflate(R.menu.main, menu);

     }
     else
     {
     getMenuInflater().inflate(R.menu.menu_card, menu);
     }
            invalidateOptionsMenu();
            return true;
        }*/

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public  void inicializar_spinners() {
        //Abrimos la base de datos 'DBUsuarios' en modo escritura

        SQLiteDatabase bd = BdSingleton.getInstance(this).getSqLiteBD().getReadableDatabase();
        Cursor c = bd.rawQuery(" SELECT * FROM categorias_especial ORDER BY categoria", null);
        //datos a mostrar
        List<Categorias> items = new ArrayList<Categorias>(c.getCount());
        items.add(new Categorias(0, "Categoría 1", "none"));

        List<Categorias> items2 = new ArrayList<Categorias>(c.getCount());
        items2.add(new Categorias(0, "Categoría 2", "none"));

        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            int id_categoria = c.getInt(3);
            String categoria = c.getString(1);
            String icono = c.getString(2);
            items.add(new Categorias(id_categoria, categoria, icono));
            items2.add(new Categorias(id_categoria, categoria, icono));
        }
        c.close();
        spinner1 = (Spinner) findViewById(R.id.spnr_cat_esp1);

        spinner1.setAdapter(new CategoriasSpinnerAdapter(this, items));
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //  Toast.makeText(adapterView.getContext(), ((Categorias) adapterView.getItemAtPosition(position)).getName(), Toast.LENGTH_SHORT).show();
                cat1 = ((Categorias) adapterView.getItemAtPosition(position)).getId_categoria();
                categoria1 = ((Categorias) adapterView.getItemAtPosition(position)).getName();

                if(cat1 != 0 && view_error.isShown()){
                    view_error.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //nothing
            }
        });

        spinner2 = (Spinner) findViewById(R.id.spnr_cat_esp2);
        assert spinner2 != null;
        spinner2.setAdapter(new CategoriasSpinnerAdapter(this, items2));
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Toast.makeText(adapterView.getContext(), ((Categorias) adapterView.getItemAtPosition(position)).getName(), Toast.LENGTH_SHORT).show();
                cat2 = ((Categorias) adapterView.getItemAtPosition(position)).getId_categoria();
                categoria2 = ((Categorias) adapterView.getItemAtPosition(position)).getName();

                if(cat2 != 0 && view_error.isShown()){
                    view_error.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (isModoEdicion){
            int[] categorias = intent1.getIntArrayExtra("categorias");
            int mCatPos1 = 0, mCatPos2 = 0;
            for (int i = 0; i < items.size(); i++) {

                int id_categoria = items.get(i).getId_categoria();
                if (id_categoria == categorias[0]) {
                    mCatPos1 = i;
                } else {
                    if (id_categoria == categorias[1]) {
                        mCatPos2 = i;
                    }
                }
            }
            spinner1.setSelection(mCatPos1);
            spinner2.setSelection(mCatPos2);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cancelarTodo = false;
        //Toast.makeText(getApplicationContext(),"restar",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelarTodo = true;
    }

    private void procesar_response(JSONObject resp){
        try {
            int estado = resp.getInt("estado");
            switch (estado){

                case 1:
                    if (isModoEdicion){
                        Toast.makeText(getBaseContext(), "La actualización se ha realizado correctamente", Toast.LENGTH_SHORT).show();
                        final int EDITADO = 44;
                        Intent intent = new Intent();
                        intent.putExtra("nombre", nombre);
                        intent.putExtra("descripcion", descrip);
                        setResult(EDITADO, intent);
                        finish();
                    }else {
                        if (id_nuevo == 0){
                            id_nuevo = resp.getInt("lastId");
                        }
                        url = Constantes.URL_BASE + "especiales/actualizar_especial.php";
                        final Intent intent = new Intent(NuevoEspecial.this, Activity_iconos.class);
                        intent.putExtra("nombre", nombre);
                        intent.putExtra("descripcion", descrip);
                        intent.putExtra("marcador", marcador);
                        intent.putExtra("tipo", "especiales");
                        intent.putExtra("lastId", id_nuevo);
                        if (!categoria1.equals("Categoría 1")) {
                            sessionManager.setIdCatOfNewAdded(cat1);
                        }else {
                            if (!categoria2.equals("Categoría 2")) {
                                sessionManager.setIdCatOfNewAdded(cat2);
                            }
                        }
                        sessionManager.setTipeOfNewAdded(3);
                        startActivity(intent);
                    }
                    break;
                case 2:
                    Toast.makeText(getBaseContext(), "Ha ocurrido un error. Vuelva a intentarlo luego", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getBaseContext(), "Error de autenticación", Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int EXITO = 2;
        if (resultCode == EXITO){
            inicializar_spinners();
        }
    }
}
