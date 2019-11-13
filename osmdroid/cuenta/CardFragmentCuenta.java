package com.esy.jaha.osmdroid.cuenta;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esy.jaha.osmdroid.R;

import static com.esy.jaha.osmdroid.cuenta.ActivityCuenta.mMapView;


public class CardFragmentCuenta extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    static RecyclerCardCuentaAdaptador adaptador;
    private RecyclerView recyclerView;


    private OnFragmentCardCuentaListener mListener;

    public CardFragmentCuenta() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CardFragmentCuenta.
     */
    public static CardFragmentCuenta newInstance(String param1, String param2) {
        CardFragmentCuenta fragment = new CardFragmentCuenta();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView.getOverlays().size() == 0){
            ActivityCuenta.mislocales = null;
            adaptador.actualizarDataset();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.card_fragment_cuenta, container, false);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity(). getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = (int) (dm.widthPixels*0.70);

        adaptador = new RecyclerCardCuentaAdaptador(getActivity(), getContext(), height, mListener);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerCardsCuenta);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Vertical Orientation By Default

        recyclerView.setAdapter(adaptador);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onFragmentCardInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof OnFragmentCardCuentaListener) {
            mListener = (OnFragmentCardCuentaListener) context;
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

    public interface OnFragmentCardCuentaListener {

        void onFragmentCardInteraction(double latitud, double longitud, int id);
    }
}
