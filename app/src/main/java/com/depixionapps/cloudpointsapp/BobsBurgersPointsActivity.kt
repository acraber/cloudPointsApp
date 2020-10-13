package com.depixionapps.cloudpointsapp

import android.app.Activity
import android.content.Context


/* Wendy 1. make a new activity and copy and paste this into it.
   Wendy 2. change BobsBurgersPointsActivity to match the activity name that you just made.
   Wendy 3. change anything you want from the top block of code (qqrCode, storeName ect.)
 */
class BobsBurgersPointsActivity : StoresInheritFromThisActivity() {

   /*  These need to be changed to match customer requests. For the most part this is the only thing
       We'll change but just in case we want different stores to look different I've kept the ability
       To incorporate new layouts*/
    override val qrCode = "AAA"
    override val storeName = "Bob's Burgers"
    override val usesNumberPicker = false //set to true if you want to add more than 1 point at a time.
    override val numberOfPointsAllowed = 5
    override val messageText = "Buy 5 burgers and get absolutely nothing!!!!!!!"



    // Only change this if we're doing a different layout - if we are, then that layout will have
    // different button id's and all that.
    override val layoutId= R.layout.store_points_layout
    override val scanBtnId = R.id.scanBtn
    override val redeemPointsBtnId = R.id.redeemPointsBtn
    override val pointsNumberTextViewId = R.id.pointsNumberTextView
    override val progressBarId = R.id.progressBar
    override val messageTextViewId = R.id.messageTextView


    //these are the same no matter what, no need to change
    override var context: Context = this
    override var thisActivity: Activity = this
}