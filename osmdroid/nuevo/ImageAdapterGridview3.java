package com.esy.jaha.osmdroid.nuevo;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;

public class ImageAdapterGridview3 extends BaseAdapter {
    private Context mContext;

    public ImageAdapterGridview3(Context c) {
        mContext = c;
    }

    public int getCount() {
        return construcciones.length;
    }

    public Object getItem(int position) {
        return construcciones[position];
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
            imageView.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open(construcciones[position] + ".png")));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return imageView;

    }

    private String[] construcciones = {

            "mk_cons_apartment",
            "mk_cons_barn",
            "mk_cons_bunker",
            "mk_cons_cabin",
            "mk_cons_cafetaria",
            "mk_cons_cathedral",
            "mk_cons_chapel",
            "mk_cons_church",
            "mk_cons_earthquake",
            "mk_cons_embassy",
            "mk_cons_factory",
            "mk_cons_farm",
            "mk_cons_flood",
            "mk_cons_historicalquarter",
            "mk_cons_home", //  cabecera
            "mk_cons_hospital_building",
            "mk_cons_hostel",
            "mk_cons_house",
            "mk_cons_hut",
            "mk_cons_japanese_temple",
            "mk_cons_moderntower",
            "mk_cons_mosquee",
            "mk_cons_office_building",
            "mk_cons_pagoda",
            "mk_cons_palace",
            "mk_cons_pleasurepier",
            "mk_cons_rockhouse",
            "mk_cons_sevilla",
            "mk_cons_solarenergy",
            "mk_cons_temple",
            "mk_cons_templehindu",
            "mk_cons_theravadapagoda",
            "mk_cons_theravadatemple",
            "mk_cons_townhouse",
            "mk_cons_treedown",
            "mk_cons_villa",
            "mk_const_condominium",
            "mk_const_conference",
            "mk_const_congress",
            "mk_const_convent",


            "mk_lug_arch",
            "mk_lug_beach",
            "mk_lug_bed_breakfast",
            "mk_lug_bigcity",
            "mk_lug_castle",
            "mk_lug_catholicgrave",
            "mk_lug_cemetary",
            "mk_lug_citywalls",
            "mk_lug_cromlech",
            "mk_lug_cropcircles",
            "mk_lug_dam",
            "mk_lug_fountain",
            "mk_lug_ghosttown",
            "mk_lug_japanese_lantern",
            "mk_lug_jewishgrave",
            "mk_lug_landmark",
            "mk_lug_lifeguard",
            "mk_lug_lighthouse",
            "mk_lug_megalith",
            "mk_lug_memorial",
            "mk_lug_modernmonument",
            "mk_lug_monument",
            "mk_lug_motel",
            "mk_lug_observatory",
            "mk_lug_planetarium",
            "mk_lug_playground",
            "mk_lug_powerplant",
            "mk_lug_powersubstation",
            "mk_lug_pyramid",
            "mk_lug_resort",
            "mk_lug_ruins",
            "mk_lug_shintoshrine",
            "mk_lug_shipwreck",
            "mk_lug_smallcity",
            "mk_lug_spaceport",
            "mk_lug_stadium",
            "mk_lug_summercamp",
            "mk_lug_swimming",
            "mk_lug_synagogue",
            "mk_lug_tollstation",
            "mk_lug_tower",
            "mk_lug_tunnel",
            "mk_lug_watermill",
            "mk_lug_waterwell",
            "mk_lug_windmill",
            "mk_lug_windturbine",
            "mk_lug_worldwildway",
            "mk_lug_museum_openair",
//nature
            "mk_nat_agritourism",
            "mk_nat_algae",
            "mk_nat_avalanche",
            "mk_nat_canyon",
            "mk_nat_cave",
            "mk_nat_cloudy",
            "mk_nat_cloudysunny",
            "mk_nat_desert",
            "mk_nat_fallingrocks",
            "mk_nat_fjord",
            "mk_nat_flowers",
            "mk_nat_forest",
            "mk_nat_forest_two",
            "mk_nat_geothermal_site",
            "mk_nat_geyser",
            "mk_nat_glacier",
            "mk_nat_grass",
            "mk_nat_lake",
            "mk_nat_moonstar",
            "mk_nat_mountains",
            "mk_nat_mushroom",
            "mk_nat_palm_tree",
            "mk_nat_panoramicview",
            "mk_nat_quadrifoglio",
            "mk_nat_rainy",
            "mk_nat_riparianhabitat",
            "mk_nat_river",
            "mk_nat_shore",
            "mk_nat_thunderstorm",
            "mk_nat_tornado",
            "mk_nat_tsunami",
            "mk_nat_vineyard",
            "mk_nat_waterfall",
            "mk_nat_wetlands",

    };

}