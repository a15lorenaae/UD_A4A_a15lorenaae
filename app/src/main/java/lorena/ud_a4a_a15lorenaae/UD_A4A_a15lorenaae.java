package lorena.ud_a4a_a15lorenaae;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class UD_A4A_a15lorenaae extends Activity {
public static enum TIPOREDE{MOBIL,WIFI,SENREDE};
    TextView texto;
    private TIPOREDE conexion;
    private String xml_Descargar="http://manuais.iessanclemente.net/images/2/20/Platega_pdm_rutas.xml";
    private Thread thread;
    private File rutaArquivo;
    String carpetaxml=Environment.getExternalStorageDirectory().getAbsolutePath()+"/RUTAS/";
    String nomeArquivo=Uri.parse(xml_Descargar).getLastPathSegment();
    private TIPOREDE comprobarRede(){
        NetworkInfo networkInfo=null;
        ConnectivityManager connMgr=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo=connMgr.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            switch (networkInfo.getType()){
                case ConnectivityManager.TYPE_MOBILE:
                    return TIPOREDE.MOBIL;
                case ConnectivityManager.TYPE_WIFI:
                    return TIPOREDE.WIFI;
            }
        }
        return TIPOREDE.SENREDE;
    }
    private void descargarArquivo(){
        URL url=null;
        try{
            url=new URL(xml_Descargar);

        }catch (MalformedURLException e1){
            e1.printStackTrace();
            return;
        }
        HttpURLConnection conn=null;
        File carpxml=new File(carpetaxml);
        rutaArquivo=new File(carpetaxml,nomeArquivo);
        if(!carpxml.exists())carpxml.mkdirs();
        try{
            conn=(HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);/*Indicamos si a conexion vai recibir datos*/
            conn.connect();
            int response=conn.getResponseCode();
            if(response!=HttpURLConnection.HTTP_OK){
                return;
            }
            OutputStream os=new FileOutputStream(rutaArquivo);
            InputStream in=conn.getInputStream();
            byte data []=new byte[1024];
            int count;
            while ((count=in.read(data))!=-1){
                os.write(data,0,count);
            }
            os.flush();
            os.close();
            in.close();
            conn.disconnect();
            Log.i("COMUNICACION", "ACABO");
        }catch (FileNotFoundException e){
            Log.e("COMUNICACION",e.getMessage());
        }
        catch(IOException e){
            e.printStackTrace();
            Log.e("COMUNICACION",e.getMessage());
        }

    }
    private  void XestionarEventos(){
        Button btndescargar=(Button)findViewById(R.id.botondescargar);
        btndescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread=new Thread(){
                    @Override
                public void run(){
                        descargarArquivo();
                    }
                };
                thread.start();
            }
        });

    }
    private void lerArquivo() throws IOException,XmlPullParserException{
        InputStream is= new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/RUTAS/"+nomeArquivo);
        XmlPullParser parser= Xml.newPullParser();
        parser.setInput(is, "UTF-8");
        int evento=parser.next();
        while (evento!=XmlPullParser.END_DOCUMENT) {
            if (evento == XmlPullParser.START_TAG) {
                if (parser.getName().equals("ruta")) {//un novo contacto
                    evento = parser.nextTag();//Pasamos a nome
                    texto.append(parser.nextText() + "-");//Espacio entre os dous datos
                    evento = parser.nextTag();//Pasamos a direccion*/
                    texto.append(parser.nextText()+"\n");//Nova linea

                }
            }


            evento = parser.next();
        }
        is.close();
        }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ud__a4_a_a15lorenaae);
        conexion=comprobarRede();
        texto=(TextView)findViewById(R.id.texto);
        if (conexion==TIPOREDE.SENREDE){
            Toast.makeText(this,"NON SE PODE FACER ESTA PRACTICA SEN CONEXION A INTERNET",Toast.LENGTH_LONG).show();
            finish();
        }
        XestionarEventos();
        try{
            lerArquivo();

            } catch (IOException e) {

                e.printStackTrace();
                Toast.makeText(this, "ERRO:"+ e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (XmlPullParserException e) {

                e.printStackTrace();
                Toast.makeText(this, "ERRO:"+ e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

