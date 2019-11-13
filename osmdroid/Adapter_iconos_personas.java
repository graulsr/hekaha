package com.esy.jaha.osmdroid;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;

public class Adapter_iconos_personas extends BaseAdapter {
    private Context mContext;

    public Adapter_iconos_personas(Context c) {

        mContext = c;
    }

    public int getCount() {
        return personas.length;
    }

    public Object getItem(int position) {
        return personas[position];
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
            imageView.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open(personas[position] + ".png")));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return imageView;

    }

    // references to our images
    private String[] personas = {

            "per1_bride",
            "per1_japonesa",
            "per1_mujer",
            "per1_mujer2",
            "per1_silueta",
            "per2_pareja",
            "per2_reunion",
            "per3_cowboy",
            "per3_cowgirl",
            "per3_horse_rider",
            "per3_jinete2",
            "per3_jinetes",
            "per3_torin",
            "per4_doctor",
            "per4_footballplayer",
            "per4_ingeniero",
            "per4_payaso",
            "per4_support",
            "per4_taekwondo",
            "per5_asilo",
            "per5_bicicleta", //cabecera
            "per5_expositor",
            "per5_hombre",
            "per5_hotel",
            "per5_library",
            "per5_natacion",
            "per5_prison",
            "per5_teacher",
            "per5_playground",

            "sport_archery",
            "sport_baseball",
            "sport_canoe",
            "sport_cricket",
            "sport_diving",
            "sport_golf",
            "sport_gym",
            "sport_gymnasium",
            "sport_gymnasium2",
            "sport_hillclimbing",
            "sport_horse_racing",
            "sport_iceskating",
            "sport_jetski",
            "sport_leisure_centre",
            "sport_minature_golf",
            "sport_motorracing",
            "sport_playground",
            "sport_sailing",
            "sport_shooting",
            "sport_skiing_crosscountry",
            "sport_skiing_downhill",
            "sport_swimming_indoor",
            "sport_swimming_outdoor",
            "sport_tennis",
            "sport_windsurfing",

            "sport2_soccer",
            "sport2_soccer_ball",
            "sport2_soccer2",
            "sport2_baseball",
            "sport2_baseball2",
            "sport2_basketball",
            "sport2_beachball",
            "sport2_bowling",
            "sport2_boxing_gloves",
            "sport2_golf_ball",
            "sport2_tennis_ball",
            "sport2_hockey",
            "sport2_pesa",
            "sport2_pesa2",
            "sport2_skateboard",
            "sport2_surfboard",
            "sport2_trophy",
            "sport2_pito",
            "sport2_parachute",

            ///-------------------

            "mk_pers_beratungsstelle",
            "mk_pers_billiard",
            "mk_pers_bullfight",
            "mk_pers_choral",
            "mk_pers_communitycentre",
            "mk_pers_construction",
            "mk_pers_cramschool",
            "mk_pers_crimescene",
            "mk_pers_crossingguard",
            "mk_pers_customs",
            "mk_pers_dance_class",
            "mk_pers_dancinghall",
            "mk_pers_daycare",
            "mk_pers_disability",
            "mk_pers_dogs_leash",
            "mk_pers_drinkingfountain",
            "mk_pers_family",
            "mk_pers_female",
            "mk_pers_fitness",
            "mk_pers_group",
            "mk_pers_hiking",
            "mk_pers_male", // cabecera
            "mk_pers_massage",
            "mk_pers_mexican",
            "mk_pers_music_hiphop",
            "mk_pers_nanny",
            "mk_pers_nursery",
            "mk_pers_nursing_home",
            "mk_pers_pedestriancrossing",
            "mk_pers_petanque",
            "mk_pers_prayer",
            "mk_pers_prison",
            "mk_pers_rollerskate",
            "mk_pers_scubadiving",
            "mk_pers_segway",
            "mk_pers_seniorsite",
            "mk_pers_shower",
            "mk_pers_snowshoeing",
            "mk_pers_strike",
            "mk_pers_stripclub",
            "mk_pers_therapy",
            "mk_pers_toilets",
            "mk_pers_trash",
            "mk_pers_waiting",
            "mk_pers_walkingtour",
            "mk_pers_watercraft",
            "mk_pers_waterpark",
            "mk_pers_yoga",


            "mk_pers_revolt",
            "mk_pers_r_bouddha",
            "mk_pers_zombie_outbreak",


            "mk_dep_anchorpier",
            "mk_dep_archery",
            "mk_dep_badminton",
            "mk_dep_baseball",
            "mk_dep_basketball",
            "mk_dep_beachvolleyball",
            "mk_dep_bike_rising",
            "mk_dep_boardercross",
            "mk_dep_bobsleigh",
            "mk_dep_boxing",
            "mk_dep_climbing",
            "mk_dep_cycling",
            "mk_dep_cycling_sprint",
            "mk_dep_diving",
            "mk_dep_finish",
            "mk_dep_golfing",
            "mk_dep_gondola",
            "mk_dep_handball",
            "mk_dep_horseriding",
            "mk_dep_hunting",
            "mk_dep_icehockey",
            "mk_dep_iceskating",
            "mk_dep_jogging",
            "mk_dep_judo",
            "mk_dep_karate",
            "mk_dep_kitesurfing",
            "mk_dep_mountainbiking",
            "mk_dep_nordicski",
            "mk_dep_paragliding",
            "mk_dep_parasailing",
            "mk_dep_speedriding",
            "mk_dep_ropescourse",
            "mk_dep_skiing",
            "mk_dep_skijump",
            "mk_dep_skilifting",
            "mk_dep_sledge",
            "mk_dep_sledge_summer",
            "mk_dep_snowboarding",
            "mk_dep_snowpark_arc",
            "mk_dep_soccer",
            "mk_dep_spelunking",
            "mk_dep_squash",
            "mk_dep_sumo",
            "mk_dep_surfacelift",
            "mk_dep_surfing",
            "mk_dep_taekwondo",
            "mk_dep_tennis",
            "mk_dep_volleyball",
            "mk_dep_weight",

//          canoas
            "mk_dep_windsurfing",
            "mk_dep_waterskiing",
            "mk_dep_surfpaddle",
            "mk_dep_kayaking",
            "mk_dep_rowboat",

//            motor
            "mk_dep_snowmobiling",
            "mk_dep_motorbike",

            "mk_dep_karting",

            "mk_dep_start_race",

            "mk_dep_usfootball",
            "mk_dep_rugbyfield",
            "mk_dep_olympicsite",
            "mk_dep_indoor_arena",
            "mk_dep_cricket",
            "mk_dep_cup",
            "mk_dep_australianfootball",

//animales
            "mk_ani_elephants",
            "mk_ani_alligator",
            "mk_ani_deer",
            "mk_ani_tiger",
            "mk_ani_dinopark",

            "mk_ani_frog",
            "mk_ani_har",
            "mk_ani_monkey",
            "mk_ani_penguin",
            "mk_ani_pets",
            "mk_ani_pig",
            "mk_ani_rodent",
            "mk_ani_seals",
            "mk_ani_shark",
            "mk_ani_snail",
            "mk_ani_snakes",
            "mk_ani_turtle",
            "mk_ani_corral",
            "mk_ani_cow",
            "mk_ani_wildlifecrossing",
            "mk_ani_toys",
            "mk_ani_veterinary",
//
//          peces
            "mk_ani_dolphins",
            "mk_ani_fish",
            "mk_ani_fishchips",
            "mk_ani_deepseafishing",
            "mk_ani_whale",
            "mk_ani_african",
            "mk_ani_aquarium",


//          aves
            "mk_ani_birds",
            "mk_ani_tweet",
            "mk_ani_duck",
            "mk_ani_chicken",
            "mk_ani_penguin",

            "mk_ani_bats",

//            insectos
            "mk_ani_lobster",
            "mk_ani_mosquito",
            "mk_ani_ant",
            "mk_ani_spider",

            "mk_ani_butterfly",


            "mk_ani_cowabduction",
            "mk_ani_zoo",
            "mk_ani_shelter",
            "mk_ani_farmstand"
    };






}