package com.trifle.android.hug.presentation.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.trifle.android.hug.MainActivity
import com.trifle.android.hug.R
import com.trifle.android.hug.presentation.home.HomeActivity

//Activity class abstrcat로 선언하면 안됨..!
public class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 9001
    private val REQ_SIGN_GOOGLE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 없앰.
        setContentView(R.layout.activity_login)
//        setGoogleButtonText() //구글 로그인 텍스트 변경

//        auth = Firebase.auth
        auth = FirebaseAuth.getInstance()

        /* 구글 로그인 관련 변수 */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1079703173153-cbuon1gj18c8br742fghdlfpi682csl2.apps.googleusercontent.com")//getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        var googleSignInClient = GoogleSignIn.getClient(this,gso) //클라이언트 정보를 담음.
        var googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, null)
            .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
            .build()

        val accunt = GoogleSignIn.getLastSignedInAccount(this) // 중복 로그인 검사용 변수.

        /* 이메일 입력 로그인 : 공란이면 에러처리 */
        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<AppCompatButton>(R.id.btnLogin)

        emailEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
        }
        passwordEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
        }

        initLoginButton(loginButton)

        /* 구글로 로그인 */
        initGoogleButton(googleSignInClient)
    }
    private fun initLoginButton(loginButton : AppCompatButton) {
        loginButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            /* Login API POST 호출 */
        }
    }

    private fun initGoogleButton(googleSignInClient: GoogleSignInClient) {
//        val googleButton : SignInButton = findViewById(R.id.btnGoogle)
        val googleButton : MaterialButton = findViewById(R.id.mtGoogle)
        googleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN && data != null){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.e("task",task.exception.toString())

            try{ // Login Success
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            }catch(e: ApiException){}
            handleSignInResult(task)
        }

    }
    private fun handleSignInResult(completedTask : Task<GoogleSignInAccount>){
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
            val familyName = account?.familyName.toString()
            val givenName = account?.givenName.toString()
            val displayName = account?.displayName.toString()

            Log.d("account", email)
            Log.d("account", familyName)
            Log.d("account", givenName)
            Log.d("account", displayName)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("failed", "signInResult:failed code=" + e.statusCode)
        }
    }
    private fun firebaseAuthWithGoogle(idToken : String){
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        // regist on firebase
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    Log.d("*****",user.toString())
                    updateUI(user);
                }else{
                    Log.d("*****","Failure",task.exception)
                    updateUI(null);
                }
            }
    }
    private fun updateUI(user : FirebaseUser?){
        if(user != null){
            val intent : Intent =  Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun getInputEmail() : String{
        return findViewById<EditText>(R.id.etEmail).text.toString()
    }
    private fun getInputPassword() : String{
        return findViewById<EditText>(R.id.etPassword).text.toString()
    }
    private fun setGoogleButtonText(loginButton: SignInButton, buttonText: String){
        var i = 0
        while (i < loginButton.childCount){
            var v = loginButton.getChildAt(i)
            if (v is TextView) {
                var tv = v
                tv.setText(buttonText)
                tv.setGravity(Gravity.CENTER)
                return
            }
            i++

        }
    }
}