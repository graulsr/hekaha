package com.esy.jaha.osmdroid;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;

public class Adapter_iconos_transportes extends BaseAdapter {
    private Context mContext;

    public Adapter_iconos_transportes(Context c) {

        mContext = c;
    }

    public int getCount() {
        return transportes.length;
    }

    public Object getItem(int position) {
        return transportes[position];
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
            imageView.setLayoutParams(new GridView.LayoutParams(55, 55));

            imageView.setPadding(5, 5, 5, 5);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        try {
            imageView.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open(transportes[position] + ".png")));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return imageView;

    }



  private String[] transportes = {

         "trans_ambulance",
         "trans_bus",
         "trans_bus2",//CABECERA
         "trans_camion2",
         "trans_camion3",
         "trans_cars",
         "trans_childhood",
         "trans_coche",
         "trans_cuasi",
         "trans_jeep",
         "trans_police_car",
         "trans_quad",
         "trans_rent_car",
         "trans_tanker",
         "trans_truck",
         "trans_yellow_car",
         "trans2_excavadora",
         "trans2_forklift_containers",
         "trans2_grua",
         "trans2_remolque",
         "trans2_road_roler",
         "trans2_scavenger",
         "trans2_servicio",
         "trans2_servicio2",
         "trans2_wheeled_tractor",
         "trans3_moto",
         "trans3_moto2",
         "trans3_motorbike",
         "trans3_motorcycle",
         "trans3_motorcycle2",
         "trans4_subway",
         "trans4_bus_stop",
         "trans4_caravan",
         "trans4_parking_bicycle",
         "trans4_parking_car",
         "trans4_parking_car_paid",
         "trans4_parking_disabled",
         "trans4_rental_bicycle",
         "trans4_rental_car",
         "trans4_taxi",
         "trans4_train",
         "trans4_train2",
         "trans5_globo_aerostatico",
         "trans6_boat",
         "trans6_bote",
         "trans6_port",
         "trans6_rubber_boat",
         "trans6_slipway",
         "trans7_aerodrome",
         "trans7_airport_terminal",
         "trans7_helicopter2",
         "trans7_space_shuttle",
         "trans8_wheel_chair",
         "trans8_wheelchair",
         "trans9_rims",
         "trans9_wheel",

         "tec_camara",
         "tec_camara2",
         "tec_camera",
         "tec_cd",
         "tec_celular",
         "tec_desktop",
         "tec_disco",
         "tec_fotos",
         "tec_headphone",
         "tec_joystick",
         "tec_laptop",
         "tec_microcontrolador",
         "tec_microfono",
         "tec_microscope",
         "tec_monitor",
         "tec_multimedia",
         "tec_parlante",
         "tec_robot",
         "tec_robot2",
         "tec_satellite",
         "tec_smartphones",
         "tec_speaker",
         "tec_usb",
         "tec_video_file",
         "tec_webcamera",

          //relativo musica
          "mus_guitar",
          "mus_guitar2",
          "mus_harp",
          "mus_horn",
          "mus_trompeta",
          "mus_trumpet2",


          "work_brocha",
          "work_canilla",
          "work_configure",
          "work_conos",
          "work_construction_bucket",
          "work_fontanero",
          "work_key",
          "work_lightning_sign",
          "work_mantenimiento",
          "work_pintura",
          "work_reparacion_audio",
          "work_scissors_and_comb",
          "work_taladro",
          "work_trabajadores",
          "work_yunque",
          "work_trabajo",
          "work_wash_basin",
          "work_wheelbarrow",

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
          "mk_veh_trafficlight"


  };



}