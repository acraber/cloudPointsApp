package com.depixionapps.cloudpointsapp

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_los_amigos_points.*
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.collections.ArrayList
class LosAmigosPointsActivity : AppCompatActivity() {

    var points = ""
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
            var thePoints ="-500"
            val ref = FirebaseDatabase.getInstance().getReference("Edit").child(storeName).child(
                FirebaseAuth.getInstance().currentUser!!.uid)


            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    thePoints = snapshot.child("points").value.toString()
                    pointsNumberTextView.text = thePoints
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Err 11 Database reach cancelled", Toast.LENGTH_LONG).show()
                }
            })
        }







    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
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


}