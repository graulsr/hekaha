package com.esy.jaha.osmdroid.syncSQLiteMySQL.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Bound Service que interactua con el sync adapter para correr las sincronizaciones
 */
public class SyncService extends Service {

    // Instancia del sync adapter
    private SyncAdapter syncAdapter = null;
    // Objeto para prevenir errores entre hilos
    private final Object lock = new Object();

    @Override
    public void onCreate() {
        synchronized (lock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /**
     * Retorna interfaz de comunicación para que el sistema llame al sync adapter
     */
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}