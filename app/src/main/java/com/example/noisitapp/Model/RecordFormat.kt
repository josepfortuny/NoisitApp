package com.example.noisitapp.Model
data class RecordFormat (
    var audio_source:Int,
    var audio_format:Int,
    var audio_encoder:Int,
    var audio_channels:Int,
    var audio_encoding_bitrate:Int,
    var audio_encoding_samplingrate:Int) {
    constructor() : this(
        -1,
        -1,
        -1,
        -1,
        -1,
        -1
    )}