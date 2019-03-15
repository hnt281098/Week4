package com.example.week4

import android.content.Intent
import android.location.Address
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_success.*
import android.location.Geocoder
import android.view.Menu
import android.view.MenuItem
import java.util.*


class SuccessActivity : AppCompatActivity() {

    var latitude: Double = 0.0
    var longitude: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        supportActionBar?.show()


        btnWhere.setOnClickListener {
            startActivity(Intent(applicationContext, PlaceSearchActivity::class.java))
            finish()
        }

        btnMap.setOnClickListener {
            val int = Intent(applicationContext, MapsActivity::class.java)
            int.putExtra("latitude", latitude)
            int.putExtra("longitude", longitude)
            startActivity(int)
        }

        val geocoder: Geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>
        latitude = intent.extras.getDouble("latitude")
        longitude = intent.extras.getDouble("longitude")
        addresses = geocoder.getFromLocation(latitude, longitude, 1)

        txtLocation.text = addresses[0].getAddressLine(0)
    }
}
