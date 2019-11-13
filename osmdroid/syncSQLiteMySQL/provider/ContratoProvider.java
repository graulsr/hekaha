package com.esy.jaha.osmdroid.syncSQLiteMySQL.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;


public class ContratoProvider {                                                                            /** * Contract Class entre el provider y las aplicaciones*/

public final static String AUTHORITY = "com.esy.jaha.osmdroid.syncSQLiteMySQL";                 /**     * Autoridad del Content Provider*/
//tablas de la BD
    static final String CATEGORIA = "categorias";                                                    /**     * Representación de la tabla a consultar*/
    static final String CATEGORIA_EVENTO = "categorias_evento";
    static final String SUPER_CATEGORIA = "super_categorias";
    static final String CATEGORIA_PROMO= "categorias_promo";
    static final String CATEGORIA_ESPECIAL = "categorias_especial";
    final static String SINGLE_MIME =  "vnd.android.cursor.item/vnd." + AUTHORITY + CATEGORIA;   /**     * Tipo MIME que retorna la consulta de una sola fila*/
    final static String SINGLE_MIME_CAT_EVENT =  "vnd.android.cursor.item/vnd." + AUTHORITY + CATEGORIA_EVENTO;
    final static String SINGLE_MIME_SUPER_CATEGORIA =  "vnd.android.cursor.item/vnd." + AUTHORITY + SUPER_CATEGORIA;
    final static String SINGLE_MIME_CAT_PROMO =  "vnd.android.cursor.item/vnd." + AUTHORITY + CATEGORIA_PROMO;
    final static String SINGLE_MIME_CAT_ESPECIAL =  "vnd.android.cursor.item/vnd." + AUTHORITY + CATEGORIA_ESPECIAL;
    final static String MULTIPLE_MIME = "vnd.android.cursor.dir/vnd." + AUTHORITY + CATEGORIA;    /**     * Tipo MIME que retorna la consulta*/
    final static String MULTIPLE_MIME_CAT_EVENT = "vnd.android.cursor.dir/vnd." + AUTHORITY + CATEGORIA_EVENTO;
    final static String MULTIPLE_MIME_SUPER_CATEGORIA = "vnd.android.cursor.dir/vnd." + AUTHORITY + SUPER_CATEGORIA;
    final static String MULTIPLE_MIME_CAT_PROMO = "vnd.android.cursor.dir/vnd." + AUTHORITY + CATEGORIA_PROMO;
    final static String MULTIPLE_MIME_CAT_ESPECIAL = "vnd.android.cursor.dir/vnd." + AUTHORITY + CATEGORIA_ESPECIAL;

    public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CATEGORIA);             /**     * URI de contenido principal*/
    public final static Uri CONTENT_URI_CAT_EVENT = Uri.parse("content://" + AUTHORITY + "/" + CATEGORIA_EVENTO);
    public final static Uri CONTENT_URI_SUPER_CATEGORIA = Uri.parse("content://" + AUTHORITY + "/" + SUPER_CATEGORIA);
    public final static Uri CONTENT_URI_CAT_PROMO = Uri.parse("content://" + AUTHORITY + "/" + CATEGORIA_PROMO);
    public final static Uri CONTENT_URI_CAT_ESPECIAL = Uri.parse("content://" + AUTHORITY + "/" + CATEGORIA_ESPECIAL);

    static final UriMatcher uriMatcher;                                                           /**     * Comparador de URIs de contenido*/
    static final int ALLROWS = 1;                                                              /**     * Código para URIs de multiples registros     */
    static final int SINGLE_ROW = 2;                                                              /**     * Código para URIS de un solo registro     */
    static final int ALLROWS_CAT_EVENT = 3;                                                              /**     * Código para URIs de multiples registros     */
    static final int SINGLE_ROW_CAT_EVENT = 4;                                                              /**     * Código para URIS de un solo registro     */
    static final int ALLROWS_SUPER_CAT = 5;                                                              /**     * Código para URIs de multiples registros     */
    static final int SINGLE_ROW_SUPER_CAT = 6;                                                              /**     * Código para URIS de un solo registro     */
    static final int ALLROWS_CAT_PROMO = 7;                                                              /**     * Código para URIs de multiples registros     */
    static final int SINGLE_ROW_CAT_PROMO = 8;                                                              /**     * Código para URIS de un solo registro     */
    static final int ALLROWS_CAT_ESPECIAL = 9;                                                              /**     * Código para URIs de multiples registros     */
    static final int SINGLE_ROW_CAT_ESPECIAL = 10;                                                              /**     * Código para URIS de un solo registro     */

    static { // Asignación de URIs
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CATEGORIA, ALLROWS);
        uriMatcher.addURI(AUTHORITY, CATEGORIA + "/#", SINGLE_ROW);

        uriMatcher.addURI(AUTHORITY, CATEGORIA_EVENTO, ALLROWS_CAT_EVENT);
        uriMatcher.addURI(AUTHORITY, CATEGORIA_EVENTO + "/#", SINGLE_ROW_CAT_EVENT);

        uriMatcher.addURI(AUTHORITY, SUPER_CATEGORIA, ALLROWS_SUPER_CAT);
        uriMatcher.addURI(AUTHORITY, SUPER_CATEGORIA + "/#", SINGLE_ROW_SUPER_CAT);

        uriMatcher.addURI(AUTHORITY, CATEGORIA_PROMO, ALLROWS_CAT_PROMO);
        uriMatcher.addURI(AUTHORITY, CATEGORIA_PROMO + "/#", SINGLE_ROW_CAT_PROMO);

        uriMatcher.addURI(AUTHORITY, CATEGORIA_ESPECIAL, ALLROWS_CAT_ESPECIAL);
        uriMatcher.addURI(AUTHORITY, CATEGORIA_ESPECIAL + "/#", SINGLE_ROW_CAT_ESPECIAL);


    }

    public static final int ESTADO_OK = 0;                                                              // Valores para la columna ESTADO
    public static final int ESTADO_SYNC = 1;

    public static class Columnas implements BaseColumns {   /**     * Estructura de la tabla     */

    private Columnas() {
        // Sin instancias
    }

        public final static String CATEGORIA = "categoria";
        public final static String ICONO = "icono";
        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "id_remota";
        public static final String ID_USER = "id_user";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

    }

    public static class ColumnasCatEvent implements BaseColumns {   /**     * Estructura de la tabla     */

    private ColumnasCatEvent() {
        // Sin instancias
    }

        public final static String CAT_EVEN = "categoria";
        public final static String ICONO = "icono";
        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "id_remota";
        public static final String ID_USER = "id_user";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

    }

    public static class ColumnasSuperCategorias implements BaseColumns {   /**     * Estructura de la tabla     */

    private ColumnasSuperCategorias() {
        // Sin instancias
    }

        public final static String SUPER_CATEGORIA = "super_categoria";
        public final static String ICONO = "icono";
        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "id_remota";
        public static final String DESCRIPCION = "descripcion";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

    }

    public static class ColumnasCatPromo implements BaseColumns {   /**     * Estructura de la tabla     */

    private ColumnasCatPromo() {
        // Sin instancias
    }

        public final static String CAT_PROMO = "categoria";
        public final static String ICONO = "icono";
        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "id_remota";
        public static final String ID_USER = "id_user";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

    }

    public static class ColumnasCatEspecial implements BaseColumns {   /**     * Estructura de la tabla     */

    private ColumnasCatEspecial() {
        // Sin instancias
    }

        public final static String CAT_ESPECIAL = "categoria";
        public final static String ICONO = "icono";
        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "id_remota";
        public static final String ID_USER = "id_user";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

    }
}
