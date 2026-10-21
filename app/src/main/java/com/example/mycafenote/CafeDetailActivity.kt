package com.example.mycafenote

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import androidx.core.view.WindowInsetsCompat


class CafeDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var cafePhotoAdapter: CafePhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cafe_detail)

        val header =
            findViewById<View>(R.id.cafeDetailHeader)

        header.applyHeaderStatusBarPadding()

        // Google Maps
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map)
                    as SupportMapFragment

        mapFragment.getMapAsync(this)

        // 表示用TextView
        val textCafeName =
            findViewById<TextView>(R.id.textDetailCafeName)

        val textAddress =
            findViewById<TextView>(R.id.textDetailAddress)

        val textVisitDate =
            findViewById<TextView>(R.id.textDetailVisitDate)

        val textRating =
            findViewById<TextView>(R.id.textDetailRating)

        val textFood =
            findViewById<TextView>(R.id.textDetailFood)

        val textMemo =
            findViewById<TextView>(R.id.textDetailMemo)

        val textFavorite =
            findViewById<TextView>(R.id.textDetailFavorite)

        // 写真RecyclerView
        val recyclerViewCafePhoto =
            findViewById<RecyclerView>(R.id.recyclerViewCafePhoto)

        cafePhotoAdapter =
            CafePhotoAdapter(emptyList())

        recyclerViewCafePhoto.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        recyclerViewCafePhoto.adapter =
            cafePhotoAdapter

        // カフェIDを取得
        val cafeId =
            intent.getIntExtra("CAFE_ID", -1)

        if (cafeId == -1) {
            finish()
            return
        }

        // Room
        val database =
            CafeDatabase.getDatabase(this)

        val cafeDao =
            database.cafeDao()

        val cafePhotoDao =
            database.cafePhotoDao()

        lifecycleScope.launch {

            // カフェ情報を取得
            val cafe =
                cafeDao.getCafeById(cafeId)

            if (cafe != null) {

                textCafeName.text =
                    cafe.cafeName

                textAddress.text =
                    "住所：${cafe.address}"

                textVisitDate.text =
                    "訪問日：${cafe.visitYear}/${cafe.visitMonth}/${cafe.visitDay}"

                textRating.text =
                    "評価：" + "★".repeat(cafe.rating)

                textFood.text =
                    if (cafe.food.isEmpty()) {
                        "食べたもの：なし"
                    } else {
                        "食べたもの：${cafe.food}"
                    }

                textMemo.text =
                    if (cafe.memo.isEmpty()) {
                        "メモ：なし"
                    } else {
                        "メモ：${cafe.memo}"
                    }

                textFavorite.text =
                    if (cafe.favorite) {
                        "♥ お気に入り"
                    } else {
                        "♡ お気に入り登録なし"
                    }
            }

            // 戻るボタン
            val buttonBack =
                findViewById<Button>(R.id.buttonBack)

            buttonBack.setOnClickListener {
                finish()
            }

            // 写真を取得
            val photos =
                cafePhotoDao.getPhotosByCafeId(cafeId)

            // 写真を表示
            cafePhotoAdapter.updateData(photos)
        }

        // 編集ボタン
        val buttonEdit =
            findViewById<Button>(R.id.buttonEditCafe)

        buttonEdit.setOnClickListener {

            val intent =
                Intent(
                    this,
                    EditCafeActivity::class.java
                )

            intent.putExtra(
                "CAFE_ID",
                cafeId
            )

            startActivity(intent)
        }
    }

    // Google Maps
    override fun onMapReady(googleMap: GoogleMap) {

        val cafeId =
            intent.getIntExtra("CAFE_ID", -1)

        if (cafeId == -1) {
            return
        }

        lifecycleScope.launch {

            val database =
                CafeDatabase.getDatabase(
                    this@CafeDetailActivity
                )

            val cafe =
                database.cafeDao().getCafeById(cafeId)

            if (cafe == null) {
                return@launch
            }

            val address =
                cafe.address

            if (address.isBlank()) {

                Toast.makeText(
                    this@CafeDetailActivity,
                    "住所が登録されていません",
                    Toast.LENGTH_SHORT
                ).show()

                return@launch
            }

            // 住所から緯度・経度を取得
            val location =
                withContext(Dispatchers.IO) {

                    val geocoder =
                        Geocoder(
                            this@CafeDetailActivity,
                            Locale.JAPAN
                        )

                    try {

                        val addresses =
                            geocoder.getFromLocationName(
                                address,
                                1
                            )

                        if (!addresses.isNullOrEmpty()) {

                            LatLng(
                                addresses[0].latitude,
                                addresses[0].longitude
                            )

                        } else {
                            null
                        }

                    } catch (e: Exception) {
                        null
                    }
                }

            // 地図を表示
            if (location != null) {

                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        location,
                        15f
                    )
                )

                googleMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title(cafe.cafeName)
                )

            } else {

                Toast.makeText(
                    this@CafeDetailActivity,
                    "住所から地図の位置を取得できませんでした",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}