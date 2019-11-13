package com.esy.jaha.osmdroid.nuevo;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;

import java.util.List;

public class GaleriaAdaptador extends RecyclerView.Adapter<GaleriaAdaptador.ViewHolder> {

    private LayoutInflater inflater;
    private ImageLoader mImageLoader;
    private Context context;
    private List<RecyclerGaleriaItem> fotos;
    private int height;
    private String carpeta;
    private String[] nombresFotos;
    private Intent intent;

     GaleriaAdaptador(Context context, String carpeta, List<RecyclerGaleriaItem> data, String tipo, String nombre, int height, String marker){

        inflater = LayoutInflater.from(context);
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        this.carpeta = carpeta;
        fotos = data;
        this.context = context;
        this.height = height;
        intent = new Intent(context, ImagenDetalleActivity.class);
        intent.putExtra("carpeta", carpeta);
        intent.putExtra("nombre", nombre);
        intent.putExtra("marcador", marker);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View view = inflater.inflate(R.layout.card_foto_galeria, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder myViewHolder, final int position) {

        String descripcionFoto = fotos.get(position).getDescripcion_foto();
/*        if (descripcionFoto.contains("euro_simbol")) {
            descripcionFoto = descripcionFoto.replace("euro_simbol", "€");
        }
        if (descripcionFoto.contains("libra_simbol")) {
            descripcionFoto = descripcionFoto.replace("libra_simbol", "£");
        }*/

            myViewHolder.txtv_descripcion.setText(descripcionFoto);

///*        myViewHolder.tres_puntos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int id = fotos.get(position).getId_foto();
//                showPopupMenu(myViewHolder.tres_puntos, id, position);
//            }
//        });*/

//*/
        myViewHolder.ntwrkImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent.putExtra("extra_image", position);
                intent.putExtra("num_fotos", fotos.size());
                intent.putExtra("nom_fotos",  nombresFotos);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        if(fotos == null){
            return 0;
        }else
        {
            return fotos.size();}
    }
    @Override
    public void onViewAttachedToWindow( ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getAdapterPosition();
        holder.ntwrkImgv. setLayoutParams( new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        holder.ntwrkImgv.setImageUrl(carpeta + "thumbs/"+ fotos.get(position).getNombre_foto(), mImageLoader);

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtv_descripcion;
        NetworkImageView ntwrkImgv;
        ImageView tres_puntos;

        private ViewHolder(View itemView) {
            super(itemView);
            txtv_descripcion = itemView.findViewById(R.id.tv_descr_gallery);
            ntwrkImgv = itemView.findViewById(R.id.niv_card_gallery);
            ntwrkImgv.setErrorImageResId(R.drawable.fondo_main);

        }
    }

    void actualizarData(List<RecyclerGaleriaItem> data){
        fotos = data;
        nombresFotos = new String[fotos.size()];
        for (int i = 0; i<fotos.size(); i++){
            nombresFotos[i] = fotos.get(i).getNombre_foto();
        }
        notifyDataSetChanged();
    }
}


