package com.esy.jaha.osmdroid.spinner;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.esy.jaha.osmdroid.R;

import java.io.IOException;
import java.util.List;


public class CategoriasSpinnerAdapter extends ArrayAdapter<Categorias>
{
	private Context context;

	private List<Categorias> datos = null;

	public CategoriasSpinnerAdapter(Context context, List<Categorias> datos)
	{
		//se debe indicar el layout para el item que seleccionado (el que se muestra sobre el botón del botón)
		super(context, R.layout.spinner_selected_item, datos);
		this.context = context;
		this.datos = datos;
	}

	//este método establece el elemento seleccionado sobre el botón del spinner
	@Override
	 public View getView(int position, View convertView, ViewGroup parent)
	 {
	    if (convertView == null) 
	    {
	         convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.spinner_selected_item,null);
	    }	        
	    ((TextView) convertView.findViewById(R.id.texto)).setText(datos.get(position).getName());
		 try {
			 ((ImageView) convertView.findViewById(R.id.icono)).setImageBitmap(BitmapFactory.decodeStream(context.getAssets().open(datos.get(position).getIcon() + ".png")));
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
//				.setBackgroundResource(datos.get(position).getIcon());
	    
	    return convertView;
	 }

	//gestiona la lista usando el View Holder Pattern. Equivale a la típica implementación del getView
	//de un Adapter de un ListView ordinario


	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		if (row == null) 
		{
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = layoutInflater.inflate(R.layout.spinner_list_item, parent, false);
		}

		if (row.getTag() == null) 
		{
			HolderCategoria holderCategoria = new HolderCategoria();
			holderCategoria.setIcono((ImageView) row.findViewById(R.id.icono_list_item));
			holderCategoria.setTextView((TextView) row.findViewById(R.id.texto));
			row.setTag(holderCategoria);
		}

		//rellenamos el layout con los datos de la fila que se está procesando
		Categorias categorias = datos.get(position);
		try {
			((HolderCategoria) row.getTag()).getIcono().setImageBitmap(BitmapFactory.decodeStream(context.getAssets().open(categorias.getIcon() + ".png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
//				.setImageResource(categorias.getIcon());
		((HolderCategoria) row.getTag()).getTextView().setText(categorias.getName());

		return row;
	}
	

	private static class HolderCategoria
	{

		private ImageView icono;

		private TextView textView;

		public ImageView getIcono()
		{
			return icono;
		}

		public void setIcono(ImageView icono)
		{
			this.icono = icono;
		}

		 TextView getTextView()
		{
			return textView;
		}

		 void setTextView(TextView textView)
		{
			this.textView = textView;
		}

	}
}


