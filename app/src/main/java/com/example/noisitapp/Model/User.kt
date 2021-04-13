package com.example.noisitapp.Model

import android.util.Log

data class User(
    var uid:String,
    var name:String,
    var email: String,
    var records: ArrayList<Recording>){
        constructor() : this("","","",ArrayList<Recording>())
}