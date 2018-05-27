package me.luisguerra495.noteapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private var firebaseAuth: FirebaseAuth? = null
    private var googleApiClient: GoogleApiClient? = null
    private val SIGN_IN_GOOGLE_CODE:Int = 1
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).
                        requestEmail()
                        .build()

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        btnSignIn.setOnClickListener {
            val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)

            startActivityForResult(intent, SIGN_IN_GOOGLE_CODE)
        }

        authStateListener = FirebaseAuth.AuthStateListener {
            var firebaseUser = it.currentUser

            if(firebaseUser != null) {
                //make something
            } else {
                Toast.makeText(applicationContext, "Cerrando sesión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signInGoogleFirebase(result: GoogleSignInResult) {
        if(result.isSuccess) {
            val authCredential = GoogleAuthProvider.getCredential(result.signInAccount!!.idToken, null)

            firebaseAuth!!.signInWithCredential(authCredential).addOnCompleteListener {
                if(it.isSuccessful) {
                    Toast.makeText(applicationContext, "Te has auntenticado correctamente", Toast.LENGTH_SHORT).show()

                    //Aqui pasa a otra activity
                    val intent = Intent(this, homeActivity::class.java)

                    startActivity(intent)
                    finish()
                } else {

                    Toast.makeText(applicationContext, "Ocurrió un error", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(applicationContext, result., Toast.LENGTH_SHORT).show()
        }
    }

    fun signOut(){
        firebaseAuth!!.signOut()

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback {
            if(it.isSuccess) {
                val intent = Intent(this, MainActivity::class.java)

                startActivity(intent)
                finish()
            } else {
                Toast.makeText(applicationContext, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SIGN_IN_GOOGLE_CODE){
            val googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            signInGoogleFirebase(googleSignInResult)
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }
}
