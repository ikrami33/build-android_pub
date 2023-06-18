package com.google.firebase.codelab.friendlychat.login.view

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.TintContextWrapper.wrap
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.firebase.ui.auth.data.remote.FacebookSignInHandler
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.codelab.friendlychat.MainActivity
import com.google.firebase.codelab.friendlychat.R
import com.google.firebase.codelab.friendlychat.SignInActivity
import com.google.firebase.codelab.friendlychat.adapters.FriendlyMessageAdapter
import com.google.firebase.codelab.friendlychat.login.viewModel.LoginViewModel
import com.google.firebase.codelab.friendlychat.databinding.ActivityLoginBinding
import com.google.firebase.codelab.friendlychat.login.view.LoginActivity.Companion.newInstance
import com.google.firebase.codelab.friendlychat.model.Status_logout
import com.google.firebase.codelab.friendlychat.registerClient.view.RegisterClientActivity
import com.google.firebase.codelab.friendlychat.shared.WaitingDialog
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

import com.facebook.login.LoginResult

import com.facebook.login.LoginManager

import com.facebook.CallbackManager.Factory.create
import java.util.*
import android.content.pm.PackageManager

import android.content.pm.PackageInfo
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import com.facebook.*
import com.google.firebase.codelab.friendlychat.ResetPassActivity
import com.google.firebase.codelab.friendlychat.shared.NoNetworkDialog
import com.google.firebase.codelab.friendlychat.utils.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat.DEFAULT


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    public override fun onStart() {
        super.onStart()


    }
    private lateinit var mBinding: ActivityLoginBinding
    private var mWaitingDialog: WaitingDialog? = null
    var       noNetworkDialog : NoNetworkDialog? =null

private var callbackManager :CallbackManager?=null
    private var mToast: Toast? = null
    private val mViewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    companion object {
        fun newInstance(activity: Activity): Intent {
            return Intent(activity, LoginActivity::class.java)
        }
    }
    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setLogo(R.drawable.bait)
        .setAvailableProviders(listOf(
           // AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            ////  AuthUI.IdpConfig.FacebookBuilder().build(),
        ))
        .build()
//    val signInIntentFace = AuthUI.getInstance()
//        .createSignInIntentBuilder()
//        .setLogo(R.drawable.bait)
//        .setAvailableProviders(listOf(
//            // AuthUI.IdpConfig.EmailBuilder().build(),
//           // AuthUI.IdpConfig.GoogleBuilder().build(),
//              AuthUI.IdpConfig.FacebookBuilder().build(),
//        ))
//        .build()
    private val signIn: ActivityResultLauncher<Intent> =
        registerForActivityResult(FirebaseAuthUIActivityResultContract(), this::onSignInResult)

//    private val signInf: ActivityResultLauncher<Intent> =
//        registerForActivityResult(, this::onSignInResult)

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        Log.d(TAG, "Sign in lost!")
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "Sign in successful! , email: " +
                    Firebase.auth.currentUser!!.email + " log_type : google")
            //Firebase.auth.currentUser!!.
            mViewModel._showWaitingDialog.value = true

          lifecycleScope.launch {
                //listResult :  List<DoneReservations>

                try {///getUserDeviceId(                    this@LoginActivity )
                    var f_name =  Firebase.auth.currentUser!!.displayName!!.split(" " )

                    Log.d(TAG, "split:  " +f_name) //getUserDeviceId(  this@LoginActivity )
                    val  listResult = MarsApi.retrofitService.social( getUserDeviceId(  this@LoginActivity ) , Firebase.auth.currentUser!!.email,
                        f_name[0] ,f_name[1] ,"", Firebase.auth.currentUser!!.phoneNumber )
                    val gson = Gson()
                    val json = gson.toJson(listResult)
                    Log.d(ContentValues.TAG, "Message data DoneReservatios google : " + json)
                    Log.d(ContentValues.TAG, "Message data DoneReservatios: " + mBinding.editTextMobile.text.toString().trim() + "#" +
                            mBinding.editTextPassword.text.toString().trim() )
                    getSharedPreferences(
                        "sharedPrefFile",
                        Context.MODE_PRIVATE
                    ).edit().putString("log_type","google").commit()
                    getSharedPreferences(
                        "sharedPrefFile",
                        Context.MODE_PRIVATE
                    ).edit().putString("token",listResult.data!!.token).commit()
//                     getSharedPreferences(
//                        "sharedPrefFile",
//                        Context.MODE_PRIVATE
//                    ).edit().putLong("UserId", listResult.data!!.i).toString()
                    mViewModel._showWaitingDialog.value = false

                    startActivity(MainActivity.newInstance(this@LoginActivity))
                    finish()
                } catch (e: HttpException) {
                    mViewModel._showWaitingDialog.value = false

                    val xxx =  e.response()!!.errorBody()!!.string() //called once only
                    //_status.value = "Failure: ${e.message}" // val name1: Button = view.findViewById(R.id.button_first) //name1.setText(n1)
                    Log.d(ContentValues.TAG, "Message data payloadnotcon3: " +
                            e.response()!!.errorBody()!!.string()   ) //will not sjow any thing cause its once only
                    try {
                        val gson = Gson()


                      val mes: Status_logout = gson.fromJson(xxx, Status_logout::class.java)

                        Log.d(ContentValues.TAG, "Message data payloadnotcon3: " +
                                xxx   )
                        Toast.makeText(
                            this@LoginActivity,
                            mes.data,
                            Toast.LENGTH_LONG
                        ).show()
                        signOut()
                    }catch (e1: JSONException) {
                        e1.printStackTrace()
                    }
                }

            }
//            startActivity(MainActivity.newInstance(this))
//            finish()
            //goToMainActivity()
        } else {
            Toast.makeText(
                this,
                "There was an error signing in",
                Toast.LENGTH_LONG).show()

            val response = result.idpResponse
            if (response == null) {
                Log.w( TAG, "Sign in canceled")
            } else {
                Log.w( TAG, "Sign in error", response.error)
                //  signIn.launch(signInIntent)

            }
        }
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar(this)
        initView()
        noNetworkDialog = NoNetworkDialog(this)
//        val userinfo:UserInfo
//        userinfo.
     //   Firebase.auth.currentUser!!.
        findViewById<View>(R.id.textView_toolbar_title).setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        )
        val log_type =    getSharedPreferences(
            "sharedPrefFile",
            Context.MODE_PRIVATE
        ).getString("log_type", "")
        if(log_type.equals("google")) {//
          goToMainActivity()
            Log.d(ContentValues.TAG, "Message data log_type: " + log_type)
            Log.d(TAG, "Sign in successful! , email: " +
                    Firebase.auth.currentUser!!.email + " log_type : google"+ "phoneNumber:  "   +
                                       Firebase.auth.currentUser!!.phoneNumber)

        }else if(log_type.equals("bait_server")){
            goToMainActivity()
            Log.d(ContentValues.TAG, "Message data log_type: " + log_type)
        }else if(log_type.equals("facebook")){
            goToMainActivity()
            Log.d(ContentValues.TAG, "Message data log_type: " + log_type)
        }
        Log.d(ContentValues.TAG, "Message data log_type: " + log_type)
        initViewModelObserver()
        initListener()

    }
    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    private fun initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        validateEnglishView()
    }

    private fun validateEnglishView() {
        if (isArabicLayout(this)) {
            mBinding.editTextMobile.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            mBinding.editTextPassword.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
        } else {
            mBinding.editTextMobile.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            mBinding.editTextPassword.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
        }
    }

    private fun initViewModelObserver() {
        mViewModel.message.observe(this, {
            if (mToast != null) {
                mToast!!.cancel()
            }
            mToast = Toast.makeText(applicationContext, getString(it), Toast.LENGTH_LONG)
            mToast!!.show()
        })


   //     mViewModel.verificationCodeRequestResult.observe(this, { verificationCodeRequestResult ->
//            val intent = ClientVerificationActivity.newInstance(this)
//            intent.putExtra(
//                AppConstants.EXTRA_MOBILE_NUMBER,
//                mBinding.editTextMobile.text.toString()
//            )
//            intent.putExtra(AppConstants.EXTRA_PASSWORD, mBinding.editTextPassword.text.toString())
//            intent.putExtra(AppConstants.EXTRA_IS_REGISTER_CLIENT, false)
//            if (verificationCodeRequestResult != null && verificationCodeRequestResult!!.msg != null) {
//                intent.putExtra(
//                    AppConstants.EXTRA_VERIFICATION_CODE,
//                    verificationCodeRequestResult!!.msg
//                )
//            }
//            startActivity(intent)
  //      })

//        mViewModel.networkError.observe(this, { throwable ->
//           // handleNetworkError(this, throwable)
//        })

//        mViewModel.status.observe(this, { status ->
//            Toast.makeText(applicationContext, status.otherTxt, Toast.LENGTH_LONG).show()
//        })

        mViewModel.showWaitingDialog.observe(this, { showWaitingDialog ->
            if (showWaitingDialog) {
                showWaiteDialog()
            } else {
                hideWaiteDialog()
            }
        })

        mViewModel.internalServerError.observe(this, { isInternalServerError ->
            Toast.makeText(
                applicationContext,
                getString(R.string.text_msg_issue_communicate_server_content),
                Toast.LENGTH_LONG
            ).show()
        })

//        mViewModel.loginRequest.observe(this, { loginRequest ->
//
//            if (loginRequest.statusResponse.success) {
//                hideKeyboard(this@LoginActivity)
//                signInAnonymous(this)
//
//                if (loginRequest.statusResponse.messageCode == -2) {
//
//                    val intent = ClientVerificationActivity.newInstance(this)
//                    intent.putExtra(
//                        AppConstants.EXTRA_MOBILE_NUMBER,
//                        mBinding.editTextMobile.text.toString()
//                    )
//                    intent.putExtra(
//                        AppConstants.EXTRA_PASSWORD,
//                        mBinding.editTextPassword.text.toString()
//                    )
//                    intent.putExtra(AppConstants.EXTRA_IS_REGISTER_CLIENT, true)
//                    if (loginRequest.loginRequestResult != null && loginRequest.loginRequestResult.msg != null) {
//                        intent.putExtra(
//                            AppConstants.EXTRA_VERIFICATION_CODE,
//                            loginRequest.loginRequestResult.msg
//                        )
//                    }
//                    startActivity(intent)
//
//                } else if (loginRequest.statusResponse.messageCode == -3) {
//
//                    val intent = ClientVerificationActivity.newInstance(this)
//                    intent.putExtra(
//                        AppConstants.EXTRA_MOBILE_NUMBER,
//                        mBinding.editTextMobile.text.toString()
//                    )
//                    intent.putExtra(
//                        AppConstants.EXTRA_PASSWORD,
//                        mBinding.editTextPassword.text.toString()
//                    )
//                    intent.putExtra(AppConstants.EXTRA_IS_REGISTER_CLIENT, false)
//                    if (loginRequest.loginRequestResult != null && loginRequest.loginRequestResult.msg != null) {
//                        intent.putExtra(
//                            AppConstants.EXTRA_VERIFICATION_CODE,
//                            loginRequest.loginRequestResult.msg
//                        )
//                    }
//                    startActivity(intent)
//
//                } else {
//                    getSharedPreferences(
//                        AppSharedData.MY_PREF,
//                        AppCompatActivity.MODE_PRIVATE
//                    ).edit {
//                        putString(
//                            AppSharedData.PREF_USER_TOKEN,
//                            loginRequest.loginRequestResult.token
//                        )
//
//                        var userPhoto = ""
//                        if (loginRequest.loginRequestResult.profileImageUrl != null) {
//                            userPhoto = loginRequest.loginRequestResult.profileImageUrl!!
//                        }
//
//                        putString(AppSharedData.PREF_USER_PHOTO, userPhoto)
//                        putString(
//                            AppSharedData.PREF_USER_MOBILE,
//                            loginRequest.loginRequestResult.mobile
//                        )
//                        putString(
//                            AppSharedData.PREF_USER_NAME,
//                            loginRequest.loginRequestResult.name
//                        )
//
//                        putBoolean(AppSharedData.PREF_IS_LOGIN, true)
//                        putBoolean(AppSharedData.PREF_IS_GUEST, false)
//                        putLong(AppSharedData.PREF_USER_ID, loginRequest.loginRequestResult.id)
//
//                        putString(
//                            AppSharedData.PREF_PASSWORD,
//                            mBinding.editTextPassword.text.toString()
//                        )
//
//                    }
//
//                    val loginRequestResult = loginRequest.loginRequestResult
//
//                    if (loginRequestResult.type == AppConstants.USER_TYPE_CUSTOMER) {

//                        getSharedPreferences(
//                            AppSharedData.MY_PREF,
//                            AppCompatActivity.MODE_PRIVATE
//                        ).edit {
//                            putBoolean(AppSharedData.PREF_IS_CLIENT, true)
//                        }

               //         goToClientMainScreen()

//                    } else {
//
//                        getSharedPreferences(
//                            AppSharedData.MY_PREF,
//                            AppCompatActivity.MODE_PRIVATE
//                        ).edit {
//                            putBoolean(AppSharedData.PREF_IS_CLIENT, false)
//                        }
//
//                        goToServiceProviderMainScreen()
//                    }
//                }


//
//            } else {
//                Toast.makeText(
//                    applicationContext,
//                    loginRequest.statusResponse.otherTxt,
//                    Toast.LENGTH_LONG
//                ).show()
//            }

     //   })
    }

    private fun goToClientMainScreen() {
        val intent = MainActivity.newInstance(this);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
//
//    private fun goToServiceProviderMainScreen() {
//        val intent = MainServiceProviderActivity.newInstance(this);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//        finish()
//    }

    private fun initListener() {

        mBinding.buttonLogin.setOnClickListener(this)
        mBinding.buttonGooglogin.setOnClickListener(this)
        mBinding.buttonFacelogin.setOnClickListener(this)
        mBinding.buttonCreateAccount.setOnClickListener(this)
        mBinding.fabGuestLogin.setOnClickListener(this)
        mBinding.linearLayoutGuestLogin.setOnClickListener(this)
        mBinding.textViewForgetPassword.setOnClickListener(this)
        mBinding.editTextPassword.setOnEditorActionListener { p0, p1, p2 ->
//            if (isNetworkConnected(this@LoginActivity)) {
//                hideKeyboard(this@LoginActivity)
//                mViewModel.validateLogin(
//                    mBinding.editTextMobile.text.toString(),
//                    mBinding.editTextPassword.text.toString()
//                )
//            } else {
//                Toast.makeText(
//                    applicationContext,
//                    getString(R.string.msg_check_network_connection),
//                    Toast.LENGTH_LONG
//                ).show()
//            }
            false
        }

        mViewModel.success.observe(this, {
            if (it) {
                val intent = MainActivity.newInstance(this);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        })

        mBinding.editTextMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setFilledEditText(mBinding.linearLayoutMobile)
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                if (editable!!.toString().isEmpty()) {
                    setUnFilledEditText(mBinding.linearLayoutMobile)
                }
            }
        })

        mBinding.editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setFilledEditText(mBinding.linearLayoutPassword)
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                if (editable!!.toString().isEmpty()) {
                    setUnFilledEditText(mBinding.linearLayoutPassword)
                }
            }

        })

    }
//
//    private fun hideStatusBar() {
//        changeStatusBarColor(this, resources.getColor(R.color.colorWhite))
//        setGrayStatusBarIconColor(this)
////        com.alieldirawi.baher.utils.hideStatusBar(this)
//    }

    override fun onClick(view: View?) {
        if (view == mBinding.buttonLogin) {
//            if (isNetworkConnected(this)) {
//                hideKeyboard(this@LoginActivity)
//                mViewModel.validateLogin(
//                    mBinding.editTextMobile.text.toString(),
//                    mBinding.editTextPassword.text.toString()
//                )
//            } else {
//                Toast.makeText(
//                    applicationContext,
//                    getString(R.string.msg_check_network_connection),
//                    Toast.LENGTH_LONG
//                ).show()
//            }
            lifecycleScope.launch {
                //listResult :  List<DoneReservations>
                try {///getUserDeviceId(
                    mViewModel._showWaitingDialog.value = true
                    //                   this@LoginActivity )
                   val  listResult = MarsApi.retrofitService.login( getUserDeviceId(
                       this@LoginActivity ), mBinding.editTextMobile.text.toString().trim(),
                       mBinding.editTextPassword.text.toString().trim())
                    val gson = Gson()
                    val json = gson.toJson(listResult)
                    Log.d(ContentValues.TAG, "Message data DoneReservatios: bait " + json)
                    Log.d(ContentValues.TAG, "Message data DoneReservatios: " + mBinding.editTextMobile.text.toString().trim() + "#" +
                            mBinding.editTextPassword.text.toString().trim() )
                    getSharedPreferences(
                        "sharedPrefFile",
                        Context.MODE_PRIVATE
                    ).edit().putString("log_type","bait_server").commit()
                    getSharedPreferences(
                        "sharedPrefFile",
                        Context.MODE_PRIVATE
                    ).edit().putString("token",listResult.data!!.token).commit()
                    mViewModel._showWaitingDialog.value = false
                    startActivity(MainActivity.newInstance(this@LoginActivity))
                    finish()
                } catch (e: HttpException) {
                    val xxx =  e.response()!!.errorBody()!!.string() //called once only
                    //_status.value = "Failure: ${e.message}" // val name1: Button = view.findViewById(R.id.button_first) //name1.setText(n1)
                    mViewModel._showWaitingDialog.value = false

                    Log.d(ContentValues.TAG, "Message data payloadnotcon3: " +
                            e.response()!!.errorBody()!!.string()   ) //will not sjow any thing cause its once only
                    try {
                        val gson = Gson()


                        val mes: Status_logout = gson.fromJson(xxx, Status_logout::class.java)

                        Log.d(ContentValues.TAG, "Message data payloadnotcon3: " +
                                xxx   )
                        Toast.makeText(
                            this@LoginActivity,
                            mes.data,
                            Toast.LENGTH_LONG
                        ).show()
                        signOut()
                    }catch (e1: JSONException) {
                        e1.printStackTrace()
                    }
                }
            }

//            startActivity(MainActivity.newInstance(this))
//            finish()
        }

        if (view == mBinding.buttonCreateAccount) {
            startActivity(RegisterClientActivity.newInstance(this))
          //  startActivity(ChooseUserTypeActivity.newInstance(this))
        }
        if(view == mBinding.buttonFacelogin){
            Toast.makeText(
                this@LoginActivity,
                "hi start facebook login",
                Toast.LENGTH_LONG
            ).show()
              callbackManager =  create()
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"))

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {


                    override fun onCancel() {
                        // App code
                        Toast.makeText(
                            this@LoginActivity,
                            "hi start facebook login cancel",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                        Toast.makeText(
                            this@LoginActivity,
                            "hi start facebook login error"+ exception.message,
                            Toast.LENGTH_LONG
                        ).show()
                        Log.v(
                            "AccessToken",
                            "hi start facebook login error"+ exception.message
                        )
                    }

                    override fun onSuccess(result: LoginResult) {
//                        val profile: Profile = Profile.getCurrentProfile()!!
//                        val name: String = profile.firstName!!
//                        val tst: String = profile.!!
                        val request = GraphRequest.newMeRequest(
                            result.accessToken,

                            object : GraphRequest.GraphJSONObjectCallback  {
                                override fun onCompleted(
                                    `object`: JSONObject?,
                                    response: GraphResponse?
                                ) {
                                    val info= response!!.jsonObject

                                    Log.v("LoginActivity",  "fc response"+ response!!.rawResponse!! + " id  "+info!!["first_name"] )
                                    Log.v(
                                        "AccessToken",
                                        AccessToken.getCurrentAccessToken()!!.token
                                    )
                                   logintolocalserver(info!!["first_name"].toString(),info!!["last_name"].toString(),info!!["email"].toString())

                                }
                            })
                        val parameters = Bundle()
                        parameters.putString(
                            "fields",
                            "id,first_name,last_name,email,gender,birthday"
                        )
                        request.parameters = parameters
                        request.executeAsync()
                        Toast.makeText(
                            this@LoginActivity,
                           "hi facebook" +result.toString(),
                            Toast.LENGTH_LONG
                        ).show()
             }
                })
        }
if(view == mBinding.buttonGooglogin){
    if (Firebase.auth.currentUser == null) {
        // Sign in with FirebaseUI, see docs for more details:
        // https://firebase.google.com/docs/auth/android/firebaseui
        Log.d( TAG, "Sign in in progress!")

        signIn.launch(signInIntent)
//            Log.d(TAG, "Sign in lost!")
//            AuthUI.getInstance().signOut(this)
        //  Log.w(FriendlyMessageAdapter.TAG, "task.userid()x0 :" + uid)

    } else {
        // Log.w(FriendlyMessageAdapter.TAG, "task.userid()x1 :" + uid)

        getSharedPreferences(
            "sharedPrefFile",
            Context.MODE_PRIVATE
        ).edit().putString("log_type","google").commit()
        Toast.makeText(this,"Welcome" + Firebase.auth.currentUser!!.displayName,Toast.LENGTH_LONG).show()
        goToMainActivity()
    }
}
        if (view == mBinding.linearLayoutGuestLogin) {
            startActivity(MainActivity.newInstance(this))
            finish()
        }

        if (view == mBinding.fabGuestLogin) {
          //  if (isNetworkConnected(this)) {
             //   sendGuestLoginRequest()
//            } else {
//                Toast.makeText(
//                    applicationContext,
//                    getString(R.string.msg_check_network_connection),
//                    Toast.LENGTH_LONG
//                ).show()
//            }
        }

        if (view == mBinding.textViewForgetPassword) {
            startActivity(ResetPassActivity.newInstance(this))
        }
    }

    private fun logintolocalserver(first_name: String, last_name: String, email: String) {
         lifecycleScope.launch {
              //listResult :  List<DoneReservations>

              try {///getUserDeviceId(                    this@LoginActivity )
               val  listResult = MarsApi.retrofitService.social( getUserDeviceId(  this@LoginActivity ) , email,
                      first_name ,last_name,"",  "" )
                  val gson = Gson()
                  val json = gson.toJson(listResult)
                  Log.d(ContentValues.TAG, "Message data DoneReservatios google : " + json)
                  Log.d(ContentValues.TAG, "Message data DoneReservatios: " + mBinding.editTextMobile.text.toString().trim() + "#" +
                          mBinding.editTextPassword.text.toString().trim() )
                  getSharedPreferences(
                      "sharedPrefFile",
                      Context.MODE_PRIVATE
                  ).edit().putString("log_type","facebook").commit()
                  getSharedPreferences(
                      "sharedPrefFile",
                      Context.MODE_PRIVATE
                  ).edit().putString("token",listResult.data!!.token).commit()
//                     getSharedPreferences(
//                        "sharedPrefFile",
//                        Context.MODE_PRIVATE
//                    ).edit().putLong("UserId", listResult.data!!.i).toString()
                  mViewModel._showWaitingDialog.value = false
                  Toast.makeText(
                      this@LoginActivity,
                      "تم تسجيل دخولك بنجاح",
                      Toast.LENGTH_LONG
                  ).show()
                  startActivity(MainActivity.newInstance(this@LoginActivity))
                  finish()
              } catch (e: HttpException) {
                  mViewModel._showWaitingDialog.value = false

                  val xxx =  e.response()!!.errorBody()!!.string() //called once only
                  //_status.value = "Failure: ${e.message}" // val name1: Button = view.findViewById(R.id.button_first) //name1.setText(n1)
                  Log.d(ContentValues.TAG, "Message data payloadnotcon3: " +
                          e.response()!!.errorBody()!!.string()   ) //will not sjow any thing cause its once only
                  try {
                      val gson = Gson()


                      val mes: Status_logout = gson.fromJson(xxx, Status_logout::class.java)

                      Log.d(ContentValues.TAG, "Message data payloadnotcon3: " +
                              xxx   )
                      Toast.makeText(
                          this@LoginActivity,
                          mes.data,
                          Toast.LENGTH_LONG
                      ).show()
                      signOut()
                  }catch (e1: JSONException) {
                      e1.printStackTrace()
                  }
              }

          }


    }


    /*
        private fun sendGuestLoginRequest() {
            showWaiteDialog()
            BahirApp.getApiService()!!.guestLoginRequestCall(getUserDeviceId(this))
                .enqueue(object : Callback<LoginRequest> {
                    override fun onResponse(
                        call: Call<LoginRequest>,
                        response: Response<LoginRequest>
                    ) {
                        hideWaiteDialog()
                        if (response.isSuccessful) {

                            val loginRequest = response.body()
                            if (loginRequest != null) {
                                if (loginRequest.statusResponse.success) {
                                    getSharedPreferences(AppSharedData.MY_PREF, MODE_PRIVATE).edit {
                                        putString(
                                            AppSharedData.PREF_USER_TOKEN,
                                            loginRequest.loginRequestResult.token
                                        )
                                        putBoolean(AppSharedData.PREF_IS_GUEST, true)
                                    }
                                    startActivity(MainClientActivity.newInstance(this@LoginActivity))
                                    finishAffinity()
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        loginRequest.statusResponse.otherTxt,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.msg_some_error_happens),
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } else {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.text_msg_issue_communicate_server_content),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginRequest>, throwable: Throwable) {
                        hideWaiteDialog()
                        handleNetworkError(this@LoginActivity, throwable)
                    }

                })
        }
    */
    private fun setFilledEditText(view: View) {
        view.setBackgroundResource(R.drawable.bg_rectangle_light_blue_filled_border_purple)
    }

    private fun setUnFilledEditText(view: View) {
        view.setBackgroundResource(R.drawable.bg_rectangle_light_blue_filled)
    }

//    override fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(wrap(newBase!!, ""))
//    }

    private fun showWaiteDialog() {
        if (mWaitingDialog == null) {
            mWaitingDialog = WaitingDialog(this)
        }
        mWaitingDialog!!.showDialog()
    }

    private fun hideWaiteDialog() {
        if (mWaitingDialog != null) {
            mWaitingDialog!!.dismissDialog()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       if(callbackManager !=null) callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }
}