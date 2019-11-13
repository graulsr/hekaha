package com.esy.jaha.osmdroid.syncSQLiteMySQL.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.esy.jaha.osmdroid.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SQLiteBD extends SQLiteOpenHelper {                                                                 /** * Clase envoltura para el gestor de Bases de datos */
    private String[] sql = {"categorias_evento.sql", "categorias.sql", "super_categorias.sql", "categorias_promo.sql"};
    private Context mContext;

public SQLiteBD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
    super(context, name, factory, version);
    mContext = context;
  //  DBPath = "/data/data/" + mContext.getPackageName() + "/databases";
}

    public void onCreate(SQLiteDatabase database) {
            createTable(database);
        // Crear la tabla "CATEGORIAS"
    }

    /**    * Crear tabla en la base de datos   * @param database Instancia de la base de datos*/
    private void createTable(SQLiteDatabase database) {
        String cmd = "CREATE TABLE " + ContratoProvider.CATEGORIA + " (" +
                ContratoProvider.Columnas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoProvider.Columnas.CATEGORIA + " TEXT UNIQUE, " +
                ContratoProvider.Columnas.ICONO + " TEXT," +
                ContratoProvider.Columnas.ID_REMOTA + " INTEGER UNIQUE," +
//                ContratoProvider.Columnas.ID_USER + " INTEGER UNSIGNED NOT NULL DEFAULT 0," +
                ContratoProvider.Columnas.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContratoProvider.ESTADO_OK+"," +
                ContratoProvider.Columnas.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd);

        String cmd_cat_event = "CREATE TABLE " + ContratoProvider.CATEGORIA_EVENTO + " (" +
                ContratoProvider.ColumnasCatEvent._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoProvider.ColumnasCatEvent.CAT_EVEN + " TEXT UNIQUE, " +
                ContratoProvider.ColumnasCatEvent.ICONO + " TEXT," +
                ContratoProvider.ColumnasCatEvent.ID_REMOTA + " INTEGER UNIQUE," +
//                ContratoProvider.ColumnasCatEvent.ID_USER + " INTEGER NOT NULL DEFAULT 0,"+
                ContratoProvider.ColumnasCatEvent.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContratoProvider.ESTADO_OK+"," +
                ContratoProvider.ColumnasCatEvent.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd_cat_event);

        String cmd_supercat = "CREATE TABLE " + ContratoProvider.SUPER_CATEGORIA + " (" +
                ContratoProvider.ColumnasSuperCategorias._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoProvider.ColumnasSuperCategorias.SUPER_CATEGORIA + " TEXT UNIQUE, " +
                ContratoProvider.ColumnasSuperCategorias.DESCRIPCION + " TEXT, " +
                ContratoProvider.ColumnasSuperCategorias.ICONO + " TEXT," +
                ContratoProvider.ColumnasSuperCategorias.ID_REMOTA + " INTEGER UNIQUE," +
//                ContratoProvider.ColumnasCatEvent.ID_USER + " INTEGER NOT NULL DEFAULT 0," +
                ContratoProvider.ColumnasSuperCategorias.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContratoProvider.ESTADO_OK+"," +
                ContratoProvider.ColumnasSuperCategorias.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd_supercat);

        String cmd_cat_promo= "CREATE TABLE " + ContratoProvider.CATEGORIA_PROMO + " (" +
                ContratoProvider.ColumnasCatPromo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoProvider.ColumnasCatPromo.CAT_PROMO + " TEXT UNIQUE, " +
                ContratoProvider.ColumnasCatPromo.ICONO + " TEXT," +
                ContratoProvider.ColumnasCatPromo.ID_REMOTA + " INTEGER UNIQUE," +
//                ContratoProvider.ColumnasCatPromo.ID_USER + " INTEGER NOT NULL DEFAULT 0," +
                ContratoProvider.ColumnasCatPromo.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContratoProvider.ESTADO_OK+"," +
                ContratoProvider.ColumnasCatPromo.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd_cat_promo);

        String cmd_cat_especial = "CREATE TABLE " + ContratoProvider.CATEGORIA_ESPECIAL + " (" +
                ContratoProvider.ColumnasCatEspecial._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoProvider.ColumnasCatEspecial.CAT_ESPECIAL + " TEXT UNIQUE, " +
                ContratoProvider.ColumnasCatEspecial.ICONO + " TEXT," +
                ContratoProvider.ColumnasCatEspecial.ID_REMOTA + " INTEGER UNIQUE," +
//                ContratoProvider.ColumnasCatEspecial.ID_USER + " INTEGER UNSIGNED NOT NULL DEFAULT 0," +
                ContratoProvider.ColumnasCatEspecial.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContratoProvider.ESTADO_OK+"," +
                ContratoProvider.ColumnasCatEspecial.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd_cat_especial);

           if(SessionManager.getInstancia(mContext).isFirstTime()) {
               InputStream is = null;
               try {
                   for (int i = 0; i < 4; i++) {
                       is = mContext.getAssets().open(sql[i]);
                       if (is != null) {
//                           database.beginTransaction();
                           BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                           String line = reader.readLine();
                           while (!TextUtils.isEmpty(line)) {
                               database.execSQL(line);
                               line = reader.readLine();
                           }
                       }
                   }
//                   database.setTransactionSuccessful();
               } catch (Exception ex) {
               } finally {
//                   database.endTransaction();
                   if (is != null) {
                       try {
                           is.close();
                       } catch (IOException e) {

                       }
                   }
               }
           }
        }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {

            db.execSQL("drop table " + ContratoProvider.CATEGORIA);
            db.execSQL("drop table " + ContratoProvider.CATEGORIA_EVENTO);
            db.execSQL("drop table " + ContratoProvider.SUPER_CATEGORIA);
            db.execSQL("drop table " + ContratoProvider.CATEGORIA_PROMO);
            db.execSQL("drop table " + ContratoProvider.CATEGORIA_ESPECIAL);
        }
        catch (SQLiteException e) {

        }
        onCreate(db);
    }

}
