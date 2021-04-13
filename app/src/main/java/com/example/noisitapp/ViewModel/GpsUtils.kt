package com.example.noisitapp.ViewModel

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.lang.Exception

class GpsUtils(context: Context){
    private val TAG = "GPS"
    private  val GPSCODE = 101
    private val mContext: Context = context
    private var mSettingsClient : SettingsClient? =null
    private var mLocationSettingsRequest : LocationSettingsRequest ?=null
    private var mLocationManager : LocationManager ?= null
    private var mLocationRequest : LocationRequest ?= null

    init{
        mLocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        mSettingsClient = LocationServices.getSettingsClient(mContext)
        mLocationRequest = LocationRequest.create()
        mLocationRequest?.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = 1000
        mLocationRequest?.fastestInterval = 500
        if (mLocationRequest != null){
            val builder:LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest!!)
            mLocationSettingsRequest = builder.build()

        }
    }
    fun turnOnGPS(){
        if (mLocationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)== false){
            mSettingsClient?.checkLocationSettings(mLocationSettingsRequest)
            ?.addOnSuccessListener(mContext as Activity){
                Log.e(TAG,"turnONGPS : Already Enabled")
            }
            ?.addOnFailureListener{ ex ->
                if ((ex as ApiException).statusCode
                == LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                    try {
                        val resolvableApiException = ex as ResolvableApiException
                        resolvableApiException.startResolutionForResult(
                            mContext,
                            GPSCODE
                        )
                    }catch (e : Exception){
                        Log.d(TAG,"CannotOpen GPS")
                    }

                }else{
                    if (ex.statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE){
                        val errorMessase = "Location Settings are inadecuate cant be fixed"
                        Toast.makeText(mContext as Activity ?,errorMessase,Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }
}