package com.helmy.coursesapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.helmy.coursesapp.Log.Login
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class Constants(var cont: Context) {


    val db = Firebase.firestore
    val storage = Firebase.storage.reference
    val auth = Firebase.auth
    val progressDialog = ProgressDialog(cont).apply {
        setTitle("Loading")
        setMessage("Loading")
        setCancelable(false)
    }

    fun logOut(){
        val s = AlertDialog.Builder(cont)
        s.setTitle("Log Out")
        s.setMessage("Are you sure to Log Out??")
        s.setIcon(R.drawable.ic_baseline_exit_to_app_24)
        s.setCancelable(true)

        s.setPositiveButton("OK") { _, _ ->
            auth.signOut()
            cont.startActivity(Intent(cont, Login::class.java))
        }
        s.setNegativeButton("Cancel") { d, _ ->
            d.cancel()
        }.show()
    }

    // region Image


    fun getFile(context: Context, uri: Uri): File? {
        val destinationFilename: File =
            File(context.getFilesDir().getPath() + File.separatorChar + queryName(context, uri))
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                createFileFromStream(
                    ins!!,
                    destinationFilename
                )
            }
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
        return destinationFilename
    }

    private fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor = context.getContentResolver().query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    // endregion


    /*
    fun mm() {
        val m: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                progressDialog.show()
                // There are no request codes
                val intent: Intent? = result.data
                val uri = intent?.data  //The uri with the location of the file
                val file = Constants().getFile(this, uri!!)
                val new_uri = Uri.fromFile(file)

                Toast.makeText(
                    this,
                    "${new_uri.lastPathSegment}",
                    Toast.LENGTH_SHORT
                ).show()
                val reference = storage.child("Images/${new_uri.lastPathSegment}")
                val uploadTask = reference.putFile(new_uri)

                uploadTask.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        progressDialog.dismiss()
                        imageUrl = it.toString()
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

     */
}