package com.example.noisitapp.View

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
import com.example.noisitapp.Model.User
import com.example.noisitapp.R
import com.example.noisitapp.ViewModel.UserViewModelComunication
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_edit_recording.*
import kotlinx.android.synthetic.main.fragment_new_recording.*
import java.io.File
import java.io.IOException
import java.lang.Exception

class EditRecordingFragment : Fragment() {
    private var myUser : User?= null
    private var index : Int = 0
    private var isPlaying =false
    private var audioPath =""
    private var fileOutput : File ?= null
    private var localFIle : File ? = null
    private val mStorage = FirebaseStorage.getInstance("gs://lasalleacousticapp.appspot.com")
    private val mstorageRef = mStorage.reference
    private var downloadRef : StorageReference ?= null
    private lateinit var user: UserViewModelComunication
    private var isDataReadyToPlay = false
    private var mplayer : MediaPlayer? = null
    private var exit_record = false
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.Toolbar_EditRecording)
        return inflater.inflate(R.layout.fragment_edit_recording, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_editrecord_delete_recording.setOnClickListener{
            exit_record = true
            findNavController().navigate(R.id.action_editRecordingFragment_to_dashboardFragment)
            stopReproducing()
            deleteRecordFirestone(myUser!!.records.get(index).path)
            myUser!!.records.removeAt(index)
            user.setUser(myUser!!)
            (activity as MenuNavigationActivity).updateRecodringUser(myUser!!)
        }
        fab_edit_record_play_recording.setOnClickListener{
            if (isDataReadyToPlay) {
                if (!isPlaying) {
                    startReproducing()
                    fab_edit_record_play_recording.setImageResource(R.drawable.ic_pause_24dp)
                    tv_editrecording_status_recording.setText(getString(R.string.tv_PlayingRecord))
                    isPlaying = true
                } else {
                    // metodo que fa stop del que s'ha grabat i el is playing
                    pauseReproducing()
                    fab_edit_record_play_recording.setImageResource(R.drawable.ic_play_arrow_24dp)
                    tv_editrecording_status_recording.setText(getString(R.string.tv_RecordingStopped))
                    isPlaying = false
                }
            }
        }
        fab_editrecording_save.setOnClickListener{
            exit_record = true
            findNavController().navigate(R.id.action_editRecordingFragment_to_dashboardFragment)
            stopReproducing()
            myUser!!.records[index].name = et_editrecording_name.text.toString()
            user.setUser(myUser!!)
            (activity as MenuNavigationActivity).updateRecodringUser(myUser!!)
        }
        tv_view_chart.setOnClickListener{
            findNavController().navigate(R.id.action_editRecordingFragment_to_viewChartRecordingFragment)
        }
        getUserViewmodel()
        chronometer_edit_recording_player.stop()
    }
    private fun deleteRecordFirestone(path : String){
        mstorageRef.child(path).delete().addOnSuccessListener {
        }.addOnFailureListener{
            Toast.makeText(context, "404: FILE NOT FOUND", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getUserViewmodel(){
        var auxString = ""
        user =
            ViewModelProviders.of(requireActivity()).get(UserViewModelComunication::class.java)
        user.getUser().observe(getViewLifecycleOwner(), Observer {
            if (!exit_record) {
                myUser = it
                index = user.getIndex()
                audioPath = Environment.getExternalStorageDirectory()
                    .absolutePath + "/Noisitapp Audio Files/" + myUser!!.records[index].path
                et_editrecording_name.setText(myUser!!.records[index].name)
                tv_editrecording_date.text = myUser!!.records[index].date
                tv_editrecording_location.text = myUser!!.records[index].address
                tv_editrecording_duration.text = myUser!!.records[index].duration
                tv_editrecording_mobile_device.text = myUser!!.records[index].mobileDevice
                for (label in myUser!!.records[index].labels) {
                    auxString += label + "\n"
                }
                tv_editrecording_labels.text = auxString.substring(0, auxString.length - 1)
                if (!isRecordingInMobileStorage()) {
                    downloadRecording()
                }else{
                    tv_editrecording_status_recording.text = getString(R.string.tv_file_downloaded)
                    isDataReadyToPlay = true
                }
            }
        })
    }
    /**
     * Function that checks if the audio file is stored in the external storage of the device.
     * It returns true if the file is stored in the device, and returns false if not.
     */
    private fun isRecordingInMobileStorage() : Boolean {
        var file =  File(audioPath)
        if (!file.exists()) {
            return false
        }
        return true
    }
    /**
     * Function that downloads the file from a Uri to the external storage of the device.
     */
    private fun downloadRecording() {
        downloadRef = mstorageRef.root.child(myUser!!.records[index].path)
        try {
            fileOutput  = File(Environment.getExternalStorageDirectory().absolutePath + "/Noisitapp Audio Files/" )
            if (!fileOutput!!.exists()){
                fileOutput!!.mkdir()
            }
            localFIle = File(fileOutput, myUser!!.records[index].path)
        }catch (e : Exception){
         //Do nothing
        }
        downloadRef!!.getFile(localFIle!!)
            .addOnSuccessListener {
                tv_editrecording_status_recording.text = getString(R.string.tv_file_downloaded)
                isDataReadyToPlay = true
            }
            .addOnFailureListener{
                Toast.makeText(context, "404: FILE NOT FOUND", Toast.LENGTH_SHORT).show()
            }
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
        chronometer_edit_recording_player.base = SystemClock.elapsedRealtime()
        chronometer_edit_recording_player.start()
        mplayer?.setOnCompletionListener {
            chronometer_edit_recording_player.stop()
            mplayer?.pause()
            isPlaying = false
            fab_edit_record_play_recording.setImageResource(R.drawable.ic_play_arrow_24dp)
            tv_editrecording_status_recording.setText(getString(R.string.tv_RecordingStopped))
        }
    }
    private fun pauseReproducing(){
        mplayer?.pause()
        chronometer?.stop()
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