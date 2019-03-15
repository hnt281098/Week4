package com.example.week4

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.week4.Modal.ListUsers
import com.example.week4.Modal.User
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.form_login.*
import kotlinx.android.synthetic.main.form_sign_up.*

@Suppress("UNREACHABLE_CODE", "DEPRECATION")
class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var users: DatabaseReference

    var list_users: ListUsers? = null
    lateinit var arrUsers: ArrayList<User>

//    private var googleApiClient: GoogleApiClient? = null

    private var lastLocation: Location? = null

//    private var latitudeText: Double = 0.0
//    private var longitudeText: Double = 0.0

//    override fun onConnected(p0: Bundle?) {
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
//        }
//        if (lastLocation != null) {
//            latitudeText = lastLocation!!.latitude
//            longitudeText = lastLocation!!.longitude
//        }
//        Log.d("last", lastLocation.toString())
//        Log.d("lati", latitudeText.toString())
//        Log.d("longi", longitudeText.toString())
//    }
//
//    override fun onConnectionSuspended(p0: Int) {
//        buildGoogleApiClient()
//    }
//
//    override fun onConnectionFailed(p0: ConnectionResult) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    private fun buildGoogleApiClient() {
//        googleApiClient = GoogleApiClient.Builder(this)
//            .addConnectionCallbacks(this)
//            .addOnConnectionFailedListener(this)
//            .addApi(LocationServices.API)
//            .build()
//        googleApiClient!!.connect()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mFusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mFusedLocationClient.lastLocation.addOnSuccessListener {
                    lastLocation = it
                }
            }

        //Init
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        users = db.getReference("Users")
        arrUsers = arrayListOf()

        //Event
        btnLogin.setOnClickListener {
            add_form_login.visibility = View.VISIBLE
            add_form_sign_up.visibility = View.GONE
            txtUserName.text = null
            txtEmailAddress.text = null
            txtPassConfirm.text = null
        }

        btnSignUp.setOnClickListener {
            add_form_login.visibility = View.GONE
            add_form_sign_up.visibility = View.VISIBLE
            txtEmail.text = null
            txtPass.text = null
        }

        btnSkip.setOnClickListener {
            val int = Intent(applicationContext, SuccessActivity::class.java)
            int.putExtra("latitude", lastLocation!!.latitude)
            int.putExtra("longitude", lastLocation!!.longitude)
            startActivity(int)
        }

        SignUpWithEmail()

        LoginWithEmail()
    }

    private fun SignUpWithEmail(){
        btnSignUpWithEmail.setOnClickListener {
            //Check validation
            when {
                txtUserName.text.isEmpty() -> {
                    Toast.makeText(applicationContext, "Please enter User Name", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                txtEmailAddress.text.isEmpty() -> {
                    Toast.makeText(applicationContext, "Please enter Email Address", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                txtPassConfirm.text.isEmpty() -> {
                    Toast.makeText(applicationContext, "Please enter Password", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            auth.createUserWithEmailAndPassword(txtEmailAddress.text.toString(), txtPassConfirm.text.toString())
                .addOnSuccessListener {
                    //Save to db
                    val user = User(txtUserName.text.toString(), txtEmailAddress.text.toString(), txtPassConfirm.text.toString())
                    arrUsers.add(user)
                    list_users = ListUsers(arrUsers)

                    users.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(user)
                        .addOnSuccessListener {
                            Toast.makeText(applicationContext, "Register success", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(applicationContext, "Register fail", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Register fail", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun LoginWithEmail(){
        btnLoginWithEmail.setOnClickListener {
            //Check validation
            when {
                txtEmail.text.isEmpty() -> {
                    Toast.makeText(applicationContext, "Please enter Email", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                txtPass.text.isEmpty() -> {
                    Toast.makeText(applicationContext, "Please enter Password", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            auth.signInWithEmailAndPassword(txtEmail.text.toString(), txtPass.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "Login success", Toast.LENGTH_LONG).show()
//                    startActivity(Intent(applicationContext, MapsActivity::class.java))
//                    finish()
                    val int = Intent(applicationContext, SuccessActivity::class.java)
                    int.putExtra("latitude", lastLocation!!.latitude)
                    int.putExtra("longitude", lastLocation!!.longitude)
                    startActivity(int)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Login fail", Toast.LENGTH_LONG).show()
                }
        }
    }
}
