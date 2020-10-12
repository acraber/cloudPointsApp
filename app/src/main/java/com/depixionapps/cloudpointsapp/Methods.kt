package com.depixionapps.cloudpointsapp

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator
import java.text.DateFormat
import java.util.*

class Methods {
    var doneWithShowingSpinner = false
    var pointsToAdd = 0
    var totalPointsAfterAdding = 0
    var tableName = ""
    var numberOfPointsAllowed = 0
    lateinit var context: Context
    lateinit var activity: Activity
    var usingNumberPicker: Boolean = false
    lateinit var storeName: String

    fun setVariablesInMethods(storeName: String, numberOfPointsAllowed: Int, thisTable: String, thisContext: Context, thisActivity: Activity, usingNumberPicker: Boolean, ){
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
        It also calculates the number of points that I should be adding.
         */
        //this part just makes sure I'm not adding too many points to the system.
        var pointsAboveMax = false
        val currentPoints = textView.text.toString().toInt()
        val totalPointsAdding = currentPoints + pointsToAdd
        if(totalPointsAdding > numberOfPointsAllowed){
            pointsToAdd = numberOfPointsAllowed - currentPoints
            Toast.makeText(context, "Attempted adding more points than allowed. Only adding $pointsToAdd", Toast.LENGTH_LONG).show()
            pointsAboveMax = true
        }

        if (pointsToAdd >0){
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Adding $pointsToAdd points")
            if(pointsAboveMax){
                builder.setMessage("A $storeName employee will need to verify before adding points." +
                        "\nAny points above a total of $numberOfPointsAllowed will be voided")
            }else{
                builder.setMessage("A $storeName employee will need to verify before adding points")
            }
            builder.setPositiveButton("OKAY") { dialogInterface: DialogInterface, i: Int ->
                //nested builder function. shows a different builder when the OKAY button is pressed
                val builder2 = AlertDialog.Builder(context)
                builder2.setTitle("Adding $pointsToAdd points")
                builder2.setMessage("Give phone to $storeName employee to verify")
                builder2.setPositiveButton("SCAN") { dialogInterface: DialogInterface, i: Int ->
                    Toast.makeText(
                        context,
                        "$pointsToAdd points are being added",
                        Toast.LENGTH_LONG
                    ).show()
                    scanCode()
                }
                builder2.setNegativeButton("GO BACK") { dialogInterface: DialogInterface, i: Int ->
                    Toast.makeText(activity, "Scan cancelled", Toast.LENGTH_SHORT).show()
                }
                builder2.show()
            }
            builder.setNegativeButton("GO BACK") { dialogInterface: DialogInterface, i: Int ->
                Toast.makeText(activity, "Scan cancelled", Toast.LENGTH_SHORT).show()
            }
        builder.show()
        }else{
            Toast.makeText(context, "You already have the maximum number of points allowed. Redeem them before adding more", Toast.LENGTH_LONG).show()
        }

    }

    fun changeFireBasePoints(textView: TextView, sharedPreferences: SharedPreferences, progressBar: ProgressBar, button: Button,
                             redeemingPoints: Boolean = false){
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

            //if the user is redeeming points, it changes the points value to 0 and audits by subtracting the total number of points allowed.
            //if not then it carries on with the other points value, and audits with the number of points being added.
            if(redeemingPoints){
                points = 0
                auditFirebasePoints(-numberOfPointsAllowed, textView)
            }else{
                auditFirebasePoints(pointsToAdd, textView)
            }



            val currentDate = DateFormat.getDateInstance().format(Date()) + ". " + DateFormat.getTimeInstance().format(Date())

            val hero = EditFirebaseHero(email!!, points, currentDate)

            ref.child(tupleName).setValue(hero).addOnCompleteListener {
                Toast.makeText(context, "Tuple saved successfully", Toast.LENGTH_SHORT).show()
            }

            matchTextViewAndButtonsToDb(textView, sharedPreferences, progressBar, button)

        }else{
            Toast.makeText(context, "User ID isn't even available. User isn't even signed in. Should never have made it this far.", Toast.LENGTH_LONG).show()
        }


    }

    fun matchTextViewAndButtonsToDb(textView: TextView, sharedPreferences: SharedPreferences, progressBar: ProgressBar, button: Button) {
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
        val ref = FirebaseDatabase.getInstance().getReference("Edit").child(storeName)
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("points")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //grabbing ONLY the points value for better data usage
                var points = snapshot.value.toString()
                //when starting with a new account, the points will be null coming from firebase
                if (points == "null"){
                    points = "0"
                }
                val editor = sharedPreferences.edit()
                    editor.apply {
                        putString("$storeName points", points)
                        putString("$storeName progress", "0")
                    }.apply()

                textView.text = points
                progressBar.max = numberOfPointsAllowed*10
                ObjectAnimator.ofInt(progressBar, "progress", points.toInt() * 10).setDuration(2000)
                    .start()
                showButtonIfUserHasFiftyPoints(button, textView)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Err 11 Database reach cancelled", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun loadSharedPreferencesData(textView: TextView, sharedPreferences: SharedPreferences, progressBar: ProgressBar){
        //null checks are just to be safe. I noticed that it might be trying to grab shared preferences that don't exist
        //which could be bad
        if(sharedPreferences.getString("$storeName points", "0")!= null){
        textView.text = sharedPreferences.getString("$storeName points", "0")
        }
        if(sharedPreferences.getString("$storeName progress", "0")!= null){
        progressBar.progress = sharedPreferences.getString("$storeName progress", "0")!!.toInt()
        }
    }

    fun redeemPoints(textView: TextView, sharedPreferences: SharedPreferences, progressBar: ProgressBar, button: Button){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Are you sure you want to redeem your points?")
        builder.setMessage("This MUST be in front of a $storeName employee.\n\nHit GO BACK if you are not at $storeName ")
        builder.setPositiveButton("OKAY") { dialogInterface: DialogInterface, i: Int ->
            //nested builder function. shows a different builder when the OKAY button is pressed
            changeFireBasePoints(textView, sharedPreferences, progressBar, button, redeemingPoints = true)
            val builder2 = AlertDialog.Builder(context)
            builder2.setTitle("POINTS REDEEMED")
            builder2.setCancelable(false)
            builder2.setMessage("Give phone to $storeName employee to verify.\n" +
                    "\nDo NOT hit finish")
            builder2.setPositiveButton("finish") { dialogInterface: DialogInterface, i: Int ->
                Toast.makeText(
                    context,
                    "Points have been redeemed",
                    Toast.LENGTH_LONG
                ).show()
            }
            builder2.show()
        }
        builder.setNegativeButton("GO BACK") { dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(activity, "Redemption cancelled", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    fun showButtonIfUserHasFiftyPoints(
        redeemPointsBtn: Button, textView: TextView
    ) {
        val numberOfPoints = textView.text.toString().toInt()
        if (numberOfPoints >= numberOfPointsAllowed) {
            redeemPointsBtn.visibility = View.VISIBLE
        } else {
            redeemPointsBtn.visibility = View.GONE
        }
    }

    fun auditFirebasePoints(pointsAdding: Int, textView: TextView){

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val currentPoints = textView.text.toString().toInt()
        val ref = FirebaseDatabase.getInstance().getReference("Audit").child(storeName).child(userId)
        var points = pointsAdding
        val tupleName = ref.push().key
        val currentDate = DateFormat.getDateInstance().format(Date()) + ". " + DateFormat.getTimeInstance().format(Date())


        val predictedPoints = currentPoints + pointsAdding
        val hero = AuditFirebaseHero(currentDate, points, predictedPoints, currentPoints)

        if (tupleName != null) {
            ref.child(tupleName).setValue(hero).addOnCompleteListener {
                Toast.makeText(context, "Tuple saved successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }
}






