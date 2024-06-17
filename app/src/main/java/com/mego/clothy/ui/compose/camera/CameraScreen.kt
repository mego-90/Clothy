package com.mego.clothy.ui.compose.camera

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.TorchState
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.net.toFile
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.mego.clothy.MyApplication
import com.mego.clothy.R
import com.mego.clothy.fileNameFormat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors


@Composable
fun CameraScreen( onSaveImageRequest:(tempImageUri: Uri)->Unit, onCancelRequest:()->Unit) {

    val context = LocalContext.current

    val previewView = remember { PreviewView(context) }

    val cameraController = LifecycleCameraController(context)
    val lifecycleOwner = LocalLifecycleOwner.current

    cameraController.bindToLifecycle(lifecycleOwner)
    cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    previewView.controller = cameraController

    val outputFileOptions = ImageCapture
        .OutputFileOptions
        //.Builder(File( MyApplication.tempFolderPath ,"${SimpleDateFormat(fileNameFormat,Locale.ENGLISH).format( System.currentTimeMillis() ) }.jpg"))
        .Builder(File( MyApplication.savedImagesFolderPath ,
            SimpleDateFormat(fileNameFormat,Locale.ENGLISH).format( System.currentTimeMillis() )
        ))
        .build()

    var tempImageUri by remember { mutableStateOf( Uri.EMPTY )}

    var mustShowTookPictureDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = {previewView}, modifier = Modifier.fillMaxSize())

        //take picture button
        IconButton(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(16.dp)
            .size(32.dp),
            onClick = {
                cameraController.takePicture(
                    outputFileOptions,
                    Executors.newSingleThreadExecutor() ,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            tempImageUri = outputFileResults.savedUri
                            mustShowTookPictureDialog =true
                        }

                        override fun onError(exception: ImageCaptureException) {
                            //TODO("Not yet implemented")
                        }

                    }
                ) 
            }) {
            Icon(imageVector = Icons.Default.Camera, contentDescription = "", tint = Color.White, modifier = Modifier.size(32.dp))
        }

        //switch camera Button
        IconButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = {
                val selectedCamera = cameraController.cameraSelector
                if (selectedCamera == CameraSelector.DEFAULT_FRONT_CAMERA )
                    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                else
                    cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            }) {
            Icon(imageVector = Icons.Default.FlipCameraAndroid, contentDescription = "", tint = Color.White)
        }

        //flash button
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            onClick = {
                val torchState = cameraController.torchState.value
                if (torchState == TorchState.ON)
                    cameraController.enableTorch(false)
                else
                    cameraController.enableTorch(true)
            }) {
            var flashIconTint = Color.White;
            if ( cameraController.torchState.value == TorchState.ON)
                flashIconTint = Color.Yellow
            Icon(imageVector = Icons.Default.FlashOn, contentDescription = "", tint = flashIconTint)
        }

        //Close Button
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            onClick = {
                tempImageUri.takeIf { it!=Uri.EMPTY }?.toFile()?.delete()
                onCancelRequest()
            }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "", tint = Color.White)
        }
    }


    if (mustShowTookPictureDialog)
        CapturedImageDialog(
            tempImageUri = tempImageUri,
            onDismissRequest = {
                mustShowTookPictureDialog = false
                tempImageUri.takeIf { it!=Uri.EMPTY }?.toFile()?.delete()
            },
            onSaveRequest = {
                onSaveImageRequest(tempImageUri)
                mustShowTookPictureDialog = false
            }
        )

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun CapturedImageDialog(tempImageUri:Uri, onDismissRequest:()->Unit, onSaveRequest:()->Unit) {

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(modifier = Modifier.fillMaxWidth(0.80f)) {

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top ){
                GlideImage(
                    model = tempImageUri,
                    contentDescription = "",
                    contentScale = ContentScale.FillWidth
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text(text = stringResource(R.string.cancel))
                    }

                    TextButton(onClick = { onSaveRequest() }) {
                        Text(text = stringResource(R.string.save))
                    }

                }
            }
        }
    }
}