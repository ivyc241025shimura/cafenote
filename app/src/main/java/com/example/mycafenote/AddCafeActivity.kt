package com.example.mycafenote

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class AddCafeActivity : AppCompatActivity() {

    // 選択した写真
    private var selectedPhotoUris: List<Uri> = emptyList()

    // 写真プレビュー用Adapter
    private lateinit var selectedPhotoAdapter: SelectedPhotoAdapter

    // 複数写真選択
    private val photoPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->

            if (uris.isNotEmpty()) {

                selectedPhotoUris = uris.take(10)

                Toast.makeText(
                    this,
                    "${selectedPhotoUris.size}枚の写真を選択しました",
                    Toast.LENGTH_SHORT
                ).show()

                // プレビューを更新
                selectedPhotoAdapter.updateData(selectedPhotoUris)

                // プレビューを表示
                findViewById<TextView>(
                    R.id.textSelectedPhotoTitle
                ).visibility = android.view.View.VISIBLE

                findViewById<RecyclerView>(
                    R.id.recyclerViewSelectedPhoto
                ).visibility = android.view.View.VISIBLE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_cafe)

        // ステータスバーを非表示
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).hide(WindowInsetsCompat.Type.statusBars())

        // ヘッダー
        val addCafeHeader =
            findViewById<android.view.View>(R.id.addCafeHeader)

        addCafeHeader.applyHeaderStatusBarPadding()

        // 戻るボタン
        val buttonBackAdd =
            findViewById<Button>(R.id.buttonBackAdd)

        buttonBackAdd.setOnClickListener {
            finish()
        }

        val editCafeName =
            findViewById<EditText>(R.id.editCafeName)

        val editAddress =
            findViewById<EditText>(R.id.editAddress)

        val editYear =
            findViewById<EditText>(R.id.editYear)

        val editMonth =
            findViewById<EditText>(R.id.editMonth)

        val editDay =
            findViewById<EditText>(R.id.editDay)

        val ratingBar =
            findViewById<RatingBar>(R.id.ratingBar)

        val editFood =
            findViewById<EditText>(R.id.editFood)

        val editMemo =
            findViewById<EditText>(R.id.editMemo)

        val checkFavorite =
            findViewById<CheckBox>(R.id.checkFavorite)

        val buttonSelectPhoto =
            findViewById<Button>(R.id.buttonSelectPhoto)
        // 写真選択ボタン
        buttonSelectPhoto.setOnClickListener {

            photoPicker.launch(
                arrayOf("image/*")
            )
        }

        val recyclerViewSelectedPhoto =
            findViewById<RecyclerView>(R.id.recyclerViewSelectedPhoto)

        selectedPhotoAdapter = SelectedPhotoAdapter(emptyList())

        recyclerViewSelectedPhoto.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        recyclerViewSelectedPhoto.adapter = selectedPhotoAdapter

        // ヘッダーの登録ボタン
        val buttonRegisterCafe =
            findViewById<Button>(R.id.buttonRegisterCafe)


// 登録ボタン
        buttonRegisterCafe.setOnClickListener {

            val cafeName =
                editCafeName.text.toString()

            val address =
                editAddress.text.toString()

            val year =
                editYear.text.toString().toIntOrNull()

            val month =
                editMonth.text.toString().toIntOrNull()

            val day =
                editDay.text.toString().toIntOrNull()

            val rating =
                ratingBar.rating.toInt()

            val food =
                editFood.text.toString()

            val memo =
                editMemo.text.toString()

            val favorite =
                checkFavorite.isChecked


            // カフェ名チェック
            if (cafeName.isEmpty()) {

                Toast.makeText(
                    this,
                    "カフェ名を入力してください",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // 日付チェック
            if (year == null ||
                month == null ||
                day == null
            ) {

                Toast.makeText(
                    this,
                    "訪問日を正しく入力してください",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // CafeEntity作成
            val cafe = CafeEntity(
                cafeName = cafeName,
                address = address,
                visitYear = year,
                visitMonth = month,
                visitDay = day,
                rating = rating,
                food = food,
                memo = memo,
                favorite = favorite
            )


            val database =
                CafeDatabase.getDatabase(this)

            val cafeDao =
                database.cafeDao()

            val cafePhotoDao =
                database.cafePhotoDao()


            lifecycleScope.launch {

                // カフェ情報を保存
                val cafeId =
                    cafeDao.insert(cafe).toInt()



                // 写真を保存
                if (selectedPhotoUris.isNotEmpty()) {

                    val photos =
                        selectedPhotoUris.mapIndexed { index, uri ->

                            try {

                                contentResolver
                                    .takePersistableUriPermission(
                                        uri,
                                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    )

                            } catch (e: Exception) {
                            }


                            CafePhotoEntity(
                                cafeId = cafeId,
                                uri = uri.toString(),
                                displayOrder = index
                            )
                        }


                    cafePhotoDao.insertPhotos(photos)
                }


                Toast.makeText(
                    this@AddCafeActivity,
                    "カフェを登録しました",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }

    }
}