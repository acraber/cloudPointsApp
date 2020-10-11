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
            readData(FirebaseDatabase.getInstance().getReference("Edit").child(storeName).child(FirebaseAuth.getInstance().currentUser!!.uid),
                object : OnGetDataListener {
                override fun onSuccess(snapshot: DataSnapshot?) {
                    val x = snapshot?.child("points")?.value.toString()
                    Toast.makeText(context, "the data is $x", Toast.LENGTH_SHORT).show()
                }

                override fun onStart() {
                }

                override fun onFailure() {
                }
            })
        }
    }


    fun readData(ref: DatabaseReference, listener: OnGetDataListener) {
        listener.onStart()
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listener.onSuccess(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Err 11 Database reach cancelled", Toast.LENGTH_LONG).show()
                listener.onFailure()
            }
        })
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


    interface OnGetDataListener {
        //this is the listener interface. I'm not really sure how it works but it does.
        fun onSuccess(dataSnapshot: DataSnapshot?)
        fun onStart()
        fun onFailure()
    }



}