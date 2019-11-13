package com.esy.jaha.osmdroid.nuevo;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;

public class ImageAdapterGridview5 extends BaseAdapter {
    private Context mContext;

    public ImageAdapterGridview5(Context c) {
        mContext = c;
    }

    public int getCount() {
        return vehiculos.length;
    }

    public Object getItem(int position) {
        return vehiculos[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(45, 52));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
        }

        try {
            imageView.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open(vehiculos[position] + ".png")));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return imageView;

    }

    private String[] vehiculos = {

//            aviones
           "mk_veh_airport_apron",
           "mk_veh_airport_runway",
           "mk_veh_airport_terminal",
           "mk_veh_airshow",
           "mk_veh_jetfighter",
           "mk_veh_kingair",
           "mk_veh_planecrash",
           "mk_veh_travel_agency",
           "mk_veh_trolley",
           "mk_veh_helicopter",


           "mk_veh_hotairbaloon",
           "mk_veh_ufo",

//            cuatro ruedas
            "mk_veh_steamtrain",
            "mk_veh_train",
            "mk_veh_tramway",
            "mk_veh_underground",

            "mk_veh_bus",
            "mk_veh_busstop",
            "mk_veh_bustour",
            "mk_veh_cablecar",
            "mk_veh_camping",
            "mk_veh_funicolar",
            "mk_veh_plowtruck",
            "mk_veh_truck",
            "mk_veh_van",
            "mk_veh_warehouse",

            "mk_veh_car", //  cabecera
            "mk_veh_car_share",
            "mk_veh_caraccident",
            "mk_veh_carrental",
            "mk_veh_carwash",
            "mk_veh_convertible",
            "mk_veh_descent",
            "mk_veh_ford",
            "mk_veh_fourbyfour",
            "mk_veh_icy_road",
            "mk_veh_jeep",
            "mk_veh_pickup",
            "mk_veh_pickup_camper",
            "mk_veh_sportscar",
            "mk_veh_sportutilityvehicle",
            "mk_veh_ambulance",
            "mk_veh_taxi",

//            tractores
            "mk_veh_bulldozer",
            "mk_veh_military",
            "mk_veh_field",


//            motos
            "mk_veh_atv",
            "mk_veh_ducati_diavel",
            "mk_veh_vespa",


            "mk_veh_bicycle_shop",
            "mk_veh_bike_downhill",

//            barcos
            "mk_veh_battleship",
            "mk_veh_boat",
            "mk_veh_boatcrane",
            "mk_veh_cruiseship",
            "mk_veh_ferry",
            "mk_veh_marina",
            "mk_veh_oilrig",
            "mk_veh_solar_cruise",
            "mk_veh_taxiboat",
            "mk_veh_waterway_canal",


            "mk_veh_submarine",
            "mk_veh_kayak",
            "mk_veh_harbor",
            "mk_veh_horse_drawn_carriage",

//            elementos ruteros
            "mk_veh_fillingstation",
            "mk_veh_oil",

            "mk_veh_barrier",
            "mk_veh_closedroad",
            "mk_veh_highway",
            "mk_veh_road",
            "mk_veh_roadtype_gravel",

            "mk_veh_bridge_modern",
            "mk_veh_bridge_old",
            "mk_veh_trafficcamera",
            "mk_veh_trafficlight",


    };
}