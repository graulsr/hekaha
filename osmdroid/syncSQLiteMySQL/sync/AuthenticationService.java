package com.esy.jaha.osmdroid.syncSQLiteMySQL.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticationService extends Service {                                                    /** * Bound service para gestionar la autenticación*/

    private ExpenseAuthenticator autenticador;                                                              // Instancia del autenticador

    @Override
    public void onCreate() {
        autenticador = new ExpenseAuthenticator(this);                                                      // Nueva instancia del autenticador
    }

    @Override                                                                                               /*  * Ligando el servicio al framework de Android */
    public IBinder onBind(Intent intent) {

        return autenticador.getIBinder();
    }
}