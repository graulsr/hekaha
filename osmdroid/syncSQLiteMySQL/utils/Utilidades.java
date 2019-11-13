package com.esy.jaha.osmdroid.syncSQLiteMySQL.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import androidx.core.content.ContextCompat;

import com.esy.jaha.osmdroid.SessionManager;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.provider.ContratoProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class Utilidades {
    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_ID_REMOTA = 1;
    public static final int COLUMNA_CATEGORIA = 2;
    public static final int COLUMNA_ICONO = 3;
    public static final int COLUMNA_ID_USER = 4;

    /**
     * Determina si la aplicación corre en versiones superiores o iguales
     * a Android LOLLIPOP
     *
     * @return booleano de confirmación
     */
    public static boolean materialDesign() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
    public static boolean isV23orLower() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N;
    }
    /**
     * Copia los datos de un cursor hacia un
     * JSONObject
     *
     * @param c cursor
     * @return objeto jason
     */
    public static JSONObject deCursorAJSONObject(Cursor c, String cat, Context context) {
        JSONObject jObject = new JSONObject();
        String categoria;
        String id_icon;
        String id_user;
        String descripcion;

        categoria = c.getString(COLUMNA_CATEGORIA);
        id_icon = c.getString(COLUMNA_ICONO);
        id_user = c.getString(COLUMNA_ID_USER);

    switch (cat) {
        case "cat":
            try {
                jObject.put(ContratoProvider.Columnas.CATEGORIA, categoria);
                jObject.put(ContratoProvider.Columnas.ICONO, id_icon);
                jObject.put(ContratoProvider.Columnas.ID_USER, id_user);
                break;

            } catch (JSONException e) {
                    e.printStackTrace();
                }
        case "cat_event":
            try {
                jObject.put(ContratoProvider.ColumnasCatEvent.CAT_EVEN, categoria);
                jObject.put(ContratoProvider.ColumnasCatEvent.ICONO, id_icon);
                jObject.put(ContratoProvider.ColumnasCatEvent.ID_USER, id_user);
                break;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        case "cat_promo":
            try {
                jObject.put(ContratoProvider.ColumnasCatPromo.CAT_PROMO, categoria);
                jObject.put(ContratoProvider.ColumnasCatPromo.ICONO, id_icon);
                jObject.put(ContratoProvider.ColumnasCatPromo.ID_USER, id_user);
                break;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        case "cat_especial":
            try {
                jObject.put(ContratoProvider.ColumnasCatEspecial.CAT_ESPECIAL, categoria);
                jObject.put(ContratoProvider.ColumnasCatEspecial.ICONO, id_icon);
                jObject.put(ContratoProvider.ColumnasCatEspecial.ID_USER, id_user);
                break;

            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
        try {
            String token = SessionManager.getInstancia(context).getToken();
            jObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObject;
    }

    private static int calculateInSampleSize(
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
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
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

    public static Bitmap decodedBitmap(InputStream inputStream, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(inputStream, new Rect(1,1,1,1), options);
    }

    public static String capitalizeFirstCharacter(String textInput){

        String input = textInput.toLowerCase();
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public  static boolean hasPermission( String permission, Context context) {
//        for (String permission : permissions)
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context, permission))
            return false;
        return true;
    }

/*    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }*/

}
