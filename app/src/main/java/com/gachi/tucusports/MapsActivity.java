package com.gachi.tucusports;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.ads.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MapsActivity extends AppCompatActivity implements View.OnClickListener, LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnInfoWindowClickListener {
    public Location location;
    public String cadena, calle, calle2, ciudad;
    private InterstitialAd interstitial;
    public static ArrayList<Complejo> listaC = new ArrayList<Complejo>();
    public GoogleMap Map;
    private LocationManager locManager;
    final static String ACT_INFO = "Descripcion";
    String[] info;
    EditText txtBuscar;
    ImageButton btnBuscar, btnInstrucciones, btnMapa;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    public String url = "";
    Marker markerBusqueda;
    private int vista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_maps);
        Map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        txtBuscar = (EditText) findViewById(R.id.txtBuscar);
        txtBuscar.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
// Comprobamos que se ha pulsado la tecla enter.
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    esconderTeclado(v);
                    buscarDireccion();
                    return true;
                }// end if.
                return false;
            }// end onKey.
        });
        btnBuscar = (ImageButton) findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(this);
        btnInstrucciones = (ImageButton) findViewById(R.id.btnInstrucciones);
        btnInstrucciones.setOnClickListener(this);
        btnMapa = (ImageButton) findViewById(R.id.btnTipoMapa);
        btnMapa.setOnClickListener(this);
        Map.getUiSettings().setMyLocationButtonEnabled(false);
        Map.setMyLocationEnabled(true);
        Map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Map.getUiSettings().setTiltGesturesEnabled(true);
        Map.getUiSettings().setZoomControlsEnabled(false);
        Map.getUiSettings().setZoomGesturesEnabled(true);
        Map.getUiSettings().setCompassEnabled(true);
        Map.getUiSettings().setScrollGesturesEnabled(true);
        Map.getUiSettings().setRotateGesturesEnabled(true);
        Map.setOnMapClickListener(this);
        Map.setOnMarkerClickListener(this);
        Map.setOnInfoWindowClickListener(this);
        Map.setOnMyLocationChangeListener(this);
        GPSTracker gpsTracker = new GPSTracker(MapsActivity.this);
        if (gpsTracker.canGetLocation()) {
            LatLng miPos = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            Log.i("Debug", "Mi posicion on create:" + miPos);
            Map.moveCamera(CameraUpdateFactory.newLatLngZoom(miPos, 15));
        } else {
            gpsTracker.showSettingsAlert();
            iraDefecto();
        }
        if (!verificaConexion(this)) {
            new AlertDialog.Builder(MapsActivity.this)
                    .setTitle(("No hay conexión a internet"))
                    .setMessage(("WiFi y Datos Desactivados, desea ir a Configuraciones?"))
                    .setIcon(R.drawable.dialogalert)
                    .setPositiveButton(("Aceptar"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            MapsActivity.this.startActivity(intent);
                        }
                    })
                    .setNegativeButton(("Cancelar"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
        generarMarker(new LatLng(-27.351232, -65.607369), "Club Medico", "Fútbol, Tenis", "3865696866", "Futbol");
        generarMarker(new LatLng(-27.347716880790642, -65.60138627886772), "C.E.F. N°24", "Futbol, Basquet, Handball, Voley, Piletas", "Público", "Futbol");
        generarMarker(new LatLng(-27.347059327662475, -65.59961602091789), "Los Cuervos CFC", "Concepcion Futbol Club", "Privado", "Futbol");
        generarMarker(new LatLng(-27.342575, -65.593381), "Open 5", "Fútbol Sintético", "3865349276", "Futbol");
        generarMarker(new LatLng(-27.341403256629903, -65.59487253427505), "Club Belgrano", "Fútbol 5, Basquet, Voley", "3865574604", "Futbol");
        generarMarker(new LatLng(-27.35692221474957, -65.60630679130554), "Azucarera FC", "Fútbol 11", "No Disponible", "Futbol");
        generarMarker(new LatLng(-27.374187299064445, -65.59260070323944), "Dario Bulacios", "Fútbol 11", "3865404381", "Futbol");
        generarMarker(new LatLng(-27.366936672758214, -27.366936672758214), "Cancha Ruta Nueva", "Fútbol 7", "No Disponible", "Futbol");
        generarMarker(new LatLng(-27.333459491680923, -65.59840232133865), "Complejo Joven Argentina 11", "Fútbol", "Público", "Futbol");
        generarMarker(new LatLng(-27.350973617187904, -65.59388682246208), "Casa Quinta", "Fútbol 11, Basquet", "Público", "Futbol");
        generarMarker(new LatLng(-27.34009998450895, -65.61359569430351), "Cancha del nevado", "Fútbol 11", "Público", "Futbol");
        generarMarker(new LatLng(-27.341179295052612, -65.61409994959831), "Cancha Miguel Lillo", "Fútbol 11", "Público", "Futbol");
        generarMarker(new LatLng(-27.3636756416269, -65.5990131944418), "Canchas Lescano", "Fútbol 7", "Acercarse al lugar", "Futbol");
        generarMarker(new LatLng(-27.362877639101654, -65.60297951102257), "Cancha de Alto Verde", "Fútbol 11", "Público", "Futbol");
        generarMarker(new LatLng(-27.343233053410714, -65.60038045048714), "Cancha Sintetica Nueva 24", "Fútbol Sintético", "3865412803", "Futbol");
        generarMarker(new LatLng(-27.343233053410714, -65.60038045048714), "Aserradero", "Fútbol 7", "Acercarse al lugar", "Futbol");
        generarMarker(new LatLng(-27.342299098412518, -65.61644285917282), "Aeroclub", "Fútbol 7, Piletas", "Acercarse al lugar", "Futbol");
        generarMarker(new LatLng(-27.348169541907783, -65.60153916478157), "Ping Pong Padel", "Paddel, Ping Pong", "No Disponible", "Tenis"); //FALTA EL NUM
        generarMarker(new LatLng(-27.347195722608607, -65.58384161442518), "Lawn Tennis", "Tenis, Paddel", "No Disponible", "Tenis");   //FALTA EL NUM
        generarMarker(new LatLng(-27.350964087740458, -65.59487756341696), "La Arboleda", "Paddel", "No Disponible", "Tenis");          //FALTA EL NUM
        generarMarker(new LatLng(-27.334574607614705, -65.60458481311798), "Huirapuca", "Rugby, Hockey", "Solo para socios", "Rugby");
        generarMarker(new LatLng(-27.354597130149887, -65.60598492622375), "Corona Golf Club", "Golf", "Solo para socios", "Golf");
        generarMarker(new LatLng(-27.34410505530781, -65.59491813182831), "Ares Gym", "Aparatos, Spinning", "Solo para socios", "Gym");
        generarMarker(new LatLng(-27.34348560111418, -65.59312641620636), "Perfil Gym", "Aparatos, Spinning", "Solo para socios", "Gym");
        generarMarker(new LatLng(-27.34839825418991, -65.59124082326889), "Atenea Gym", "CrossFit", "Solo para socios", "Gym");
        generarMarker(new LatLng(-27.345162884462418, -65.6008431315422), "Haymes Gym", "Aparatos", "Solo para socios", "Gym");
        generarMarker(new LatLng(-27.3462921774135, -65.58760911226273), "Leo Gym", "Aparatos", "Solo para socios", "Gym");
        //Alberdi
        generarMarker(new LatLng(-27.597, -65.616939), "Estadio Marapa", "Futbol", "Público", "Futbol");
        generarMarker(new LatLng(-27.595136, -65.618033), "Complejo Municipal Prof. Villagra", "Polideportivo", "Público", "Run");
        generarMarker(new LatLng(-27.582413, -65.623387), "Futbol 5 Lucenas", "Futbol", "No disponible", "Futbol");
        generarMarker(new LatLng(-27.584325, -65.610907), "Futbol 5 El Hangar", "Futbol", "No disponible", "Futbol");
        generarMarker(new LatLng(-27.590382, -65.619525), "Futbol 5 Ofempe", "Futbol", "No disponible", "Futbol");
        generarMarker(new LatLng(-27.593786, -65.613602), "Futbol 5", "Futbol", "No disponible", "Futbol");
        generarMarker(new LatLng(-27.595117, -65.617915), "Futbol 5 Viviana", "Futbol", "No disponible", "Futbol");
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-9407277542003955/7452371823");
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);

    }

    public void buscarDireccion() {
        if (!verificaConexion(this)) {
            new AlertDialog.Builder(MapsActivity.this)
                    .setTitle(("No hay conexión a internet"))
                    .setMessage(("WiFi y Datos Desactivados, desea ir a Configuraciones?"))
                    .setIcon(R.drawable.dialogalert)
                    .setPositiveButton(("Aceptar"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            MapsActivity.this.startActivity(intent);
                        }
                    })
                    .setNegativeButton(("Cancelar"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } else {
            cadena = txtBuscar.getText().toString();
            if (cadena.length() > 0) {
                txtBuscar.setText("");
                calle = eliminarEspacios(cadena);
                 calle2 = limpiarAcentos(calle);
                ciudad = "";
                location=this.Map.getMyLocation();
                if (location != null) {
                    final JSONParse json1 = new JSONParse();
                    json1.execute();
                    Thread thread1 = new Thread(){
                        public void run(){
                            try {
                                json1.get(15, TimeUnit.SECONDS);
                            } catch (Exception e) {
                                json1.cancel(true);
                                MapsActivity.this.runOnUiThread(new Runnable() {
                                    @SuppressLint("ShowToast")
                                    public void run() {
                                        json1.pDialog.dismiss();
                                        json1.isCancelled();
                                        CustomToast t = new CustomToast(MapsActivity.this, Toast.LENGTH_SHORT);
                                        t.show("Tiempo de espera excedido, verifica tu conexión a internet");
                                    }
                                });
                            }
                        }
                    };
                    thread1.start();
                } else {
                    CustomToast t = new CustomToast(this, Toast.LENGTH_SHORT);
                    t.show("Todavia esperando tu ubicación, asegurate de tener prendido el GPS");
                }
            } else {
                CustomToast t = new CustomToast(this, Toast.LENGTH_SHORT);
                t.show("Tenés que ingresar una direccion!");
            }
        }
    }


    public void displayInterstitial() {
        if ((interstitial.isLoaded())) {
            interstitial.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        int id = menu.getItemId();
        if (id == R.id.action_info) {
            showInfo(MapsActivity.this);
            return true;
        }
        return super.onOptionsItemSelected(menu);
    }

    private void iraDefecto() {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //Inicializamos el servicio de localizacion
        Map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-27.351232, -65.607369), 15));
    }
    public void generarMarker(LatLng cord, String titulo, String descripcion, String numero, String tipo) {
        listaC.add(new Complejo(cord, titulo, descripcion, numero));
        MarkerOptions m = new MarkerOptions().position(cord).title(titulo).snippet(descripcion);
        if (tipo.equals("Futbol")) {
            m.icon(BitmapDescriptorFactory.fromResource(R.drawable.mfutbol));
        }
        if (tipo.equals("Tenis")) {
            m.icon(BitmapDescriptorFactory.fromResource(R.drawable.mtenis));
        }
        if (tipo.equals("Golf")) {
            m.icon(BitmapDescriptorFactory.fromResource(R.drawable.mgolf));
        }
        if (tipo.equals("Rugby")) {
            m.icon(BitmapDescriptorFactory.fromResource(R.drawable.mrugby));
        }
        if (tipo.equals("Gym")) {
            m.icon(BitmapDescriptorFactory.fromResource(R.drawable.mgym));
        }
        if (tipo.equals("Run")) {
            m.icon(BitmapDescriptorFactory.fromResource(R.drawable.mrun));
        }
        if (tipo.equals("Hockey")) {
            m.icon(BitmapDescriptorFactory.fromResource(R.drawable.mhockey));
        }
        Map.addMarker(m);
    }

    private void alternarVista()
    {
        vista = (vista + 1) % 4;

        switch(vista)
        {
            case 0:
                Map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                Map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 2:
                Map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 3:
                Map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        String descripcion = marker.getSnippet();
        String numero = null;
        String titulo = marker.getTitle();
        LatLng ll = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
       final infowWindow iw = new infowWindow(this, ll, titulo, numero, descripcion);
        iw.execute();
        Thread thread1 = new Thread(){
            public void run(){
                try {
                    iw.get(15, TimeUnit.SECONDS);  //set time in milisecond(in this timeout is 30 seconds

                } catch (Exception e) {
                    iw.cancel(true);
                    ((Activity) MapsActivity.this).runOnUiThread(new Runnable() {
                        @SuppressLint("ShowToast")
                        public void run() {
                            iw.pDialog.dismiss();
                            iw.isCancelled();
                            CustomToast t = new CustomToast(MapsActivity.this, Toast.LENGTH_SHORT);
                            t.show("Tiempo de espera excedido, verifica tu conexión a internet");
                        }
                    });
                }
            }
        };
        thread1.start();

    }

    public class infowWindow extends AsyncTask<Void, Void, String> {
        ProgressDialog pDialog;
        MapsActivity mps;
        Double lat, lon;
        String direccion, titulo, numero, descripcion;
        public infowWindow(MapsActivity mps, LatLng ll,String titulo, String num, String descripcion){
            this.mps = mps;
            this.lat = ll.latitude;
            this.lon = ll.longitude;
            this.titulo = titulo;
            this.numero = num;
            this.descripcion = descripcion;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapsActivity.this);
            pDialog.show();
            pDialog.setContentView(R.layout.progress_dialog);
            pDialog.setCancelable(true);
        }
        @Override
        protected String doInBackground(Void... params) {
            direccion = getCurrentLocationViaJSON(lat, lon);

            return direccion;
        }
        @Override
        protected void onPostExecute(String dir) {
            pDialog.dismiss();
            info = new String[3];
            for (int i = 0; i < listaC.size(); i++) {
                if (listaC.get(i).getTitulo().equals(titulo)) {
                    numero = listaC.get(i).getNumero();
                    break;
                }
            }
            info[0] = numero;
            info[1] = direccion;
            info[2] = descripcion;
            Intent i = new Intent(MapsActivity.this, Descripcion.class);
            i.putExtra(ACT_INFO, info); //Aqui mandamos el arreglo a la clase junto con su path
            startActivity(i);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //ALGUN EVENTO CUANDO HACES CLICK EN CUALQUIER PARTE DEL MAPA
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng target = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.zoom(18);
        builder.target(target);
        this.Map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
        return false;
    }

    @Override
    public void onMyLocationChange(Location loc) {
        LatLng posicion = new LatLng(loc.getLatitude(), loc.getLongitude());
        Map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 15));
        Map.setOnMyLocationChangeListener(null);
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        CustomToast t = new CustomToast(this, Toast.LENGTH_SHORT);
        t.show("GPS ON");
    }

    @Override
    public void onProviderDisabled(String provider) {
        CustomToast t = new CustomToast(this, Toast.LENGTH_SHORT);
        t.show("GPS OFF");
    }

    public void iramiUbicacion(final View v) {
        Location location = this.Map.getMyLocation();
        if (location != null) {
            LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
            Log.i("Debug", target.toString());
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(15);
            builder.target(target);
            this.Map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            displayInterstitial();
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Salir")
                    .setMessage("Estás seguro?")
                    .setNegativeButton(android.R.string.cancel, null)//sin listener
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    }).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static JSONObject getLocationInfo(double lat, double lng) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HttpGet httpGet = new HttpGet(
                    "http://maps.googleapis.com/maps/api/geocode/json?latlng="
                            + lat + "," + lng + "&sensor=true");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                int b;
                while ((b = reader.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(stringBuilder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }
        return null;
    }


    public static String getCurrentLocationViaJSON(double lat, double lng) {

        JSONObject jsonObj = getLocationInfo(lat, lng);
        Log.i("JSON string =>", jsonObj.toString());
        String Address1 = "";
        String Address2 = "";
        String City = "";
        String State = "";
        String Country = "";
        String County = "";
        String PIN = "";
        String currentLocation = "";
        try {
            String status = jsonObj.getString("status").toString();
            Log.i("status", status);
            if (status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObj.getJSONArray("results");
                JSONObject zero = Results.getJSONObject(0);
                JSONArray address_components = zero
                        .getJSONArray("address_components");
                for (int i = 0; i < address_components.length(); i++) {
                    JSONObject zero2 = address_components.getJSONObject(i);
                    String long_name = zero2.getString("long_name");
                    JSONArray mtypes = zero2.getJSONArray("types");
                    String Type = mtypes.getString(0);
                    if (Type.equalsIgnoreCase("street_number")) {
                        Address1 = long_name + "";
                    } else if (Type.equalsIgnoreCase("route")) {
                        Address1 = long_name + " " + Address1;
                        //} else if (Type.equalsIgnoreCase("sublocality")) {
                        //  Address2 = long_name;
                    } else if (Type.equalsIgnoreCase("locality")) {
                        // Address2 = Address2 + long_name + ", ";
                        City = long_name;
                    } else if (Type
                            .equalsIgnoreCase("administrative_area_level_2")) {
                        County = long_name;
                    } else if (Type
                            .equalsIgnoreCase("administrative_area_level_1")) {
                        State = long_name;
                    } else if (Type.equalsIgnoreCase("country")) {
                        Country = long_name;
                    } else if (Type.equalsIgnoreCase("postal_code")) {
                        PIN = long_name;
                    }
                }
                currentLocation = Address1 + " - " + City + " - "
                        + State + " - " + Country + " - " + PIN;
            }
        } catch (Exception e) {
        }
        return currentLocation;
    }

    public void showInfo(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.acercade, null)).
                setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public LatLng getLatLng(JSONObject jsonObject) {
        Double lon = new Double(0);
        Double lat = new Double(0);
        try {
            String status = jsonObject.getString("status");
            Log.i("Debug", "Estado final: " + status);
            if (status.equals("ZERO_RESULTS")) {
                Toast.makeText(this, "No se puede encontrar la dirección", Toast.LENGTH_SHORT).show();
            } else if (status.equals("OK")) {
                lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");
                lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("Debug", "Latitud: " + lat);
        Log.i("Debug", "Longitud: " + lon);
        return new LatLng(lat, lon);
    }

    public static String eliminarEspacios(String cadena) {
        String cad = (cadena.replace(" ", "+"));
        String cad2 = (cad.replace("\n", "+"));
        String cad3 = cad2.replace(",", "+");
        String cad4 = cad3.replace("-", "+");

        Log.i("Debug", "Cadena sin espacios" + cad4);
        return cad4;
    }

    public static String limpiarAcentos(String cadena) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = cadena;
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }//remove1

    public void esconderTeclado(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(MapsActivity.this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == findViewById(R.id.btnInstrucciones).getId()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.instrucciones, null)).
                    setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        if (v.getId() == findViewById(R.id.btnBuscar).getId()) {
            esconderTeclado(v);
            buscarDireccion();
        }
        if (v.getId() == findViewById(R.id.btnTipoMapa).getId()) {
            alternarVista();
        }

    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapsActivity.this);
            pDialog.show();
            pDialog.setContentView(R.layout.progress_dialog);
            pDialog.setCancelable(true);

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            LatLng miPos = new LatLng(location.getLatitude(), location.getLongitude());
            Log.i("Debug", miPos.toString());
            JSONObject json = getLocationInfo(miPos.latitude, miPos.longitude);
            try {
                String status = json.getString("status").toString();
                Log.i("status", status);

                if (status.equalsIgnoreCase("OK")) {
                    JSONArray Results = json.getJSONArray("results");
                    JSONObject zero = Results.getJSONObject(0);
                    JSONArray address_components = zero
                            .getJSONArray("address_components");

                    for (int i = 0; i < address_components.length(); i++) {
                        JSONObject zero2 = address_components.getJSONObject(i);
                        String long_name = zero2.getString("long_name");
                        JSONArray mtypes = zero2.getJSONArray("types");
                        String Type = mtypes.getString(0);
                        if (Type.equalsIgnoreCase("locality")) {

                            ciudad = long_name;
                            Log.i("Debug", "Ciudad: " + ciudad);
                        } else if (Type
                                .equalsIgnoreCase("administrative_area_level_2")) {
                            ciudad = ciudad + " " + long_name;
                            Log.i("Debug", "Ciudad2: " + ciudad);
                        } else if (Type
                                .equalsIgnoreCase("administrative_area_level_1")) {
                            ciudad = ciudad + " " + long_name;
                            Log.i("Debug", "Ciudad3 " + ciudad);
                        }
                    }
                }
            } catch (Exception e) {
                Log.i("Error", "" + e.toString());
            }

            String city = eliminarEspacios(ciudad);
            Log.i("Debug", "City " + city);
            String city2 = limpiarAcentos(city);
            Log.i("Debug", "City2 " + city2);
            url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + calle2 + city2 + "&sensor=true";
            Log.i("Debug", "URL: " + url);
            JSONParser jParser = new JSONParser();
            JSONObject json1 = jParser.getJSONFromUrl(url);
            return json1;
        }

        @Override
        protected void onPostExecute(JSONObject json1) {
            pDialog.dismiss();
            try {
                if (json1.getString("status").equals("OK")) {
                    LatLng Source = getLatLng(json1);
                    Log.i("Debug", "Source " + Source.toString());
                    if (markerBusqueda != null) {
                        markerBusqueda.remove();
                    }
                    markerBusqueda = Map.addMarker(new MarkerOptions()
                            .position(Source)
                            .title("Resultado")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));//icon(BitMapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    CameraPosition.Builder builder = new CameraPosition.Builder();
                    builder.zoom(18);
                    builder.target(Source);
                    Map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
                } else {
                    CustomToast t = new CustomToast(MapsActivity.this, Toast.LENGTH_SHORT);
                    t.show("No se encuentra la direccion");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onStop() {
        MapStateManager mgr = new MapStateManager(MapsActivity.this);
        mgr.saveMapState(Map);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MapStateManager mgr = new MapStateManager(MapsActivity.this);

        CameraPosition position = mgr.getSavedCameraPosition();

        if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            Map.moveCamera(update);
        }
    }

public static boolean verificaConexion(Context ctx) {
		boolean bConectado = false;
		ConnectivityManager connec = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// No sólo wifi, también GPRS
		NetworkInfo[] redes = connec.getAllNetworkInfo();
		// este bucle debería no ser tan ñapa
		for (int i = 0; i < 2; i++) {
			// ¿Tenemos conexión? ponemos a true
			if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
				bConectado = true;
			}
		}
		return bConectado;
	}
}





