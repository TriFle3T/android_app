package com.trifle.android.hug.presentation.logout

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.trifle.android.hug.R
import com.trifle.android.hug.presentation.login.LoginActivity
import kotlin.math.sign


class LogoutActivity : AppCompatActivity() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val GOOGLE_ACCOUNT = "google_account"
    private var profileName: TextView? = null
    private var profileEmail:TextView? = null
    private var profileImage: ImageView? = null
    private var mGoogleSignInAccount: GoogleSignInAccount? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        profileName = findViewById(R.id.profile_text);
        profileEmail = findViewById(R.id.profile_email);
        profileImage = findViewById(R.id.profile_image);

        mGoogleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
        setDataOnView();
        val logoutButton = findViewById<AppCompatButton>(R.id.btnLogout)

        logoutButton.setOnClickListener {
            val alt_bld = AlertDialog.Builder(this)
            alt_bld.setTitle("로그아웃 하시겠습니까?")
                .setMessage("사용시 재로그인이 필요합니다.")
                .setIcon(R.mipmap.ic_main_round)
                .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, which ->
                    when(which){
                        DialogInterface.BUTTON_POSITIVE -> signOut()
                    }
                })
                .setNegativeButton("아니오",
                DialogInterface.OnClickListener { dialog, which ->
                    when(which){
                        DialogInterface.BUTTON_NEGATIVE -> dialog.cancel()
                    }
                })
            alt_bld.show()
        }



    }
    private fun setDataOnView(){
        Picasso.get().load(mGoogleSignInAccount?.getPhotoUrl()).centerInside().fit().into(profileImage);
        profileName?.setText(mGoogleSignInAccount?.getDisplayName());
        profileEmail?.setText(mGoogleSignInAccount?.getEmail());
    }
    private fun signOut(){
        auth.signOut()
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener { //On Succesfull signout we navigate the user back to LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
    }
}