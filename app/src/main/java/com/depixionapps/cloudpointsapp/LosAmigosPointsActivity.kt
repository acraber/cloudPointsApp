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
class LosAmigosPointsActivity : StoresInheritFromThisActivity() {
    //These are for the specific store request
    override val qrCode = "AAA"
    override val storeName = "Los Amigos"
    override val usesNumberPicker = true
    override val numberOfPointsAllowed = 50


    //these need to match the buttons in the xml file
    override val layoutId= R.layout.activity_los_amigos_points
    override val scanBtnId = R.id.scanBtn
    override val redeemPointsBtnId = R.id.redeemPointsBtn



    //these are the same no matter what, no need to change
    override var context: Context = this
    override var thisActivity: Activity = this
}



