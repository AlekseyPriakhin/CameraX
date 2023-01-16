package com.example.camerax

import android.media.ThumbnailUtils
import android.os.Build
import android.os.CancellationSignal
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.camerax.databinding.GalleryItemBinding
import java.io.File


class GalleryAdapter(private val fileArray: Array<File>) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    class ViewHolder(private val binding: GalleryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.Q)
        fun bind(file: File) {
            if(file.extension == "mp4")
            {
                /*val mSize = Size(binding.photo.maxWidth,binding.photo.maxHeight)
                val bitmapThumbnail = ThumbnailUtils.createVideoThumbnail(
                    file, mSize,null
                )
                binding.photo.setImageBitmap(bitmapThumbnail);
                binding.photo.scaleType = ImageView.ScaleType.CENTER
                binding.photo.visibility = View.GONE;*/
                binding.video.visibility = View.VISIBLE;
                binding.video.setVideoURI(file.toUri());
                binding.video.setMediaController(MediaController(binding.root.context))
                //binding.video.setZOrderOnTop(true);
                binding.video.start()
            }

            else {
                binding.photo.visibility = View.VISIBLE;
                binding.photo.setImageURI(file.toUri())
            }
            Log.i("camera_x_gall" ,"${file.name} ${binding.photo.visibility} ${binding.video.visibility}")

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(GalleryItemBinding.inflate(layoutInflater, parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fileArray[position])
    }

    override fun getItemCount(): Int {
        return fileArray.size
    }
}