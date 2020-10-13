package com.depixionapps.cloudpointsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_los_amigos_points.*

//I can write the layout id != 0 do the good layout if not then have an error layout
open class StoresInheritFromThisActivity : AppCompatActivity() {

    open val layoutId= 0
    open val scanBtnId = 0
    open val redeemPointsBtnId = 0

    open val qrCode = ""
    open val storeName = ""
    open val usesNumberPicker = false
    open val numberOfPointsAllowed = 0
    open lateinit var context: Context
    open lateinit var thisActivity: Activity

    private val methods = Methods()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Wendy 2 change activity_los_amigos_points to the new layout
        setContentView(layoutId)


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
            methods.matchTextViewAndButtonsToDb(bbPointsNumberTextView, getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE), progressBar, redeemPointsBtn)
            //makes the transition to changing points text a little smoother as long as the shared preferences matches what's in the database.
            methods.loadSharedPreferencesData(bbPointsNumberTextView, getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE), progressBar)

            methods.showButtonIfUserHasFiftyPoints(redeemPointsBtn, bbPointsNumberTextView)
        }else{
            Toast.makeText(this, "Internet connection required", Toast.LENGTH_LONG).show()
        }



        scanBtn.setOnClickListener{
            if(methods.isNetworkAvailable()) {
                methods.onAddPointsButton(bbPointsNumberTextView)
            }else{
                Toast.makeText(this, "Internet connection required", Toast.LENGTH_LONG).show()
            }
        }



        redeemPointsBtn.setOnClickListener{
            if(methods.isNetworkAvailable()) {
                methods.redeemPoints(bbPointsNumberTextView, getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE), progressBar, redeemPointsBtn)
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
                    methods.changeFireBasePoints(bbPointsNumberTextView,  getSharedPreferences("sharedPrefs",
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