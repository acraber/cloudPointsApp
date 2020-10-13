package com.depixionapps.cloudpointsapp

import android.app.Activity
import android.content.Context
import kotlinx.android.synthetic.main.activity_bobs_burgers_points.*

class BobsBurgersPointsActivity : StoresInheritFromThisActivity() {
    //These are for the specific store request
    override val qrCode = "AAA"
    override val storeName = "Bob's Burgers"
    override val usesNumberPicker = false
    override val numberOfPointsAllowed = 5

    //these need to match the buttons in the xml file
    override val layoutId= R.layout.activity_los_amigos_points



    //these are the same no matter what, no need to change
    override var context: Context = this
    override var thisActivity: Activity = this
}