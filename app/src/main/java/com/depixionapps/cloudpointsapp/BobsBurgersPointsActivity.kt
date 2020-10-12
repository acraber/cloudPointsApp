package com.depixionapps.cloudpointsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_los_amigos_points.pointsNumberTextView
import kotlinx.android.synthetic.main.activity_los_amigos_points.progressBar
import kotlinx.android.synthetic.main.activity_los_amigos_points.redeemPointsBtn
import kotlinx.android.synthetic.main.activity_los_amigos_points.scanBtn


class BobsBurgersPointsActivity : AppCompatActivity() {

    private val context: Context = this
    private val thisActivity: Activity = this
    private val methods = Methods()



    private val qrCode = "AAA"
    private val storeName = "Bob's Burgers"
    private val usingNumberPicker = false
    private val numberOfPointsAllowed = 5



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bobs_burgers_points)

        methods.setVariables(
            //This has to be done before any of the other methods are called from the MethodsHandler
            storeName,
            numberOfPointsAllowed,
            context,
            thisActivity,
            usingNumberPicker
        )

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

