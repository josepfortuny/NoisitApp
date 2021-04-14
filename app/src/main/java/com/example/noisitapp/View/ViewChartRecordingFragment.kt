package com.example.noisitapp.View

import android.app.DownloadManager
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_edit_recording.*
import kotlinx.android.synthetic.main.fragment_new_recording.*
import kotlinx.android.synthetic.main.fragment_view_chart_recording.*
import java.io.File
import java.io.IOException

class ViewChartRecordingFragment : Fragment() {
    private var exit_record = false
    private var myUser : User?= null
    private var index : Int = 0
    private var isPlaying =false
    private var audioPath =""
    private val storageRef = FirebaseStorage.getInstance().reference
    private lateinit var user: UserViewModelComunication
    private var isDataReadyToPlay = false
    private var mplayer : MediaPlayer? = null
    private var mediaExtractor : MediaExtractor ?= null
    private lateinit var mediaFormat : MediaFormat
    private lateinit var mediaCodec : MediaCodec
    private lateinit var  vis : Visualizer

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
    /*private fun recordToBytes(fileSaved : File){
        mediaExtractor = MediaExtractor()
        mediaExtractor!!.setDataSource(fileSaved.toString())
        mediaExtractor!!.selectTrack(0)
        mediaFormat = mediaExtractor!!.getTrackFormat(0)
        val mime:String = mediaFormat.getString(MediaFormat.KEY_MIME)!!
        mediaCodec = MediaCodec.createDecoderByType(mime)
        mediaCodec.configure(mediaFormat,null,null,0)
        mediaCodec.start()
        var bufferInfo = MediaCodec.BufferInfo()
        var inputDone = false
        while (true) {

        }
    }*/
    private fun getUserViewmodel() {
        user = ViewModelProviders.of(requireActivity()).get(UserViewModelComunication::class.java)
        user.getUser().observe(getViewLifecycleOwner(), Observer {
            if (!exit_record) {
                myUser = it
                index = user.getIndex()
                audioPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/Noisitapp Audio Files/" + myUser!!.records[index].path
                if (!isRecordingInMobileStorage()) {
                    downloadRecording()
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

    private fun downloadRecording() {
        storageRef.child(myUser!!.records[index].path).getDownloadUrl().addOnSuccessListener {
            downloadFile(it.toString())
        }.addOnFailureListener{
            Toast.makeText(context, "404: FILE NOT FOUND", Toast.LENGTH_SHORT).show()
        }
    }
    private fun downloadFile(url : String) {
        val downloadManager = getActivity()?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request =  DownloadManager.Request(uri)
        val fileSaved =  File(audioPath)
        //recordToBytes(fileSaved)
        request.setDestinationUri(Uri.fromFile(fileSaved))
        downloadManager.enqueue(request)
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
        //attachVisualizer()
        visualizerView.link(mplayer)
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
    /*private fun attachVisualizer() {
        vis = Visualizer(mplayer!!.audioSessionId)
        vis.scalingMode = Visualizer.SCALING_MODE_NORMALIZED
        vis.captureSize = Visualizer.getCaptureSizeRange()[0]
        Log.e("A","A"+ vis.captureSize)
        vis.setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
            override fun onFftDataCapture(
                vis: Visualizer?,
                fft: ByteArray?,
                samplingRate: Int
            ) {
                val n = fft?.size
                val magnitudes = FloatArray(n!! / 2 + 1)
                val phases = FloatArray(n / 2 + 1)
                magnitudes[0] = Math.abs(fft[0].toFloat())  // DC
                magnitudes[n / 2] = Math.abs(fft[1].toFloat()) // Float
                phases[n / 2] = 0f
                phases[0] = phases[n / 2]
                for (k in 1 until n / 2) {
                    val i = k * 2
                    magnitudes[k] = Math.hypot(fft[i].toDouble(), fft[i + 1].toDouble()).toFloat()
                    phases[k] = Math.atan2(fft[i + 1].toDouble(), fft[i].toDouble()).toFloat()
                    //Log.e("MAGNITUDES" , "Magnitudes" + magnitudes[k].toString())
                    //Log.e("FASES" , "FASES" + phases[k].toString())
                }


            }

            override fun onWaveFormDataCapture(
                vis: Visualizer?,
                bytes: ByteArray?,
                samplingRate: Int
            ) {

            }
        },Visualizer.getMaxCaptureRate() / 2, false, true)
        vis.enabled = true
    }*/
    private fun addBarGraphRenderers() {
        var paint =  Paint()
        paint.strokeWidth = 50f
        paint.isAntiAlias = true
        paint.color = Color.argb(200, 56, 138, 252)
        var  barGraphRendererBottom =  BarGraphRenderer(16, paint, false)
        //mVisualizerView.addRenderer(barGraphRendererBottom);

        var paint2 =  Paint()
        paint2.strokeWidth = 12f
        paint2.isAntiAlias = true
        paint2.color = Color.argb(200, 181, 111, 233)
        var barGraphRendererTop =  BarGraphRenderer(4, paint2, true)
        visualizerView.addRenderer(barGraphRendererTop)
    }
    private fun pauseReproducing(){
        vis.release()
        mplayer?.pause()
        chronometer?.stop()
    }
    private fun stopReproducing(){
        vis.release()
        mplayer?.release()
        mplayer = null

    }
    override fun onStop() {
        super.onStop()
        stopReproducing()
    }
}