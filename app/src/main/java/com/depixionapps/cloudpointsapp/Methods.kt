package com.depixionapps.cloudpointsapp

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_los_amigos_points.*
import java.lang.IllegalArgumentException
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

    fun startNumberPicker(textView: TextView){
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
            setAlertDialogs(textView)
        }//31 and also //6 earlier

        b2.setOnClickListener {
            d.dismiss()
        }
        d.show()
    }

    fun setAlertDialogs(textView: TextView){
        /*all the alert dialog stuff I wrote kinda sucks. I know it's confusing but it works. Probably a good idea to re-write.
        Sets the alert dialog to add points. Called on after choosing the number from the number picker.
         */

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Adding $pointsToAdd points")
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

    fun changeFireBasePoints(textView: TextView, sharedPreferences: SharedPreferences){
        /* Used to change the number of points in the firebase database for record keeping.
        Does not provide update data for each update. Only provides most recent update.
        Is placed during the add points methods and in the redeem points builder.
           -this allows it to be called on whenever we add points locally and redeem points locally.
         */
        var points = 0
        if(FirebaseAuth.getInstance().currentUser?.uid != null){
            val ref = FirebaseDatabase.getInstance().getReference("Edit").child(storeName)
            val email = FirebaseAuth.getInstance().currentUser?.email
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val tupleName = userId
            if(textView.text.toString() != "null"){
            val existingPoints = textView.text.toString().toInt()
             points = pointsToAdd + existingPoints
            }
            else{
                points = pointsToAdd
            }

            val currentDate = DateFormat.getDateInstance().format(Date())
            val currentTime = DateFormat.getTimeInstance().format(Date())

            val hero = EditFirebaseHero(email!!, points, currentDate, currentTime)

            ref.child(tupleName).setValue(hero).addOnCompleteListener {
                Toast.makeText(context, "Tuple saved successfully", Toast.LENGTH_SHORT).show()
            }
            changePointsAndText(textView, sharedPreferences)

        }else{
            Toast.makeText(context, "User ID isn't even available. User isn't even signed in. Should never have made it this far.", Toast.LENGTH_LONG).show()
        }


    }

    fun changePointsAndText(textView: TextView, sharedPreferences: SharedPreferences) {
        /*
        changes the number of points in the local shared preferences and the pointsNumberTextView to match whatever
        is in the database.
        This will be placed every time the system loads as well as every time the database is updated.
        The pointsNumberTextView is where I'm going to be getting all my data so I need to be really careful with this.
        */
        // If I'm going to do this workaround and not return a value with this function, I have to make DAMN
        // sure I'm changing the points value on startup every time. And then every time points are changed I need to
        // make sure I'm changing the text every single time.
        //Some questions are what if someone logs into a different phone with their account. If their points don't update right
        // and I didn't update their points, the user might lose their points when they update.
        // I might need to make an audit function to make sure the points in the database match what's shown on the textView.
        //      maybe something that makes sure shared preferences and the pointsNumberTextView are matching
        val ref = FirebaseDatabase.getInstance().getReference("Edit").child(storeName).child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var points = snapshot.child("points").value.toString()
                //when starting with a new account, the points will be null coming from firebase
                if (points == "null"){
                    points = "0"
                }
                val editor = sharedPreferences.edit()
                    editor.apply {
                        putString("$storeName points", points)
                    }.apply()
                    textView.text = points
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Err 11 Database reach cancelled", Toast.LENGTH_LONG).show()
            }
        })
    }


    fun loadSharedPreferencesData(textView: TextView, sharedPreferences: SharedPreferences){
        textView.text = sharedPreferences.getString("$storeName points", "0")
    }
}






