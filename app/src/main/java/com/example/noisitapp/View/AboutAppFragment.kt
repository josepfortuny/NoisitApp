package com.example.noisitapp.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.noisitapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AboutAppFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.Toolbar_AboutUs)
        return inflater.inflate(R.layout.fragment_about, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<FloatingActionButton>(R.id.fab_go_to_dashboard).setOnClickListener(){
            findNavController().navigate(R.id.action_aboutFragment2_to_dashboardFragment)
        }
    }

}