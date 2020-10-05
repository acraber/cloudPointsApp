package com.depixionapps.cloudpointsapp

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.integration.android.IntentIntegrator
import java.text.DateFormat
import java.util.*

class Methods {
    var doneWithShowingSpinner = false
    var actualPointsAdding = 0
    var pointsToAdd = 0
    var totalPointsAfterAdding = 0
    var tableName = ""
    var numberOfPointsAllowed = 0
    lateinit var context: Context
    lateinit var activity: Activity
    var usingNumberPicker: Boolean = false
    var phoneID = "Phone02"
    var storeName = ""


    fun setVariablesInMethods(storeName: String, numberOfPointsAllowed: Int, thisTable: String, thisContext: Context, thisActivity: Activity, usingNumberPicker: Boolean){
        this.numberOfPointsAllowed = numberOfPointsAllowed
        this.tableName = thisTable
        this.context = thisContext
        this.activity = thisActivity
        this.usingNumberPicker = usingNumberPicker
        this.storeName = storeName
    }

    fun scanCode() {
        val integrator = IntentIntegrator(activity)
        integrator.captureActivity = CaptureAct::class.java
        integrator.setOrientationLocked(false)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        integrator.setPrompt("Scanning Code")
        integrator.initiateScan()
    } //5

    fun qrScanSuccess(redeemPointsBtn: Button, pointsNumberTextView: TextView, progressBar: ProgressBar){
        /*Sets the progress bar and points number to match what's in the database.
        Is placed in the onActivityResult() and is used every time something's scanned */

        /*
        addPointsToDb(pointsToAdd)//24
        showButtonIfUserHasFiftyPoints(redeemPointsBtn)

        setProgressBarAndPointsNumber(
            getPointsValueFromDb(),
            progressBar, pointsNumberTextView
        )
        Toast.makeText(context, "$actualPointsAdding Points added", Toast.LENGTH_LONG).show()
        //auditFirebasePoints(actualPointsAdding)
        actualPointsAdding = 0
        pointsToAdd = 0


        if(isThereMoreThanOneSetOfPoints()){
            val databaseHandler = DatabaseHandler(context)
            databaseHandler.deleteFirstRow(tableName)
            databaseHandler.close()
        }}*/
    }

    fun startNumberPicker(){
        //shows the number picker
        pointsToAdd = 0


        doneWithShowingSpinner = false
        val d = Dialog(context)
        d.setTitle("NumberPicker")
        d.setContentView(R.layout.dialog)
        val b1: Button = d.findViewById(R.id.setButton) as Button
        val b2: Button = d.findViewById(R.id.cancelButton) as Button
        val numberPicker = d.findViewById(R.id.numberPicker1) as NumberPicker
        numberPicker.maxValue = 25
        numberPicker.minValue = 1
        numberPicker.wrapSelectorWheel = false

        b1.setOnClickListener {
            totalPointsAfterAdding = 0
            pointsToAdd = numberPicker.value
            d.dismiss()
            doneWithShowingSpinner = true
            /*totalPointsAfterAdding = pointsToAdd + getPointsValueFromDb()
            if (totalPointsAfterAdding >= numberOfPointsAllowed) {
                actualPointsAdding = numberOfPointsAllowed - getPointsValueFromDb()
                pointsToAdd = actualPointsAdding
            } else {
                actualPointsAdding = pointsToAdd
            }*/
            setAlertDialogs()
        }//31 and also //6 earlier

        b2.setOnClickListener {
            d.dismiss()
        }
        d.show()
    }

    fun setAlertDialogs(){
        /*all the alert dialog stuff I wrote kinda sucks. I know it's confusing but it works. Probably a good idea to re-write.
        Sets the alert dialog to add points. Called on after choosing the number from the number picker.
         */

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Adding ${pointsToAdd} points")
        builder.setPositiveButton("SCAN") { dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(
                context,
                "$pointsToAdd points are being added",
                Toast.LENGTH_LONG
            ).show()
            scanCode()
        }
        builder.setNegativeButton("GO BACK") { dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(activity, "Scan cancelled", Toast.LENGTH_SHORT).show()
        }
//was if (totalPointsAfterAdding >= numberOfPointsAllowed) {
        if (false) {
            builder.setMessage(
                "A Los Amigos employee must verify points before scanning.\n\nThe maximum total points allowed is $numberOfPointsAllowed\n\n" +
                        "Any points above a total of $numberOfPointsAllowed will not be added"
            )
            totalPointsAfterAdding = 0
            builder.show()
        } else {
            builder.setMessage("A Los Amigos employee must verify points before scanning.")
            totalPointsAfterAdding = 0
            builder.show()
        }
    }

    fun changeFireBasePoints(){
        /* Used to change the number of points in the firebase database for record keeping.
        Does not provide update data for each update. Only provides most recent update.
        Is placed during the add points methods and in the redeem points builder.
           -this allows it to be called on whenever we add points locally and redeem points locally.
         */

        if(FirebaseAuth.getInstance().currentUser?.uid != null){
            val ref = FirebaseDatabase.getInstance().getReference("Edit").child(storeName)
            val email = FirebaseAuth.getInstance().currentUser?.email
            val tupleName = FirebaseAuth.getInstance().currentUser!!.uid

            val points = ref.child("points").ref

            Toast.makeText(context, "Points: $points", Toast.LENGTH_LONG).show()

            val currentDate = DateFormat.getDateInstance().format(Date())
            val currentTime = DateFormat.getTimeInstance().format(Date())

            val hero = EditFirebaseHero(email!!, pointsToAdd, currentDate, currentTime)

            ref.child(tupleName).setValue(hero).addOnCompleteListener {

            }

        }else{
            Toast.makeText(context, "User ID isn't even available. User isn't even signed in. Should never have made it this far.", Toast.LENGTH_LONG).show()
        }


    }






}