package com.mego.clothy

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

// When change it, Change it also in File Provider Config "file_paths.xml"
private const val SAVED_IMAGES_FOLDER_PATH_SUFFIX = "/myImages"

const val fileNameFormat = "yyyy-MM-dd-HH-mm-ss-SSS"

@HiltAndroidApp
class MyApplication : Application() {



    val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        applicationScope.launch {
            rootFilesDir = filesDir.toString()

            //Save Folder
            savedImagesFolderPath = rootFilesDir + SAVED_IMAGES_FOLDER_PATH_SUFFIX
            File( savedImagesFolderPath )
                .takeIf { !it.exists() }?.mkdir()
        }

    }

    companion object {
        var rootFilesDir = ""
        var savedImagesFolderPath = ""
    }
}