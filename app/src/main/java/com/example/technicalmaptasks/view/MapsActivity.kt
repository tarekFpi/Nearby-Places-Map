
package com.example.technicalmaptasks.view
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.technicalmaptasks.R
import com.example.technicalmaptasks.utils.GetNearbyPlacesData
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener{

    private lateinit var mMap: GoogleMap


    private var client: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null
    private var lastlocation: Location? = null
    private var currentLocationmMarker: Marker? = null
    val REQUEST_LOCATION_CODE = 99
    var PROXIMITY_RADIUS = 10000
    var latitude = 0.0
    var longitude=0.0

    val dataTransfer = ArrayList<String>(2)

    private  val getNearbyPlacesData = GetNearbyPlacesData()

    var mapTilerKey="NDgwMjo2WkgzUkE3N1dP"

    val styleUrl  = "https://api.maptiler.com/maps/streets-v2/style.json?key=${mapTilerKey}";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        checkLocationPermission()
    }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_CODE
                )
            }
            false

        } else true
    }


    override fun onMapReady(googleMap: GoogleMap) {


        mMap = googleMap


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            bulidGoogleApiClient()
            mMap.isMyLocationEnabled = true
        }
    }

    @Synchronized
    protected fun bulidGoogleApiClient() {
        client = GoogleApiClient.Builder(this).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
        client!!.connect()
    }

    override fun onConnected(p0: Bundle?) {

        locationRequest = LocationRequest()
        locationRequest!!.setInterval(100)
        locationRequest!!.setFastestInterval(1000)
        locationRequest!!.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                client!!,
                locationRequest!!,
                this
            )
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(location: Location) {


        latitude = location.getLatitude()
        longitude = location.getLongitude()
        lastlocation = location

        val latLng = LatLng(location.getLatitude(), location.getLongitude())
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Location")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        currentLocationmMarker = mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomBy(18f))


        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client!!, this)
        }


    }

    private fun getUrl(latitude: Double, longitude: Double, nearbyPlace: String): String? {
        val googlePlaceUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googlePlaceUrl.append("location=$latitude,$longitude")
        googlePlaceUrl.append("&radius=$PROXIMITY_RADIUS")
        googlePlaceUrl.append("&type=$nearbyPlace")
        googlePlaceUrl.append("&sensor=true")
       googlePlaceUrl.append("&key=" + getString(R.string.google_maps_key))
       // googlePlaceUrl.append("&key=" + google_maps_key)
        Log.d("MapsActivity", "url = $googlePlaceUrl")
        return googlePlaceUrl.toString()
    }

    fun showBank(view: View) {

        //  mMap.clear()
        val hospital = "hospital"
        val url = getUrl(latitude, longitude, hospital)
        dataTransfer[0] = mMap.toString()
        dataTransfer[1] = url.toString()

        getNearbyPlacesData.execute(dataTransfer.toString())
    }
}