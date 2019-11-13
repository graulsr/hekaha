package com.esy.jaha.osmdroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.esy.jaha.osmdroid.constants.OpenStreetMapConstants;

public class SessionManager implements OpenStreetMapConstants {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    private static SessionManager mInstancia;
    // Shared Preferences
    private SharedPreferences pref;

    private Editor editor;
    private Context _context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    private SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static synchronized SessionManager getInstancia(Context ctx){

        if(mInstancia == null){
            mInstancia = new SessionManager(ctx);
        }
        return  mInstancia;
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(SESION_INICIADA, isLoggedIn);
        // apply changes
        editor.commit();
    }

    public String getSuperCAt4Name(){

        return pref.getString(SUPER_CAT4_NAME, "");
    }

    public void setSuperCat4(String superCat4){

        editor.putString(SUPER_CAT4_NAME, superCat4);
        editor.commit();

    }

     boolean isNewAdded(){

        return pref.getBoolean(NEW_ADDED, false);
    }


    public void setNewAddedToTrue(){
        editor.putBoolean(NEW_ADDED, true);
        editor.commit();
    }

    public void setIsNewAddedToFalse(){
        editor.putBoolean(NEW_ADDED, false);
        editor.commit();
    }
    public void setTipeOfNewAdded(int tipe){
        editor.putInt(TIPE_OF_NEW_ADDED, tipe);
        editor.commit();
    }

    public void setUserEmail(String email){
        editor.putString(USER_EMAIL, email);
        editor.commit();
    }

     void set_user_id(int user_id) {

        editor.putInt(USER_ID, user_id);
        // apply changes
        editor.commit();
    }

    boolean isLoggedIn(){

        return pref.getBoolean(SESION_INICIADA, false);
    }

    public int getUserId(){
        return pref.getInt(USER_ID, 0);
    }

     void setSpinerItemSelected(int position){
        editor.putInt(SPINER_ITEM_SELECTED, position);
        editor.apply();
    }

    public void setMainDrawerItemSelected(int position){
        editor.putInt(MAIN_DRAWER_ITEM_SELECTED, position);
        editor.apply();
    }

    public void setCuentaDrawerItemSelected(int position){
        editor.putInt(CUENTA_DRAWER_ITEM_SELECTED, position);
        editor.apply();
    }

    int getSpinerItemSelected(){
        return pref.getInt(SPINER_ITEM_SELECTED, 1);
    }

    public String getUserName(){
        return pref.getString(USER_NAME, "Nombre de usuario");
    }

    public String getEmail(){
        return pref.getString(USER_EMAIL, "Correo electrónico");
    }

    public int getMainDrawerItemSelected(){
        return pref.getInt(MAIN_DRAWER_ITEM_SELECTED, 2);
    }

    public int getCuentaDrawerItemSelected(){
        return pref.getInt(CUENTA_DRAWER_ITEM_SELECTED, 2);
    }

    public void setToken(String token){
        editor.putString(TOKEN, token);
        editor.commit();
    }
    public String getToken(){
        return pref.getString(TOKEN, "");
    }

    public void setUserName(String name){
        editor.putString(USER_NAME, name);
        editor.commit();
    }
     void setUltimaSicronizacion(long timeMillis){
        editor.putLong(ULTIMA_SINCRONIZACION, timeMillis);
         editor.apply();
    }

     long getUltimaSicronizacion(){

        return pref.getLong(ULTIMA_SINCRONIZACION, 0);
    }

    public boolean isFirstTime(){

        return pref.getBoolean(PRIMERA_VEZ, true);
    }

    public void setFirstTimeToFalse(){
        editor.putBoolean(PRIMERA_VEZ, false);
        editor.apply();
    }

    public void setIdCatOfNewAdded(int idCatOfNewAdded){
        editor.putInt(ID_CAT_OF_NEW_ADDED, idCatOfNewAdded);
        editor.apply();
    }

    int getIdCatOfNewAdded(){
        return pref.getInt(ID_CAT_OF_NEW_ADDED, 0);
    }

    int getTipeOfNewAdded(){
        return pref.getInt(TIPE_OF_NEW_ADDED, 0);
    }

    public void setInfo(String info){

        editor.putString(INFO, info);
        editor.apply();
    }

    public  String getInfo(){

        return pref.getString(INFO, "Sin novedades");
    }
}