package com.esy.jaha.osmdroid.nuevo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.esy.jaha.osmdroid.R;

public class Fragment_descripcion_foto extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    Bitmap bitmap;
    private OnFragmentFotoInteractionListener mListener;

    public Fragment_descripcion_foto() {
        // Required empty public constructor
    }

    public static Fragment_descripcion_foto newInstance(String param1, String param2, Bitmap bitmap) {
        Fragment_descripcion_foto fragment = new Fragment_descripcion_foto();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        args.putParcelable("imagen", bitmap);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
           bitmap = getArguments().getParcelable("imagen");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_descripcion_foto, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView_fragment);
        final EditText descripcion = (EditText) view.findViewById(R.id.edtxFoto) ;
        Button button = (Button) view.findViewById(R.id.btn_foto) ;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descrip = descripcion.getText().toString().trim();
                if (mListener != null) {
                    mListener.onFragmentFotoInteraction(descrip);
                }
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int height_max = (int) (height*0.75 );;
        imageView.setMaxHeight(height_max);
        imageView.setImageBitmap(bitmap);
        return view;
    }

    public void onButtonPressed(String str) {
        if (mListener != null) {
            mListener.onFragmentFotoInteraction(str);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentFotoInteractionListener   ) {
            mListener = (OnFragmentFotoInteractionListener) context;
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


    public interface OnFragmentFotoInteractionListener {
        void onFragmentFotoInteraction(String descrip);

    }
}
