package com.depixionapps.cloudpointsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator


//I can write the layout id != 0 do the good layout if not then have an error layout
open class StoresInheritFromThisActivity() : AppCompatActivity() {

    open val layoutId= 0
    open val scanBtnId = 0
    open val redeemPointsBtnId = 0
    open val pointsNumberTextViewId = 0
    open val progressBarId = 0
    open val messageTextViewId =0

    open val messageText = ""
    open val qrCode = ""
    open val storeName = ""
    open val usesNumberPicker = false
    open val numberOfPointsAllowed = 0
    open lateinit var context: Context
    open lateinit var thisActivity: Activity

    private lateinit var scanBtn: Button
    private lateinit var redeemPointsBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var pointsNumberTextView: TextView
    private lateinit var messageTextView: TextView

    private val methods = Methods()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        //Declaring the buttons from the ID's given from the store activity
        scanBtn = findViewById(scanBtnId)
        redeemPointsBtn = findViewById(redeemPointsBtnId)
        progressBar = findViewById(progressBarId)
        pointsNumberTextView = findViewById(pointsNumberTextViewId)
        messageTextView = findViewById(messageTextViewId)

        methods.setVariables(
            //This has to be done before any of the other methods are called from the MethodsHandler
            storeName,
            numberOfPointsAllowed,
            context,
            thisActivity,
            usesNumberPicker
        )

        // setting up the message text here from the text we were given in the store's activity
        messageTextView.text = messageText

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
                methods.redeemPoints(findViewById(pointsNumberTextViewId), getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE), progressBar, redeemPointsBtn)
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