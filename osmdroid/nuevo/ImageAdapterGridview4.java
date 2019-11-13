package com.esy.jaha.osmdroid.nuevo;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;

public class ImageAdapterGridview4 extends BaseAdapter {
    private Context mContext;

    public ImageAdapterGridview4(Context c) {
        mContext = c;
    }

    public int getCount() {
        return simbolos.length;
    }

    public Object getItem(int position) {
        return simbolos[position];
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
            imageView.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open(simbolos[position] + ".png")));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return imageView;

    }


    private String[] simbolos = {

            "mk_work_barber",
            "mk_work_compost",
            "mk_work_constructioncrane",
            "mk_work_contract",
            "mk_work_court",
            "mk_work_dentist",
            "mk_work_drinkingwater",
            "mk_work_elevator",
            "mk_work_elevator_down",
            "mk_work_findajob",
            "mk_work_flagman",
            "mk_work_foundry",
            "mk_work_haybale",
            "mk_work_industry", // cabecera
            "mk_work_laboratory",
            "mk_work_landfill",
            "mk_work_levelcrossing",
            "mk_work_linedown",
            "mk_work_lock",
            "mk_work_lockerrental",
            "mk_work_mastcrane",
            "mk_work_mine",
            "mk_work_mobilephonetower",
            "mk_work_moving_walkway_entert",
            "mk_work_mural",
            "mk_work_oido",
            "mk_work_oilpumpjack",
            "mk_work_olfass",
            "mk_work_paint",
            "mk_work_pick_stand",
            "mk_work_powerlinepole",
            "mk_work_presentation",
            "mk_work_repair",
            "mk_work_sawmill",
            "mk_work_septic_tank",
            "mk_work_tailor",
            "mk_work_tools",
            "mk_work_treasure_chest",
            "mk_work_water",
            "mk_work_waterfilter",
            "mk_work_water",
            "mk_work_waterwellpump",
            "mk_work_welding",
            "mk_work_workoffice",
            "mk_work_workshop",

           "mk_tec_anthropo",
           "mk_tec_atm",
           "mk_tec_audio",
           "mk_tec_bike_charging",
           "mk_tec_bike_rental",
           "mk_tec_cctv",
           "mk_tec_cinema",
           "mk_tec_computers",
           "mk_tec_database",
           "mk_tec_degrees",
           "mk_tec_folder",
           "mk_tec_metronetwork",
           "mk_tec_music",
           "mk_tec_phones",
           "mk_tec_photo",
           "mk_tec_printer",
           "mk_tec_qr_code",
           "mk_tec_radar",
           "mk_tec_radio_control",
           "mk_tec_radio_station",
           "mk_tec_tortillas",
           "mk_tec_webcam",

           "mk_sim_aboriginal",
           "mk_sim_accesdenied",
           "mk_sim_acupuncture",
           "mk_sim_administrativeboundary",
           "mk_sim_aed",
           "mk_sim_alien",
           "mk_sim_amphitheater",
           "mk_sim_amphitheater_two",
           "mk_sim_anemometer_mono",
           "mk_sim_army",
           "mk_sim_award",
           "mk_sim_bank",
           "mk_sim_campfire",
           "mk_sim_caution",
           "mk_sim_channelchange",
           "mk_sim_chart",
           "mk_sim_chemistry",
           "mk_sim_chiropractor",
           "mk_sim_citysquare",
           "mk_sim_comment",
           "mk_sim_company",
           "mk_sim_conversation",
           "mk_sim_country",
           "mk_sim_cross",
           "mk_sim_cserkesz_ikon",
           "mk_sim_currencyexchange",
           "mk_sim_curveleft",
           "mk_sim_diagonal_reverse",
           "mk_sim_direction_down",
           "mk_sim_direction_uturn",
           "mk_sim_doublebendright",
           "mk_sim_downloadicon",
           "mk_sim_emblem",
           "mk_sim_entrance",
           "mk_sim_exchequer",
           "mk_sim_exit",
           "mk_sim_fire",
           "mk_sim_firstaid",
           "mk_sim_footprint",
           "mk_sim_fossils",
           "mk_sim_freqchg",
           "mk_sim_gay_male",
           "mk_sim_gay_female",
           "mk_sim_geocaching",
           "mk_sim_headstone",
           "mk_sim_helipad",
           "mk_sim_highschool",
           "mk_sim_honeycomb",
           "mk_sim_hotel",
           "mk_sim_icon",
           "mk_sim_indian",
           "mk_sim_information",
           "mk_sim_iobridge",
           "mk_sim_jewishquarter",
           "mk_sim_junction",
           "mk_sim_kebab",
           "mk_sim_mainroad",
           "mk_sim_mapicon",
           "mk_sim_maxheight",
           "mk_sim_maxweight",
           "mk_sim_maxwidth",
           "mk_sim_medicine",
           "mk_sim_metano",
           "mk_sim_monument_historique",
           "mk_sim_music_classical",
           "mk_sim_no_nuke",
           "mk_sim_notvisited",
           "mk_sim_outlet",
           "mk_sim_parkandride",
           "mk_sim_parking_bicycle",
           "mk_sim_parking_meter",
           "mk_sim_parkinggarage",
           "mk_sim_peace",
           "mk_sim_petroglyphs",
           "mk_sim_pirates",
           "mk_sim_police",
           "mk_sim_publicart",
           "mk_sim_radiation",
           "mk_sim_reatorlogowhite",
           "mk_sim_recycle",
           "mk_sim_regroup",
           "mk_sim_science",
           "mk_sim_scoutgroup",
           "mk_sim_share",
           "mk_sim_sight",
           "mk_sim_signpost",
           "mk_sim_sikh",
           "mk_sim_skull",
           "mk_sim_smiley_happy",
           "mk_sim_smoking",
           "mk_sim_snowy",
           "mk_sim_sozialeeinrichtung",
           "mk_sim_speed",
           "mk_sim_speedhump",
           "mk_sim_splice",
           "mk_sim_square_compass",
           "mk_sim_st_margarets_cross",
           "mk_sim_stairs",
           "mk_sim_star",
           "mk_sim_stargate_raw",
           "mk_sim_stop",
           "mk_sim_sunny",
           "mk_sim_sunsetland",
           "mk_sim_surveying",
           "mk_sim_taxiway",
           "mk_sim_thecapturelab",
           "mk_sim_three_d",
           "mk_sim_tidaldiamond",
           "mk_sim_treasure_mark",
           "mk_sim_triskelion",
           "mk_sim_tv",
           "mk_sim_up",
           "mk_sim_wendepunkt",
           "mk_sim_wifi",
           "mk_sim_woodshed",
           "mk_sim_world",
           "mk_sim_worldheritagesite",
           "mk_sim_you_are_here",
           "mk_sim_zoom",
    };
    }


