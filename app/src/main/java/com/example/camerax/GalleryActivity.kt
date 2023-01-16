package com.example.camerax

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.util.Log
import androidx.camera.video.MediaStoreOutputOptions
import androidx.core.net.toFile
import com.example.camerax.databinding.ActivityGalleryBinding
import java.io.File

class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val directory = File(externalMediaDirs[0].absolutePath);
        val files= directory.listFiles() as Array<File>

        val adapter = GalleryAdapter(files.reversedArray());
        binding.recyclerView.adapter = adapter;



    }
}