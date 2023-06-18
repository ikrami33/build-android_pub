package com.google.firebase.codelab.friendlychat.login.viewModel

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {


    private val _message =
        MutableLiveData<Int>()
    val message: LiveData<Int>
        get() = _message

    private val _success =
        MutableLiveData<Boolean>()
    val success: LiveData<Boolean>
        get() = _success

/*    private val _verificationCodeRequestResult =
        MutableLiveData<SendVerificationCodeRequestResult?>()
    val verificationCodeRequestResult: LiveData<SendVerificationCodeRequestResult?>
        get() = _verificationCodeRequestResult

    private val _status = MutableLiveData<StatusResponse>()
    val status: LiveData<StatusResponse>
        get() = _status
*/
    private val _networkError = MutableLiveData<Throwable>()
    val networkError: LiveData<Throwable>
        get() = _networkError

      val _showWaitingDialog = MutableLiveData<Boolean>()
    val showWaitingDialog: LiveData<Boolean>
        get() = _showWaitingDialog

    private val _internalServerError = MutableLiveData<Boolean>()
    val internalServerError: LiveData<Boolean>
        get() = _internalServerError
//
//    private val _loginRequest = MutableLiveData<LoginRequest>()
//    val loginRequest : LiveData<LoginRequest>
//    get() = _loginRequest


    public fun validateLogin(mobile: String, password: String) {

      /*  if (!mobile.isEmpty()) {
            if (mobile.length == 10 || mobile.length == 9) {
                if (!password.isEmpty()) {

                    _showWaitingDialog.value = true

                    BahirApp.getApiService()!!.loginRequestCall(
                        mobile,
                        "966",
                        password,
                        ""
                    )
                        .enqueue(object : Callback<LoginRequest?> {
                            override fun onResponse(
                                call: Call<LoginRequest?>,
                                response: Response<LoginRequest?>
                            ) {
                                _showWaitingDialog.value = false
                                if (response.isSuccessful) {
                                    val loginRequest = response.body()
                                    if (loginRequest != null) {
                                       _loginRequest.value = loginRequest
                                    } else {
                                        _message.value = R.string.msg_some_error_happens
                                    }
                                } else {
                                    if (response.code() != 401){
                                        _message.value = R.string.text_msg_issue_communicate_server_content
                                    }
                                }
                            }

                            override fun onFailure(call: Call<LoginRequest?>, throwable: Throwable) {
                                _showWaitingDialog.value = false
                                _networkError.value = throwable
                            }

                        })


                } else {

                    _message.value = R.string.text_msg_enter_password
                }

            } else {

                _message.value = R.string.text_msg_enter_valid_mobile

            }
        } else {

            _message.value = R.string.text_msg_enter_mobile

        }*/
    }
}
