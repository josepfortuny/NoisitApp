package com.example.noisitapp.View

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.noisitapp.Model.User
import com.example.noisitapp.R
import com.example.noisitapp.ViewModel.UserViewModelComunication
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_about_user.*

class AboutUserFragment: Fragment(){
    var myUser : User ?= null
    // Use the 'by activityViewModels()' Kotlin property delegate
    // from the fragment-ktx artifact
    private lateinit var user: UserViewModelComunication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.Toolbar_MyAccount)
        return inflater.inflate(R.layout.fragment_about_user, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserViewmodel()
        view.findViewById<FloatingActionButton>(R.id.fab_goBack_Login).setOnClickListener(){
            findNavController().navigate(R.id.action_aboutUserFragment_to_dashboardFragment)
        }
        view.findViewById<TextView>(R.id.tv_edit_pass_actual).setOnClickListener(){
            ChangePasswordUserFragment.newInstance((activity as MenuNavigationActivity).getUserPassword()).show(this@AboutUserFragment.parentFragmentManager, ChangePasswordUserFragment.TAG)
        }
    }
    private fun getUserViewmodel(){
        user = ViewModelProviders.of(requireActivity()).get(UserViewModelComunication::class.java)
        user.getAuxuser().observe(getViewLifecycleOwner(), Observer {
            myUser=it
            tv_name_view.text = myUser!!.name
            tv_email_view.text = myUser!!.email
            if (myUser!!.records.size > 0){
                tv_last_recording_info.text = myUser!!.records[0].name
            }else{
                tv_last_recording_info.text = getString(R.string.tv_no_recordings_yet_account)
            }
        })
    }
}