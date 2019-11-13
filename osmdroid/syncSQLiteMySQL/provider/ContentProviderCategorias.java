package com.esy.jaha.osmdroid.syncSQLiteMySQL.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.esy.jaha.osmdroid.MainActivity;

public class ContentProviderCategorias extends ContentProvider {    /**Content Provider personalizado  */

public static final String DATABASE_NAME = "TESIS.db";          /**     * Nombre de la base de datos     */
//final int DATABASE_VERSION = 4;                    /**     * Versión actual de la base de datos     */
private ContentResolver contentResolver;                    /**    * Instancia global del Content Resolver     */

@Override
public boolean onCreate() {

   // databaseHelper = new SQLiteBD(getContext(), DATABASE_NAME, null, DATABASE_VERSION);// Inicializando gestor BD
    try {
        contentResolver = getContext().getContentResolver();

    }catch (java.lang.NullPointerException e)
    {

    }
    return true;
}

    @Override  //PROJECTION: determina las columnas y SELECTION: determina las filas.
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = MainActivity.db;//databaseHelper.getWritableDatabase();       // Obtener base de datos
        int match = ContratoProvider.uriMatcher.match(uri);              // Comparar Uri, retorna el codigo de la URI
        Cursor c = null;
        switch (match) {
            case ContratoProvider.ALLROWS:                                  // Consultando todos los registros

                c = db.query(ContratoProvider.CATEGORIA, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(contentResolver, ContratoProvider.CONTENT_URI);
                break;
            case ContratoProvider.SINGLE_ROW:                            // Consultando un solo registro basado en el Id del Uri
                long idCategoria = ContentUris.parseId(uri);
                c = db.query(ContratoProvider.CATEGORIA, projection, ContratoProvider.Columnas._ID + " = " + idCategoria, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(contentResolver, ContratoProvider.CONTENT_URI);
                break;
            case ContratoProvider.ALLROWS_CAT_EVENT:                                  // Consultando todos los registros
                c = db.query(ContratoProvider.CATEGORIA_EVENTO, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(contentResolver, ContratoProvider.CONTENT_URI_CAT_EVENT);
                break;
            case ContratoProvider.SINGLE_ROW_CAT_EVENT:                            // Consultando un solo registro basado en el Id del Uri
                long id_cat_even = ContentUris.parseId(uri);
                c = db.query(ContratoProvider.CATEGORIA_EVENTO, projection, ContratoProvider.ColumnasCatEvent._ID + " = " + id_cat_even, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(contentResolver, ContratoProvider.CONTENT_URI_CAT_EVENT);
                break;
            case ContratoProvider.ALLROWS_SUPER_CAT:                                  // Consultando todos los registros
                c = db.query(ContratoProvider.SUPER_CATEGORIA, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(contentResolver, ContratoProvider.CONTENT_URI_SUPER_CATEGORIA);
                break;
            case ContratoProvider.SINGLE_ROW_SUPER_CAT:                            // Consultando un solo registro basado en el Id del Uri
                long id_super_cat = ContentUris.parseId(uri);
                c = db.query(ContratoProvider.SUPER_CATEGORIA, projection, ContratoProvider.ColumnasSuperCategorias._ID + " = " + id_super_cat, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(contentResolver, ContratoProvider.CONTENT_URI_SUPER_CATEGORIA);
                break;
            case ContratoProvider.ALLROWS_CAT_PROMO:                                  // Consultando todos los registros
                c = db.query(ContratoProvider.CATEGORIA_PROMO, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(contentResolver, ContratoProvider.CONTENT_URI_CAT_PROMO);
                break;
            case ContratoProvider.SINGLE_ROW_CAT_PROMO:                            // Consultando un solo registro basado en el Id del Uri
                long id_cat_promo = ContentUris.parseId(uri);
                c = db.query(ContratoProvider.CATEGORIA_PROMO, projection, ContratoProvider.ColumnasCatPromo._ID + " = " + id_cat_promo, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(contentResolver, ContratoProvider.CONTENT_URI_CAT_PROMO);
                break;
            case ContratoProvider.ALLROWS_CAT_ESPECIAL:                                  // Consultando todos los registros
                try {
                    c = db.query(ContratoProvider.CATEGORIA_ESPECIAL, projection, selection, selectionArgs, null, null, sortOrder);
                    c.setNotificationUri(contentResolver, ContratoProvider.CONTENT_URI_CAT_ESPECIAL);
                    break;
                }catch (NullPointerException e){
                    break;
                }
            case ContratoProvider.SINGLE_ROW_CAT_ESPECIAL:                            // Consultando un solo registro basado en el Id del Uri
                long id_cat_especial = ContentUris.parseId(uri);
                c = db.query(ContratoProvider.CATEGORIA_ESPECIAL, projection, ContratoProvider.ColumnasCatEspecial._ID + " = " + id_cat_especial, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(contentResolver, ContratoProvider.CONTENT_URI_CAT_ESPECIAL);
                break;
            default:
                throw new IllegalArgumentException("URI no soportada: " + uri);
        }
        return c;

    }

    @Override
    public String getType(Uri uri) {
        switch (ContratoProvider.uriMatcher.match(uri)) {
            case ContratoProvider.ALLROWS:
                return ContratoProvider.MULTIPLE_MIME;
            case ContratoProvider.SINGLE_ROW:
                return ContratoProvider.SINGLE_MIME;
            case ContratoProvider.ALLROWS_CAT_EVENT:
                return ContratoProvider.MULTIPLE_MIME_CAT_EVENT;
            case ContratoProvider.SINGLE_ROW_CAT_EVENT:
                return ContratoProvider.SINGLE_MIME_CAT_EVENT;
            case ContratoProvider.ALLROWS_SUPER_CAT:
                return ContratoProvider.MULTIPLE_MIME_SUPER_CATEGORIA;
            case ContratoProvider.SINGLE_ROW_SUPER_CAT:
                return ContratoProvider.SINGLE_MIME_SUPER_CATEGORIA;
            case ContratoProvider.ALLROWS_CAT_PROMO:
                return ContratoProvider.MULTIPLE_MIME_CAT_PROMO;
            case ContratoProvider.SINGLE_ROW_CAT_PROMO:
                return ContratoProvider.SINGLE_MIME_CAT_PROMO;
            case ContratoProvider.ALLROWS_CAT_ESPECIAL:
                return ContratoProvider.MULTIPLE_MIME_CAT_ESPECIAL;
            case ContratoProvider.SINGLE_ROW_CAT_ESPECIAL:
                return ContratoProvider.SINGLE_MIME_CAT_ESPECIAL;
            default:
                throw new IllegalArgumentException("Tipo de REGISTRO desconocido: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //la URI de la solicitud de inserción
        int match = ContratoProvider.uriMatcher.match(uri);
        if (match!= ContratoProvider.ALLROWS && match!=ContratoProvider.ALLROWS_CAT_EVENT && match!= ContratoProvider.ALLROWS_SUPER_CAT
                && match!= ContratoProvider.ALLROWS_CAT_PROMO && match!= ContratoProvider.ALLROWS_CAT_ESPECIAL ) {                   // Validar la uri
            throw new IllegalArgumentException("URI desconocida : " + uri);
        }
        ContentValues contentValues;
        if (values != null) {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }
        SQLiteDatabase db = MainActivity.db;//databaseHelper.getWritableDatabase();
        long rowId = 0;
        Uri uri_utilizado = null;
        switch (match) {
            case ContratoProvider.ALLROWS:
                rowId = db.insert(ContratoProvider.CATEGORIA, null, contentValues);
                uri_utilizado = ContratoProvider.CONTENT_URI;
                break;
            case ContratoProvider.ALLROWS_CAT_EVENT:
                rowId = db.insert(ContratoProvider.CATEGORIA_EVENTO, null, contentValues);
                uri_utilizado = ContratoProvider.CONTENT_URI_CAT_EVENT;
                break;
            case ContratoProvider.ALLROWS_SUPER_CAT:
                rowId = db.insert(ContratoProvider.SUPER_CATEGORIA, null, contentValues);
                uri_utilizado = ContratoProvider.CONTENT_URI_SUPER_CATEGORIA;
                break;
            case ContratoProvider.ALLROWS_CAT_PROMO:
                rowId = db.insert(ContratoProvider.CATEGORIA_PROMO, null, contentValues);
                uri_utilizado = ContratoProvider.CONTENT_URI_CAT_PROMO;
                break;
            case ContratoProvider.ALLROWS_CAT_ESPECIAL:
                rowId = db.insert(ContratoProvider.CATEGORIA_ESPECIAL, null, contentValues);
                uri_utilizado = ContratoProvider.CONTENT_URI_CAT_ESPECIAL;
                break;
        }
        if (rowId > 0) {

            Uri uri_categoria = ContentUris.withAppendedId( uri_utilizado , rowId);
            contentResolver.notifyChange(uri_categoria, null, false);
            return uri_categoria;
        }
        throw new SQLException("Falla al insertar fila en : " + uri+" match = "+ match);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = MainActivity.db;

        int match = ContratoProvider.uriMatcher.match(uri);
        int affected;
        switch (match) {
            case ContratoProvider.ALLROWS:
                affected = db.delete(ContratoProvider.CATEGORIA, selection, selectionArgs);
                break;
            case ContratoProvider.SINGLE_ROW:
                long idCategoria = ContentUris.parseId(uri);
                affected = db.delete(ContratoProvider.CATEGORIA, ContratoProvider.Columnas.ID_REMOTA + "=" + idCategoria + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                contentResolver.notifyChange(uri, null, false);                     // Notificar cambio asociado a la uri
                break;
            case ContratoProvider.ALLROWS_CAT_EVENT:
                affected = db.delete(ContratoProvider.CATEGORIA_EVENTO, selection, selectionArgs);
                break;
            case ContratoProvider.SINGLE_ROW_CAT_EVENT:
                idCategoria = ContentUris.parseId(uri);
                affected = db.delete(ContratoProvider.CATEGORIA_EVENTO, ContratoProvider.ColumnasCatEvent.ID_REMOTA + "=" + idCategoria + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                contentResolver.notifyChange(uri, null, false);                     // Notificar cambio asociado a la uri
                break;
            case ContratoProvider.ALLROWS_SUPER_CAT:
                affected = db.delete(ContratoProvider.SUPER_CATEGORIA, selection, selectionArgs);
                break;
            case ContratoProvider.SINGLE_ROW_SUPER_CAT:
                idCategoria = ContentUris.parseId(uri);
                affected = db.delete(ContratoProvider.SUPER_CATEGORIA, ContratoProvider.ColumnasSuperCategorias.ID_REMOTA + "=" + idCategoria + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                contentResolver.notifyChange(uri, null, false);                     // Notificar cambio asociado a la uri
                break;
            case ContratoProvider.ALLROWS_CAT_PROMO:
                affected = db.delete(ContratoProvider.CATEGORIA_PROMO, selection, selectionArgs);
                break;
            case ContratoProvider.SINGLE_ROW_CAT_PROMO:
                idCategoria = ContentUris.parseId(uri);
                affected = db.delete(ContratoProvider.CATEGORIA_PROMO, ContratoProvider.ColumnasCatPromo.ID_REMOTA + "=" + idCategoria + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                contentResolver.notifyChange(uri, null, false);                     // Notificar cambio asociado a la uri
                break;
            case ContratoProvider.ALLROWS_CAT_ESPECIAL:
                affected = db.delete(ContratoProvider.CATEGORIA_ESPECIAL, selection, selectionArgs);
                break;
            case ContratoProvider.SINGLE_ROW_CAT_ESPECIAL:
                idCategoria = ContentUris.parseId(uri);
                affected = db.delete(ContratoProvider.CATEGORIA_ESPECIAL, ContratoProvider.ColumnasCatEspecial.ID_REMOTA + "=" + idCategoria + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                contentResolver.notifyChange(uri, null, false);                     // Notificar cambio asociado a la uri
                break;
            default:
                throw new IllegalArgumentException("Elemento  desconocido: " + uri);
        }
        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = MainActivity.db;
        String idCategoria;
        int affected;
        switch (ContratoProvider.uriMatcher.match(uri)) {
            case ContratoProvider.ALLROWS:
                affected = db.update(ContratoProvider.CATEGORIA, values, selection, selectionArgs);
                break;
            case ContratoProvider.SINGLE_ROW:
                idCategoria = uri.getPathSegments().get(1);
                affected = db.update(ContratoProvider.CATEGORIA, values, ContratoProvider.Columnas.ID_REMOTA + "=" + idCategoria
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case ContratoProvider.ALLROWS_CAT_EVENT:
                affected = db.update(ContratoProvider.CATEGORIA_EVENTO, values, selection, selectionArgs);
                break;
            case ContratoProvider.SINGLE_ROW_CAT_EVENT:
                idCategoria = uri.getPathSegments().get(1);
                affected = db.update(ContratoProvider.CATEGORIA_EVENTO, values, ContratoProvider.ColumnasCatEvent.ID_REMOTA + "=" + idCategoria
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case ContratoProvider.ALLROWS_SUPER_CAT:
                affected = db.update(ContratoProvider.SUPER_CATEGORIA, values, selection, selectionArgs);
                break;
            case ContratoProvider.SINGLE_ROW_SUPER_CAT:
                idCategoria = uri.getPathSegments().get(1);
                affected = db.update(ContratoProvider.SUPER_CATEGORIA, values, ContratoProvider.ColumnasSuperCategorias.ID_REMOTA + "=" + idCategoria
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case ContratoProvider.ALLROWS_CAT_PROMO:
                affected = db.update(ContratoProvider.CATEGORIA_PROMO, values, selection, selectionArgs);
                break;
            case ContratoProvider.SINGLE_ROW_CAT_PROMO:
                idCategoria = uri.getPathSegments().get(1);
                affected = db.update(ContratoProvider.CATEGORIA_PROMO, values, ContratoProvider.ColumnasCatPromo.ID_REMOTA + "=" + idCategoria
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case ContratoProvider.ALLROWS_CAT_ESPECIAL:
                affected = db.update(ContratoProvider.CATEGORIA_ESPECIAL, values, selection, selectionArgs);
                break;
            case ContratoProvider.SINGLE_ROW_CAT_ESPECIAL:
                idCategoria = uri.getPathSegments().get(1);
                affected = db.update(ContratoProvider.CATEGORIA_ESPECIAL, values, ContratoProvider.ColumnasCatEspecial.ID_REMOTA + "=" + idCategoria
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }
        contentResolver.notifyChange(uri, null, false);
        return affected;
    }

}

