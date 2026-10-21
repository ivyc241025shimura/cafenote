package com.example.mycafenote

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ImagePreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_image_preview)

        val imagePreview =
            findViewById<ImageView>(R.id.imagePreview)

        val imageUri =
            intent.getStringExtra("IMAGE_URI")

        if (imageUri != null) {
            imagePreview.setImageURI(
                Uri.parse(imageUri)
            )
        }

        // 戻るボタン
        val buttonBackPreview =
            findViewById<Button>(R.id.buttonBackPreview)

        buttonBackPreview.setOnClickListener {
            finish()
        }
    }
}