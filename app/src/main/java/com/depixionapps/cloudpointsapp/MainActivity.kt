package com.depixionapps.cloudpointsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var mFirebaseAuth: FirebaseAuth? = null
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAuth = FirebaseAuth.getInstance()

        //Sign Up Page

        val mFirebaseUser = FirebaseAuth.getInstance().currentUser

        if (mFirebaseUser != null) {
            llSignIn.visibility = View.GONE
            llSignUp.visibility = View.GONE
            llLoggedInScreen.visibility = View.VISIBLE
            }else{
            llSignIn.visibility = View.VISIBLE
            llSignUp.visibility = View.GONE
        }

        losAmigosButton.setOnClickListener{
            val intent = Intent(this, LosAmigosPointsActivity::class.java)
            startActivity(intent)
        }

        btnLogOut.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            if (FirebaseAuth.getInstance().currentUser == null){
                llSignIn.visibility = View.VISIBLE
                llSignUp.visibility = View.GONE
            }else{
                Toast.makeText(this, "Trouble Logging Out", Toast.LENGTH_LONG).show()
            }
        }


        btnSignUp.setOnClickListener {
            val email = etSuEmail.text.toString()
            val pwd = etSuPassword.text.toString()
            val verifyPwd = etSuVerifyPassword.text.toString()
            if (email.isEmpty()) {
                etSuEmail.error = "Please enter email"
                etSuEmail.requestFocus()
            } else if (pwd.isEmpty()) {
                etSuPassword.error = "Please enter password"
                etSuPassword.requestFocus()
                //Possible error - didn't follow the video cause I don't like her programming style
            }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                etSuEmail.error = "Email must be in correct format (ex email@gmail.com)"
                etSuEmail.requestFocus()
            } else if(pwd != verifyPwd){
                etSuVerifyPassword.error = "Passwords must match"
                etSuVerifyPassword.requestFocus()
            }else {
                mFirebaseAuth!!.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(
                        this
                ) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(
                                this,
                                "Unsuccessful",
                                Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        startActivity(Intent(this, LoggedInActivity::class.java))
                    }
                }
            }
        }

        btnSignIn.setOnClickListener{
            val email = etLiEmail.text.toString()
            val pwd = etLiPassword.text.toString()
            if (email.isEmpty()) {
                etLiEmail.error = "Please enter email id"
                etLiEmail.requestFocus()
            } else if (pwd.isEmpty()) {
                etLiPassword.error = "Please enter password"
                etLiPassword.requestFocus()
                //Possible error - didn't follow the video cause I don't like her programming style
            }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                etLiEmail.error = "Email must be in correct format (username@email.com)"
                etLiEmail.requestFocus()
            } else {
                mFirebaseAuth!!.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this, "Login Unsuccessful. Ensure that the username and password is correct (passwords are case-sensitive)",
                                Toast.LENGTH_SHORT).show()
                    } else {
                        llSignIn.visibility = View.GONE
                        llSignUp.visibility = View.GONE
                        llLoggedInScreen.visibility = View.VISIBLE
                    }
                }
            }
        }



        tvLogin.setOnClickListener {
            llSignUp.visibility = View.GONE
            llSignIn.visibility = View.VISIBLE
        }

        tvSignUp2.setOnClickListener {
            llSignIn.visibility = View.GONE
            llSignUp.visibility = View.VISIBLE
        }

    }

}