package com.gachi.tucusports;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {

	private final Context mContext;

    // bandera para el estado del GPS
    boolean isGPSEnabled = false;
 
    // bandera para el estado de la posicion por RED
    boolean isNetworkEnabled = false;
 
    private boolean canGetLocation = false;
    
    Location location; // ubicacion
    double latitude; // latitud
    double longitude; // longitud
    
    // La distancia m�nima en metros para obtener actualizar la posicion
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 metros
 
    // El tiempo minimo en milisegundos entre actualizaciones
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minuto
 
    // Declarando un  LocationManager
    protected LocationManager locationManager;
    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);
 
            // Obtengo el estado de GPS
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
 
            // Obtengo el estado de la posicion por RED
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
 
            if (isGPSEnabled || isNetworkEnabled) {
                this.canGetLocation = true;
                // Promero obtenemos la posicion por RED
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // Si el GPS esta activo obtenemos su posicion
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }
    /**
     * Funcion para obtener la latitud
     * */
     public double getLatitude(){
         if(location != null){
             latitude = location.getLatitude();
         }
  
         return latitude;
     }
  
     /**
      * Funci�n para obtener la longitud
      * */
     public double getLongitude(){
         if(location != null){
             longitude = location.getLongitude();
         }
  
         return longitude;
     }
  
     /**
      * Funci�n para chequear si se puede obtener la posicion
      * */
     public boolean canGetLocation() {
         return this.canGetLocation;
     }
  
     /**
      * Funcion para abrir un AlertDialog para ir a las configuraciones de Ubicacion
      * */
     public void showSettingsAlert(){
         new AlertDialog.Builder(mContext)
                 .setTitle(("GPS no activado"))
                 .setMessage(("EL GPS no esta activo, desea ir a las Configuraciones?"))
                 .setIcon(R.drawable.dialogalert)
                 .setPositiveButton(("Aceptar"), new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog,int which) {
                         Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                         mContext.startActivity(intent);
                     }
                 })
                 .setNegativeButton(("Cancelar"), new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.cancel();
                     }
                 })
                 .show();
     }
  
     /**
      * Detener el uso del listener
      * LLamando esta funcion la aplicacion dejar� de usar el GPS
      * */
     public void stopUsingGPS(){
         if(locationManager != null){
             locationManager.removeUpdates(GPSTracker.this);
         }
     }
  
     /**
     * Al cambiar la ubicacion actualizo los valores de latitud y longitud
     * */
     @Override
     public void onLocationChanged(Location location) {
         if (location != null) {
             latitude = location.getLatitude();
             longitude = location.getLongitude();
         }
     }

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}



}
