package com.example.mycafenote

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CafeAdapter(
    private var cafeList: List<CafeListItem>,
    private val onCafeClick: (CafeEntity) -> Unit
) : RecyclerView.Adapter<CafeAdapter.CafeViewHolder>() {

    class CafeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageCafe: ImageView =
            itemView.findViewById(R.id.imageCafe)

        val textCafeName: TextView =
            itemView.findViewById(R.id.textCafeName)

        val textFavorite: TextView =
            itemView.findViewById(R.id.textFavorite)

        val textAddress: TextView =
            itemView.findViewById(R.id.textAddress)

        val textRating: TextView =
            itemView.findViewById(R.id.textRating)

        val textVisitDate: TextView =
            itemView.findViewById(R.id.textVisitDate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CafeViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_cafe,
                    parent,
                    false
                )

        return CafeViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CafeViewHolder,
        position: Int
    ) {

        val item =
            cafeList[position]

        val cafe =
            item.cafe

        // 写真
        if (item.firstPhotoUri != null) {

            holder.imageCafe.setImageURI(
                Uri.parse(item.firstPhotoUri)
            )

        } else {

            holder.imageCafe.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }

        // カフェ名
        holder.textCafeName.text =
            cafe.cafeName

        // お気に入り
        holder.textFavorite.text =
            if (cafe.favorite) {
                "♥"
            } else {
                "♡"
            }

        // 住所
        holder.textAddress.text =
            cafe.address

        // 評価
        holder.textRating.text =
            "★".repeat(cafe.rating)

        // 訪問日
        holder.textVisitDate.text =
            "${cafe.visitYear}/${cafe.visitMonth}/${cafe.visitDay}"

        // カードをタップ
        holder.itemView.setOnClickListener {
            onCafeClick(cafe)
        }
    }

    override fun getItemCount(): Int =
        cafeList.size

    fun updateData(
        newList: List<CafeListItem>
    ) {

        cafeList = newList

        notifyDataSetChanged()
    }
}