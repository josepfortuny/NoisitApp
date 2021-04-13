package com.example.noisitapp.View

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.noisitapp.R
import com.example.noisitapp.ViewModel.FirebaseViewModel

import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_resetpassword.*
import org.w3c.dom.Text

class ResetPasswordFragment : Fragment() {
    private lateinit var user: FirebaseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when MyFragment is at least Started.
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
        }
        // Handle the back button event
    }
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_resetpassword, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Seteamos el mensaje a null
        user= ViewModelProviders.of(requireActivity()).get(FirebaseViewModel::class.java)
        tv_remember_password.text = ""
        view.findViewById<FloatingActionButton>(R.id.b_goBack).setOnClickListener{
            //do Something
            findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
        }
        view.findViewById<Button>(R.id.b_recoverpass).setOnClickListener(){
            if(isEverythingFilled()){
                if (user.sendEmailRecoverPassword(et_mail_recoverpassword.text.toString())){
                    tv_remember_password.text = getString(R.string.forgot_pass_instructions)
                    findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
                }
            }
        }
    }
    private fun isEverythingFilled(): Boolean{
        if(et_mail_recoverpassword.text.toString().trim().isBlank()){
            et_mail_recoverpassword.error = getString(R.string.error_email_empty)
            return false
        }else if (isEmailValid(et_mail_recoverpassword.toString())) {
            et_mail_recoverpassword.error = getString(R.string.error_introduce_valid_email)
            return false
        }
        return true
    }
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        // Aqui suposo que es fará una call al repository
    }
}