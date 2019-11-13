package com.esy.jaha.osmdroid.syncSQLiteMySQL.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.esy.jaha.osmdroid.R;

import java.io.IOException;

/**
 * Adaptador del recycler view
 */
public class AdaptadorDeCategorias extends RecyclerView.Adapter<AdaptadorDeCategorias.ViewHolder> {
    private Cursor cursor;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        TextView  nueva_categoria;
        ImageView ic_nva_cat;
        ViewHolder(View v) {
            super(v);
            nueva_categoria =  (TextView) v.findViewById(R.id.textView_nueva_categoria);
            ic_nva_cat = (ImageView) v.findViewById(R.id.iv_cat);
         }
    }

    AdaptadorDeCategorias(Context context) {

        this.context= context;
    }

    @Override
    public int getItemCount() {
        if (cursor != null){

            return cursor.getCount();
        }
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        cursor.moveToPosition(i);
        final int COL_NOM_CATEGORIA = 1;
        final int COL_ICONO = 2;
        String categoria;
        String icono;

        categoria = cursor.getString(COL_NOM_CATEGORIA);
        icono = cursor.getString(COL_ICONO);

        viewHolder.nueva_categoria.setText(categoria);
        try {
            viewHolder.ic_nva_cat.setImageBitmap(BitmapFactory.decodeStream(context.getAssets().open(icono + ".png")));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
//        viewHolder.ic_nva_cat.setImageResource(id_icono);

    }

    void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return cursor;
    }
}