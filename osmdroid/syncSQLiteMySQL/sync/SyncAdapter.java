package com.esy.jaha.osmdroid.syncSQLiteMySQL.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.SessionManager;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.provider.ContratoProvider;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Utilidades;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.web.Categoria;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.web.CategoriaEspecial;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.web.CategoriaEvento;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.web.CategoriaPromo;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.web.SuperCategoria;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncAdapter extends AbstractThreadedSyncAdapter {              /** * Maneja la transferencia de datos entre el servidor y el cliente */
    private static final String TAG = SyncAdapter.class.getSimpleName();

    private ContentResolver resolver;
    private Gson gson = new Gson();
    private Context context;
    private static final String[] PROJECTION = new String[]{                 /**     * Proyección para las consultas     */
            ContratoProvider.Columnas._ID,
            ContratoProvider.Columnas.ID_REMOTA,
            ContratoProvider.Columnas.CATEGORIA,
            ContratoProvider.Columnas.ICONO,
//            ContratoProvider.Columnas.ID_USER //
    };
    private static final String[] PROJECTION_CAT_EVENT = new String[]{                 /**     * Proyección para las consultas     */
            ContratoProvider.ColumnasCatEvent._ID,
            ContratoProvider.ColumnasCatEvent.ID_REMOTA,
            ContratoProvider.ColumnasCatEvent.CAT_EVEN,
            ContratoProvider.ColumnasCatEvent.ICONO,
//            ContratoProvider.ColumnasCatEvent.ID_USER

    };
    private static final String[] PROJECTION_SUPER_CAT = new String[]{                 /**     * Proyección para las consultas     */
            ContratoProvider.ColumnasSuperCategorias._ID,
            ContratoProvider.ColumnasSuperCategorias.ID_REMOTA,
            ContratoProvider.ColumnasSuperCategorias.SUPER_CATEGORIA,
            ContratoProvider.ColumnasSuperCategorias.ICONO,
            ContratoProvider.ColumnasSuperCategorias.DESCRIPCION
    };
    private static final String[] PROJECTION_CAT_PROMO = new String[]{                 /**     * Proyección para las consultas     */
            ContratoProvider.ColumnasCatPromo._ID,
            ContratoProvider.ColumnasCatPromo.ID_REMOTA,
            ContratoProvider.ColumnasCatPromo.CAT_PROMO,
            ContratoProvider.ColumnasCatPromo.ICONO,
//            ContratoProvider.ColumnasCatPromo.ID_USER

    };
    private static final String[] PROJECTION_CAT_ESPECIAL = new String[]{                 /**     * Proyección para las consultas     */
            ContratoProvider.ColumnasCatEspecial._ID,
            ContratoProvider.ColumnasCatEspecial.ID_REMOTA,
            ContratoProvider.ColumnasCatEspecial.CAT_ESPECIAL,
            ContratoProvider.ColumnasCatEspecial.ICONO,
//            ContratoProvider.ColumnasCatEspecial.ID_USER

    };
    private static final int COLUMNA_ID = 0;                                     // Indices para las columnas indicadas en la proyección
    private static final int COLUMNA_ID_REMOTA = 1;
    private static final int COLUMNA_CATEGORIA = 2;
    private static final int COLUMNA_ICONO = 3;
    private static final int COLUMNA_DESCRIPCION = 4;

    SyncAdapter(Context context, boolean autoInitialize) {

        super(context, autoInitialize);
        resolver = context.getContentResolver();
        this.context = context;
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {      /**     * Constructor para mantener compatibilidad en versiones inferiores a 3.0     */

        super(context, autoInitialize, allowParallelSyncs);
        resolver = context.getContentResolver();
        this.context = context;
    }

    public static void inicializarSyncAdapter(Context context) {

        obtenerCuentaASincronizar(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, final SyncResult syncResult) {

        boolean soloSubida = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);

        if (!soloSubida){
            realizarSincronizacionLocal(syncResult);
        }
//        else {
            //realizarSincronizacionRemota();
//        }
    }

    private void realizarSincronizacionLocal(final SyncResult syncResult) {

        MySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_URL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGet(response, syncResult);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }
                )
        );
    }

    /**    * Procesa la respuesta del servidor al pedir que se retornen todAs las categorias.
     * @param response   Respuesta en formato Json
     * @param syncResult Registro de resultados de sincronización
     */
    private void procesarRespuestaGet(JSONObject response, SyncResult syncResult) {
        try {
            int estado = response.getInt(Constantes.ESTADO); // Obtener atributo "estado"

            switch (estado) {
                case Constantes.SUCCESS: // EXITO
                    actualizarDatosLocales(response, syncResult);
                    break;
                case Constantes.FAILED: // FALLIDO
                    String mensaje = response.getString(Constantes.MENSAJE);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void realizarSincronizacionRemota() {
       // Toast.makeText(getContext(),"Actualizando...",Toast.LENGTH_SHORT).show();
        iniciarActualizacion();
        Cursor c = obtenerRegPendientes_cat();
        Cursor cat_even = obtenerRegPendientes_cat_event();

        Cursor cat_promo = obtenerRegPendientes_cat_promo();
        Cursor cat_especial = obtenerRegPendientes_cat_especial();

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                final int idLocal = c.getInt(COLUMNA_ID);

                MySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_URL,
                                Utilidades.deCursorAJSONObject(c, "cat", context),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsert(response, idLocal);

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                }

                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
            }

        }
        c.close();

        if (cat_even.getCount() > 0) {
            while (cat_even.moveToNext()) {
                final int idLocal = cat_even.getInt(COLUMNA_ID);

                MySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_CAT_EVENT_URL,
                                Utilidades.deCursorAJSONObject(cat_even, "cat_event", context),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsert(response, idLocal);

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                }

                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
            }

        }
        cat_even.close();


        if (cat_promo.getCount() > 0) {
            while (cat_promo.moveToNext()) {
                final int idLocal = cat_promo.getInt(COLUMNA_ID);

                MySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_CAT_PROMO_URL,
                                Utilidades.deCursorAJSONObject(cat_promo, "cat_promo", context),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsert(response, idLocal);

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                }

                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
            }

        }
        cat_promo.close();

        if (cat_especial.getCount() > 0) {
            while (cat_especial.moveToNext()) {
                final int idLocal = cat_especial.getInt(COLUMNA_ID);

                MySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_CAT_ESPECIAL_URL,
                                Utilidades.deCursorAJSONObject(cat_especial, "cat_especial", context),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsert(response, idLocal);

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                }

                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
            }

        }
        cat_especial.close();
    }

    /*** Obtiene el registro que se acaba de marcar como "pendiente por sincronizar" y
     * con "estado de sincronización"    * @return Cursor con el registro.*/
    private Cursor obtenerRegPendientes_cat() {
        Uri uri = ContratoProvider.CONTENT_URI;
        String selection = ContratoProvider.Columnas.PENDIENTE_INSERCION + "=? AND " + ContratoProvider.Columnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContratoProvider.ESTADO_SYNC + ""};

        return resolver.query(uri,  PROJECTION, selection, selectionArgs, null);
    }
    private Cursor obtenerRegPendientes_cat_event() {
        Uri uri = ContratoProvider.CONTENT_URI_CAT_EVENT;
        String selection = ContratoProvider.ColumnasCatEvent.PENDIENTE_INSERCION + "=? AND " + ContratoProvider.ColumnasCatEvent.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContratoProvider.ESTADO_SYNC + ""};

        return resolver.query(uri,  PROJECTION_CAT_EVENT, selection, selectionArgs, null);
    }

    private Cursor obtenerRegPendientes_cat_promo() {
        Uri uri = ContratoProvider.CONTENT_URI_CAT_PROMO;
        String selection = ContratoProvider.ColumnasCatPromo.PENDIENTE_INSERCION + "=? AND " + ContratoProvider.ColumnasCatPromo.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContratoProvider.ESTADO_SYNC + ""};

        return resolver.query(uri,  PROJECTION_CAT_PROMO, selection, selectionArgs, null);
    }
    private Cursor obtenerRegPendientes_cat_especial() {
        Uri uri = ContratoProvider.CONTENT_URI_CAT_ESPECIAL;
        String selection = ContratoProvider.ColumnasCatEspecial.PENDIENTE_INSERCION + "=? AND " + ContratoProvider.ColumnasCatEspecial.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContratoProvider.ESTADO_SYNC + ""};

        return resolver.query(uri,  PROJECTION_CAT_ESPECIAL, selection, selectionArgs, null);
    }
    /*** Cambia a estado "de sincronización" el registro que se acaba de insertar localmente*/
    private void iniciarActualizacion() {
        Uri uri = ContratoProvider.CONTENT_URI;
        String selection = ContratoProvider.Columnas.PENDIENTE_INSERCION + "=? AND "
                + ContratoProvider.Columnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContratoProvider.ESTADO_OK + ""};

        ContentValues v = new ContentValues();
        v.put(ContratoProvider.Columnas.ESTADO, ContratoProvider.ESTADO_SYNC);

        int results = resolver.update(uri, v, selection, selectionArgs);

        Uri uri_cat_event = ContratoProvider.CONTENT_URI_CAT_EVENT;
        String selection_cat_event = ContratoProvider.ColumnasCatEvent.PENDIENTE_INSERCION + "=? AND "
                + ContratoProvider.ColumnasCatEvent.ESTADO + "=?";
        String[] selectionArgs_cat_event = new String[]{"1", ContratoProvider.ESTADO_OK + ""};

        ContentValues v_cat_event = new ContentValues();
        v_cat_event.put(ContratoProvider.ColumnasCatEvent.ESTADO, ContratoProvider.ESTADO_SYNC);

        int results_cat_event = resolver.update(uri_cat_event, v_cat_event, selection_cat_event, selectionArgs_cat_event);

        Uri uri_super_cat = ContratoProvider.CONTENT_URI_SUPER_CATEGORIA;
        String selection_super_cat = ContratoProvider.ColumnasSuperCategorias.PENDIENTE_INSERCION + "=? AND "
                + ContratoProvider.ColumnasSuperCategorias.ESTADO + "=?";
        String[] selectionArgs_super_cat = new String[]{"1", ContratoProvider.ESTADO_OK + ""};

        ContentValues v_super_cat = new ContentValues();
        v_super_cat.put(ContratoProvider.ColumnasSuperCategorias.ESTADO, ContratoProvider.ESTADO_SYNC);

        int results_super_cat = resolver.update(uri_super_cat, v_super_cat, selection_super_cat, selectionArgs_super_cat);

        Uri uri_cat_promo = ContratoProvider.CONTENT_URI_CAT_PROMO;
        String selection_cat_promo = ContratoProvider.ColumnasCatPromo.PENDIENTE_INSERCION + "=? AND "
                + ContratoProvider.ColumnasCatPromo.ESTADO + "=?";
        String[] selectionArgs_cat_promo = new String[]{"1", ContratoProvider.ESTADO_OK + ""};

        ContentValues v_cat_promo = new ContentValues();
        v_cat_promo.put(ContratoProvider.ColumnasCatPromo.ESTADO, ContratoProvider.ESTADO_SYNC);

        int results_cat_promo = resolver.update(uri_cat_promo, v_cat_promo, selection_cat_promo, selectionArgs_cat_promo);

        Uri uri_cat_especial = ContratoProvider.CONTENT_URI_CAT_ESPECIAL;
        String selection_cat_especial = ContratoProvider.ColumnasCatEspecial.PENDIENTE_INSERCION + "=? AND "
                + ContratoProvider.ColumnasCatEspecial.ESTADO + "=?";
        String[] selectionArgs_cat_especial = new String[]{"1", ContratoProvider.ESTADO_OK + ""};

        ContentValues v_cat_especial = new ContentValues();
        v_cat_especial.put(ContratoProvider.ColumnasCatEspecial.ESTADO, ContratoProvider.ESTADO_SYNC);

        resolver.update(uri_cat_especial, v_cat_especial, selection_cat_especial, selectionArgs_cat_especial);

    }

    /*** Limpia el registro que se sincronizó y le asigna la nueva id remota proveida
     * por el servidor * @param idRemota id remota*/
    private void finalizarActualizacion(String idRemota, int idLocal, String tipo_cat) {

        switch (tipo_cat) {
            case "cat":
                Uri uri = ContratoProvider.CONTENT_URI;
                String selection = ContratoProvider.Columnas._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(idLocal)};

                ContentValues v = new ContentValues();
                v.put(ContratoProvider.Columnas.PENDIENTE_INSERCION, "0");
                v.put(ContratoProvider.Columnas.ESTADO, ContratoProvider.ESTADO_OK);
                v.put(ContratoProvider.Columnas.ID_REMOTA, idRemota);

                resolver.update(uri, v, selection, selectionArgs);
                break;

            case "cat_event":
                Uri uri_cat_event = ContratoProvider.CONTENT_URI_CAT_EVENT;
                String sel_cat_event = ContratoProvider.ColumnasCatEvent._ID + "=?";
                String[] args_cat_event = new String[]{String.valueOf(idLocal)};

                ContentValues cv_cat_event = new ContentValues();
                cv_cat_event.put(ContratoProvider.ColumnasCatEvent.PENDIENTE_INSERCION, "0");
                cv_cat_event.put(ContratoProvider.ColumnasCatEvent.ESTADO, ContratoProvider.ESTADO_OK);
                cv_cat_event.put(ContratoProvider.ColumnasCatEvent.ID_REMOTA, idRemota);

                resolver.update(uri_cat_event, cv_cat_event, sel_cat_event, args_cat_event);
                break;

            case "super_cat":
                Uri uri_super_cat = ContratoProvider.CONTENT_URI_SUPER_CATEGORIA;
                String sel_super_cat = ContratoProvider.ColumnasSuperCategorias._ID + "=?";
                String[] args_super_cat = new String[]{String.valueOf(idLocal)};

                ContentValues cv_super_cat = new ContentValues();
                cv_super_cat.put(ContratoProvider.ColumnasSuperCategorias.PENDIENTE_INSERCION, "0");
                cv_super_cat.put(ContratoProvider.ColumnasSuperCategorias.ESTADO, ContratoProvider.ESTADO_OK);
                cv_super_cat.put(ContratoProvider.ColumnasSuperCategorias.ID_REMOTA, idRemota);

                resolver.update(uri_super_cat, cv_super_cat, sel_super_cat, args_super_cat);
                break;

            case "cat_promo":
                Uri uri_cat_promo = ContratoProvider.CONTENT_URI_CAT_PROMO;
                String sel_cat_promo = ContratoProvider.ColumnasCatPromo._ID + "=?";
                String[] args_cat_promo = new String[]{String.valueOf(idLocal)};

                ContentValues cv_cat_promo = new ContentValues();
                cv_cat_promo.put(ContratoProvider.ColumnasCatPromo.PENDIENTE_INSERCION, "0");
                cv_cat_promo.put(ContratoProvider.ColumnasCatPromo.ESTADO, ContratoProvider.ESTADO_OK);
                cv_cat_promo.put(ContratoProvider.ColumnasCatPromo.ID_REMOTA, idRemota);

                resolver.update(uri_cat_promo, cv_cat_promo, sel_cat_promo, args_cat_promo);
                break;

            case "cat_especial":
                Uri uri_cat_especial = ContratoProvider.CONTENT_URI_CAT_ESPECIAL;
                String sel_cat_especial = ContratoProvider.ColumnasCatEspecial._ID + "=?";
                String[] args_cat_especial = new String[]{String.valueOf(idLocal)};

                ContentValues cv_cat_especial = new ContentValues();
                cv_cat_especial.put(ContratoProvider.ColumnasCatEspecial.PENDIENTE_INSERCION, "0");
                cv_cat_especial.put(ContratoProvider.ColumnasCatEspecial.ESTADO, ContratoProvider.ESTADO_OK);
                cv_cat_especial.put(ContratoProvider.ColumnasCatEspecial.ID_REMOTA, idRemota);

                resolver.update(uri_cat_especial, cv_cat_especial, sel_cat_especial, args_cat_especial);
                break;

        }

    }
    /*** Procesa los diferentes tipos de respuesta obtenidos del servidor
     * @param response Respuesta en formato Json
     * @param idLocal El id en el registro local */
    private void procesarRespuestaInsert(JSONObject response, int idLocal) {
        try {
            int estado = response.getInt(Constantes.ESTADO);          // Obtener estado
            String mensaje = response.getString(Constantes.MENSAJE);  // Obtener InfoCupones
            String idRemota = response.getString(Constantes.ID_CATEGORIA);// Obtener identificador del nuevo registro creado en el servidor
            String tipo_cat = response.getString(Constantes.TIPO_CAT); // obtener tipo de Categoria insertada
            switch (estado) {
                case Constantes.SUCCESS:
                    finalizarActualizacion(idRemota, idLocal, tipo_cat);
                    break;

                case Constantes.FAILED:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor    * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización     */
    private void actualizarDatosLocales(JSONObject response, SyncResult syncResult) {

        JSONArray categorias = null;
        JSONArray categorias_evento = null;
        JSONArray super_categorias = null;
        JSONArray categorias_promo = null;
        JSONArray categorias_especial = null;
        try {
            categorias = response.getJSONArray(Constantes.CATEGORIA);    // Obtener array "categorias"
            categorias_evento = response.getJSONArray(Constantes.CATEGORIA_EVENTO);
            super_categorias = response.getJSONArray(Constantes.SUPER_CATEGORIA);
            categorias_promo = response.getJSONArray(Constantes.CATEGORIA_PROMO);
            categorias_especial = response.getJSONArray(Constantes.CATEGORIA_ESPECIAL);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Categoria[] res = gson.fromJson(categorias != null ? categorias.toString() : null, Categoria[].class);   // Parsear con Gson
        List<Categoria> data = Arrays.asList(res);
        CategoriaEvento[] res_event = gson.fromJson(categorias_evento != null ? categorias_evento.toString() : null, CategoriaEvento[].class);   // Parsear con Gson
        List<CategoriaEvento> data_event = Arrays.asList(res_event);
        SuperCategoria[] res_super_cat = gson.fromJson(super_categorias != null ? super_categorias.toString() : null, SuperCategoria[].class);   // Parsear con Gson
        List<SuperCategoria> data_super_cat = Arrays.asList(res_super_cat);
        CategoriaPromo[] res_cat_promo = gson.fromJson(categorias_promo != null ? categorias_promo.toString() : null, CategoriaPromo[].class);   // Parsear con Gson
        List<CategoriaPromo> data_cat_promo = Arrays.asList(res_cat_promo);
        CategoriaEspecial[] res_cat_especial = gson.fromJson(categorias_especial != null ? categorias_especial.toString() : null, CategoriaEspecial[].class);   // Parsear con Gson
        List<CategoriaEspecial> data_cat_especial = null;

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();            // Lista para recolección de operaciones pendientes

        if(res_cat_especial != null) {
            data_cat_especial = Arrays.asList(res_cat_especial);
            HashMap<String, CategoriaEspecial> map_cat_especial = new HashMap<>();
            for (CategoriaEspecial e : data_cat_especial) {
                map_cat_especial.put(e.id_categoria, e);
            }

            Uri uri_cat_especial = ContratoProvider.CONTENT_URI_CAT_ESPECIAL;              // Consultar registros remotos actuales
            String select_cat_especial = ContratoProvider.ColumnasCatEspecial.ID_REMOTA + " IS NOT NULL";
            Cursor c_cat_especial = resolver.query(uri_cat_especial, PROJECTION_CAT_ESPECIAL, select_cat_especial, null, null);
            assert c_cat_especial != null;            //  if (c == null) throw new AssertionError();

            while (c_cat_especial.moveToNext()) {
                syncResult.stats.numEntries++;

               String id = c_cat_especial.getString(COLUMNA_ID_REMOTA);
               String categoria = c_cat_especial.getString(COLUMNA_CATEGORIA);
               String icono = c_cat_especial.getString(COLUMNA_ICONO);

                CategoriaEspecial cat_especial = map_cat_especial.get(id);

                if (cat_especial != null) {
                    map_cat_especial.remove(id);// Esta entrada existe, por lo que se remueve del mapeado

                    Uri existingUri = ContratoProvider.CONTENT_URI_CAT_ESPECIAL.buildUpon()
                            .appendPath(id).build();

                    boolean b = !cat_especial.categoria.equals(categoria);                           // Comprobar  necesita ser actualizado
                    boolean b3 = !cat_especial.icono.equals("") && !cat_especial.icono.equals(icono);   // boolean b3 = match.id_icono != null && !match.id_icono.equals(id_icono);

                    if (b || b3) {
                        ops.add(ContentProviderOperation.newUpdate(existingUri)
                                .withValue(ContratoProvider.ColumnasCatEspecial.CAT_ESPECIAL, cat_especial.categoria)
                                .withValue(ContratoProvider.ColumnasCatEspecial.ICONO, cat_especial.icono)
                                .build());
                        syncResult.stats.numUpdates++;
                    }
                } else {
                    // Debido a que la entrada no existe, es removida de la base de datos
                    Uri deleteUri = ContratoProvider.CONTENT_URI_CAT_ESPECIAL.buildUpon()
                            .appendPath(id).build();
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                    syncResult.stats.numDeletes++;
                }
            }
            c_cat_especial.close();

            for (CategoriaEspecial e : map_cat_especial.values()) {
                ops.add(ContentProviderOperation.newInsert(ContratoProvider.CONTENT_URI_CAT_ESPECIAL)
                        .withValue(ContratoProvider.ColumnasCatEspecial.ID_REMOTA, e.id_categoria)
                        .withValue(ContratoProvider.ColumnasCatEspecial.CAT_ESPECIAL, e.categoria)
                        .withValue(ContratoProvider.ColumnasCatEspecial.ICONO, e.icono)
                        .build());
                syncResult.stats.numInserts++;
            }
        }

        HashMap<String, Categoria> map_categoria = new HashMap<String, Categoria>();// Tabla hash para recibir las  entrantes
        HashMap<String, CategoriaEvento> map_cat_event = new HashMap<String, CategoriaEvento>();
        HashMap<String, SuperCategoria> map_super_categoria = new HashMap<>();
        HashMap<String, CategoriaPromo> map_cat_promo = new HashMap<>();


        for (Categoria e : data) {
            map_categoria.put(e.id_categoria, e);
        }
        for (CategoriaEvento e : data_event) {
            map_cat_event.put(e.id_categoria, e);
        }
        for (SuperCategoria e : data_super_cat) {
            map_super_categoria.put(e.id_supercategoria, e);
        }
        for (CategoriaPromo e : data_cat_promo) {
            map_cat_promo.put(e.id_categoria, e);
        }

        Uri uri = ContratoProvider.CONTENT_URI;              // Consultar registros remotos actuales
        String select = ContratoProvider.Columnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, PROJECTION, select, null, null);
        assert c != null;            //  if (c == null) throw new AssertionError();

        Uri uri_cat_event = ContratoProvider.CONTENT_URI_CAT_EVENT;              // Consultar registros remotos actuales
        String select_cat_event = ContratoProvider.ColumnasCatEvent.ID_REMOTA + " IS NOT NULL";
        Cursor cat_event = resolver.query(uri_cat_event, PROJECTION_CAT_EVENT, select_cat_event, null, null);
        assert cat_event != null;

        Uri uri_super_cat = ContratoProvider.CONTENT_URI_SUPER_CATEGORIA;              // Consultar registros remotos actuales
        String select_super_cat = ContratoProvider.ColumnasSuperCategorias.ID_REMOTA + " IS NOT NULL";
        Cursor c_super_cat = resolver.query(uri_super_cat, PROJECTION_SUPER_CAT, select_super_cat, null, null);
        assert c_super_cat != null;            //  if (c == null) throw new AssertionError();

        Uri uri_cat_prom = ContratoProvider.CONTENT_URI_CAT_PROMO  ;              // Consultar registros remotos actuales
        String select_cat_promo = ContratoProvider.ColumnasCatPromo.ID_REMOTA + " IS NOT NULL";
        Cursor c_cat_promo = resolver.query(uri_cat_prom, PROJECTION_CAT_PROMO, select_cat_promo, null, null);
        assert c_cat_promo != null;            //  if (c == null) throw new AssertionError();


        // Encontrar datos obsoletos
        String id;
        String categoria;
        String icono;
        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            id = c.getString(COLUMNA_ID_REMOTA);
            categoria = c.getString(COLUMNA_CATEGORIA);
            icono = c.getString(COLUMNA_ICONO);

            Categoria cat = map_categoria.get(id);

            if (cat != null) {
                map_categoria.remove(id);// Esta entrada existe, por lo que se remueve del mapeado

                Uri existingUri = ContratoProvider.CONTENT_URI.buildUpon()
                        .appendPath(id).build();

                boolean b = !cat.categoria.equals(categoria);                           // Comprobar si necesita ser actualizado

                boolean b3 = !cat.icono.equals("") && !cat.icono.equals(icono);   // boolean b3 = match.id_icono != null && !match.id_icono.equals(id_icono);

                if (b || b3) {
                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContratoProvider.Columnas.CATEGORIA, cat.categoria)
                            .withValue(ContratoProvider.Columnas.ICONO, cat.icono)
                            .build());
                    syncResult.stats.numUpdates++;
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContratoProvider.CONTENT_URI.buildUpon()
                        .appendPath(id).build();
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        while (cat_event.moveToNext()) {
            syncResult.stats.numEntries++;

            id = cat_event.getString(COLUMNA_ID_REMOTA);
            categoria = cat_event.getString(COLUMNA_CATEGORIA);
            icono = cat_event.getString(COLUMNA_ICONO);

            CategoriaEvento cat_even = map_cat_event.get(id);

            if (cat_even != null) {
                map_cat_event.remove(id);// Esta entrada existe, por lo que se remueve del mapeado

                Uri existingUri = ContratoProvider.CONTENT_URI_CAT_EVENT.buildUpon()
                        .appendPath(id).build();

                boolean b = !cat_even.categoria.equals(categoria);                           // Comprobar si necesita ser actualizado
                boolean b3 = !cat_even.icono.equals("") && !cat_even.icono.equals(icono);   // boolean b3 = match.id_icono != null && !match.id_icono.equals(id_icono);

                if (b || b3) {
                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContratoProvider.ColumnasCatEvent.CAT_EVEN, cat_even.categoria)
                            .withValue(ContratoProvider.ColumnasCatEvent.ICONO, cat_even.icono)
                            .build());
                    syncResult.stats.numUpdates++;
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContratoProvider.CONTENT_URI_CAT_EVENT.buildUpon()
                        .appendPath(id).build();
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        cat_event.close();

        while (c_super_cat.moveToNext()) {
            syncResult.stats.numEntries++;

            id = c_super_cat.getString(COLUMNA_ID_REMOTA);
            categoria = c_super_cat.getString(COLUMNA_CATEGORIA);
            icono = c_super_cat.getString(COLUMNA_ICONO);
            String descripcion  = c_super_cat.getString(COLUMNA_DESCRIPCION);
            SuperCategoria super_cat = map_super_categoria.get(id);

            if (super_cat != null) {
                map_super_categoria.remove(id);// Esta entrada existe, por lo que se remueve del mapeado

                Uri existingUri = ContratoProvider.CONTENT_URI_SUPER_CATEGORIA.buildUpon()
                        .appendPath(id).build();

                boolean b = !super_cat.super_categoria.equals(categoria);                           // Comprobar si necesita ser actualizado
                boolean b3 = !super_cat.icono.equals("") && !super_cat.icono.equals(icono);   // boolean b3 = match.id_icono != null && !match.id_icono.equals(id_icono);
                boolean b2 = !super_cat.descripcion.equals(descripcion);
                if (b ||b2|| b3) {
                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContratoProvider.ColumnasSuperCategorias.SUPER_CATEGORIA, super_cat.super_categoria)
                            .withValue(ContratoProvider.ColumnasSuperCategorias.DESCRIPCION, super_cat.descripcion)
                            .withValue(ContratoProvider.ColumnasSuperCategorias.ICONO, super_cat.icono)
                            .build());
                    syncResult.stats.numUpdates++;
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContratoProvider.CONTENT_URI_SUPER_CATEGORIA.buildUpon()
                        .appendPath(id).build();
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c_super_cat.close();

        while (c_cat_promo.moveToNext()) {
            syncResult.stats.numEntries++;

            id = c_cat_promo.getString(COLUMNA_ID_REMOTA);
            categoria = c_cat_promo.getString(COLUMNA_CATEGORIA);
            icono = c_cat_promo.getString(COLUMNA_ICONO);

            CategoriaPromo cat_promo = map_cat_promo.get(id);

            if (cat_promo != null) {
                map_cat_promo.remove(id);// Esta entrada existe, por lo que se remueve del mapeado

                Uri existingUri = ContratoProvider.CONTENT_URI_CAT_PROMO.buildUpon()
                        .appendPath(id).build();

                boolean b = !cat_promo.categoria.equals(categoria);                           // Comprobar si  necesita ser actualizado
                boolean b3 = !cat_promo.icono.equals("") && !cat_promo.icono.equals(icono);   // boolean b3 = match.id_icono != null && !match.id_icono.equals(id_icono);

                if (b || b3) {
                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                                .withValue(ContratoProvider.ColumnasCatPromo.CAT_PROMO, cat_promo.categoria)
                            .withValue(ContratoProvider.ColumnasCatPromo.ICONO, cat_promo.icono)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContratoProvider.CONTENT_URI_CAT_PROMO.buildUpon()
                        .appendPath(id).build();
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c_cat_promo.close();


        // Insertar items resultantes

        for (Categoria e : map_categoria.values()) {
            ops.add(ContentProviderOperation.newInsert(ContratoProvider.CONTENT_URI)
                    .withValue(ContratoProvider.Columnas.ID_REMOTA, e.id_categoria)
                    .withValue(ContratoProvider.Columnas.CATEGORIA, e.categoria)
                    .withValue(ContratoProvider.Columnas.ICONO, e.icono)
                    .build());
            syncResult.stats.numInserts++;
        }
        for (CategoriaEvento e : map_cat_event.values()) {

            ops.add(ContentProviderOperation.newInsert(ContratoProvider.CONTENT_URI_CAT_EVENT)
                    .withValue(ContratoProvider.ColumnasCatEvent.ID_REMOTA, e.id_categoria)
                    .withValue(ContratoProvider.ColumnasCatEvent.CAT_EVEN, e.categoria)
                    .withValue(ContratoProvider.ColumnasCatEvent.ICONO, e.icono)
                    .build());
            syncResult.stats.numInserts++;
        }
        for (SuperCategoria e : map_super_categoria.values()) {
            ops.add(ContentProviderOperation.newInsert(ContratoProvider.CONTENT_URI_SUPER_CATEGORIA)
                    .withValue(ContratoProvider.ColumnasSuperCategorias.ID_REMOTA, e.id_supercategoria)
                    .withValue(ContratoProvider.ColumnasSuperCategorias.SUPER_CATEGORIA, e.super_categoria)
                    .withValue(ContratoProvider.ColumnasSuperCategorias.DESCRIPCION, e.descripcion)
                    .withValue(ContratoProvider.ColumnasSuperCategorias.ICONO, e.icono)
                    .build());
            syncResult.stats.numInserts++;
        }
        for (CategoriaPromo e : map_cat_promo.values()) {
            ops.add(ContentProviderOperation.newInsert(ContratoProvider.CONTENT_URI_CAT_PROMO)
                    .withValue(ContratoProvider.ColumnasCatPromo.ID_REMOTA, e.id_categoria)
                    .withValue(ContratoProvider.ColumnasCatPromo.CAT_PROMO, e.categoria)
                    .withValue(ContratoProvider.ColumnasCatPromo.ICONO, e.icono)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            try {
                resolver.applyBatch(ContratoProvider.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(ContratoProvider.CONTENT_URI, null, false);
            resolver.notifyChange(ContratoProvider.CONTENT_URI_CAT_EVENT, null, false);
            resolver.notifyChange(ContratoProvider.CONTENT_URI_SUPER_CATEGORIA, null, false);
            resolver.notifyChange(ContratoProvider.CONTENT_URI_CAT_PROMO, null, false);
            resolver.notifyChange(ContratoProvider.CONTENT_URI_CAT_ESPECIAL, null, false);

        }
         SessionManager sessionManager =  SessionManager.getInstancia(context);
        if (sessionManager.isFirstTime()) {
            sessionManager.setFirstTimeToFalse();
        }

    }


    /**
     * Inicia manualmente la sincronización
     *
     * @param context    Contexto para crear la petición de sincronización
     * @param onlyUpload Usa true para sincronizar el servidor o false para sincronizar el cliente
     */
    public static void sincronizarAhora(Context context, boolean onlyUpload) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (onlyUpload)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
        ContentResolver.requestSync(obtenerCuentaASincronizar(context),
                context.getString(R.string.provider_authority), bundle);
    }

    /**
     * Crea u obtiene una cuenta existente
     * @param context Contexto para acceder al administrador de cuentas
     * @return cuenta auxiliar.
     */
    private static Account obtenerCuentaASincronizar(Context context) {
        // Obtener instancia del administrador de cuentas
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(context.getString(R.string.app_name), Constantes.ACCOUNT_TYPE);  // Crear cuenta por defecto

        // Comprobar existencia de la cuenta
        if (null == accountManager.getPassword(newAccount)) {

            // Añadir la cuenta al account manager sin password y sin datos de usuario
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
                return null;
        }
        return newAccount;
    }

}