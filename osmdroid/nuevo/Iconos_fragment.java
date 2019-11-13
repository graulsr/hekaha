package com.esy.jaha.osmdroid.nuevo;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.TextView;

import com.esy.jaha.osmdroid.R;

public class Iconos_fragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String origen_llamada, activity_origen;

    public Iconos_fragment() {
    } // Required empty public constructor

    public static Iconos_fragment newInstance(String origen, String activity_origen) {
        Iconos_fragment fragment = new Iconos_fragment();
        Bundle args = new Bundle();
        args.putString("origen", origen);
        args.putString("activity_origen", activity_origen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            origen_llamada = getArguments().getString("origen");
            activity_origen = getArguments().getString("activity_origen");
        }
       // obtenerMarcadores();
   }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_iconos, container, false);
        GridView gridView1, gridView2, gridView3, gridView4, gridView5;
        TextView title;
        gridView1 = v.findViewById(R.id.gridview1);
        gridView2 = v.findViewById(R.id.gridview2);
        gridView3 = v.findViewById(R.id.gridview3);
        gridView4 = v.findViewById(R.id.gridview4);
        gridView5 = v.findViewById(R.id.gridview5);
        title = v.findViewById(R.id.txt_title_markers);
        title.setText(getTitle(origen_llamada));

        TabHost tabs = v.findViewById(android.R.id.tabhost);
        tabs.setup();
        TabHost.TabSpec spec = tabs.newTabSpec("mitab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator(null, getResources().getDrawable(R.drawable.restaurant_marker));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator(null, getResources().getDrawable(R.drawable.mk_pers_male));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("", getResources().getDrawable(R.drawable.cons_home));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab4");
        spec.setContent(R.id.tab4);
        spec.setIndicator(null, getResources().getDrawable(R.drawable.work_industry));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab5");
        spec.setContent(R.id.tab5);
        spec.setIndicator("", getResources().getDrawable(R.drawable.veh_car));
        tabs.addTab(spec);

        gridView1.setAdapter(new ImageAdapterGridview1(getActivity()));
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mListener != null) {
                    String icono = (String)parent.getItemAtPosition(position) ;
                    mListener.onFragmentInteraction(origen_llamada, position, icono, activity_origen);
                }
            }

        });

        gridView2.setAdapter(new ImageAdapterGridview2(getActivity()));
        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mListener != null) {
                    String icono = (String)parent.getItemAtPosition(position) ;
                    mListener.onFragmentInteraction(origen_llamada, position, icono, activity_origen);
                }
            }

        });

        gridView3.setAdapter(new ImageAdapterGridview3(getActivity()));
        gridView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mListener != null) {
                    String icono = (String)parent.getItemAtPosition(position) ;
                    mListener.onFragmentInteraction(origen_llamada, position, icono, activity_origen);
                }
            }

        });

        gridView4.setAdapter(new ImageAdapterGridview4(getActivity()));
        gridView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mListener != null) {
                    String icono = (String)parent.getItemAtPosition(position) ;
                    mListener.onFragmentInteraction(origen_llamada, position, icono, activity_origen);
                }
            }

        });

        gridView5.setAdapter(new ImageAdapterGridview5(getActivity()));
        gridView5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mListener != null) {
                    String icono = (String)parent.getItemAtPosition(position) ;
                    mListener.onFragmentInteraction(origen_llamada, position, icono, activity_origen);
                }
            }

        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String origen, int pos, String icono, String activity_origen);
    }

    private CharSequence getTitle(String origen){

        CharSequence title;
        switch (origen){
            case "nuevo_local":
                title = "Nuevo Local:\nSeleccione un marcador";
                break;
            case "nuevo_evento":
                title = "Nuevo Evento:\nSeleccione un marcador";
                break;
            case "nueva_promo":
                title = "Nueva Oferta:\nSeleccione un marcador";
                break;
            default:
                title = "Seleccione un marcador";
                break;
        }
        return title;
    }

}

