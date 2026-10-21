package com.example.mycafenote

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class SelectedPhotoAdapter(
    private var photoUris: List<Uri>
) : RecyclerView.Adapter<SelectedPhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageSelectedPhoto: ImageView =
            itemView.findViewById(R.id.imageSelectedPhoto)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_photo, parent, false)

        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PhotoViewHolder,
        position: Int
    ) {
        holder.imageSelectedPhoto.setImageURI(photoUris[position])
    }

    override fun getItemCount(): Int {
        return photoUris.size
    }

    fun updateData(newUris: List<Uri>) {
        photoUris = newUris
        notifyDataSetChanged()
    }
}