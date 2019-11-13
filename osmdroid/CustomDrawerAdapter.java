package com.esy.jaha.osmdroid;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CustomDrawerAdapter extends ArrayAdapter<DrawerItem> {

        static int currentSelected;
        static String mTitle;
        Context context;
        private List<SpinnerItem> spinerList;
        static List<DrawerItem> drawerItemList;
        private int layoutResID;
        private final int LOCALES = 0;
        private final int EVENTOS = 1;
        private final int PROMOCIONADOS = 2;
        private final int ESPECIAL = 3;
        private Collection<DrawerItem> collectionCat, collectionCatEvent, collectionCatPromo, collectionCatEspecial,collectionHoyTodosEvent, currentCollection;
        private boolean primer_llamado = true;
        private CustomSpinnerAdapter adapter;

        CustomDrawerAdapter(Context context, int layoutResourceID, List<DrawerItem> listItems) {
            super(context, layoutResourceID, listItems);
            this.context = context;
            this.layoutResID = layoutResourceID;
            drawerItemList = listItems;
            currentSelected = SessionManager.getInstancia(context).getSpinerItemSelected();
            cargarColecciones();
            adapter = new CustomSpinnerAdapter(context, R.layout.custom_spiner_item, spinerList);
        }

            private void cargarColecciones(){
            try {
                    Cursor super_cat;
                    Cursor cat = null; Cursor cat_event = null; Cursor cat_promo = null; Cursor cat_especial = null;
                    cat = MainActivity.db.rawQuery("SELECT categoria, icono, id_remota FROM categorias ORDER BY categoria", null);
                    cat_event = MainActivity.db.rawQuery("SELECT categoria, icono, id_remota FROM categorias_evento ORDER BY categoria", null);
                    cat_promo = MainActivity.db.rawQuery("SELECT categoria, icono, id_remota FROM categorias_promo ORDER BY categoria", null);
                    collectionCat = getCollection(cat);
                    collectionCatEvent = getCollection(cat_event);
                    collectionCatPromo = getCollection(cat_promo);

                    currentCollection = new ArrayList<>();
                    super_cat = MainActivity.db.rawQuery("SELECT super_categoria, descripcion, icono, id_remota FROM super_categorias ORDER BY id_remota", null);
                    SessionManager sessionManager =  SessionManager.getInstancia(getContext());

                if(super_cat.getCount() == 4){

                    cat_especial = MainActivity.db.rawQuery("SELECT categoria, icono, id_remota FROM categorias_especial ORDER BY categoria", null);
                    collectionCatEspecial = getCollection(cat_especial);
                        super_cat.moveToPosition(3);
                        String cat4 = super_cat.getString(0);
                        sessionManager.setSuperCat4(cat4);
                    }else {
                        if (currentSelected == ESPECIAL){
                            currentSelected = EVENTOS;
                        }
                         sessionManager.setSuperCat4("");
                }
                    inicializarSpinnerList(super_cat);

                collectionHoyTodosEvent = new ArrayList<>();
                collectionHoyTodosEvent.add(new DrawerItem("Todos los eventos", "mk_fest_fireworks", -2));//categoria,id_remota
                collectionHoyTodosEvent.add(new DrawerItem("Eventos de hoy", "mk_fest_party", -3));//categoria,id_remota
//                collectionHoyTodosEvent.add(new DrawerItem("Categorías"));//categoria,id_remota

            }catch (SQLiteException e){
             }
        }

        private void inicializarSpinnerList(Cursor super_cat) {

            spinerList = new ArrayList<SpinnerItem>();

            if (super_cat.moveToFirst()) {

                do {
                    String icono = super_cat.getString(2); // las posiciones dependen de el orden en que se efectuó la consulta
//                    Toast.makeText(getContext(), "icono64: " + icono64, Toast.LENGTH_SHORT).show();
                    String nombre = super_cat.getString(0);
                    String descripcion = super_cat.getString(1);
                    spinerList.add(new SpinnerItem(icono, nombre, descripcion));
                } while (super_cat.moveToNext());
                super_cat.close();
            }

        }


        private Collection<DrawerItem> getCollection(Cursor c){

            Collection<DrawerItem> collection = null;
            if (c.moveToFirst()) {
                collection = new ArrayList<DrawerItem>();
                //Recorremos el cursor hasta que no haya más registros
                do {
                    collection.add(new DrawerItem(c.getString(0), c.getString(1), c.getInt(2)));//categoria,id_remota

                } while(c.moveToNext());
            }
            c.close();
            return  collection;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final DrawerItemHolder drawerHolder;
            View view = convertView;
           // Toast.makeText(context, "Llamando a getView",  Toast.LENGTH_SHORT).show();

            if (view == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                drawerHolder = new DrawerItemHolder();
               // Toast.makeText(context, "convertView resultó null",  Toast.LENGTH_SHORT).show();
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

            DrawerItem dItem = (DrawerItem) drawerItemList.get(position);

            if (dItem.isSpinner()) {
                drawerHolder.imageLayout.setVisibility(LinearLayout.GONE);
                drawerHolder.headerLayout.setVisibility(LinearLayout.GONE);
                drawerHolder.itemLayout.setVisibility(LinearLayout.GONE);
                drawerHolder.spinnerLayout.setVisibility(LinearLayout.VISIBLE);
                drawerHolder.spinner.setAdapter(adapter);
                drawerHolder.spinner.setSelection(currentSelected);
                //imagen cabecera, spinner, titulo categorias
                drawerHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int posicion, long id) {
                                //parent.getItemAtPosition(posicion);
                               // DrawerItem[] mlistCat = gson.fromJson(categorias_promo != null ? categorias_promo.toString() : null, CategoriaPromo[].class);   // Parsear con Gson
                                //MainActivity.dataList = Arrays.asList(res_cat_promo);
                              if (currentSelected != posicion || primer_llamado) {
                                  if (currentSelected == 1){
                                      drawerItemList.removeAll(currentCollection);
                                      drawerItemList.removeAll(collectionHoyTodosEvent);

                                  }else {
                                      drawerItemList.removeAll(currentCollection);
                                      //Toast.makeText(context, "removeAll resultó:  " + b,  Toast.LENGTH_SHORT).show();
                                  }
                                  notifyDataSetChanged();
                                  switch (posicion) {

                                      case LOCALES:
                                          currentSelected = LOCALES;
                                          currentCollection = collectionCat;
                                          drawerItemList.addAll(3, collectionCat);
                                          MapFragment.tipo = "locales";
                                          break;

                                      case EVENTOS:
                                          currentSelected = EVENTOS;
                                          currentCollection = collectionCatEvent;
                                          drawerItemList.addAll(2, collectionHoyTodosEvent);
                                          drawerItemList.addAll(5, collectionCatEvent);
                                          MapFragment.tipo = "eventos";
                                          break;

                                      case PROMOCIONADOS:
                                          currentSelected = PROMOCIONADOS;
                                          currentCollection = collectionCatPromo;
                                          drawerItemList.addAll(3, collectionCatPromo);
                                          MapFragment.tipo = "promos";
                                          break;

                                      case ESPECIAL:
                                          currentSelected = ESPECIAL;
                                          if (collectionCatEspecial != null) {
                                              currentCollection = collectionCatEspecial;
                                              drawerItemList.addAll(3, collectionCatEspecial);
                                          }
                                          MapFragment.tipo = "especiales";
                                          break;
                                  }

                                  mTitle = adapter.getItem(posicion).getName();;
                                  if (currentSelected == EVENTOS){
                                      MainActivity.mDrawerList.setItemChecked(4, true);
                                  }else {
                                      MainActivity.mDrawerList.setItemChecked(2, true);
                                  }
                                  notifyDataSetChanged();
                              }

                              primer_llamado = false;
                                //Toast.makeText(context, "User Changed: posicion  " + posicion,  Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {

                            }
                        });

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

//                drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(dItem.getImgResID()));
//                drawerHolder.icon.setImageResource(dItem.getImgResID());
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

    public String getTitle() {
        return mTitle;
    }
}
