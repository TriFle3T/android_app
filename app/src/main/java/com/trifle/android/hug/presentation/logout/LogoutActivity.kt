package com.trifle.android.hug.presentation.logout

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
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
import com.trifle.android.hug.MainActivity
import com.trifle.android.hug.R
import com.trifle.android.hug.RetrofitClass
import com.trifle.android.hug.data.database.userDb
import com.trifle.android.hug.data.entity.token
import com.trifle.android.hug.data.request.logout
import com.trifle.android.hug.domain.api.API
import com.trifle.android.hug.domain.dto.SignOutRequestDto
import com.trifle.android.hug.presentation.diary.DiaryActivity
import com.trifle.android.hug.presentation.login.LoginActivity
import com.trifle.android.hug.presentation.write.WriteActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class LogoutActivity : AppCompatActivity() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var profileName: TextView? = null
    private var profileEmail:TextView? = null
    private var profileImage: CircleImageView? = null
    private var mGoogleSignInAccount: GoogleSignInAccount? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var db : userDb? = null
    private var rtf : Retrofit? = null
    private var tkList : List<token>? = null

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
        db = userDb.getInstance(applicationContext)
        rtf = RetrofitClass().getRetrofitInstance()
        CoroutineScope(Dispatchers.IO).launch {
            tkList = db?.tokenDao()?.getAll()
            Log.d("token : ",tkList.toString())
        }
        setEmotion()
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
        val diaryButton = findViewById<AppCompatButton>(R.id.btnDown)
        val writeButton = findViewById<AppCompatButton>(R.id.btnWrite)

        writeButton.setOnClickListener {
            val intent : Intent =  Intent(this, WriteActivity::class.java)
            startActivity(intent)
        }
        homeButton.setOnClickListener {
            val intent : Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        diaryButton.setOnClickListener {
            val intent : Intent = Intent(this, DiaryActivity::class.java)
            intent.putExtra("emoji",getIntent().getIntExtra("emoji",-1))
            startActivity(intent)
            finish()
        }

    }
    private fun setEmotion(){
        val imgEmoji = findViewById<ImageView>(R.id.barEmoji)
        val emo = intent.getIntExtra("emoji",-1)

        when(emo){
            0 -> imgEmoji?.setImageResource(R.drawable.ic_happy)
            1 -> imgEmoji?.setImageResource(R.drawable.ic_angry)
            2 -> imgEmoji?.setImageResource(R.drawable.ic_disgust)
            3 -> imgEmoji?.setImageResource(R.drawable.ic_fear)
            4 -> imgEmoji?.setImageResource(R.drawable.ic_neutral)
            5 -> imgEmoji?.setImageResource(R.drawable.ic_sad)
            6 -> imgEmoji?.setImageResource(R.drawable.ic_amazed)
            7 -> imgEmoji?.setImageResource(R.drawable.ic_guitar)
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
    private fun callRequestLogout(){

        val api = rtf?.create(API::class.java)
        val dto = SignOutRequestDto(tkList?.get(0)?.tk.toString())

        var strToken = "Bearer ${tkList?.get(0)?.tk}"
        var strEmail = "${tkList?.get(0)?.email}"
        var callAPI = api?.requestLogout(token=strToken,strEmail,dto)

        callAPI?.enqueue(object : Callback<logout> {
            override fun onResponse(call: Call<logout>, response: Response<logout>) {
                if (response.isSuccessful) {
                    Log.d("Logout Success", response.code().toString())
                } else{
                    Log.d("Logout : Code 400 Error", response.toString())
                }
            }
            override fun onFailure(call: Call<logout>, t: Throwable) {
                Log.d("Logout : Code 500 Error", t.toString())
            }
        })

    }
    private fun signOut(){
        if(tkList != null) {
            if(tkList!!.size != 0)
                callRequestLogout()
        }
        CoroutineScope(Dispatchers.IO).launch {
            db?.tokenDao()?.deleteAll()
            val tk = db?.tokenDao()?.getAll()
            Log.d("token : ",tk.toString())
        }
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