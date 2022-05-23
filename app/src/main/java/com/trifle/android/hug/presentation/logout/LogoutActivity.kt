package com.trifle.android.hug.presentation.logout

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.trifle.android.hug.R
import com.trifle.android.hug.presentation.diary.DiaryActivity
import com.trifle.android.hug.presentation.home.HomeActivity
import com.trifle.android.hug.presentation.login.LoginActivity
import com.trifle.android.hug.presentation.write.WriteActivity
import de.hdodenhof.circleimageview.CircleImageView


class LogoutActivity : AppCompatActivity() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var profileName: TextView? = null
    private var profileEmail:TextView? = null
    private var profileImage: CircleImageView? = null
    private var mGoogleSignInAccount: GoogleSignInAccount? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        profileName = findViewById(R.id.tvName);
        profileEmail = findViewById(R.id.tvEmail);
        profileImage = findViewById(R.id.ivProfile);

        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)

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
        val homeButton = findViewById<AppCompatButton>(R.id.btnHome)
        val diaryButton = findViewById<AppCompatButton>(R.id.btnDiary)
        val writeButton = findViewById<AppCompatButton>(R.id.btnWrite)

        writeButton.setOnClickListener {
            val intent : Intent =  Intent(this, WriteActivity::class.java)
            startActivity(intent)
            finish()
        }
        homeButton.setOnClickListener {
            val intent : Intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        diaryButton.setOnClickListener {
            val intent : Intent = Intent(this, DiaryActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun setDataOnView(){
        mGoogleSignInAccount?.let{
            val name = it.displayName
            val email = it.email
            val url = it.photoUrl
            profileName?.text = name
            profileEmail?.setText(email)
            profileImage?.let { it ->
                Glide.with(this)
                    .load(url)
                    .error(R.drawable.ic_hug)
                    .override(150, 150)
                    .circleCrop()
                    .into(it)
            }
        }
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