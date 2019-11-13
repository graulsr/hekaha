package com.esy.jaha.osmdroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RecyclerCardAdaptador extends RecyclerView.Adapter<RecyclerCardAdaptador.MyViewHolder> {

    private ImageLoader mImageLoader;
    private Activity context;
    private Gson gson;
    public static List<RecyclerCardItem> data;
    private LayoutInflater inflater;
    private CardFragment.OnFragmentInteraction mListener;
    private int height;
    private float densidad;

    RecyclerCardAdaptador(Context context, int height, CardFragment.OnFragmentInteraction mListener) {
        gson = new Gson();
        this.mListener = mListener;
        RecyclerCardItem[] res = gson.fromJson(MapFragment.localesBoxJSON != null ? MapFragment.localesBoxJSON.toString() : null, RecyclerCardItem[].class);   // Parsear con Gson
        if(res != null) {
            data = Arrays.asList(res);
        }
        this.context = (Activity) context;
        this.height = height;
        inflater = LayoutInflater.from(context);
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        densidad = dm.density;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View view = inflater.inflate(R.layout.card_item, parent, false);

        MyViewHolder holder;
        holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {

        myViewHolder.networkImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ActivityPagina.class);
                    intent.putExtra("id", data.get(position).getId());
                    intent.putExtra("latitud", data.get(position).getLatitud());
                    intent.putExtra("longitud", data.get(position).getLongitud());
                    intent.putExtra("marcador", data.get(position).getMarcador());
                    intent.putExtra("icono", data.get(position).getIcono());
                    intent.putExtra("nombre", data.get(position).getNombreLocal());
                    intent.putExtra("descripcion", data.get(position).getdescripcion());
                    intent.putExtra("foto_portada", data.get(position).getPortada());
                    intent.putExtra("isNuevo", false);
                    intent.putExtra("tipo", MapFragment.tipo);
                    context.startActivityForResult(intent, 111);//NO IMPORTA EL CODIGO
                }
            });
            myViewHolder.tv_title.setText(data.get(position).getNombreLocal());

            String descr = data.get(position).getdescripcion();

            myViewHolder.tv_descripcion.setText(descr);

            try {
                Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(data.get(position).getMarcador() + ".png"));
                if (densidad > 1) {
                    Matrix matrix = new Matrix();
                    matrix.postScale(1.4f, 1.4f);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, 32, 37, matrix, false);
                }
                myViewHolder.imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mListener.onFragmentCardInteraction(data.get(position).getLatitud(), data.get(position).getLongitud(), data.get(position).getId());
                }
            });
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
       holder.networkImageView. setLayoutParams( new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));

        int position = holder.getAdapterPosition();
        if(data.get(position).getPortada().equals("null")){
            holder.networkImageView.setImageUrl(Constantes.URL_IMAGENES + MapFragment.tipo + "/portadaDefault/thumbs/portadaDefault.png", mImageLoader);
        }else {
            holder.networkImageView.setImageUrl(Constantes.URL_IMAGENES + MapFragment.tipo + "/" + data.get(position).getId() + "/portada/thumbs/" + data.get(position).getPortada(), mImageLoader);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

    }

    @Override
    public int getItemCount() {
        if(data == null){
            return 0;
        }else
        {
        return data.size();}
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_title, tv_descripcion;
        NetworkImageView networkImageView;
        ImageView imageView;

        MyViewHolder(View itemView) {
            super(itemView);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.netImgView);
            networkImageView.setErrorImageResId(R.drawable.fondo_main);
            tv_title = (TextView) itemView.findViewById(R.id.tv_card_title);
            tv_descripcion = (TextView) itemView.findViewById(R.id.tv_card_descripcion);
            imageView = (ImageView) itemView.findViewById(R.id.iv_posicion);
        }
    }

    void actualizarDataset(){

        RecyclerCardItem[] res = gson.fromJson(MapFragment.localesBoxJSON != null ? MapFragment.localesBoxJSON.toString() : null, RecyclerCardItem[].class);   // Parsear con Gson
        if(res != null) {
            data = Arrays.asList(res);
        }else{
            data = null;
        }
        notifyDataSetChanged();

    }
}
