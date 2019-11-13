package com.esy.jaha.osmdroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mUserName;
    private boolean mRegisterResult;
    private String email;
    String generatedSecuredPasswordHash, generatedSecuredPasswordHash2;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.
        mUserName = (EditText) findViewById(R.id.edt_name);

        mEmailView = (EditText) findViewById(R.id.email_register);

        mPasswordView = (EditText) findViewById(R.id.password_register);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });
        CheckBox checkBox = findViewById(R.id.show_password);
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
        Button mEmailSignInButton = (Button) findViewById(R.id.email_reg_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });


    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mUserName.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUserName.getText().toString().trim();
        email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
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
        if (TextUtils.isEmpty(userName) || !isValidUserName(userName)) {
            mUserName.setError(getString(R.string.error_field_required));
            focusView = mUserName;
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
            try {
                generatedSecuredPasswordHash = LoginActivity. generateStorngPasswordHash(password);
                //   generatedSecuredPasswordHash2 = generateStorngPasswordHash(et_confirmar.getText().toString());
                //  matched = validatePassword(et_confirmar.getText().toString(), generatedSecuredPasswordHash);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();

            }
            mAuthTask = new UserLoginTask(userName, email, generatedSecuredPasswordHash);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isValidUserName(String name){
        if( name.length()<3) {
            return false;
        }else{
            return true;
        }
    }

    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserName;
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String userName, String email, String password) {
            mUserName = userName;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            final String REGISTER_URL = Constantes.URL_BASE + "usuarios/procesar_registro.php";
            String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            JSONObject json_register = new JSONObject();
            try {
                json_register.put("user_name" , mUserName);
                json_register.put("email", mEmail);
                json_register.put("password", mPassword);
                json_register.put("android_id", android_id );
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                    new JsonObjectRequest(
                            Request.Method.POST,
                            REGISTER_URL, json_register,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                   procesar_registro(response);
                                    mAuthTask = null;
                                    progress.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(),"No se puede conectar con el servidor",Toast.LENGTH_SHORT).show();
                                    mAuthTask = null;
                                    progress.dismiss();
                                }
                            }
                    )
            );

            return mRegisterResult;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            progress.dismiss();
            Toast.makeText(getBaseContext(), "La operación fue cancelada", Toast.LENGTH_SHORT).show();
        }
    }
    private void procesar_registro(JSONObject response){
        int result;
        try {
            mRegisterResult = false;
            result = response.getInt("estado");
            if(result == Constantes.SUCCESS){
                int id_user = response.getInt("id_user");
                String userName = response.getString("user_name");
                String token = response.getString("token");
                // Session manager
                SessionManager session = SessionManager.getInstancia(getApplicationContext());
                // user successfully logged in
                // Create login session
                session.setLogin(true);
                session.set_user_id(id_user);
                session.setUserName(userName);
                session.setUserEmail(email);
                session.setToken(token);
                mRegisterResult = true;


                Toast.makeText(RegisterActivity.this, "Se ha registrado exitosamente", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("email", email);
                intent.putExtra("nombre", userName);
                setIntent(intent);
                setResult(Constantes.NUEVO_REGISTRO, intent);
                finish();
            }else{
                switch (result){
                    case Constantes.USER_ALREADY_EXISTS:
                        mEmailView.setError("Ya existe un usuario con este email");
                        mEmailView.setText(email);
                        break;
                    case Constantes.ERROR_DESCONOCIDO:
                        Toast.makeText(getBaseContext(), "Ha ocurrido un error, por favor vuelva a intentarlo más tarde", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        } catch (JSONException e) {
            Toast.makeText(RegisterActivity.this, "No se ha obtenido respuesta del servidor", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}