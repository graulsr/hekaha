package com.esy.jaha.osmdroid.nuevo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.esy.jaha.osmdroid.MySingleton;
import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.SessionManager;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

public class EditDescripcion extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DESC = "desc";
    private static final String POS = "pos";
    private static final String ID = "id";
    private static final String URL = "url";
    private static final String TIPO = "tipo";
    private String descripcion;
    private int pos;
    private int id;
    private String url;
    private String tipo;
    private OnFragmentInteractionListener mListener;
    private boolean cancelarTodo;

    public EditDescripcion() {
        // Required empty public constructor
    }
    public static EditDescripcion newInstance(int id_foto, int pos, String desc, String urlImagen, String tipo ) {
        EditDescripcion fragment = new EditDescripcion();
        Bundle args = new Bundle();
        args.putInt(ID, id_foto);
        args.putInt(POS, pos);
        args.putString(DESC, desc);
        args.putString(URL, urlImagen);
        args.putString(TIPO, tipo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            descripcion = getArguments().getString(DESC);
            id = getArguments().getInt(ID);
            pos = getArguments().getInt(POS);
            url = getArguments().getString(URL);
            tipo = getArguments().getString(TIPO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v = inflater.inflate(R.layout.fragment_edit_descripcion, container, false);
        final EditText desc = (EditText) v.findViewById(R.id.edtx_descripcion);
        desc.setText(descripcion);
        desc.selectAll();
        NetworkImageView networkImageView = (NetworkImageView) v.findViewById(R.id.imageView_foto);
        networkImageView.setErrorImageResId(R.drawable.fondo_main);
        networkImageView.setImageUrl(url, MySingleton.getInstance(getContext()).getImageLoader());
        Button aceptar = (Button) v.findViewById(R.id.btn_aceptar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String nueva_descripcion = desc.getText().toString().trim();
               if (!descripcion.equals(nueva_descripcion)) {
                   String ruta = Constantes.URL_BASE + tipo + "/actualizarDescripcion.php";
                   JSONObject jsonObject = new JSONObject();
                   try {
                       jsonObject.put("token", SessionManager.getInstancia(getContext()).getToken());
                       jsonObject.put("id_user", SessionManager.getInstancia(getContext()).getUserId());
                       jsonObject.put("id_foto", id);
                       jsonObject.put("descripcion", nueva_descripcion);

                       final ProgressDialog pDialog = new ProgressDialog(getContext());
                       pDialog.setMessage("Un momento..." );
                       pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                       pDialog.setCancelable(true);
                       pDialog.show();

                       JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                               ruta, jsonObject, new Response.Listener<JSONObject>() {
                           @Override
                           public void onResponse(JSONObject jsonObject) {
                               if(!cancelarTodo) {
                                   pDialog.dismiss();
                                   procesar_respuesta(jsonObject, desc.getText().toString().trim());
                               }
                           }
                       }, new Response.ErrorListener() {
                           @Override
                           public void onErrorResponse(VolleyError volleyError) {
                               if(!cancelarTodo) {
                                   pDialog.dismiss();
                                   Toast.makeText(getContext(), getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
                       MySingleton.getInstance(getContext()).addToRequestQueue(request);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }else{
                   getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                   getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));

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
    public void onResume() {
        super.onResume();
        cancelarTodo = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelarTodo = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentDescripcionInteraction(int posicion, String desc);
    }

    private void procesar_respuesta(JSONObject response, String newDescripcion){
        try {
            int estado = response.getInt("estado");
            if(estado == 1){
//                Toast.makeText(getContext(), "Operación exitosa", Toast.LENGTH_SHORT).show();
                mListener.onFragmentDescripcionInteraction(pos, newDescripcion);
            }else{
                if (estado == 3){

                    Toast.makeText(getContext(), "Error de autenticación", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
