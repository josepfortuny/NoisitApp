package com.example.noisitapp.View

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.noisitapp.Model.User
import com.example.noisitapp.R
import com.example.noisitapp.ViewModel.UserViewModelComunication
import kotlinx.android.synthetic.main.dialogfragment_change_password.*

class ChangePasswordUserFragment : DialogFragment() {

    private lateinit var user: UserViewModelComunication
    private var myUser : User ?= null
    public lateinit var userPass  : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*arguments?.let {
            currentPassword = it.getString(ARG_PASSWORD)
            Log.e("actualpassword" ,currentPassword.toString())
        }*/
    }
    override fun onCreateView(
        /** The system calls this to get the DialogFragment's layout, regardless
        of whether it's being displayed as a dialog or an embedded fragment. */
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout to use as dialog or embedded fragment
        // Perform remaining operations here. No null issues.
        return inflater.inflate(R.layout.dialogfragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserViewmodel()
        btn_cp_back.setOnClickListener(){
            dismiss()
        }
        btn_cp_savepassword.setOnClickListener(){
            if (isEverithingcorrect()){
                user.setUser(myUser!!)
                dismiss()
            }
        }
    }
    private fun getUserViewmodel(){
        user = ViewModelProviders.of(requireActivity()).get(UserViewModelComunication::class.java)
        user.getAuxuser().observe(getViewLifecycleOwner(), Observer {
            myUser=it
        })
    }

    private fun isEverithingcorrect(): Boolean {
        if (et_actual_password_recover.text.toString().equals(userPass)){
            if(isEverythingFilled()) {
                if (passwordsCorrect()) {
                    if (passwordsMach()) {
                        (activity as MenuNavigationActivity).changePasswordFirebase(et_new_password_recover.text.toString())
                        return true
                    }
                }
            }

        }else{
            et_actual_password_recover.setError(getString(R.string.error_introduce_password))
        }
        return false
    }
    private fun passwordsCorrect():Boolean{
        var ok = true
        val PASSWORD_REGEX="""^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\S+$).{6,}""".toRegex()
        if (!PASSWORD_REGEX.matches(et_new_password_recover.text.toString())){
            et_new_password_recover.setError(getString(R.string.error_password_characters))
            ok = false
        }
        if (et_new_password_recover.text.toString().length < 6){
            et_new_password_recover.setError(getString(R.string.error_lenght_password))
            ok = false
        }
        if (et_repeat_password_recover.text.toString().length < 6){
            et_repeat_password_recover.setError(getString(R.string.error_lenght_password))
            ok = false
        }
        if (et_new_password_recover.text.toString() != et_repeat_password_recover.text.toString()){
            et_new_password_recover.setError(getString(R.string.error_no_password_match))
            ok = false
        }

        return ok
    }
    private fun passwordsMach():Boolean{
        var ok = true
        if (et_new_password_recover.text.toString() != et_repeat_password_recover.text.toString()){
            et_actual_password_recover.setError(getString(R.string.error_password_doesntmatch))
            ok = false
        }
          return ok
    }
    private fun isEverythingFilled ():Boolean{
        if(et_actual_password_recover.text.toString().trim().isBlank() ||
            et_new_password_recover.text.toString().trim().isBlank() ||
            et_repeat_password_recover.text.toString().trim().isBlank()){
            if (et_actual_password_recover.text.toString().trim().isBlank()){
                et_actual_password_recover.setError(getString(R.string.error_password_empty))
            }
            if(et_new_password_recover.text.toString().trim().isBlank()){
                et_new_password_recover.setError(getString(R.string.error_password_empty))
            }
            if (et_repeat_password_recover.text.toString().trim().isBlank()){
                et_repeat_password_recover.setError(getString(R.string.error_password_empty))
            }
            return false
        }
        return true
    }
    companion object {
        const val TAG = "myDialog"
        private const val ARG_PASSWORD = "CURRENTPASSWORD"

        fun newInstance(currentPassword: String) = ChangePasswordUserFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PASSWORD, currentPassword)
                userPass = currentPassword
            }
        }
    }
}