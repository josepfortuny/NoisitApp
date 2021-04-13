package com.example.noisitapp.View

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.noisitapp.R
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {
    private val TAG : String = "Login Fragment"
    //internal var loginCommunication

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.b_login).setOnClickListener {
            if (isEverythingFilled()){
                if(isCorrectEmail()){
                    (activity as LoginActivity).checkUserFirebase(et_mail_login.text.toString().trim(),et_mail_password_login.text.toString().trim())
                }else{
                    et_mail_login.setError(getString(R.string.error_introduce_valid_email))
                }
            }
        }
        view.findViewById<Button>(R.id.b_signup).setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_navRegisterFragment)
        }
        view.findViewById<TextView>(R.id.tv_remember_password).setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }
    }
    private fun isEverythingFilled(): Boolean{
        if(et_mail_login.text.toString().trim().isBlank() || et_mail_password_login.text.toString().trim().isBlank()){
            if (et_mail_login.text.toString().trim().isBlank() ){
                et_mail_login.setError(getString(R.string.error_introduce_valid_email))
            }
            if(et_mail_password_login.text.toString().trim().isBlank()){
                et_mail_password_login.setError(getString(R.string.error_introduce_password))
            }
            return false
        }
        return true
    }
    private fun isCorrectEmail():Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(et_mail_login.text.toString().trim()).matches()
    }
}