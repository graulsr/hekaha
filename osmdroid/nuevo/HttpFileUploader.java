package com.esy.jaha.osmdroid.nuevo;

import android.content.Context;

import com.esy.jaha.osmdroid.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpFileUploader {
    private JSONObject respuesta;
    private URL connectURL;
    private String nom_carpeta;
    private String fileName;
    private int thumbWidth, thumbHeight;
    private Context ctx;
    private boolean crearThumb = false;
    private DataOutputStream dos;
    private String lineEnd = "\r\n";
    private String twoHyphens = "--";
    private String boundary = "*****";

    HttpFileUploader(String urlString, String fileName, String carpeta, int thumbWidth, int thumbHeight, String descripcion, Context context){
        try{
            connectURL = new URL(urlString);
        }catch(Exception ex){
        }
        nom_carpeta = carpeta;
        this.fileName = fileName;
        this.thumbWidth = thumbWidth;
        this.thumbHeight = thumbHeight;
        crearThumb = true;
        ctx = context;
    }

    HttpFileUploader(String url, String fileName, String carpeta, Context context){//para subir iconos
        try{
            connectURL = new URL(url);
        }catch(Exception ex){
        }
        nom_carpeta = carpeta;
        this.fileName = fileName;
        ctx = context;
        crearThumb = false;

    }

    JSONObject doStart(FileInputStream stream){
        fileInputStream = stream;
        thirdTry();
        return respuesta;
    }

    private FileInputStream fileInputStream = null;

    private void thirdTry() {

        final String NOM_CARPETA = "nom_carpeta";
        final String THUMB_WIDTH = "thumbWidth";
        final String THUMB_HEIGHT = "thumbHeight";
        String existingFileName = fileName;
        //String lineEnd = "\r\n";
        //String twoHyphens = "--";
        //String boundary = "*****";
        String Tag="HttpUploader";

        try
        {
            //------------------ CLIENT REQUEST
            // PHP Service connection
            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data; charset=utf-8;boundary="+boundary);

            dos = new DataOutputStream( conn.getOutputStream() );
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"imagen\";filename=\"" + existingFileName +"\""  +lineEnd);
            dos.writeBytes(lineEnd);
            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

          while (bytesRead > 0)
            {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + NOM_CARPETA + "\"" + lineEnd);
            dos.writeBytes("Content-Type: text/plain" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(nom_carpeta);//
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            //este cambio es mio
            addPostData("token", SessionManager.getInstancia(ctx).getToken());
            addPostData("id_user", String.valueOf(SessionManager.getInstancia(ctx).getUserId()));
            addPostData("nombre", String.valueOf(System.currentTimeMillis()/1000));
            if(crearThumb) {

              addPostData(THUMB_WIDTH, String.valueOf((thumbWidth)));
              addPostData(THUMB_HEIGHT, String.valueOf((thumbHeight)));
//              addPostData("descripcion", descripcion);
            }
            //hasta aqui

            // Close input stream
            fileInputStream.close();
            dos.flush();
            InputStream is = conn.getInputStream();
            // retrieve the response from server
            int ch;
            StringBuilder b = new StringBuilder();
            while( ( ch = is.read() ) != -1 ){
                b.append( (char)ch );
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(b.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            respuesta = jsonObject;
            //Toast.makeText(ctx,respuesta,Toast.LENGTH_SHORT).show();
            dos.close();

        }catch (IOException ioe)
        {
        }
    }


    private void addPostData(String var_name, String valor) throws IOException {

        dos.writeBytes(lineEnd);
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + var_name + "\"" + lineEnd);
        dos.writeBytes("Content-Type: text/plain;charset:utf-8" + lineEnd);
        dos.writeBytes(lineEnd);

        dos.writeBytes(valor);//
        dos.writeBytes(lineEnd);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

    }




}
