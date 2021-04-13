package com.example.noisitapp.Repository

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.noisitapp.Model.Recording
import com.example.noisitapp.Model.User
import com.example.noisitapp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class AuthRepository{
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database: FirebaseDatabase by lazy {FirebaseDatabase.getInstance()}
    private val myRef : DatabaseReference by lazy { database.getReference("/") }
    fun writeNewUserFirebaseAuth(name : String,email: String, password: String) : MutableLiveData<User>{
        var auxUserMutableLiveData = MutableLiveData<User>()
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if (it.isSuccessful){
                var aux_user = User()
                aux_user.name = name
                aux_user.email = email
                aux_user.uid = FirebaseAuth.getInstance().uid.toString()
                auxUserMutableLiveData.value = aux_user
                writeNewUserFirebase(auxUserMutableLiveData)
            }else{
                auxUserMutableLiveData.value = null
                Log.i("User Repository", "createUserWithEmail:failure", it.getException())
            }
        }
        return auxUserMutableLiveData
    }
    fun writeNewUserFirebase(auxuser : MutableLiveData<User>){
        auxuser.value?.uid?.let { myRef.child("/Users").child(it) }?.setValue(auxuser.value)
    }
    fun checkUserExistsFirebase(email: String, password: String): MutableLiveData<User>{
        var auxUserMutableLiveData = MutableLiveData<User>()
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
            if(it.isSuccessful){
                var u = User()
                u.uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
                auxUserMutableLiveData.value = u
            }else{
                auxUserMutableLiveData.value = null
            }
        }
        return auxUserMutableLiveData
    }
    fun checkUserExistsByIDFirebase(uid : String): MutableLiveData<User>{
        var auxUserMutableLiveData = MutableLiveData<User>()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
                auxUserMutableLiveData.value = null
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                // val children = snapshot!!.children
                // This returns the correct child count...
                var userExists = false
                snapshot.children.forEach {
                    if (it.child(uid).child("uid").value == uid){
                        userExists = true
                        val u = User()
                        u.uid = uid
                        u.name = it.child(uid).child("name").value.toString()
                        u.email = it.child(uid).child("email").value.toString()
                        u.records = ArrayList<Recording>()

                        if (it.child(uid).child("records").exists()){
                            //auxRecords = it.child(uid).child("records").value as ArrayList<Recording>
                            for (item in it.child(uid).child("records").children){
                                val record = item.getValue(Recording::class.java)
                                u.records.add(record!!)
                            }
                        }
                        Log.e("A", "B" + u.records)
                        auxUserMutableLiveData.value = u
                    }
                }
                if (!userExists){
                    auxUserMutableLiveData.value = null
                }
            }
        })
            Log.e("AuthRepository" ,"Valor Usuari" + auxUserMutableLiveData.value + "UID" + uid)
            return auxUserMutableLiveData
    }
    fun checkUserExistsByIDFirebaseReturn(uid : String): MutableLiveData<User>{
        var auxUserMutableLiveData = MutableLiveData<User>()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
                auxUserMutableLiveData.value = null
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                // val children = snapshot!!.children
                // This returns the correct child count...
                var userExists = false
                snapshot.children.forEach {
                    if (it.child(uid).child("uid").value == uid){
                        userExists = true
                        var u = User()
                        u.uid = uid
                        u.name = it.child(uid).child("name").value.toString()
                        u.email = it.child(uid).child("email").value.toString()
                        Log.e("Exists","Exists")
                        if (it.child(uid).child("records").exists()){
                            Log.e("Exists","Exists")
                        }
                        auxUserMutableLiveData.value = u
                    }
                }
                if (!userExists){
                    auxUserMutableLiveData.value = null
                }
            }
        })
        Log.e("ABANS de Sortir" ,"Valor Usuari" + auxUserMutableLiveData.value)
        return auxUserMutableLiveData
    }
    fun sendEmailRecoverPassword(email:String):Boolean{
        var aux : Boolean = true
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener( OnCompleteListener {
            aux = it.isSuccessful
        })
        return aux
    }
    fun updateUserInfo (auxuser : User){
        firebaseAuth.currentUser?.uid?.let { myRef.child("/Users").child(it).setValue(auxuser)}
    }
    fun updateUserPassword( password: String):Boolean{
        var aux : Boolean = true
        FirebaseAuth.getInstance().currentUser?.updatePassword(password.trim())?.addOnCompleteListener( OnCompleteListener {
            aux = it.isSuccessful
        })
        return aux
    }
}