package com.example.camerax

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.camerax.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private lateinit var imageCaptureExecutor: ExecutorService
    private lateinit var videoCapture : VideoCapture

    private val TAG : String = "camera_x_tag";

    private val startRecordString = "Начать запись";
    private val stopRecordString = "Остановить запись"
    private val takePhotoString = "Сделать фото"

    private var PERMISSIONS_REQUIRED = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO)

    private val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            run {
                var permissionGranted = true
                permissions.entries.forEach {
                    if (it.key in PERMISSIONS_REQUIRED && !it.value)
                        permissionGranted = false
                }
                if (permissionGranted && permissions.isNotEmpty()) {
                    startCamera()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraPermissionResult.launch(PERMISSIONS_REQUIRED);
        imageCaptureExecutor = Executors.newSingleThreadExecutor()

        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA


        binding.btnAction.setOnClickListener {
            handleActonBtn();
        }

        binding.btnCameraChange.setOnClickListener {
            cameraSelector = if(cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
                CameraSelector.DEFAULT_BACK_CAMERA
            else CameraSelector.DEFAULT_FRONT_CAMERA;
            startCamera()
        }

        binding.btnToGallery.setOnClickListener {
            openGallery();
        }

        binding.btnSwitchAction.setOnClickListener {
            handleSwitchActionBtn();
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        imageCaptureExecutor.shutdown()
    }
    @SuppressLint("RestrictedApi")
    private fun startCamera() {


        val preview = Preview.Builder()
            .build()
            .also {
            it.setSurfaceProvider(binding.preview.surfaceProvider)
        }


        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({


            val cameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder().build()
            videoCapture = VideoCapture.Builder().build();
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture,videoCapture)
            }
            catch (e: Exception) {
            }
        }, ContextCompat.getMainExecutor(this))
    }




    @SuppressLint("RestrictedApi")
    private fun takePhoto() {
        imageCapture?.let {

            val fileName = "Image_${System.currentTimeMillis()}.jpg"
            val file = File(externalMediaDirs[0], fileName)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            it.takePicture(
                outputFileOptions,
                imageCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        Log.i(TAG, "Фото сохранено  ${output.savedUri}")
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.d(TAG, "Ошибка(фото):$exception")
                    }

                })
        }
    }


    @SuppressLint("RestrictedApi", "MissingPermission")
    private fun startVideoRecord()
    {
        binding.btnSwitchAction.isEnabled = false;
        val fileName = "Video_${System.currentTimeMillis()}.mp4"
        val file = File(externalMediaDirs[0].absolutePath, fileName)
        val outputFileOptions = VideoCapture.OutputFileOptions.Builder(file).build()

        videoCapture.startRecording(outputFileOptions,imageCaptureExecutor,
            object : VideoCapture.OnVideoSavedCallback {
                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    Log.i(TAG,"Видео сохранено ${outputFileResults.savedUri}")
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                }

            })
    }

    @SuppressLint("RestrictedApi")
    private fun stopVideoRecord()
    {
        videoCapture.stopRecording();
    }

    private fun openGallery()
    {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }

    private fun toVideoActivity()
    {
        val intent = Intent(this,VideoRecordActivity::class.java)
        startActivity(intent)
    }

    private fun handleActonBtn()
    {
        when(binding.btnAction.text)
        {
            takePhotoString -> takePhoto();
            startRecordString ->
            {
                startVideoRecord()
                binding.btnAction.text = stopRecordString;
            };

            stopRecordString -> {
                stopVideoRecord()
                binding.btnAction.text = startRecordString;
            };
        }
    }

    private fun handleSwitchActionBtn()
    {
        if(binding.btnSwitchAction.text == "Видео")
        {
            binding.btnAction.text = startRecordString;
            binding.btnSwitchAction.text = "Фото"
        }
        else
        {
            binding.btnAction.text = takePhotoString;
            binding.btnSwitchAction.text = "Видео"
        }

    }

}