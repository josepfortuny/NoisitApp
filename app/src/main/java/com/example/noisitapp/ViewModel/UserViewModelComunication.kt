package com.example.noisitapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.noisitapp.Model.User

class UserViewModelComunication : ViewModel() {
    private var user = MutableLiveData<User>()
    private val auxUser = MutableLiveData<User>()
    private var auxIndexRecording = 0
    fun setUser(newuser:User){
        user.value = newuser
    }
    fun copyUser(){
        auxUser.value = user.value
    }
    fun getAuxuser():LiveData<User> {
        return auxUser
    }
    fun getUser(): LiveData<User> {
        return user
    }
    fun setselectedIndexRecording(index:Int){
        auxIndexRecording = index
    }
    fun getIndex(): Int{
        return auxIndexRecording
    }
}