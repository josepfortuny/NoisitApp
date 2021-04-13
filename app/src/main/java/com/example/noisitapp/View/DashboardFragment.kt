package com.example.noisitapp.View

import android.Manifest.permission
import android.Manifest.permission.RECORD_AUDIO
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noisitapp.Adapters.RecordItemClickListener
import com.example.noisitapp.Adapters.RecordingsAdapter
import com.example.noisitapp.Model.Recording
import com.example.noisitapp.Model.User
import com.example.noisitapp.R
import com.example.noisitapp.ViewModel.UserViewModelComunication
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_dashboards.*

class DashboardFragment : Fragment() , RecordItemClickListener {
    private var myUser : User ?=null
    private lateinit var user: UserViewModelComunication
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val LOGSTRING = "DASHBOARD"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.Toolbar_Dashboard)
        return inflater.inflate(R.layout.fragment_dashboards, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragmentView()
        fab_new_recording.setOnClickListener(){
            findNavController().navigate(R.id.action_dashboardFragment_to_newRecordingFragment)
        }
    }
    private fun initFragmentView(){
        user= ViewModelProviders.of(requireActivity()).get(UserViewModelComunication::class.java)
        user.getUser().observe(getViewLifecycleOwner(), Observer {
            myUser=it
            isAnyRecording()
        })
    }
    private fun isAnyRecording(){
        if (myUser!!.records.size <= 0){
            tv_name_dashboard.setText(R.string.dashboard_no_recordings)
            tv_name_dashboard.visibility = View.VISIBLE
        }else{
            tv_name_dashboard.visibility = View.GONE
            initRecyclerView()
        }
        progressBar.visibility = View.GONE
    }
    private fun initRecyclerView(){
        var dividerItemDecoration = DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(getDrawable(((activity as? AppCompatActivity)!!) ,R.drawable.divider_shape)!!)
        viewManager = LinearLayoutManager(activity)
        Log.e(LOGSTRING,"User" + myUser)
        viewAdapter = RecordingsAdapter(myUser!!.records,this)
        recycler_recordings.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = viewManager
            adapter = viewAdapter
            addItemDecoration(dividerItemDecoration)
        }
    }
    override fun onRecordItemClickListener(index: Int) {
        findNavController().navigate(R.id.action_dashboardFragment_to_editRecordingFragment)
        Log.e(LOGSTRING,"index"+index)
        user.setselectedIndexRecording((index))

    }
}