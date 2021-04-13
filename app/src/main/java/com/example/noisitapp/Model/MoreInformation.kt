package com.example.noisitapp.Model

data class MoreInformation (
    var loud: Boolean,
    var annoying: Boolean,
    var like_to_hear: Boolean){
    constructor() : this(
        false,
        false,
        false
    )}