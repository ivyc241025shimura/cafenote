package com.example.mycafenote

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class CafePhotoAdapter(
    private var photoList: List<CafePhotoEntity>
) : RecyclerView.Adapter<CafePhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imagePhoto: ImageView =
            itemView.findViewById(R.id.imageCafePhoto)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cafe_photo, parent, false)

        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PhotoViewHolder,
        position: Int
    ) {

        val photo = photoList[position]

        val uri = Uri.parse(photo.uri)

        // 写真を表示
        holder.imagePhoto.setImageURI(uri)

        // 写真をタップしたとき
        holder.imagePhoto.setOnClickListener {

            val intent =
                Intent(
                    holder.itemView.context,
                    ImagePreviewActivity::class.java
                )

            intent.putExtra(
                "IMAGE_URI",
                photo.uri
            )

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    fun updateData(newList: List<CafePhotoEntity>) {

        photoList = newList

        notifyDataSetChanged()
    }
}