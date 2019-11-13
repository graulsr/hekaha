package com.esy.jaha.osmdroid;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;

public class Adapter_iconos_objetos extends BaseAdapter {
    private Context mContext;

    public Adapter_iconos_objetos(Context c) {

        mContext = c;
    }

    public int getCount() {
        return objetos.length;
    }

    public Object getItem(int position) {
        return objetos[position];
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
            imageView.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open(objetos[position] + ".png")));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return imageView;

    }


  private String[] objetos = {
          //relativo a comidas
          "bar_restaurant",
          "bar_restaurant2",
          "bar_chafing_dish",
          "bar_chinabox",
          "bar_fast_food_menu",
          "bar_fast_food",
          "bar_fries",
          "bar_popcorn",
          "bar_hamburguesa",
          "bar_hot_dog_car",
          "bar_meat",
          "bar_pancho",
          "bar_pancho2",
          "bar_pizza",
          "bar_pizza2",
          "bar_sandwich",

          "bar_cheese",
          "bar_eggs",
          "bar_chipa",
          "bar_cake",
          "bar_pastel",
          "bar_cupcake",
          "bar_helado",
          "bar_icecream",


          "bar1_cherry",
          "bar1_grape",
          "bar1_orange",
          "bar1_pineapple",
          "bar1_strawberry",
          "bar1_watermelon",

          "bar1_tomate",

          "bar2_beer_clink",
          "bar2_beer_glass",
          "bar2_beer1",
          "bar2_beer2",
          "bar2_drink_bucket",
          "bar2_vaso_cerveza",

          "bar2_glass",
          "bar2_wine_glasses",

          "bar2_cocacola",


          "bar2_cocktail",

          "bar2_coffee",
          "bar2_mug",


          "obj_anillo_oro",
          "obj_bandera_azul",
          "obj_bandera_roja",
          "obj_beach_chair",
          "obj_bench",
          "obj_biblia",
          "obj_bola8",
          "obj_bolsa2",
          "obj_bouquet",
          "obj_bouquet2",
          "obj_flower",
          "obj_rosa",

          "obj_bowl3",
          "obj_bridge",
          "obj_cama",
          "obj_chair",
          "obj_seatar",
          "obj_park_bench",
          "obj_parasol",
          "obj_card",
          "obj_carrito",
          "obj_biberon",
          "obj_chupete",
          "obj_cesta",

          "obj_christmas_tree",
          "obj_navidad",

          "obj_clip_board",
          "obj_pencil",
          "obj_graduacion",
          "obj_contact",
          "obj_credit_cards",
          "obj_correo",
          "obj_mail",
          "obj_postage_stamp",
          "obj_map",
          "obj_newspaper",
          "obj_newsweather",
          "obj_notepad",
          "obj_paperclip",
          "obj_secret_book",
          "obj_palette",

          "obj_corte",
          "obj_coffin",
          "obj_coins",
          "obj_column_chart",
          "obj_comfits",
          "obj_pumpkin",
          "obj_pildoras",

          "obj_corazon",
          "obj_corona",
          "obj_corona2",
          "obj_maquillaje",
          "obj_dice",
          "obj_pawn",
          "obj_esposas",
          "obj_estrella",
          "obj_fuente",
          "obj_linterna",
          "obj_mascara",
          "obj_medal",
          "obj_money",
          "obj_regadera",
          "obj_washing_machine",
          "obj_regalo",
          "obj_sofa",
          "obj_stethoscope",
          "obj_theater",
          "obj_tickets",
          "obj_explosion",
          "obj_fuego",

          "obj_embassy",
          "obj_hongo",
          "obj_leaves",
          "obj_sprout",
//          -----------
          "mk_restaurant",
          "mk_rest_fooddeliveryservice",
          "mk_rest_foodtruck",
          "mk_rest_romantic",
          "mk_rest_terrace",
          "mk_rest_sandwich",
          "mk_rest_burger",
          "mk_rest_pizzaria",
          "mk_rest_hotdog",
          "mk_rest_thai",
          "mk_rest_turkish",
          "mk_rest_fastfood",
          "mk_rest_breakfast",
          "mk_rest_milk_and_cookies",
          "mk_rest_muffin_bagle",
          "mk_rest_bread",
          "mk_rest_italian",
          "mk_rest_japanese_food",
          "mk_rest_chinese",
          "mk_rest_japanese_sweet",
          "mk_rest_korean",
          "mk_rest_buffet",
          "mk_rest_barbecue",
          "mk_rest_gourmet",
          "mk_rest_butcher",
          "mk_rest_greek",
          "mk_rest_steakhouse",
          "mk_rest_cheese",
          "mk_rest_tapas",
          "mk_rest_patisserie",
          "mk_rest_anniversary",
          "mk_rest_mediterranean",
          "mk_rest_eggs",
          "mk_rest_fruits",
          "mk_restaurant_vegetarian",
          "mk_rest_apple",
          "mk_rest_icecream",
//            bebidas
          "mk_beb_bar",
          "mk_beb_beergarden",
          "mk_beb_coktail",
          "mk_beb_drink",
          "mk_beb_drugstore",
          "mk_beb_grocery",
          "mk_beb_liquor",
          "mk_beb_milk_bottle",
          "mk_beb_winebar",
          "mk_beb_winetasting",
          "mk_beb_coffee",


          "mk_shop_administration",
          "mk_shop_babymarkt",
          "mk_shop_bags",
          "mk_shop_clothers_female",
          "mk_shop_bollie",
          "mk_shop_clothers_male",
          "mk_shop_conveniencestore",
          "mk_shop_craftstore",
          "mk_shop_cycling_feed",
          "mk_shop_departmentstore",
          "mk_shop_fishing",
          "mk_shop_fishingboat",
          "mk_shop_fishingstore",
          "mk_shop_highhills",
          "mk_shop_homecenter",
          "mk_shop_jewelry",
          "mk_shop_kiosk",
          "mk_shop_kitchen",
          "mk_shop_laundromat",
          "mk_shop_library",
          "mk_shop_lingerie",
          "mk_shop_love_date",
          "mk_shop_loveinterest",
          "mk_shop_mall",
          "mk_shop_market",
          "mk_shop_medicalstore",
          "mk_shop_movierental",
          "mk_shop_ophthalmologist",
          "mk_shop_paintball",
          "mk_shop_perfumery",
          "mk_shop_photography",
          "mk_shop_picnic",
          "mk_shop_price_tag",
          "mk_shop_sauna",
          "mk_shop_shoes",
          "mk_shop_skis",
          "mk_shop_sledgerental",
          "mk_shop_sneakers",
          "mk_shop_snorkeling",
          "mk_shop_spa",
          "mk_shop_supermarket",
          "mk_shop_textiles",
          "mk_shop_tile_shop",
          "mk_shop_velocimeter",
          "mk_shop_video",
          "mk_shop_videogames",
          "mk_shop_wedding",
          "mk_shop_wiki",
          "mk_shop_wrestling",
          "mk_shop_youthhostel",


          "mk_fest_blast",
          "mk_fest_candy",
          "mk_fest_candy_cane",
          "mk_fest_christmasmarket",
          "mk_fest_circus",
          "mk_fest_comedyclub",
          "mk_fest_ferriswheel",
          "mk_fest_fireworks",
          "mk_fest_gifts",
          "mk_fest_magicshow",
          "mk_fest_party",
          "mk_fest_phantom",
          "mk_fest_santa",
          "mk_fest_theater",
          "mk_fest_themepark",
          "mk_festival",
//            juegos
          "mk_jue_bowling",
          "mk_jue_casino",
          "mk_jue_poker",

          "mk_obj_archeological",
          "mk_obj_art_museum",
          "mk_obj_artgallery",
          "mk_obj_battlefield",
          "mk_obj_beautifulview",
          "mk_obj_binoculars",
          "mk_obj_bomb",
          "mk_obj_brewery",
          "mk_obj_calendar",
          "mk_obj_childmuseum",
          "mk_obj_clock",
          "mk_obj_coins",
          "mk_obj_crafts",
          "mk_obj_crematorium",
          "mk_obj_curling",
          "mk_obj_dalben",
          "mk_obj_expert",
          "mk_obj_fire_hydrant",
          "mk_obj_fireexstinguisher",
          "mk_obj_firemen",
          "mk_obj_flag",
          "mk_obj_gas_cylinder",
          "mk_obj_glasses",
          "mk_obj_hats",
          "mk_obj_historical_museum",
          "mk_obj_hookah_final",
          "mk_obj_jacuzzi",
          "mk_obj_japanese_sake",
          "mk_obj_juice",
          "mk_obj_lodging",
          "mk_obj_map",
          "mk_obj_missile",
          "mk_obj_music_jazzclub",
          "mk_obj_music_live",
          "mk_obj_music_rock",
          "mk_obj_naval",
          "mk_obj_newsagent",
          "mk_obj_oyster",
          "mk_obj_pens",
          "mk_obj_pin",
          "mk_obj_pond",
          "mk_obj_postal",
          "mk_obj_poweroutage",
          "mk_obj_rescue",
          "mk_obj_school",
          "mk_obj_schreibwaren_web",
          "mk_obj_shootingrange",
          "mk_obj_ski_shoe",
          "mk_obj_slipway",
          "mk_obj_statue",
          "mk_obj_surface_metal",
          "mk_obj_tajine",
          "mk_obj_targ",
          "mk_obj_teahouse",
          "mk_obj_tebletennis",
          "mk_obj_text",
          "mk_obj_theft",
          "mk_obj_ticket_office",
          "mk_obj_tires",
          "mk_obj_umbrella",
          "mk_obj_university",
          "mk_obj_war",
          "mk_obj_beautysalon"

  };
    
}