package com.esy.jaha.osmdroid.syncSQLiteMySQL.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.esy.jaha.osmdroid.R;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.provider.ContratoProvider;
import com.esy.jaha.osmdroid.syncSQLiteMySQL.sync.SyncAdapter;


public class ActivityNuevaCategoria extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private AdaptadorDeCategorias adapter;
    private TextView emptyView;
    private String tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_cat);
        setToolbar();

        tipo = getIntent().getStringExtra("tipoElemento");
        setTitle(obtenerTitulo(tipo));
        RecyclerView recyclerView;
        LinearLayoutManager layoutManager;
        recyclerView = findViewById(R.id.reciclador);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdaptadorDeCategorias(this);
        recyclerView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(0, null, this);

//        SyncAdapter.inicializarSyncAdapter(this);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_nueva_cat);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
       // SyncAdapter.sincronizarAhora(this, false);
    }

    public void onClickFab(View v) {

        Intent intent = new Intent(this, InsertActivity.class);
        intent.putExtra("tipoElemento", tipo);

       startActivityForResult(intent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sync) {
            SyncAdapter.sincronizarAhora(this, false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader c = null;
        switch (tipo){
            case "local":
                c = new CursorLoader( this, ContratoProvider.CONTENT_URI, null, null, null, "categoria");
                break;
            case "evento":
                c = new CursorLoader( this, ContratoProvider.CONTENT_URI_CAT_EVENT, null, null, null, "categoria");
                break;
            case "promo":
                c = new CursorLoader( this, ContratoProvider.CONTENT_URI_CAT_PROMO, null, null, null, "categoria");
                break;
            case "especial":
                c = new CursorLoader( this, ContratoProvider.CONTENT_URI_CAT_ESPECIAL, null, null, null, "categoria");
                break;
        }
        return c;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Toast.makeText(ActivityNuevaCategoria.this, "onLoadFinised", Toast.LENGTH_SHORT).show();
        adapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
       // Toast.makeText(ActivityNuevaCategoria.this, "onLoaderReset", Toast.LENGTH_SHORT).show();
        adapter.swapCursor(null);
    }

    private CharSequence obtenerTitulo(String tipo){

        CharSequence titulo = "";
        switch (tipo){
            case "local":
                titulo = "Categorías (Locales)";
                break;
            case "evento":
                titulo = "Categorías (Eventos)";
                break;
            case "promo":
                titulo = "Categorías (Ofertas)";
                break;
            case "especial":
                titulo = "Categorías";
                break;
        }
        return titulo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int EXITO = 2;
        if(resultCode == EXITO){
            setResult(EXITO);
        }
    }
}
