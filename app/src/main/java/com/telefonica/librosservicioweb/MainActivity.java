package com.telefonica.librosservicioweb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText autor;
    TextView tvPagina;
    private ListView listaLibros;
    ArrayList<String> titulos = new ArrayList<String>();
    ArrayList<String> anos = new ArrayList<String>();
    ArrayList<String> paginas = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        autor = (EditText) this.findViewById(R.id.autor);
        tvPagina = (TextView) this.findViewById(R.id.tvPagina);
        listaLibros = (ListView) this.findViewById(R.id.listaLibros);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cargarAutor(View v) {
        //iniciar tarea en segundo plano
        ComunicacionTask com = new ComunicacionTask();
        //le pasa como parámetro la dirección
        //de la página
        com.execute("http://lorenamonasterio.esy.es/libros/peticion.php", autor.getText().toString());
        titulos.clear();
        anos.clear();
        paginas.clear();

    }

    private class ComunicacionTask extends AsyncTask<String, Void, String> {

        //    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {

            String cadenaJson="";
            try{
                //monta la url con la dirección y parámetro
                //de envío
                URL url=new URL(params[0]+"?parametro="+params[1]);
                URLConnection con=url.openConnection();
                //recuperacion de la respuesta JSON
                String s;
                InputStream is=con.getInputStream();
                //utilizamos UTF-8 para que interprete
                //correctamente las ñ y acentos
                BufferedReader bf=new BufferedReader(
                        new InputStreamReader(is, Charset.forName("UTF-8")));
                while((s=bf.readLine())!=null){
                    cadenaJson+=s;
                }

            }
            catch(IOException ex){
                ex.printStackTrace();
            }
            return cadenaJson;
        }

        @Override
        protected void onPostExecute(String result) {
            String[] datos=null;
            try{
                //creamos un array JSON a partir de la cadena recibida
                JSONArray jarray=new JSONArray(""+result);
                //creamos el array de String con el tamaño
                //del array JSON
                datos=new String[jarray.length()];
                for(int i=0;i<jarray.length();i++){
                    JSONObject job=jarray.getJSONObject(i);
                    titulos.add(job.getString("titulo"));
                    anos.add(job.getString("publicacion"));
                    paginas.add(job.getString("paginas"));
                    //libros = "Titulo: "+job.getString("titulo")+"\n Número de páginas: "+job.getString("paginas")+" \n Año de publicación: "+job.getString("publicacion");
                }
                ArrayAdapter<String> adapter= new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,titulos);
                listaLibros.setAdapter(adapter);
                listaLibros.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id) {
                                Toast.makeText(MainActivity.this,
                                        "Años de publicación: "+anos.get(position)+"\nNúmero de páginas: "+paginas.get(position),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                //tvPagina.setText(libros);
            }
            catch(JSONException ex){
                ex.printStackTrace();
            }
        }

    }
}
