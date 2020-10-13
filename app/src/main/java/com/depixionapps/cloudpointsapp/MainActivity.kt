package com.depixionapps.cloudpointsapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

/*  Possible problems
    - when adding a table to the database, the app crashes unless I uninstall and re-install. This could
        be bad when updating.
    - When changing tables to handle different types of rewards, we might need to completely erase the tables and start new ones. The user having the table
        stored on their devices still might cause a crash. It might also wipe points that people have rightfully earned.
    - Firebase - I need to re=lookup the rules tab. I have read and write set to true which might not be a good idea.
        - I might need to set something up where it creates a "0 points" tuple for new users who haven't added points yet.
    - The problem with calculating how many points are being subtracted is that the software never actually subtract points - it just changes them to zero
    - I might need to test both the internet connection AND connection to firebase before I do anything.
    - I need to be able to tell users when an email has already been used
    - I need to set up more password verifications to make sure people know what's going on if firebase doesn't let them make an acount

    Notes
    - make sure they have wifi
    - I can possibly do something without so many childs and be able to convert it to
    SQL much easier like in my JS class
    - Maybe delete accounts that haven't been active in a month or so?
    ✓ Maybe I want to figure out how to have login and sign up on the same activity so I'm not always jumping everywhere
    - Email auth only takes an actual email so I have to put in stuff for that
    - I might want to not switch activities every time
    - It might be a MASSIVE headache if I have someone logged in without the ability to reach firebase, either the database or login info.
      What if the system is trying to load up points from firebase with a bad connection to firebase? that would suuuck. I might want to do an internet
      check in the login activity and if that works then check if they can get the user data and if that works then immediately jump to the next activity
    - The logout button wasn't working - I added some wait time before it jumped classes to see if that would let it fully log out
    - I might need database connection tests every step of the way to make sure it's connected at all times. Same with username/password
    - I need to test this app with a bad internet connection
    - Coroutines can be used to pause and set a time for a future upload of points if I wanna go that route
    - it might not be a good idea to get data from the database in unnecessary times
    - If someone changes their store name it would be a huge hassle
    - I still need to make it useful for visually impaired people
*/

class MainActivity : AppCompatActivity() {

    var mFirebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAuth = FirebaseAuth.getInstance()

        //Sign Up Page

        val mFirebaseUser = FirebaseAuth.getInstance().currentUser

        if (mFirebaseUser != null) {
            setUpLoggedInPage()
            }else{
            llSignIn.visibility = View.VISIBLE
            llSignUp.visibility = View.GONE
            llLoggedInScreen.visibility = View.GONE
        }


        /*
        Wendy 6 copy and paste the below code.
        then change losAmigosButton and LosAmigosPointsActivity

        Once this is done it's a good time to test it and make sure it loads.
        */
        losAmigosButton.setOnClickListener{
            if(isNetworkAvailable()){
            val intent = Intent(this, LosAmigosPointsActivity::class.java)
            startActivity(intent)
            }else{
                Toast.makeText(this, "Internet connection required", Toast.LENGTH_LONG).show()
            }
        }

        bobsBurgersButton.setOnClickListener{
            if(isNetworkAvailable()){
                val intent = Intent(this, BobsBurgersPointsActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Internet connection required", Toast.LENGTH_LONG).show()
            }
        }


        btnLogOut.setOnClickListener{
            if(isNetworkAvailable()){
                FirebaseAuth.getInstance().signOut()
                if (FirebaseAuth.getInstance().currentUser == null){
                    llSignIn.visibility = View.VISIBLE
                    llSignUp.visibility = View.GONE
                    llLoggedInScreen.visibility = View.GONE
                }else{
                    Toast.makeText(this, "Trouble Logging Out", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "Internet connection required", Toast.LENGTH_LONG).show()
            }
        }


        btnSignUp.setOnClickListener {
            if(isNetworkAvailable()){
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
                            setUpLoggedInPage()
                        }
                    }
                }
            }else{
                Toast.makeText(this, "Internet connection required", Toast.LENGTH_LONG).show()
            }

        }

        btnSignIn.setOnClickListener{
            if(isNetworkAvailable()){
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
                            setUpLoggedInPage()
                        }
                    }
                }
            }else{
                Toast.makeText(this, "Internet connection required", Toast.LENGTH_LONG).show()
            }

        }

        tvLogin.setOnClickListener {
            llSignUp.visibility = View.GONE
            llSignIn.visibility = View.VISIBLE
            llLoggedInScreen.visibility = View.GONE
        }

        tvSignUp2.setOnClickListener {
            llSignIn.visibility = View.GONE
            llSignUp.visibility = View.VISIBLE
            llLoggedInScreen.visibility = View.GONE
        }

    }

    private fun setUpLoggedInPage(){
        /*
        Checks to make sure there's a current user before setting up the page. If there is one, it sets all other visibilities to GONE
            and turns the logged in screen visibility to VISIBLE. If there isn't one, it displays an error message.
        */
        if(isNetworkAvailable()){
            if(FirebaseAuth.getInstance().currentUser != null){
                llSignIn.visibility = View.GONE
                llSignUp.visibility = View.GONE
                llLoggedInScreen.visibility = View.VISIBLE

                tvLiLoggedInAs.text = "Logged in as \n${FirebaseAuth.getInstance().currentUser?.email}"

            }else{
                Toast.makeText(this,"Err 14: trouble with transition. firebase instance is showing null when it should be logged in", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Internet connection required", Toast.LENGTH_LONG).show()
        }
    }


    fun isNetworkAvailable(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }



}