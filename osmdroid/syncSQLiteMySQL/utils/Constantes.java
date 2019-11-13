package com.esy.jaha.osmdroid.syncSQLiteMySQL.utils;

/**
 * Constantes
 */
public class Constantes {

    /**
     * Puerto que utilizas para la conexión.
     * Dejalo en blanco si no has configurado esta característica.
     */
//    private static final String PUERTO_HOST = ":80";

    /**
     * Dirección IP
     */
//  public static final String IP = "https://do-it-yourself-wool.000webhostapp.com";  KAQPY
 private static final String IP = "http://jaha.esy.es";
//  private static final String IP = "http://192.168.1.5";
// public static final String IP = "http://169.254.161.169";
  public static final String URL_BASE = IP + "/proyecto/codigos/";
  public static final String URL_IMAGENES = IP + "/proyecto/imagenes/";
 /**
     * URLs del Web Service
     */

    public static final String GET_URL = URL_BASE + "categorias/obtener_categorias.php";
    public static final String INSERT_URL = URL_BASE + "locales/insertar_categoria.php";
    public static final String INSERT_CAT_EVENT_URL = URL_BASE + "eventos/insertar_categoria_evento.php";
    public static final String INSERT_CAT_PROMO_URL = URL_BASE + "promos/insertar_categoria_promo.php";
    public static final String INSERT_CAT_ESPECIAL_URL = URL_BASE + "especiales/insertar_categoria_especial.php";

    /**
      Campos de las respuestas Json
     */

    public static final String ID_CATEGORIA = "id_categoria";
    public static final String ESTADO = "estado";
    public static final String CATEGORIA = "categorias";
    public static final String CATEGORIA_EVENTO = "categorias_evento";
    public static final String SUPER_CATEGORIA = "super_categorias";
    public static final String CATEGORIA_PROMO = "categorias_promo";
    public static final String CATEGORIA_ESPECIAL = "categorias_especial";
    public static final String MENSAJE = "mensaje";
    public static final String TIPO_CAT = "tipo_cat";

    /**
     * Códigos del campo ESTADO
     */
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;
    public static final int INCORRECT_PASSWORD = 2;
    public static final int ERROR_DESCONOCIDO = 3;
    public static final int EMAIL_NO_EXISTE = 4;
    public static final int USER_ALREADY_EXISTS = 5;
   public static final int LOGIN_ERROR = 5;

    // codigos de requestCode para el método onActivityResult
    public static final int NUEVO_LOCAL = 3;
    public static final int NUEVO_EVENTO = 4;
    public static final int NUEVA_PROMO = 5;
    public static final int NUEVO_LOC_ESP = 6;
    public static final int LOGIN = 7;
    public static final int ONCLICK_DRAWER_HEADER = 8;
    public static final int NUEVO_REGISTRO = 9;
    public static final int COMENTAR = 10;
    public static final int ACERCA_DE = 12;
    public static final int SINCRONIZAR = 13;
    public static final int FIN_NUEVO = 11;
    public static final int CARD_FRAGMENT = 1;
    public static final int MI_UBICACION = 7;
    /**
     * Tipo de cuenta para la sincronización
     */
    public static final String ACCOUNT_TYPE = "com.esy.jaha.osmdroid.syncSQLiteMySQL.account";
}
