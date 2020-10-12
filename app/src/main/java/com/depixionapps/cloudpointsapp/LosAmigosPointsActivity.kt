package com.depixionapps.cloudpointsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_los_amigos_points.*


class LosAmigosPointsActivity : AppCompatActivity() {

    private val context: Context = this
    private val thisActivity: Activity = this
    private val usingNumberPicker = true
    private val numberOfPointsAllowed = 50
    private val methodsHandler = Methods()
    private val TAG = "LosAmigosPointsActivity"



    private val pointsTableName = "PointsTable"
    private val qrCode = "AAA"
    private val storeName = "LosAmigos"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_los_amigos_points)

        methodsHandler.setVariablesInMethods(
            //This has to be done before any of the other methods are called from the MethodsHandler
            storeName,
            numberOfPointsAllowed,
            pointsTableName,
            context,
            thisActivity,
            usingNumberPicker
        )

        // changes the points AND shared preferences to whatever's in the database. I'm going to have to do this
        // very often to make sure the pointsNumberTextView matches what's in the database. this is one of the FIRST
        // things I have to do
        //     it might be better to
        methodsHandler.changePointsAndText(pointsNumberTextView, getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE))


        //makes the transition to changing points text a little smoother as long as the shared preferences matches what's in the database.
        methodsHandler.loadSharedPreferencesData(pointsNumberTextView, getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE))




        scanBtn.setOnClickListener{
            methodsHandler.startNumberPicker()
        }



        redeemPointsBtn.setOnClickListener{
            methodsHandler.changePointsAndText(pointsNumberTextView, getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE))
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        //This is for when we get the result from the barcode scanner.
        if (result != null) {
            if (result.contents != null) {
                if(result.contents == qrCode) {
                    methodsHandler.changeFireBasePoints(pointsNumberTextView,  getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE))
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



