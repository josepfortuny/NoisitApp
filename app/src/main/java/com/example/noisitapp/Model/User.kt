package com.example.noisitapp.Model


data class User(
    var uid:String,
    var name:String,
    var email: String,
    var records: ArrayList<Recording>){
        constructor() : this("","","",ArrayList<Recording>())
}