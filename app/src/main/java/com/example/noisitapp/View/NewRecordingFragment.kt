package com.example.noisitapp.View

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.noisitapp.Model.MoreInformation
import com.example.noisitapp.Model.RecordFormat
import com.example.noisitapp.Model.Recording
import com.example.noisitapp.Model.User
import com.example.noisitapp.R
import com.example.noisitapp.R.*
import com.example.noisitapp.ViewModel.GpsUtils
import com.example.noisitapp.ViewModel.UserViewModelComunication
import com.google.android.gms.location.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_new_recording.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class NewRecordingFragment: Fragment() {
    private var mContext : Context ?= null
    private  val GPSCODE = 101
    //user
    private lateinit var user: UserViewModelComunication
    private var myUser : User ?= null

    // AudioRecord
    private lateinit var mediaRecorder : MediaRecorder
    private var mplayer : MediaPlayer? = null
    private var pathSave =""
    private var firebasePathSave =""

    //Labellist arrays
    private var labelsList : Array<String> ?=null
    private var selectedItems : BooleanArray ?= null

    // Date & Elapsed Tipe for Recording
    private var millisRecorded : Long = 0

    //Google Maps Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    //private var
    private var isPlaying = false
    private var labelsSelected = false
    private var noErrorRecording = true

    // Audio Record Parameters
    private val AUDIO_ENCODER_SAMPLERATE = 48000
    private val AUDIO_ENCODER_BITRATE = 128000
    private val AUDIO_CHANNELS = 1
    private val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
    private val AUDIO_FORMAT = MediaRecorder.OutputFormat.THREE_GPP
    private val AUDIO_ENCODER = MediaRecorder.AudioEncoder.AMR_NB

    //Audio Convertion
    private var audioFile : File ?= null
    private var fis : FileInputStream?= null
    private var bos : ByteArrayOutputStream?= null

    private val uploadRecording = Recording()
    // Nova llibreria
    private var timer: Timer? = null



    override fun onAttach( context : Context) {
        super.onAttach(context);
        mContext = context;
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(string.Toolbar_NewRecording)
        labelsList = context?.resources?.getStringArray(R.array.labels_item);
        selectedItems = BooleanArray(labelsList!!.size)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        return inflater.inflate(layout.fragment_new_recording, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        getLocation()
        b_delete_recording.setOnClickListener(){
            b_play_recording.setImageResource(drawable.ic_play_arrow_24dp)
            tv_recording_status.setText(getString(R.string.tv_RecordingStopped))
            et_recording_name.setError(null)
            setVisibilityOnesDeleteAudioisPressed()
            stopReproducing()
        }
        b_play_recording.setOnClickListener(){
            if(!isPlaying){
                startReproducing()
                b_play_recording.setImageResource(drawable.ic_pause_24dp)
                tv_recording_status.setText(getString(R.string.tv_PlayingRecord))
                isPlaying = true
            }else{
                pauseReproducing()
                b_play_recording.setImageResource(drawable.ic_play_arrow_24dp)
                tv_recording_status.setText(getString(R.string.tv_RecordingStopped))
                isPlaying = false
            }
        }
        b_upload_recording.setOnClickListener(){
            tv_recording_status.setText(getString(R.string.tv_Status_Uploading))
            if(isEverithingcorrect()){
                if (uploadRecording()){
                    if (isPlaying){
                        stopReproducing()
                    }
                    findNavController().navigate(R.id.action_newRecordingFragment_to_dashboardFragment)
                }
            }else{
                tv_recording_status.setText(getString(R.string.tv_RecordingStopped))
            }
        }
        b_start_recording.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> microButtonPressed()//Do Something
                    MotionEvent.ACTION_UP -> microButtonUnpressed()
                }
                return true
            }
        })
        tv_recording_label.setOnClickListener(){
            showDialogFragmentLabels()
        }
        annoyance_switch.setOnCheckedChangeListener{ _, isChecked  ->
                displayAnnoyanceInfo(isChecked);
            // do whatever you need to do when the switch is toggled here
        }
    }
    private fun initView(){
        user = ViewModelProviders.of(requireActivity()).get(UserViewModelComunication::class.java)
        user.getUser().observe(getViewLifecycleOwner(), androidx.lifecycle.Observer {
            myUser=it
        })
    }
    @SuppressLint("MissingPermission")
    private fun getLocation(){
        if (checkGPSPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location != null) {
                        Toast.makeText(mContext, "location" + location.latitude, Toast.LENGTH_LONG)
                        uploadRecording.latitude = location.latitude
                        uploadRecording.longitude = location.longitude
                        uploadRecording.address = getInfoFromLocation(uploadRecording.latitude, uploadRecording.longitude)
                    } else {
                        requestNewLocationData()
                    }
                }
            } else{
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        fusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var location: Location = locationResult.lastLocation
            uploadRecording.latitude = location.latitude
            uploadRecording.longitude = location.longitude
            uploadRecording.address = getInfoFromLocation(uploadRecording.latitude, uploadRecording.longitude)
        }
    }

    private fun checkGPSPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun displayAnnoyanceInfo(checked: Boolean){
        if(checked){
            linear_layout_noise_annoyance.visibility= View.VISIBLE
        }else{
            linear_layout_noise_annoyance.visibility= View.GONE
        }
    }
    private fun isEverithingcorrect():Boolean{
        var ok = true
        if (et_recording_name.text.trim().isBlank()) {
            et_recording_name.setError(getString(R.string.error_recordingname_empty))
            ok = false
        }
        if(!labelsSelected){
            Toast.makeText(getActivity(),getString(R.string.recording_no_labels), Toast.LENGTH_SHORT).show()
            ok = false
        }
        return ok
    }
    private fun uploadRecording():Boolean{
        getRecordingInfo()
        (activity as MenuNavigationActivity).updateRecodringUser(myUser!!)
        uploadToFirebaseStorage()
        return true
    }
    fun  uploadToFirebaseStorage() {
        val file = Uri.fromFile(File(pathSave))// Where the file is stored.
        val storageRef = FirebaseStorage.getInstance().reference
        val riversRef = storageRef.child(firebasePathSave) // The path where the file will be stored.
        riversRef.putFile(file)
    }
    private fun getRecordingInfo(){
        uploadRecording.name = et_recording_name.text.toString()
        uploadRecording.date = Date().toString()
        uploadRecording.duration = timeToHHMMSS(millisRecorded)
        uploadRecording.path = firebasePathSave //PathFirebase
        uploadRecording.mobileDevice = getMobileDeviceModel()
        uploadRecording.latitude= uploadRecording.latitude
        uploadRecording.longitude = uploadRecording.longitude
        uploadRecording.address = uploadRecording.address
        uploadRecording.labels = tv_recording_label.text.toString().split("\n")
        uploadRecording.recordFormat = RecordFormat(
            AUDIO_SOURCE,
            AUDIO_FORMAT,
            AUDIO_ENCODER,
            AUDIO_CHANNELS,
            AUDIO_ENCODER_BITRATE,
            AUDIO_ENCODER_SAMPLERATE)//
        uploadRecording.MoreInformation = getCheckedBoxInfo()
        myUser!!.records.add(0,uploadRecording)
        user.setUser(myUser!!)
    }
    private fun getCheckedBoxInfo() : MoreInformation{
        var moreInformation = MoreInformation()
        moreInformation.loud = cb_is_loud.isChecked
        moreInformation.like_to_hear = cb_like.isChecked
        moreInformation.annoying = cb_is_annoying.isChecked
        return moreInformation
    }
    /**
     * Function that uploads the file right into the Firebase Storage repository of audio files.
     */
    private fun microButtonPressed(){
        setupMediaRecorder()
        b_start_recording.setImageResource(drawable.ic_mic_black_24dp)
        tv_recording_status.setText(getString(R.string.tv_Recording))
        startRecording()
    }
    private fun microButtonUnpressed(){
        if (stopRecording()){
            chronometer.stop()
            millisRecorded = SystemClock.elapsedRealtime() - chronometer.base
            tv_recording_status.setText(getString(R.string.tv_RecordingStopped))
            setVisibilityOnesAudioRecorded()
        }else{
            chronometer.stop()
            tv_recording_status.setText(getString(R.string.tip0_recording_status))
            Toast.makeText(getActivity(),getString(R.string.toast_error_tap_button), Toast.LENGTH_SHORT).show()
        }
        b_start_recording.setImageResource(drawable.ic_mic_white_24dp)
    }
    private fun setVisibilityOnesAudioRecorded(){
        b_start_recording.visibility= View.GONE
        b_delete_recording.visibility = View.VISIBLE
        b_play_recording.visibility = View.VISIBLE
        b_upload_recording.visibility = View.VISIBLE
        new_recording_layoutInfo.visibility = View.VISIBLE
    }
    private fun setVisibilityOnesDeleteAudioisPressed(){
        chronometer.base = SystemClock.elapsedRealtime()
        tv_recording_status.setText(getString(R.string.tip0_recording_status))
        b_start_recording.visibility= View.VISIBLE
        b_delete_recording.visibility = View.GONE
        b_play_recording.visibility = View.GONE
        b_upload_recording.visibility = View.GONE
        new_recording_layoutInfo.visibility = View.GONE
    }
    /**
     * Function that sets the data for the MediaRecorder and RecordFormat objects.
     */
    private fun setupMediaRecorder(){
        firebasePathSave = myUser!!.uid + "_audio_record_" + UUID.randomUUID().toString() + ".3gp"
        pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Noisitapp Audio Files/" + firebasePathSave
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(AUDIO_SOURCE)
        mediaRecorder.setOutputFormat(AUDIO_FORMAT)
        mediaRecorder.setAudioEncoder(AUDIO_ENCODER)
        mediaRecorder.setAudioChannels(1)
        mediaRecorder.setAudioEncodingBitRate(AUDIO_ENCODER_BITRATE)
        mediaRecorder.setAudioSamplingRate(AUDIO_ENCODER_SAMPLERATE)
        mediaRecorder.setOutputFile(pathSave)
        mediaRecorder.prepare()
    }
    /**
     * Function that stops the recorder and the chronometer.
     */
    private fun stopRecording() : Boolean{
        noErrorRecording = false
        try {
            mediaRecorder.stop()
            mediaRecorder.reset()
            mediaRecorder.release()
            chronometer?.stop()
            stopDrawing()
            noErrorRecording = true
        } catch (stopException: RuntimeException) {
            //Do nothing
        }
        return noErrorRecording

    }

    /**
     * Function that prepares and starts the MediaRecorder object so the device starts
     * recording using the setup done in the MediaRecorder.
     */
    private fun startRecording() {
        try {
            audioRecordView.visibility = View.VISIBLE
            mediaRecorder.start()
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
            startDrawingRecording()
        }catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    // part del drawing
    private fun startDrawingRecording() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                val currentMaxAmplitude = mediaRecorder.maxAmplitude
                audioRecordView.update(currentMaxAmplitude) //redraw view
            }
        }, 0, 200)
    }
    private fun stopDrawing() {
        timer?.cancel()
        audioRecordView.recreate()
        audioRecordView.visibility = View.GONE
    }
    /**
     * Function that gets the mobile device model information.
     */
    private fun getMobileDeviceModel():String { return Build.MANUFACTURER + " " + Build.MODEL }  //+ " " + Build.VERSION.RELEASE + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName(); }
    /**
     * Function that opens an AlertDialog object and allows the user to select labels through
     * a checkbox.
     */
    private fun startReproducing(){
        mplayer = MediaPlayer().apply {
            try {
                setDataSource(pathSave)
                prepare()
                start()
            } catch (e: IOException) {
                //Do something
            }
        }
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
        mplayer?.setOnCompletionListener {
            chronometer.stop()
            mplayer?.pause()
            isPlaying = false
            b_play_recording.setImageResource(drawable.ic_play_arrow_24dp)
            tv_recording_status.setText(getString(R.string.tv_RecordingStopped))
        }
    }
    /**
     * Function that opens Stops reproducing the audio file recorded
     */
    private fun pauseReproducing(){
        mplayer?.pause()
        chronometer?.stop()
    }
    private fun stopReproducing(){
        mplayer?.release()
        mplayer = null
    }
    /**
     * function convertst time
     */
    @SuppressLint("ResourceAsColor")
    private fun showDialogFragmentLabels(){
        val auxselectedItems = selectedItems
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.select_labels)
                .setMultiChoiceItems(labelsList,auxselectedItems,
                    DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                        auxselectedItems?.set(which, isChecked)
                    })
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User clicked OK button
                        selectedItems = auxselectedItems
                        if (auxselectedItems != null) {
                            var auxString  = ""
                            for ( i in auxselectedItems.indices){
                                if(auxselectedItems[i]){
                                    if (i != auxString.lastIndex){
                                        auxString +=  labelsList?.get(i).toString() +"\n"
                                    }else{
                                        auxString +=  labelsList?.get(i).toString()
                                    }
                                }
                            }
                            tv_recording_label.text = auxString
                            labelsSelected = true
                        }

                    })
                .setNegativeButton(R.string.close_alert_dialog,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
                .setNeutralButton(R.string.clear_all,
                    DialogInterface.OnClickListener { dialog, id ->
                        selectedItems = BooleanArray(labelsList!!.size)
                        tv_recording_label.text = getString(R.string.tap_to_edit_labels)
                        labelsSelected = false
                    })
            // Create the AlertDialog
            builder.create()
            builder.show()
        }
        alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f)
        alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getResources().getColor(R.color.colorPrimary))
        alertDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f)
        alertDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(getResources().getColor(R.color.colorPrimary))
        alertDialog?.getButton(AlertDialog.BUTTON_NEUTRAL)?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f)
        alertDialog?.getButton(AlertDialog.BUTTON_NEUTRAL)?.setTextColor(getResources().getColor(R.color.colorPrimary))
    }
    private fun timeToHHMMSS(miliSeconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(miliSeconds).toInt() % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(miliSeconds).toInt() % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(miliSeconds).toInt() % 60
        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            minutes > 0 -> String.format("%02d:%02d", minutes, seconds)
            seconds > 0 -> String.format("00:%02d", seconds)
            else -> {
                "00:00"
            }
        }
    }
    private fun getInfoFromLocation(latitude: Double, longitude: Double):String{
        val geocoder = Geocoder(context, getResources().getConfiguration().locale)
        val addresses = geocoder.getFromLocation(latitude, longitude, 1);
        /*var city = addresses.get(0).locality
        var state = addresses.get(0).adminArea
        var country = addresses.get(0).countryName
        var postalCode = addresses.get(0).postalCode
        var knownName = addresses.get(0).featureName
        ("aa","Locality" + city + "adminArea" +state +"country"+country+"postalcode"+postalCode+"knownname"+knownName)*/
        return addresses.get(0).locality // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
    }
    override fun onStop() {
        super.onStop()
        stopReproducing()
    }
}