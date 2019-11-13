package com.esy.jaha.osmdroid;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public  class CardFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView reciclerView;
    private OnFragmentInteraction mListener;
    static RecyclerCardAdaptador adaptador;

    public CardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CardFragment.
     */
    public static CardFragment newInstance(String param1, String param2) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity(). getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = (int) (dm.widthPixels*0.70);
        adaptador = new RecyclerCardAdaptador(getContext(), height, mListener);
        reciclerView = view.findViewById(R.id.recyclerCards);
        reciclerView.setAdapter(adaptador);
        reciclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Vertical Orientation By Default


/*        String rotacion = Utilidades.getRotation(getContext());
        if (rotacion.equals("vertical")){
            reciclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Vertical Orientation By Default
        }else{
            reciclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Vertical Orientation By Default

        }*/


//        s = (android.widget.SearchView) view.findViewById(R.id.searchView);
        //  s.setQueryRefinementEnabled(true);


/*
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                 onSearchRequested();

                s.setQuery(s.getQuery(), true);
//                s.clearFocus();
                //s.setOnQueryTextListener();
            }
        });
        s.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
       */  // Do not iconify the widget; expand it by default

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteraction) {
            mListener = (OnFragmentInteraction) context;
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

    public interface OnFragmentInteraction {
        void onFragmentCardInteraction(double latitud, double longitud, int id);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
