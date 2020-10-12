package com.depixionapps.cloudpointsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_los_amigos_points.*

/*
Wendy 1
    Go to app -> java. Right click on the top folder. hover over new -> activity -> click empty activity.
        name the activity match the store name

    Then Copy and paste everything from here on to a new activity.
    After copying, Change the LosAmigosPointsActivity below to the new activity name

    */
class LosAmigosPointsActivity : AppCompatActivity() {

    private val context: Context = this
    private val thisActivity: Activity = this
    private val methods = Methods()


    //Wendy 7 change these to fit the store's requests. That's it. Should be good to go!
    private val qrCode = "AAA"
    private val storeName = "Los Amigos"
    private val usesNumberPicker = true
    private val numberOfPointsAllowed = 50



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Wendy 2 change activity_los_amigos_points to the new layout
        setContentView(R.layout.activity_los_amigos_points)

        methods.setVariables(
            //This has to be done before any of the other methods are called from the MethodsHandler
            storeName,
            numberOfPointsAllowed,
            context,
            thisActivity,
            usesNumberPicker
        )

    //startup methods
        if(methods.isNetworkAvailable()) {
            /* changes the points AND shared preferences to whatever's in the database. I'm going to have to do this
            // very often to make sure the pointsNumberTextView matches what's in the database. this is one of the FIRST
            // things I have to do*/
            methods.matchTextViewAndButtonsToDb(pointsNumberTextView, getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE), progressBar, redeemPointsBtn)
            //makes the transition to changing points text a little smoother as long as the shared preferences matches what's in the database.
            methods.loadSharedPreferencesData(pointsNumberTextView, getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE), progressBar)

            methods.showButtonIfUserHasFiftyPoints(redeemPointsBtn, pointsNumberTextView)
        }else{
            Toast.makeText(this, "Internet connection required", Toast.LENGTH_LONG).show()
        }



        scanBtn.setOnClickListener{
            if(methods.isNetworkAvailable()) {
                methods.onAddPointsButton(pointsNumberTextView)
            }else{
                Toast.makeText(this, "Internet connection required", Toast.LENGTH_LONG).show()
            }
        }



        redeemPointsBtn.setOnClickListener{
            if(methods.isNetworkAvailable()) {
                methods.redeemPoints(pointsNumberTextView, getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE), progressBar, redeemPointsBtn)
            }else{
                Toast.makeText(this, "Internet connection required", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        //This is for when we get the result from the barcode scanner.
        if (result != null) {
            if (result.contents != null) {
                if(result.contents == qrCode) {
                    methods.changeFireBasePoints(pointsNumberTextView,  getSharedPreferences("sharedPrefs",
                        Context.MODE_PRIVATE), progressBar, redeemPointsBtn)
                }else {
                    Toast.makeText(this, "Barcode Not Recognized", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "No Results", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    } //7




}



