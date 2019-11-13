package com.esy.jaha.osmdroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {


    String generatedSecuredPasswordHash, generatedSecuredPasswordHash2;
    String password;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    String origen;
    private Context ctx;
    ProgressDialog progress;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        ctx = getBaseContext();
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        CheckBox checkBox = findViewById(R.id.mostrar_password);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    // show password
                    mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                mPasswordView.setSelection(mPasswordView.length());
            }
        });
        origen = getIntent().getStringExtra("origen");

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString().trim();
        password = mPasswordView.getText().toString().trim();


        //   Snackbar.make(view,"mi hash es: "+generatedSecuredPasswordHash, Snackbar.LENGTH_LONG)
        //         .setAction("Action", null).show();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) ) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            progress = ProgressDialog.show(this, "Conectando con el servidor", "Por favor espere...", true, true);

//            try {
////                generatedSecuredPasswordHash = generateStorngPasswordHash(password);
//                //   generatedSecuredPasswordHash2 = generateStorngPasswordHash(et_confirmar.getText().toString());
//                //  matched = validatePassword(et_confirmar.getText().toString(), generatedSecuredPasswordHash);
//
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (InvalidKeySpecException e) {
//                e.printStackTrace();
//            }
            mAuthTask = new UserLoginTask(email);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
//        private final String mPassword;

        UserLoginTask(String email) {
            mEmail = email;
//            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            final String PASWWORD_URL = Constantes.URL_BASE + "usuarios/getPassword.php";
            JSONObject json_login = new JSONObject();

            try {
                json_login.put("email", mEmail);
//                json_login.put("password", mPassword);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                    new JsonObjectRequest(
                            Request.Method.POST,
                            PASWWORD_URL,json_login,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    mAuthTask = null;
                                    progress.dismiss();
                                    verificar_contraseña(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    mAuthTask = null;
                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(),"No se puede conectar con el servidor",Toast.LENGTH_SHORT).show();

                                }
                            }
                    )
            );


            return true;
        }



        @Override
        protected void onCancelled() {
            mAuthTask = null;
            progress.dismiss();
            Toast.makeText(getBaseContext(), "La operación fue cancelada", Toast.LENGTH_SHORT).show();
        }
    }

    public static String generateStorngPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }
    /**Compara el hash del password ingresado con el hash almacenado en el servidor*/
    private static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
        {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }
    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException
    {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

     public void abrirActivityRegister(View v){

         Intent intent = new Intent(this, RegisterActivity.class);
         startActivityForResult(intent, Constantes.NUEVO_REGISTRO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Constantes.NUEVO_REGISTRO){
//
//            CustomDrawerAdapter.drawerItemList.remove(0);
//            CustomDrawerAdapter.drawerItemList.add(0, new DrawerItem(data.getStringExtra("nombre"), data.getStringExtra("email")));
//            MainActivity.adapter.notifyDataSetChanged();
            setResult(getRequestCode(getIntent().getStringExtra("origen")), data);
            finish();
        }
    }

    private int getRequestCode(String origen){

        int code = 0;
        switch (origen) {

            case "iniciar_sesion":
                code = Constantes.LOGIN;
                break;

            case "nuevo_local":
                code = Constantes.NUEVO_LOCAL;
                break;

            case "nuevo_evento":
                code = Constantes.NUEVO_EVENTO;
                break;

            case "nueva_promo":
                code = Constantes.NUEVA_PROMO;
                break;

            case "onClickDrawerHeader":
                code = Constantes.ONCLICK_DRAWER_HEADER;
                break;

            case "comentar":
                code = Constantes.COMENTAR;
                break;
        }

        return code;
    }

    private void verificar_contraseña(JSONObject response){

        try {
            int estado = response.getInt("estado");
            if (estado == 1){
                String contraseña = response.getString("contraseña");
                try {
                    if (validatePassword(password, contraseña)){

                                final String LOGIN_URL = Constantes.URL_BASE + "usuarios/procesar_login.php";
                                JSONObject json_login = new JSONObject();

                                try {
                                    json_login.put("email", email);
        //                json_login.put("password", mPassword);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                                        new JsonObjectRequest(
                                                Request.Method.POST,
                                                LOGIN_URL,json_login,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {

                                                        mAuthTask = null;
                                                        progress.dismiss();
                                                        procesar_login(response);
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        mAuthTask = null;
                                                        progress.dismiss();
                                                        Toast.makeText(getApplicationContext(),"No se puede conectar con el servidor",Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                        )
                                );
                    }else{
                            mPasswordView.setError("La contraseña es incorrecta");      //getString(R.string.error_incorrect_password)
                            mPasswordView.requestFocus();
                    }

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            }else{
                switch (estado) {
                    case Constantes.EMAIL_NO_EXISTE:
                        mEmailView.setError("No existe usuario con este correo electrónico");
                        mEmailView.requestFocus();
                        break;

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void procesar_login(JSONObject response){
        int result;
        try {
            result = response.getInt("estado");
            if(result == Constantes.SUCCESS){
                int user_id = response.getInt("user_id") ;
                String user_name = response.getString("user_name");
                String token = response.getString("token");
                SessionManager sessionManager = SessionManager.getInstancia(ctx);
                sessionManager.set_user_id(user_id);
                sessionManager.setUserName(user_name);
                sessionManager.setUserEmail(email);
                sessionManager.setLogin(true);
                sessionManager.setToken(token);


                setResult(getRequestCode(origen));

                Toast.makeText(ctx, "Operación exitosa. Ha iniciado sesión", Toast.LENGTH_SHORT).show();
                finish();

            }else {
                switch (result){
                    case Constantes.LOGIN_ERROR:
                        Toast.makeText(getBaseContext(), "Ha ocurrido un error al tratar de iniciar sesión", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "No se ha podido establecer comunicación con el servidor", Toast.LENGTH_SHORT).show();

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "No se ha recibido respuesta" , Toast.LENGTH_SHORT).show();
        }
    }
}

