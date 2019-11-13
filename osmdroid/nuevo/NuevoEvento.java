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
import java.util.Calendar;
import java.util.List;

public class NuevoEvento extends AppCompatActivity {

    static String[] meses = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO",
            "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
    private EditText nombre_evento;
    private EditText descripcion;
    private EditText horario;
    private EditText telefono1;
    private EditText telefono2;
    private EditText cuenta1;
    private EditText cuenta2;
    private EditText cuenta3;
    private EditText informacion;
    private Context context;
    private int cat1;
    private int cat2;
    private int EXITO = 2;
    String nombre_event;
    String descrip, categoria1, categoria2;
    private boolean cancelarTodo = false;
    Spinner spinner1, spinner2;
    TextView view_error;
    CoordinatorLayout layout;
    SQLiteDatabase bd;
    String url;
    JSONObject jsonForm;
    static String fecha_mysql;
    static String hora_mysql;
    static Button fecha_event;
    static Button hora_event;
    int id_nuevo_evento = 0;
    Intent intent1;
    boolean isModoEdicion;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_evento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent1 = getIntent();
        isModoEdicion = intent1.getBooleanExtra("modoEdicion", false);
//
//        if (Build.VERSION.SDK_INT >= 21){
//            getWindow().setNavigationBarColor(getResources().getColor(R.color.accentColor));
//        }
        setTitle("Nuevo Evento");
        view_error = (TextView) findViewById(R.id.event_error);
        context = this;
        url = Constantes.URL_BASE + "eventos/insertar_evento.php";
        inicializar_spinners();

        final Button nueva_categoria = (Button) findViewById(R.id.btn_new_cat_event);
        assert nueva_categoria != null;
        nueva_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityNuevaCategoria.class);
                intent.putExtra("tipoElemento", "evento");
                int REQUEST_CODE = 1;
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        layout = (CoordinatorLayout) findViewById(R.id.layout_event);
        nombre_evento = (EditText) findViewById(R.id.edtx_nom_even);
        descripcion = (EditText) findViewById(R.id.edtx_descrip_event);
        informacion = (EditText) findViewById(R.id.edtx_info_event);
        telefono1 = (EditText) findViewById(R.id.event_telef1);
        telefono2 = (EditText) findViewById(R.id.event_telef2);
        fecha_event = (Button) findViewById(R.id.btn_fecha);
        hora_event = (Button) findViewById(R.id.btn_hora);
        cuenta1 = (EditText) findViewById(R.id.event_cuenta1);
        cuenta2 = (EditText) findViewById(R.id.event_cuenta2);
        cuenta3 = (EditText) findViewById(R.id.event_cuenta3);
        final EditText edtx_ciudad = (EditText) findViewById(R.id.edtx_ciudad_event);
        fecha_event.setText(obtener_fecha());
        hora_event.setText(obtener_hora());
        if (isModoEdicion){
            setTitle("  Editar Evento  ");
            nombre_evento.setText(intent1.getStringExtra("nombre"));
            descripcion.setText(intent1.getStringExtra("descripcion"));
            informacion.setText(intent1.getStringExtra("informacion"));
            fecha_event.setText(convertirFecha(intent1.getStringExtra("fechaEvento")));
            hora_event.setText(intent1.getStringExtra("horaEvento"));
            edtx_ciudad.setText(intent1.getStringExtra("ciudad"));
            String[] tels;
            tels = intent1.getStringArrayExtra("telefonos");

            telefono1.setText(tels[0]);
            telefono2.setText(tels[1]);
            String[] cuentas;
            cuentas = intent1.getStringArrayExtra("cuentas");

            cuenta1.setText(cuentas[0]);
            cuenta2.setText(cuentas[1]);
            cuenta3.setText(cuentas[2]);
            url = Constantes.URL_BASE + "eventos/actualizar_evento.php";
        }else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Nuevo Evento");
            url = Constantes.URL_BASE + "eventos/insertar_evento.php";
        }
        Button btn_aceptar = (Button) findViewById(R.id.event_btn_submit);

        assert btn_aceptar != null;
        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sessionManager = SessionManager.getInstancia(context);
                String token = sessionManager.getToken();
                int id_user = sessionManager.getUserId();
                nombre_event = nombre_evento.getText().toString().trim();
                descrip = descripcion.getText().toString().trim();
                String info = informacion.getText().toString().trim();
//                String fecha_evento = fecha_event.getText().toString();
//                String hora_evento  = hora_event.getText().toString();
                String ciudad = edtx_ciudad.getText().toString().trim();
                String telef1 = telefono1.getText().toString().trim();
                String telef2 = telefono2.getText().toString().trim();
                String cuenta_1 = cuenta1.getText().toString().trim();
                String cuenta_2 = cuenta2.getText().toString().trim();
                String cuenta_3 = cuenta3.getText().toString().trim();

                final String marcador = getIntent().getStringExtra("marcador");

                final Intent intent = new Intent(NuevoEvento.this, Activity_iconos.class);

                intent.putExtra("nombre", nombre_event);
                intent.putExtra("descripcion", descrip);
                intent.putExtra("marcador", marcador);
                intent.putExtra("tipo", "eventos");//esto formara parte de una ruta

                boolean cancel = false;
                View focusView = null;
                nombre_evento.setError(null);
                view_error.setError(null);
                if (cat1 == cat2) {
                    cat2 = 0;
                }
                if (TextUtils.isEmpty(nombre_event)) {
                    nombre_evento.setError("Este campo es requerido");
                    cancel = true;
                    focusView = nombre_evento;
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
                    jsonForm = new JSONObject();
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
                        jsonForm.put("nombre", nombre_event);
                        jsonForm.put("descripcion", descrip);
                        jsonForm.put("informacion", info);

                        jsonForm.put("id_categoria1", cat1);
                        jsonForm.put("id_categoria2", cat2);

                        jsonForm.put("hora_evento", hora_mysql);
                        jsonForm.put("fecha_evento", fecha_mysql);
                        jsonForm.put("telefono1", telef1);
                        jsonForm.put("telefono2", telef2);

                        jsonForm.put("cuenta1", cuenta_1);
                        jsonForm.put("cuenta2", cuenta_2);
                        jsonForm.put("cuenta3", cuenta_3);

                        jsonForm.put("token", token);
                        jsonForm.put("id_user", id_user);
                        jsonForm.put("ciudad", ciudad);
                        jsonForm.put("id_local", 0);//todo: estudiar este caso: permitir abrir un local desde un evento
                        if (id_nuevo_evento != 0){
                            jsonForm.put("id_evento", id_nuevo_evento); //se actualizarán
                        }
                        if (isModoEdicion){
                            jsonForm.put("id_evento", intent1.getIntExtra("id", 0));
                        }else{
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
                    progress = ProgressDialog.show(context, "Conectando con el servidor", "Por favor espere...", false, true);
                    cancelarTodo = false;
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.POST, url, jsonForm, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    // mTxtDisplay.setText("Response: " + response.toString());
                                    if(!cancelarTodo) {
                                        progress.dismiss();
                                        int lastId = procesar_respuesta(response);
                                        if (lastId != 0) {
//                                            Toast.makeText(getBaseContext(), "Operacion exitosa", Toast.LENGTH_SHORT).show();
                                            intent.putExtra("lastId", lastId);
                                            if (!categoria1.equals("Categoría 1")) {
                                                sessionManager.setIdCatOfNewAdded(cat1);
                                            }else {
                                                if (!categoria2.equals("Categoría 2")) {
                                                    sessionManager.setIdCatOfNewAdded(cat2);
                                                }
                                            }
                                            sessionManager.setTipeOfNewAdded(1);

                                            startActivity(intent);
                                        }

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
        SQLiteDatabase bd = BdSingleton.getInstance(context).getSqLiteBD().getReadableDatabase();
        Cursor c = bd.rawQuery(" SELECT * FROM categorias_evento ORDER BY categoria", null);
        //datos a mostrar
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
        spinner1 = (Spinner) findViewById(R.id.spnr_cat_event1);

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
//// TODO: 03/03/2018 implementar opcion de: marcar como suspendido un evento 
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //nothing
            }
        });

        spinner2 = (Spinner) findViewById(R.id.spnr_cat_event2);
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
   }

    @Override
    protected void onPause() {
        cancelarTodo = true;
        super.onPause();
    }

    private String obtener_fecha() {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        fecha_mysql = year + "-" + (month + 1) + "-" + day;
        return day + " - " + meses[month] + " - " + year;
    }
        private String obtener_hora(){

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            String min = String.valueOf(minute);
            String hora = String.valueOf(hour);

            if(minute < 10) {
                min = "0" + minute;
            }
            if(hour < 10){
                hora = "0" + hour;
            }
            hora_mysql = hora + ":" + min;
            return hora + ":" + min + " hs." ;
    }

    private int procesar_respuesta(JSONObject respuesta){
        try {
            int estado = respuesta.getInt("estado");
            switch (estado){
                case 1:
                    if (isModoEdicion){
                        Toast.makeText(getBaseContext(), "La actualización se ha realizado correctamente", Toast.LENGTH_SHORT).show();
                        final int EDITADO = 44;
                        Intent intent = new Intent();
                        intent.putExtra("nombre", nombre_event);
                        intent.putExtra("descripcion", descrip);
                        setResult(EDITADO, intent);
                        finish();
                    }else {
                        if (id_nuevo_evento == 0){
                            id_nuevo_evento = respuesta.getInt("lastId");
                        }
                        url = Constantes.URL_BASE + "eventos/actualizar_evento.php";
                    }
                    break;
                case 2:
                    Toast.makeText(NuevoEvento.this, "Ha ocurrido un error.\n " +
                            "Por favor, Inténtelo de nuevo", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(NuevoEvento.this, "Ha ocurrido un error de autenticación", Toast.LENGTH_SHORT).show();
                    break;
                case 4://actualizacion
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return id_nuevo_evento;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == EXITO){
            inicializar_spinners();
        }
    }

    private String convertirFecha(String fechaMysql){
        String[] array = fechaMysql.split("-");
        return array[2] + " - " +meses[Integer.valueOf(array[1]) - 1] + " - " + array[0];
    }
}
