package com.esy.jaha.osmdroid.nuevo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class NuevoLocal extends AppCompatActivity {

    private EditText editText_nombre_local;
    private EditText editText_descripcion_local;
    private EditText editText_horario_local;
    private EditText editText_telefono;
    private EditText editText_telefono2;
    private EditText editText_cuenta1;
    private EditText editText_cuenta2;
    private EditText editText_cuenta3;
    private EditText edtx_informacion;
    private EditText edtx_ciudad;
    SQLiteDatabase bd;
    String nombre_local;
    String descripcion, categoria1, categoria2;
    Intent intent;
    SessionManager sessionManager;
    boolean isModoEdicion;
    private Context context;
    private int cat1;
    private int cat2;
    private final int REQUEST_CODE = 1;
    private final int RESULT_CODE = 2;
    private String url;
    Spinner spinner1;
    TextView view_error;
    LinearLayout linearLayout;
    Intent intent1;
    private int id_nuevo;
    private boolean cancelarTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_nuevo_local);
        setSupportActionBar(toolbar);
        intent1 = getIntent();
        isModoEdicion = intent1.getBooleanExtra("modoEdicion", false);

        view_error = (TextView) findViewById(R.id.textView_error);
        context = this;
        inicializar_spinners();

        final Button nueva_categoria = (Button) findViewById(R.id.btn_new_cat);
        assert nueva_categoria != null;
        nueva_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityNuevaCategoria.class);
                intent.putExtra("tipoElemento", "local");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout_form);
        editText_nombre_local = (EditText) findViewById(R.id.editText_nombre_local);
        editText_descripcion_local = (EditText) findViewById(R.id.ediTtext_local_descripcion);
        edtx_informacion = (EditText) findViewById(R.id.edtx_info_local);
        edtx_ciudad = (EditText) findViewById(R.id.edtx_ciudad);
        editText_horario_local = (EditText) findViewById(R.id.editText_horario_local);
        editText_telefono = (EditText) findViewById(R.id.editText_telefono1);
        editText_telefono2 = (EditText) findViewById(R.id.editText_telfono2);
        editText_cuenta1 = (EditText) findViewById(R.id.editText_cuenta1);
        editText_cuenta2 = (EditText) findViewById(R.id.editText_cuenta2);
        editText_cuenta3 = (EditText) findViewById(R.id.editText_cuenta3);
        if (isModoEdicion){
            setTitle("Editar Local");
            editText_nombre_local.setText(intent1.getStringExtra("nombre"));
            editText_descripcion_local.setText(intent1.getStringExtra("descripcion"));
            edtx_informacion.setText(intent1.getStringExtra("informacion"));
            edtx_ciudad.setText(intent1.getStringExtra("ciudad"));
            editText_horario_local.setText(intent1.getStringExtra("horario"));
            String[] tels;
            tels = intent1.getStringArrayExtra("telefonos");



            if (tels[0] != null){
                editText_telefono.setText(tels[0]);
            }
            if (tels[1] != null){
                editText_telefono2.setText(tels[1]);
            }

            String[] cuentas;
            cuentas = intent1.getStringArrayExtra("cuentas");

            editText_cuenta1.setText(cuentas[0]);
            editText_cuenta2.setText(cuentas[1]);
            editText_cuenta3.setText(cuentas[2]);
            url = Constantes.URL_BASE + "locales/actualizar_local.php";
        }else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Nuevo Local");
            url = Constantes.URL_BASE + "locales/insertar_local.php";
        }
        Button btn_aceptar = (Button) findViewById(R.id.boton_submit);

        assert btn_aceptar != null;
        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sessionManager = SessionManager.getInstancia(context);
                String token = sessionManager.getToken();
                int id_user = sessionManager.getUserId();
                nombre_local = editText_nombre_local.getText().toString().trim();
                descripcion = editText_descripcion_local.getText().toString().trim();
                String info = edtx_informacion.getText().toString().trim();
                String horario = editText_horario_local.getText().toString().trim();
                String telefono1 = editText_telefono.getText().toString().trim();
                String telefono2 = editText_telefono2.getText().toString().trim();
                String ciudad = edtx_ciudad.getText().toString().trim();
                String cuenta1 = editText_cuenta1.getText().toString().trim();
                String cuenta2 = editText_cuenta2.getText().toString().trim();
                String cuenta3 = editText_cuenta3.getText().toString().trim();

                final String marcador = getIntent().getStringExtra("marcador");

                intent = new Intent(NuevoLocal.this, Activity_iconos.class);
                intent.putExtra("nombre", nombre_local);
                intent.putExtra("descripcion", descripcion);
                intent.putExtra("marcador", marcador);
                intent.putExtra("tipo", "locales");

                boolean cancel = false;
                View focusView = null;
                editText_nombre_local.setError(null);
                view_error.setError(null);
                if (cat1 == cat2) {
                    cat2 = 0;
                }
                if (TextUtils.isEmpty(nombre_local)) {
                    editText_nombre_local.setError("Este campo es requerido");
                    cancel = true;
                    focusView = editText_nombre_local;
                }
                if( cat1 == 0 && cat2 == 0) {

                    Snackbar.make(linearLayout,"Debes elegir al menos una categoría", Snackbar.LENGTH_LONG).show();
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
                    if (descripcion.isEmpty()){
                        if (!categoria1.equals("Categoría 1")) {
                            descripcion = categoria1;
                            if (!categoria2.equals("Categoría 2") && !categoria2.equals(categoria1)) {
                                descripcion = descripcion + " - " + categoria2;
                            }
                        }else {
                            if (!categoria2.equals("Categoría 2")) {
                                descripcion = categoria2;
                            }
                        }
                    }
                    try {

                        jsonForm.put("marcador", marcador);
                        jsonForm.put("nombre", nombre_local);
                        jsonForm.put("descripcion", descripcion);
                        jsonForm.put("informacion", info);
                        jsonForm.put("ciudad", ciudad);
                        jsonForm.put("id_categoria1", cat1);
                        jsonForm.put("id_categoria2", cat2);

                        jsonForm.put("horario", horario);
                        jsonForm.put("telefono1", telefono1);
                        jsonForm.put("telefono2", telefono2);

                        jsonForm.put("cuenta1", cuenta1);
                        jsonForm.put("cuenta2", cuenta2);
                        jsonForm.put("cuenta3", cuenta3);

                        jsonForm.put("token", token);
                        jsonForm.put("id_user", id_user);

                        if (id_nuevo != 0){
                            jsonForm.put("id_local", id_nuevo); //se actualizarán
                        }
                        if (isModoEdicion){
                            jsonForm.put("id_local", intent1.getIntExtra("id", 0));
                        }else {
                            double latitud = getIntent().getDoubleExtra("latitud", 0);
                            double longitud = getIntent().getDoubleExtra("longitud", 0);
                            jsonForm.put("latitud", latitud);
                            jsonForm.put("longitud", longitud);
                        }
                    } catch (JSONException e) {
                        // Toast.makeText(getBaseContext(),"Operacion exitosa",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    // Envio los parámetros post.

                    final ProgressDialog progress;
                    progress = ProgressDialog.show(context, "", "Por favor espere...", false, true);

                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.POST, url, jsonForm, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    // mTxtDisplay.setText("Response: " + response.toString());
                                    if(!cancelarTodo) {
                                        progress.dismiss();
                                        procesar_respuesta(response);
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

    public  void inicializar_spinners() {

        SQLiteDatabase bd = BdSingleton.getInstance(context).getSqLiteBD().getReadableDatabase();
        Cursor c = bd.rawQuery(" SELECT * FROM categorias ORDER BY categoria", null);

        List<Categorias> items = new ArrayList<Categorias>(c.getCount());
        List<Categorias> items2 = new ArrayList<Categorias>(c.getCount());

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
        spinner1 = (Spinner) findViewById(R.id.spinner_localcat1);
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
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner_localcat2);
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
                //nothing
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_CODE){

            inicializar_spinners();
        }
    }

    private void procesar_respuesta(JSONObject resp){
        try {
            int estado = resp.getInt("estado");
            switch (estado){

                case 1:
                        if (isModoEdicion){
                            Toast.makeText(getBaseContext(), "La actualización se ha realizado correctamente", Toast.LENGTH_SHORT).show();
                            final int EDITADO = 44;
                            Intent intent = new Intent();
                            intent.putExtra("nombre", nombre_local);
                            intent.putExtra("descripcion", descripcion);
                            setResult(EDITADO, intent);
                            finish();
                        }else {
                            if (id_nuevo == 0){
                                id_nuevo = resp.getInt("lastId");
                            }
                            intent.putExtra("lastId", id_nuevo);
                            url = Constantes.URL_BASE + "locales/actualizar_local.php";
                            if (!categoria1.equals("Categoría 1")) {
                                sessionManager.setIdCatOfNewAdded(cat1);
                            }else {
                                if (!categoria2.equals("Categoría 2")) {
                                    sessionManager.setIdCatOfNewAdded(cat2);
                                }
                            }
                            sessionManager.setTipeOfNewAdded(0);


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
}



