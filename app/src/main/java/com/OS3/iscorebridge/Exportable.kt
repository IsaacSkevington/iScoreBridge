package com.OS3.iscorebridge

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.time.LocalDate

abstract class Exportable(val defaultName : String, val defaultExtension : String) {

    private var activityResultImportLauncher : ActivityResultLauncher<Intent>? = null
    private var activityResultExportLauncher : ActivityResultLauncher<Intent>? = null


    fun setupForActivity(activity: ComponentActivity, onExportSuccess: () -> Unit, onImportSuccess: () -> Unit){
        activityResultImportLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result -> if(processImportResult(activity.applicationContext, result.resultCode, result.data)) onImportSuccess()
        }
        activityResultExportLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result -> if(processExportResult(activity.applicationContext, result.resultCode, result.data)) onExportSuccess()
        }
    }

    fun setupForFragment(fragment: Fragment, onExportSuccess: () -> Unit, onImportSuccess: () -> Unit){
        activityResultImportLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result -> if(processImportResult(fragment.requireContext(), result.resultCode, result.data)) onImportSuccess()
        }
        activityResultExportLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result -> if(processExportResult(fragment.requireContext(), result.resultCode, result.data)) onExportSuccess()
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun export(){
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/txt"
            val d = LocalDate.now()
            d.dayOfMonth.toString() + d.month.toString() + d.year.toString()
            putExtra(Intent.EXTRA_TITLE, defaultName + LocalDate.now().toString() + defaultExtension)
        }
        activityResultExportLauncher?.launch(intent)
    }

    fun import(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/txt"
        }
        activityResultImportLauncher?.launch(intent)
    }



    abstract fun write(fileOutputStream: FileOutputStream)
    abstract fun read(fileInputStream: FileInputStream) : Boolean

    fun processExportResult(context: Context, resultCode: Int, resultData: Intent?) : Boolean{
        if(resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                val contentResolver = context.contentResolver
                try {
                    val descriptor = contentResolver.openFileDescriptor(uri, "w")
                    FileOutputStream(descriptor?.fileDescriptor).use {
                        write(it)
                    }
                    Toast.makeText(context, "Export successful", Toast.LENGTH_LONG).show()
                    return true
                } catch (e: FileNotFoundException) {
                    Toast.makeText(context, "Export failed", Toast.LENGTH_LONG).show()
                }
            }
        }
        return false
    }


    fun processImportResult(context: Context, resultCode: Int, resultData: Intent?) : Boolean{
        if(resultCode == Activity.RESULT_OK){
            resultData?.data?.also { uri ->
                val contentResolver = context.contentResolver
                try {
                    val descriptor = contentResolver.openFileDescriptor(uri, "r")
                    FileInputStream(descriptor?.fileDescriptor).use {
                        if(read(it)){
                            Toast.makeText(context, "Import successful", Toast.LENGTH_LONG).show()
                            return true
                        }
                        else{
                            Toast.makeText(context, "Import failed: File Corrupted", Toast.LENGTH_LONG).show()
                        }
                    }

                } catch (e: FileNotFoundException) {
                    Toast.makeText(context, "Import failed: File not Found", Toast.LENGTH_LONG).show()
                }
            }
        }
        return false
    }

}