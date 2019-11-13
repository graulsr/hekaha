package com.esy.jaha.osmdroid.nuevo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class GaleriaFotosAdaptador extends RecyclerView.Adapter<GaleriaFotosAdaptador.ViewHolder> {

    private LayoutInflater inflater;
    private ImageLoader mImageLoader;
    private Context context;
    private static List<RecyclerGaleriaItem> fotos;
    private int height;
    private String carpeta;
    private String[] nombresFotos;
    private Intent intent;
    ActivityManager.MemoryInfo memoryInfo;
    private Activity activity;

     GaleriaFotosAdaptador(Context context, String carpeta, List<RecyclerGaleriaItem> data, String nombre, int height, String marker){

        inflater = LayoutInflater.from(context);
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        this.carpeta = carpeta;
        fotos = data;
        this.context = context;
        this.height = height;
        activity = (Activity)context;
        intent = new Intent(context, ImagenDetalleActivity.class);
        intent.putExtra("carpeta", carpeta);
        intent.putExtra("nombre", nombre);
        intent.putExtra("marcador", marker);
     }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View view = inflater.inflate(R.layout.card_foto_galeria, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder myViewHolder, final int position) {

        String descripcionFoto = fotos.get(myViewHolder.getAdapterPosition()).getDescripcion_foto();

            myViewHolder.txtv_descripcion.setText(descripcionFoto);



                    myViewHolder.ntwrkImgv.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              if (fotos.size() > 1) {
                                  nombresFotos = new String[fotos.size() - 1];
                                  for (int i = 0; i < fotos.size() - 1; i++) {
                                      nombresFotos[i] = fotos.get(i).getNombre_foto();
                                  }
                                  intent.putExtra("extra_image", myViewHolder.getAdapterPosition());
                                  intent.putExtra("num_fotos", fotos.size() - 1);
                                  intent.putExtra("nom_fotos", nombresFotos);

                                  context.startActivity(intent);
                              }
                          }
                      }
                    );


        final int id = fotos.get(myViewHolder.getAdapterPosition()).getId_foto();
        final String nombre_imagen = fotos.get(myViewHolder.getAdapterPosition()).getNombre_foto();
        final String finalDescripcionFoto = fotos.get(myViewHolder.getAdapterPosition()).getDescripcion_foto();
        myViewHolder.ntwrkImgv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                Intent intentMenu = new Intent()
                        .putExtra("id_imagen", id)
                        .putExtra("nombre_imagen", nombre_imagen)
                        .putExtra("position", myViewHolder.getAdapterPosition())
                        .putExtra("descripcion", finalDescripcionFoto);
                contextMenu.add(0, 1, 0, "Eliminar Imagen")
                        .setIntent(intentMenu);
                contextMenu.add(0, 2, 1, "Editar Descripción")
                        .setIntent(intentMenu);
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
        holder.ntwrkImgv.setLayoutParams( new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));

        memoryInfo = getAvailableMemory();

        if (!memoryInfo.lowMemory) {
            holder.ntwrkImgv.setImageUrl(carpeta + fotos.get(position).getNombre_foto(), mImageLoader);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtv_descripcion;
        NetworkImageView ntwrkImgv;
        ProgressBar progressBar;

         ViewHolder(View itemView) {
            super(itemView);
            txtv_descripcion = itemView.findViewById(R.id.tv_descr_gallery);
            ntwrkImgv = itemView.findViewById(R.id.niv_card_gallery);
             ntwrkImgv.setErrorImageResId(R.drawable.fondo_main);
            progressBar = itemView.findViewById(R.id.progressbar);
            ntwrkImgv.setMaxHeight(height);
        }
    }
        void actualizarData(List<RecyclerGaleriaItem> data) {
            fotos = data;
            notifyDataSetChanged();
        }

        void remover(int position) {
            fotos.remove(position);
            notifyItemRemoved(position);
        }

        void actualizarItem(int position, String descripcion) {

            fotos.get(position).setDescripcion_foto(descripcion);

            notifyItemChanged(position);

        }

        void addItem(RecyclerGaleriaItem infoData) {
            int index = 0;
            fotos.add(index, infoData);
            notifyItemInserted(index);

        }
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }
    }



