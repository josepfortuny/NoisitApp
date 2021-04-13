package com.example.noisitapp.ViewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.noisitapp.Model.User
import com.example.noisitapp.Repository.AuthRepository

class FirebaseViewModel : ViewModel() {
    private var user = MutableLiveData<User>()
    private val repository: AuthRepository by lazy { AuthRepository() }
    fun addUserFirebaseAuth(name: String, email: String, password: String) {
        user = repository.writeNewUserFirebaseAuth(name, email, password)
    }
    fun checkUserFirebaseAuth(email: String, password: String) {
        user = repository.checkUserExistsFirebase(email,password)
    }
    fun checkUserExistFirebase(uID : String)  {
        user = repository.checkUserExistsByIDFirebase(uID)
    }
    fun getUser(): LiveData<User> {
        return user
    }
    fun sendEmailRecoverPassword( email : String) : Boolean{
        return repository.sendEmailRecoverPassword(email)
    }
    fun updateRecordingsFirebase( user : User ){
        repository.updateUserInfo(user)
    }
    fun changePasswordFirebase ( password: String ):Boolean{
         return repository.updateUserPassword(password)
    }
}