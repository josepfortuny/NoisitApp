package com.example.noisitapp.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.noisitapp.R
import com.example.noisitapp.ViewModel.FirebaseViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.dialogfragment_change_password.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class RegisterFragment : Fragment() {
    private lateinit var user: FirebaseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when MyFragment is at least Started.
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigate(R.id.action_newRecordingFragment_to_dashboardFragment)
        }
        // Handle the back button event
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user= ViewModelProviders.of(requireActivity()).get(FirebaseViewModel::class.java)
        view.findViewById<FloatingActionButton>(R.id.b_goBack_toLogin).setOnClickListener {
            findNavController().navigate(R.id.action_navRegisterFragment_to_loginFragment)
        }
        view.findViewById<FloatingActionButton>(R.id.b_create_account).setOnClickListener {
            if (isEverithingFilled()){
                if(isEmailValid(et_mail_register.text.toString())){
                    if (passwordsCorrect()) {
                        user.addUserFirebaseAuth(
                            et_name_register.text.toString(),
                            et_mail_register.text.toString(),
                            et_pass_register.text.toString()
                        )
                        user.getUser().observe(viewLifecycleOwner, Observer {
                            if (it != null) {
                                Log.e("Shacreat", "Shacreat")
                                findNavController().navigate(R.id.action_navRegisterFragment_to_loginFragment)
                            }
                        })
                    }
                }else{
                    et_mail_register.error = getString(R.string.error_introduce_valid_email)
                }
            }
        }
    }
    private fun passwordsCorrect():Boolean {
        var ok = true
        val PASSWORD_REGEX = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\S+$).{6,}""".toRegex()
        if (!PASSWORD_REGEX.matches(et_pass_register.text.toString())) {
            et_pass_register.setError(getString(R.string.error_password_characters))
            ok = false
        }
        if (et_pass_register.text.toString().length < 6) {
            et_pass_register.setError(getString(R.string.error_lenght_password))
            ok = false
        }
        if (et_pass_repeat_register.text.toString().length < 6) {
            et_pass_repeat_register.setError(getString(R.string.error_lenght_password))
            ok = false
        }
        if (et_pass_repeat_register.text.toString() != et_pass_register.text.toString()){
            et_pass_register.setError(getString(R.string.error_no_password_match))
            ok = false
        }
        return ok
    }
    fun isEverithingFilled():Boolean{
        if(et_name_register.text.toString().trim().isBlank() ||
                et_mail_register.text.toString().trim().isBlank() ||
                et_pass_register.text.toString().trim().isBlank() ||
                et_pass_repeat_register.text.toString().trim().isBlank() ||
                !cb_terms_register.isChecked
        ){
            if(et_name_register.text.toString().trim().isBlank()) et_name_register.error = getString(R.string.error_introduce_valid_email)

            if (et_mail_register.text.toString().trim().isBlank()) et_mail_register.error = getString(R.string.error_email_empty)

            if (et_pass_register.text.toString().trim().isBlank()) et_pass_register.error = getString(R.string.error_password_empty)

            if (et_pass_repeat_register.text.toString().trim().isBlank()) et_pass_repeat_register.error = getString(R.string.error_password_empty)

            if(!cb_terms_register.isChecked) cb_terms_register.error = "Missing Point" else cb_terms_register.error = null

            return false
        }
        return true
    }
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        // Aqui suposo que es fará una call al repository
    }
}