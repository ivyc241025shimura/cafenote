package com.example.mycafenote

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog

class EditCafeActivity : AppCompatActivity() {

    private var cafeId: Int = -1

    private var selectedPhotoUris: List<Uri> = emptyList()

    private lateinit var selectedPhotoAdapter: SelectedPhotoAdapter

    private val photoPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->

            if (uris.isNotEmpty()) {

                selectedPhotoUris = uris.take(10)

                selectedPhotoAdapter.updateData(selectedPhotoUris)

                findViewById<TextView>(
                    R.id.textSelectedPhotoTitleUpdate
                ).visibility = View.VISIBLE

                findViewById<RecyclerView>(
                    R.id.recyclerViewSelectedPhotoUpdate
                ).visibility = View.VISIBLE

                Toast.makeText(
                    this,
                    "${selectedPhotoUris.size}枚の写真を選択しました",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_cafe)

        val header =
            findViewById<View>(R.id.editCafeHeader)

        header.applyHeaderStatusBarPadding()

        cafeId = intent.getIntExtra("CAFE_ID", -1)

        if (cafeId == -1) {
            finish()
            return
        }

        val editCafeName =
            findViewById<EditText>(R.id.editCafeNameUpdate)

        val editAddress =
            findViewById<EditText>(R.id.editAddressUpdate)

        val editYear =
            findViewById<EditText>(R.id.editYearUpdate)

        val editMonth =
            findViewById<EditText>(R.id.editMonthUpdate)

        val editDay =
            findViewById<EditText>(R.id.editDayUpdate)

        val ratingBar =
            findViewById<RatingBar>(R.id.ratingBarUpdate)

        val editFood =
            findViewById<EditText>(R.id.editFoodUpdate)

        val editMemo =
            findViewById<EditText>(R.id.editMemoUpdate)

        val checkFavorite =
            findViewById<CheckBox>(R.id.checkFavoriteUpdate)

        val buttonBack =
            findViewById<Button>(R.id.buttonBackEdit)

        val buttonUpdate =
            findViewById<Button>(R.id.buttonUpdateCafe)

        val buttonSelectPhoto =
            findViewById<Button>(R.id.buttonSelectPhotoUpdate)

        val buttonDelete =
            findViewById<Button>(R.id.buttonDeleteCafe)

        val recyclerViewPhoto =
            findViewById<RecyclerView>(
                R.id.recyclerViewSelectedPhotoUpdate
            )

        // 写真用RecyclerView
        selectedPhotoAdapter =
            SelectedPhotoAdapter(emptyList())

        recyclerViewPhoto.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        recyclerViewPhoto.adapter =
            selectedPhotoAdapter

        // 戻る
        buttonBack.setOnClickListener {
            finish()
        }

        // 写真選択
        buttonSelectPhoto.setOnClickListener {
            photoPicker.launch(arrayOf("image/*"))
        }

        val database =
            CafeDatabase.getDatabase(this)

        val cafeDao =
            database.cafeDao()

        val cafePhotoDao =
            database.cafePhotoDao()

        // 登録済みデータを取得
        lifecycleScope.launch {

            val cafe =
                cafeDao.getCafeById(cafeId)

            if (cafe == null) {
                Toast.makeText(
                    this@EditCafeActivity,
                    "カフェ情報が見つかりません",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
                return@launch
            }

            // 入力欄に既存データをセット
            editCafeName.setText(cafe.cafeName)

            editAddress.setText(cafe.address)

            editYear.setText(cafe.visitYear.toString())

            editMonth.setText(cafe.visitMonth.toString())

            editDay.setText(cafe.visitDay.toString())

            ratingBar.rating =
                cafe.rating.toFloat()

            editFood.setText(cafe.food)

            editMemo.setText(cafe.memo)

            checkFavorite.isChecked =
                cafe.favorite

            // 既存写真を取得
            val photos =
                cafePhotoDao.getPhotosByCafeId(cafeId)

            if (photos.isNotEmpty()) {

                selectedPhotoUris =
                    photos.map {
                        Uri.parse(it.uri)
                    }

                selectedPhotoAdapter.updateData(
                    selectedPhotoUris
                )

                findViewById<TextView>(
                    R.id.textSelectedPhotoTitleUpdate
                ).visibility = View.VISIBLE

                recyclerViewPhoto.visibility =
                    View.VISIBLE
            }
        }

        // 更新
        buttonUpdate.setOnClickListener {

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
            if (
                year == null ||
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

            lifecycleScope.launch {

                // 更新するデータ
                val cafe =
                    CafeEntity(
                        id = cafeId,
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

                // Cafeテーブルを更新
                cafeDao.update(cafe)

                // 写真を一度削除
                cafePhotoDao.deletePhotosByCafeId(
                    cafeId
                )

                // 写真を再登録
                if (selectedPhotoUris.isNotEmpty()) {

                    val photos =
                        selectedPhotoUris.mapIndexed {
                                index, uri ->

                            try {
                                contentResolver.takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                                )
                            } catch (e: Exception) {
                            }

                            CafePhotoEntity(
                                cafeId = cafeId,
                                uri = uri.toString(),
                                displayOrder = index
                            )
                        }

                    cafePhotoDao.insertPhotos(
                        photos
                    )
                }

                Toast.makeText(
                    this@EditCafeActivity,
                    "カフェ情報を更新しました",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }

        // 削除
        buttonDelete.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("カフェを削除")
                .setMessage("このカフェの記録を削除しますか？")
                .setNegativeButton("キャンセル", null)
                .setPositiveButton("削除") { _, _ ->

                    lifecycleScope.launch {

                        // カフェ本体を削除
                        cafeDao.deleteCafeById(cafeId)

                        Toast.makeText(
                            this@EditCafeActivity,
                            "カフェを削除しました",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@EditCafeActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    }
                }
                .show()
        }
    }
}