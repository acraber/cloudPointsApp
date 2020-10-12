package com.depixionapps.cloudpointsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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

        /*
        val ref = FirebaseDatabase.getInstance().getReference("Edit").child(storeName).child(
            FirebaseAuth.getInstance().currentUser!!.uid)


        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                points = snapshot.child("points").value.toString()
                pointsNumberTextView.text = points
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })*/

        pointsNumberTextView.setText(loadSharedPreferencesData())

        changePointsText()

        methodsHandler.setVariablesInMethods(
            storeName,
            numberOfPointsAllowed,
            pointsTableName,
            context,
            thisActivity,
            usingNumberPicker
        )

        scanBtn.setOnClickListener{
            methodsHandler.startNumberPicker()
        }



        redeemPointsBtn.setOnClickListener{
            changePointsText()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        //This is for when we get the result from the barcode scanner.
        if (result != null) {
            if (result.contents != null) {
                if(result.contents == qrCode) {
                    methodsHandler.changeFireBasePoints()
                    methodsHandler.qrScanSuccess(
                        redeemPointsBtn,
                        pointsNumberTextView, progressBar
                    )
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

    fun changePointsText() {
        // If I'm going to do this workaround and not return a value with this function, I have to make DAMN
        // sure I'm changing the points value on startup every time. And then every time points are changed I need to
        // make sure I'm changing the text every single time.
        //Some questions are what if someone logs into a different phone with their account. If their points don't update right
        // and I didn't update their points, the user might lose their points when they update.
        // I might need to make an audit function to make sure the points in the database match what's shown on the textView.
        val ref = FirebaseDatabase.getInstance().getReference("Edit").child(storeName).child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val points = snapshot.child("points").value.toString()
                pointsNumberTextView.text = points
                editor.apply{
                    putString("$storeName points", points)
                }.apply()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Err 11 Database reach cancelled", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun loadSharedPreferencesData(): String?{
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("$storeName points", "0")
    }







}



