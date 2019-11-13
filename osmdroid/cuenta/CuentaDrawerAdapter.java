package com.esy.jaha.osmdroid.cuenta;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.esy.jaha.osmdroid.BdSingleton;
import com.esy.jaha.osmdroid.DrawerItem;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CuentaDrawerAdapter extends ArrayAdapter<DrawerItem> {

    private int currentSelected;
    private Context context;
    static List<DrawerItem> drawerItemList;
    private int layoutResID;

        CuentaDrawerAdapter(Context context, int layoutResourceID, List<DrawerItem> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.layoutResID = layoutResourceID;
        drawerItemList = listItems;
        cargarColecciones();
    }

        private void cargarColecciones(){
        try {

                Collection<DrawerItem> currentCollection = new ArrayList<>();

                SQLiteDatabase db = BdSingleton.getInstance(getContext()).getSqLiteBD().getReadableDatabase();
                Cursor super_cat = db.rawQuery("SELECT super_categoria, icono, id_remota FROM super_categorias ORDER BY id_remota", null);
                SessionManager sessionManager =  SessionManager.getInstancia(getContext());

            if(super_cat.getCount() == 4){

                    super_cat.moveToPosition(3);
                    String cat4 = super_cat.getString(0);
                    sessionManager.setSuperCat4(cat4);
                }else {
                    final int ESPECIAL = 3;
                    if (currentSelected == ESPECIAL){
                        final int EVENTOS = 1;
                        currentSelected = EVENTOS ;
                    }

                    sessionManager.setSuperCat4("");
            }

                currentCollection = getCollection(super_cat);
                drawerItemList.addAll(2, currentCollection);

        }catch (SQLiteException e){
        }
        }

    private Collection<DrawerItem> getCollection(Cursor c){

        Collection<DrawerItem> collection = null;

        if (c.moveToFirst()) {
            collection = new ArrayList<DrawerItem>();
            //Recorremos el cursor hasta que no haya más registros
            do {
                if (c.isLast() && c.getCount() == 4)
                {
                    collection.add(new DrawerItem( c.getString(0), c.getString(1), c.getInt(2)));//categoria,id_remota
                    break;
                }
                collection.add(new DrawerItem("Mis " + c.getString(0), c.getString(1), c.getInt(2)));//categoria,id_remota

            } while(c.moveToNext());
        }
        c.close();
        return  collection;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final DrawerItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new DrawerItemHolder();
            view = inflater.inflate(layoutResID, parent, false);
            drawerHolder.ItemName = (TextView) view.findViewById(R.id.drawer_itemName);
            drawerHolder.icon = (ImageView) view.findViewById(R.id.drawer_icon);
            drawerHolder.spinner = (Spinner) view.findViewById(R.id.drawerSpinner);
            drawerHolder.title = (TextView) view.findViewById(R.id.drawerTitle);
            drawerHolder.textViewHeaderName = (TextView) view.findViewById(R.id.tv_header_name);
            drawerHolder.textViewHaderEmail = (TextView) view.findViewById(R.id.tv_header_email);
            drawerHolder.headerLayout = (LinearLayout) view.findViewById(R.id.headerLayout);
            drawerHolder.itemLayout = (LinearLayout) view.findViewById(R.id.itemLayout);
            drawerHolder.spinnerLayout = (LinearLayout) view.findViewById(R.id.spinnerLayout);
            drawerHolder.imageLayout = (RelativeLayout) view.findViewById(R.id.imageLayout);
            view.setTag(drawerHolder);
        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();
        }

        DrawerItem dItem = (DrawerItem) this.drawerItemList.get(position);

        if (dItem.isSpinner()) {
            drawerHolder.imageLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.headerLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.itemLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.spinnerLayout.setVisibility(LinearLayout.VISIBLE);
//            drawerHolder.spinner.setAdapter(adapter);
            drawerHolder.spinner.setSelection(currentSelected);


        } else if (dItem.getTitle() != null) {//es titulo cabecera
            drawerHolder.headerLayout.setVisibility(LinearLayout.VISIBLE);
            drawerHolder.itemLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.spinnerLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.imageLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.title.setText(dItem.getTitle());


        } else if(dItem.isCabecera){

            drawerHolder.imageLayout.setVisibility(LinearLayout.VISIBLE);
            drawerHolder.headerLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.spinnerLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.itemLayout.setVisibility(LinearLayout.GONE);

            drawerHolder.textViewHeaderName.setText(dItem.getUserName());
            drawerHolder.textViewHaderEmail.setText(dItem.getUserEmail());

        }
          else {//es item categoria
            drawerHolder.imageLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.headerLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.spinnerLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.itemLayout.setVisibility(LinearLayout.VISIBLE);

            drawerHolder.ItemName.setText(dItem.getItemName());
            try {
                drawerHolder.icon.setImageBitmap(BitmapFactory.decodeStream(context.getAssets().open(dItem.getIcoName() + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    private static class DrawerItemHolder {

        private TextView ItemName, title;
        TextView textViewHeaderName, textViewHaderEmail;
        ImageView icon;
        LinearLayout headerLayout;
        LinearLayout itemLayout;
        LinearLayout spinnerLayout;
        RelativeLayout imageLayout;
        Spinner spinner;
    }


}
