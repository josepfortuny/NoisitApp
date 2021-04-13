package com.example.noisitapp.Model

data class Recording (
    var name : String,
    var date : String,
    var duration : String,
    var path : String, // es com la ID
    var mobileDevice : String,
    var address: String,
    var latitude : Double,
    var longitude : Double,
    var labels : List<String>,
    var machineLabels : List<String>,
    var machineLearningApplied : Boolean,
    var recordFormat: RecordFormat,
    var MoreInformation: MoreInformation){
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        0.0,
        0.0,
        emptyList(),
        emptyList(),
        false,
        RecordFormat(),
        MoreInformation())
}