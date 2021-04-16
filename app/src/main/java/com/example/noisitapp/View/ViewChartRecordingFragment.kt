package com.example.noisitapp.View

import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.noisitapp.JosepFortunyClasses.Render.BarGraphRenderer
import com.example.noisitapp.Model.User
import com.example.noisitapp.R
import com.example.noisitapp.ViewModel.UserViewModelComunication
import kotlinx.android.synthetic.main.fragment_view_chart_recording.*
import java.io.File
import java.io.IOException

class ViewChartRecordingFragment : Fragment() {
    private var exit_record = false
    private var myUser : User?= null
    private var index : Int = 0
    private var isPlaying =false
    private var audioPath =""
    private lateinit var user: UserViewModelComunication
    private var isDataReadyToPlay = false
    private var mplayer : MediaPlayer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.Toolbar_ViewChartRecording)
        return inflater.inflate(R.layout.fragment_view_chart_recording, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab_viewchart_play_recording.setOnClickListener{
            if (isDataReadyToPlay) {
                if (!isPlaying) {
                    startReproducing()
                    fab_viewchart_play_recording.setImageResource(R.drawable.ic_pause_24dp)
                    tv_view_chart_status_recording.text = getString(R.string.tv_PlayingRecord)
                    isPlaying = true
                } else {
                    // metodo que fa stop del que s'ha grabat i el is playing
                    pauseReproducing()
                    fab_viewchart_play_recording.setImageResource(R.drawable.ic_play_arrow_24dp)
                    tv_view_chart_status_recording.text = getString(R.string.tv_RecordingStopped)
                    isPlaying = false
                }
            }
        }
        tv_view_edit_recording.setOnClickListener {
            findNavController().navigate(R.id.action_viewChartRecordingFragment_to_editRecordingFragment)
        }
        getUserViewmodel()
        chronometer_view_chards.stop()
        configGraphs()
    }
    private fun configGraphs(){
        //graphDB.title = "DB Analizer"

    }
    private fun getUserViewmodel() {
        user = ViewModelProviders.of(requireActivity()).get(UserViewModelComunication::class.java)
        user.getUser().observe(getViewLifecycleOwner(), Observer {
            if (!exit_record) {
                myUser = it
                index = user.getIndex()
                audioPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/Noisitapp Audio Files/" + myUser!!.records[index].path
                if (!isRecordingInMobileStorage()) {
                    Toast.makeText(context, "404: FILE NOT FOUND", Toast.LENGTH_SHORT).show()
                }
                tv_view_chart_status_recording.text = getString(R.string.tv_file_downloaded)
                isDataReadyToPlay = true
            }
        })
    }
    private fun isRecordingInMobileStorage() : Boolean {
        var file =  File(audioPath)
        if (!file.exists()) {
            return false
        }
        return true
    }

    private fun startReproducing(){
        mplayer = MediaPlayer().apply {
            try {
                setDataSource(audioPath)
                prepare()
                start()
            } catch (e: IOException) {
                //Do something
            }
        }
        visualizerView.link(mplayer)
        addBarGraphRenderers()
        chronometer_view_chards.base = SystemClock.elapsedRealtime()
        chronometer_view_chards.start()
        mplayer?.setOnCompletionListener {
            chronometer_view_chards.stop()
            mplayer?.pause()
            isPlaying = false
            fab_viewchart_play_recording.setImageResource(R.drawable.ic_play_arrow_24dp)
            tv_view_chart_status_recording.text = getString(R.string.tv_RecordingStopped)
        }
    }
    private fun addBarGraphRenderers() {
        var paint =  Paint()
        paint.strokeWidth = 10f
        paint.isAntiAlias = true
        paint.color = Color.argb(200, 56, 138, 252)
        var  barGraphRendererBottom =  BarGraphRenderer(4, paint, false)
        visualizerView.addRenderer(barGraphRendererBottom);
    }
    private fun pauseReproducing(){
        mplayer?.pause()
        chronometer_view_chards?.stop()
    }
    private fun stopReproducing(){
        mplayer?.release()
        mplayer = null
    }
    override fun onStop() {
        super.onStop()
        stopReproducing()
    }
}