package com.esy.jaha.osmdroid;

/**
 * Created by Romi on 02/11/2016.
 */
public class DrawerItem {

    private String ItemName;
    String nombre;
    private String email;
    private String icoName;
    private int id_remota;
    private String title;
    private boolean isSpinner;
    public boolean isCabecera, isTitle;

    public DrawerItem(String itemName, String ico_name, int id_remota) {
        ItemName = itemName;
        this.icoName = ico_name;
        this.id_remota = id_remota;
    }

    DrawerItem(boolean isSpinner) {
         //this(null, 0);
        this.isSpinner = isSpinner;
    }

    public DrawerItem(String title) {
        //this(null, 0);
        this.title = title;
        this.isTitle = true;
    }

    public DrawerItem(String username, String email){
        isCabecera = true;
        this.nombre = username;
        this.email = email;
    }

    public int getId_remota(){
        return id_remota;}

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }
    public String getIcoName() {
        return icoName;
    }

    public void setImgResID(String ico_name) {
        this.icoName = ico_name;
    }
    public String getUserName(){
        return nombre;
    }
    public String getUserEmail(){
        return email;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSpinner() {
        return isSpinner;
    }
}
