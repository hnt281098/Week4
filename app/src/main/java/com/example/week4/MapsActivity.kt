package com.example.week4

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.example.week4.Adapters.PlacesAutocompleteAdapter
import com.example.week4.PlaceSearchModal.Prediction
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity()
    ,OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
    ,GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private lateinit var mMap: GoogleMap

    private val Request_User_Location_Code = 99

    private lateinit var locationRequest: LocationRequest
    private var googleApiClient: GoogleApiClient? = null
    private lateinit var lastLocation: Location

    private var current: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

//        val placeAutocompleteFragment: PlaceAutocompleteFragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment
//        placeAutocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{
//            override fun onPlaceSelected(p0: Place?) {
//                if(current != null){
//                    current!!.remove()
//                }
//
//                val latLng = p0!!.latLng
//
//                current = mMap.addMarker(
//                    MarkerOptions()
//                        .position(latLng)
//                        .title(p0.name.toString()))
//
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//                mMap.animateCamera(CameraUpdateFactory.zoomBy(12.toFloat()))
//            }
//
//            override fun onError(p0: Status?) {
//                Toast.makeText(this@MapsActivity, "Error", Toast.LENGTH_SHORT).show()
//            }
//
//        })

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserLocationPermission()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        loadData()
    }

//    private fun loadData(){
//        val predictions: ArrayList<Prediction> = ArrayList()
//        val placesAutocompleteAdapter = PlacesAutocompleteAdapter(this, predictions)
//        place_autocomplete.threshold = 1
//        place_autocomplete.setAdapter(placesAutocompleteAdapter)
//    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            Request_User_Location_Code -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    if(googleApiClient != null){
                        buildGoogleApiClient()
                    }
                    mMap.isBuildingsEnabled = true
                }
                else{
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

//    private fun search(){
//        val address: String = txtSearch.text.toString()
//
//        val listAddress: ArrayList<Address>?
//
//        if(!address.isEmpty()){
//            val geoCoder = Geocoder(this)
//
//            listAddress = geoCoder.getFromLocationName(address, 6) as ArrayList<Address>
//
//            var i = 0
//
//            when(i < listAddress.size){
//                true -> {
//                    val userAddress: Address = listAddress[i]
//                    val latLng = LatLng(userAddress.latitude, userAddress.longitude)
//
//                    mMap.addMarker(MarkerOptions().position(latLng).title(userAddress.featureName))
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10.toFloat()))
//
//                    i += 1
//                }
//            }
//
//            if(i == 0){
//                Toast.makeText(this, "Not found location", Toast.LENGTH_LONG).show()
//            }
//        }
//        else{
//            Toast.makeText(this, "Please write location", Toast.LENGTH_LONG).show()
//        }
//    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 1100
        locationRequest.fastestInterval = 1100
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
        }
    }

    private fun buildGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .addApi(Places.GEO_DATA_API)
            .addApi(Places.PLACE_DETECTION_API)
            .build()
        googleApiClient!!.connect()
    }

    private fun checkUserLocationPermission(): Boolean {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Request_User_Location_Code)
            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Request_User_Location_Code)
            }
            return false
        }

        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient()
            mMap.isMyLocationEnabled = true
        }
    }

    override fun onConnected(p0: Bundle?) {
        createLocationRequest()
    }

    override fun onConnectionSuspended(p0: Int) {
        googleApiClient!!.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLocationChanged(location: Location?) {
        lastLocation = location!!
        if(current != null){
            current!!.remove()
        }

        val latLng = LatLng(location.latitude, location.longitude)

        current = mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("My"))

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12.toFloat()))

        if(googleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)

        }
    }
}
