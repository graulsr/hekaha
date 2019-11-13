package com.esy.jaha.osmdroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;


public class CustomSpinnerAdapter extends ArrayAdapter<SpinnerItem>{

    Context context;
    private int layoutResID;
    private List<SpinnerItem> spinnerData;

    public CustomSpinnerAdapter(Context context, int layoutResourceID, int textViewResourceId, List<SpinnerItem> spinnerDataList) {
        super(context, layoutResourceID, textViewResourceId, spinnerDataList);

        this.context = context;
        this.layoutResID = layoutResourceID;
        this.spinnerData = spinnerDataList;


    }

    CustomSpinnerAdapter(Context context, int layoutResourceID, List<SpinnerItem> spinnerDataList) {
        super(context, layoutResourceID, spinnerDataList);
        this.context = context;
        this.layoutResID = layoutResourceID;
        this.spinnerData = spinnerDataList;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        return getCustomView(position, convertView, parent);
    }

    @Override
    public long getItemId(int position) {
        return 0; //getItem(position)
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        SpinnerHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();

            row = inflater.inflate(layoutResID, parent, false);
            holder = new SpinnerHolder();
            holder.userImage = (ImageView)row.findViewById(R.id.left_pic);
            holder.name = (TextView)row.findViewById(R.id.text_main_name);
            holder.descripcion = (TextView)row.findViewById(R.id.subdescripcion);

            row.setTag(holder);
        }
        else
        {
            holder = (SpinnerHolder)row.getTag();

        }

        SpinnerItem spinnerItem = spinnerData.get(position);

        try {
            holder.userImage.setImageBitmap(BitmapFactory.decodeStream(context.getAssets().open(spinnerItem.getIcono() + ".png")));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
//        holder.userImage.setImageDrawable(row.getResources().getDrawable(R.drawable.sim_award_symbol));

        holder.name.setText(spinnerItem.getName());
        holder.descripcion.setText(spinnerItem.getDescripcion());

        return row;
    }

    private static class SpinnerHolder
    {
        ImageView userImage;
        TextView name;
        TextView descripcion;

    }

}