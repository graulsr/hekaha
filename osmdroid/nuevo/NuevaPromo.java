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

public class NuevaPromo extends AppCompatActivity {

    private EditText nombre_promo;
    private EditText descripcion;
    private EditText telefono;
    private EditText telefono2;
    private EditText cuenta1;
    private EditText cuenta2;
    private EditText cuenta3;
    private EditText informacion;
    private EditText ciudad;
    private String nombre, descrip, categoria1, categoria2;
    private Context context;
    private int cat1, id_nuevo;
    private int cat2;
    private String marcador;
    private TextView view_error;
    private CoordinatorLayout layout;
    private String url;
    private boolean cancelarTodo;
    boolean modoEdicion;
    Intent intent1;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_promo);
        Toolbar toolbar = findViewById(R.id.toolbar_nueva_promo);
        setSupportActionBar(toolbar);

        view_error = findViewById(R.id.txtv_error_promo);
        context = this;
        intent1 = getIntent();
        modoEdicion = intent1.getBooleanExtra("modoEdicion", false);
        inicializar_spinners();

        final Button nueva_categoria = findViewById(R.id.btn_new_cat_promo);
        assert nueva_categoria != null;
        nueva_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityNuevaCategoria.class);
                intent.putExtra("tipoElemento", "promo");
                int REQUEST_CODE = 1;
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        layout = findViewById(R.id.layout_promo);
        nombre_promo = findViewById(R.id.edtx_nom_promo);
        descripcion = findViewById(R.id.edtx_descrip_promo);
        informacion = findViewById(R.id.edtx_info_promo);
        ciudad = findViewById(R.id.edtx_ciudad_promo);
        telefono = findViewById(R.id.promo_telef1);
        telefono2 = findViewById(R.id.promo_telef2);
        cuenta1 = findViewById(R.id.promo_cuenta1);
        cuenta2 = findViewById(R.id.promo_cuenta2);
        cuenta3 = findViewById(R.id.promo_cuenta3);
        if (modoEdicion){
            setTitle("Editar información");
            nombre_promo.setText(intent1.getStringExtra("nombre"));
            descripcion.setText(intent1.getStringExtra("descripcion"));
            informacion.setText(intent1.getStringExtra("informacion"));
            ciudad.setText(intent1.getStringExtra("ciudad"));
            String[] tels;
            tels = intent1.getStringArrayExtra("telefonos");

            if (tels[0] != null){
                telefono.setText(tels[0]);
            }
            if (tels[1] != null) {
                telefono2.setText(tels[1]);
            }
                String[] cuentas;
                cuentas = intent1.getStringArrayExtra("cuentas");

                cuenta1.setText(cuentas[0]);
                cuenta2.setText(cuentas[1]);
                cuenta3.setText(cuentas[2]);
                url = Constantes.URL_BASE + "promos/actualizar_promo.php";

        }else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Nueva Oferta");
            url = Constantes.URL_BASE + "promos/insertar_promo.php";
        }
        Button btn_aceptar = findViewById(R.id.promo_btn_submit);

        assert btn_aceptar != null;
        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sessionManager = SessionManager.getInstancia(context);
                String token = sessionManager.getToken();
                int id_user = sessionManager.getUserId();
                nombre = nombre_promo.getText().toString().trim();
                descrip = descripcion.getText().toString().trim();
                String info = informacion.getText().toString().trim();
                String ciuda = ciudad.getText().toString().trim();
                String telef1 = telefono.getText().toString().trim();
                String telef2 = telefono2.getText().toString().trim();
                String cuenta_1 = cuenta1.getText().toString().trim();
                String cuenta_2 = cuenta2.getText().toString().trim();
                String cuenta_3 = cuenta3.getText().toString().trim();

                marcador = getIntent().getStringExtra("marcador");

                boolean cancel = false;
                View focusView = null;
                nombre_promo.setError(null);
                view_error.setError(null);
                if (cat1 == cat2) {
                    cat2 = 0;
                }
                if (TextUtils.isEmpty(nombre)) {
                    nombre_promo.setError("Este campo es requerido");
                    cancel = true;
                    focusView = nombre_promo;
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

                        jsonForm.put("token", token);
                        jsonForm.put("id_user", id_user);
                        jsonForm.put("ciudad", ciuda);
                        jsonForm.put("id_local", 0);
                        if (id_nuevo != 0){
                            jsonForm.put("id_promo", id_nuevo); //se actualizarán
                        }
                        if (modoEdicion){
                            jsonForm.put("id_promo", intent1.getIntExtra("id", 0));
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
                    progress = ProgressDialog.show(context, "Conectando con el servidor", "Por favor espere...", false, true);

                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.POST, url, jsonForm, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    // mTxtDisplay.setText("Response: " + response.toString());
                                    if(!cancelarTodo) {
                                        progress.dismiss();
                                        procesar_response(response);
                                    }

                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if(!cancelarTodo) {
                                        progress.dismiss();
                                        Toast.makeText(getBaseContext(), "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    MySingleton.getInstance(getApplication()).addToRequestQueue(jsObjRequest);
                }
            }});
    }



    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public  void inicializar_spinners() {

        Spinner spinner1, spinner2;
        SQLiteDatabase bd = BdSingleton.getInstance(context).getSqLiteBD().getReadableDatabase();
        List<Categorias> items, items2;
        Cursor c = bd.rawQuery(" SELECT * FROM categorias_promo ORDER BY categoria", null);
        //datos a mostrar
        if(c != null) {
            items = new ArrayList<>(c.getCount());
            items2 = new ArrayList<>(c.getCount());
            items.add(new Categorias(0, "Categoría 1", "none"));
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
            spinner1 = findViewById(R.id.spnr_cat_promo1);

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

            spinner2 = findViewById(R.id.spnr_cat_promo2);
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
            if (modoEdicion){
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

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cancelarTodo = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelarTodo = true;
    }

    private void procesar_response(JSONObject json){
        try {
            int estado = json.getInt("estado");
            switch (estado){

                case 1:
                    if (modoEdicion){
                        Toast.makeText(getBaseContext(), "La actualización se ha realizado correctamente", Toast.LENGTH_SHORT).show();
                        final int EDITADO = 44;
                        Intent intent = new Intent();
                        intent.putExtra("nombre", nombre);
                        intent.putExtra("descripcion", descrip);
                        setResult(EDITADO, intent);
                        finish();
                    }else {
                        if (id_nuevo == 0){
                            id_nuevo = json.getInt("lastId");
                        }
                        url = Constantes.URL_BASE + "promos/actualizar_promo.php";
                        Intent intent = new Intent(this, Activity_iconos.class);
                        intent.putExtra("nombre", nombre);
                        intent.putExtra("descripcion", descrip);
                        intent.putExtra("marcador", marcador);
                        intent.putExtra("tipo", "promos");
                        intent.putExtra("lastId", id_nuevo);
                        //                    Toast.makeText(getBaseContext(), "Operacion exitosa", Toast.LENGTH_SHORT).show();
                        if (!categoria1.equals("Categoría 1")) {
                            sessionManager.setIdCatOfNewAdded(cat1);
                        }else {
                            if (!categoria2.equals("Categoría 2")) {
                                sessionManager.setIdCatOfNewAdded(cat2);
                            }
                        }
                        sessionManager.setTipeOfNewAdded(2);
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
        int NUEVA_CAT_CREADA = 2;
        if (resultCode == NUEVA_CAT_CREADA){
            inicializar_spinners();
        }
    }
}
