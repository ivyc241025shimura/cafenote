package com.example.mycafenote

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var cafeAdapter: CafeAdapter

    private var cafeList = listOf<CafeListItem>()

    // 現在の並び替え
    // 0 = 並び替え
    // 1 = 訪問日の新しい順
    // 2 = 訪問日の古い順
    // 3 = 店名のあいうえお順
    // 4 = 評価の高い順
    // 5 = 評価の低い順
    private var currentSortType = 0

    // お気に入り絞り込み
    // 0 = すべて
    // 1 = お気に入りのみ
    private var currentFavoriteType = 0

    // 都道府県絞り込み
    private var currentPrefecture = "都道府県"

    private lateinit var spinnerPrefecture: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView =
            findViewById<RecyclerView>(R.id.recyclerViewCafe)

        val spinnerSort =
            findViewById<Spinner>(R.id.spinnerSort)

        val spinnerFavorite =
            findViewById<Spinner>(R.id.spinnerFavorite)

        spinnerPrefecture =
            findViewById(R.id.spinnerPrefecture)

        val fabAddCafe =
            findViewById<FloatingActionButton>(R.id.buttonAddCafe)

        // =========================
        // カフェ一覧
        // =========================

        cafeAdapter = CafeAdapter(
            emptyList()
        ) { cafe ->

            val intent = Intent(
                this,
                CafeDetailActivity::class.java
            )

            intent.putExtra(
                "CAFE_ID",
                cafe.id
            )

            startActivity(intent)
        }

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter = cafeAdapter

        // =========================
        // 並び替えメニュー
        // =========================

        val sortItems = arrayOf(
            "並び替え",
            "訪問日：新しい順",
            "訪問日：古い順",
            "店名：あいうえお順",
            "評価：高い順",
            "評価：低い順"
        )

        val sortAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item_small,
            sortItems
        )

        sortAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerSort.adapter = sortAdapter

        spinnerSort.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    currentSortType = position
                    displayCafes()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }

        // =========================
        // お気に入り絞り込みメニュー
        // =========================

        val favoriteItems = arrayOf(
            "お気に入り",
            "すべて",
            "お気に入りのみ"
        )

        val favoriteAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item_small,
            favoriteItems
        )

        favoriteAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerFavorite.adapter = favoriteAdapter

        spinnerFavorite.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    currentFavoriteType = when (position) {
                        2 -> 1
                        else -> 0
                    }

                    displayCafes()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }

        // =========================
        // 都道府県絞り込みメニュー
        // =========================

        spinnerPrefecture.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    currentPrefecture =
                        parent?.getItemAtPosition(position)
                            ?.toString()
                            ?: "都道府県"

                    displayCafes()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }

        // =========================
        // カフェ追加ボタン
        // =========================

        fabAddCafe.setOnClickListener {

            val intent = Intent(
                this,
                AddCafeActivity::class.java
            )

            startActivity(intent)
        }

        // =========================
        // カフェ読み込み
        // =========================

        loadCafes()
    }

    // =========================
    // 画面に戻ってきたとき
    // =========================

    override fun onResume() {
        super.onResume()

        if (::cafeAdapter.isInitialized) {
            loadCafes()
        }
    }

    // =========================
    // カフェ情報を読み込む
    // =========================

    private fun loadCafes() {

        val database =
            CafeDatabase.getDatabase(this)

        val cafeDao =
            database.cafeDao()

        val cafePhotoDao =
            database.cafePhotoDao()

        lifecycleScope.launch {

            val cafes =
                cafeDao.getAllCafes()

            val firstPhotos =
                cafePhotoDao.getFirstPhotos()

            val photoMap =
                firstPhotos.associateBy {
                    it.cafeId
                }

            cafeList = cafes.map { cafe ->

                CafeListItem(
                    cafe = cafe,
                    firstPhotoUri =
                        photoMap[cafe.id]?.uri
                )
            }

            // 登録されている都道府県だけを設定
            updatePrefectureSpinner()

            // 一覧を表示
            displayCafes()
        }
    }

    // =========================
    // 絞り込み＋並び替え
    // =========================

    private fun displayCafes() {

        // 最初は全カフェ
        var filteredList = cafeList

        // =========================
        // ① お気に入りで絞り込み
        // =========================

        if (currentFavoriteType == 1) {

            filteredList =
                filteredList.filter {
                    it.cafe.favorite
                }
        }

        // =========================
        // ② 都道府県で絞り込み
        // =========================

        if (currentPrefecture != "都道府県") {

            filteredList =
                filteredList.filter {

                    getPrefecture(
                        it.cafe.address
                    ) == currentPrefecture
                }
        }

        // =========================
        // ③ 並び替え
        // =========================

        val sortedList =
            when (currentSortType) {

                // 並び替え
                0 -> filteredList

                // 訪問日の新しい順
                1 -> filteredList.sortedByDescending {

                    it.cafe.visitYear * 10000 +
                            it.cafe.visitMonth * 100 +
                            it.cafe.visitDay
                }

                // 訪問日の古い順
                2 -> filteredList.sortedBy {

                    it.cafe.visitYear * 10000 +
                            it.cafe.visitMonth * 100 +
                            it.cafe.visitDay
                }

                // 店名のあいうえお順
                3 -> filteredList.sortedBy {

                    it.cafe.cafeName
                }

                // 評価の高い順
                4 -> filteredList.sortedByDescending {

                    it.cafe.rating
                }

                // 評価の低い順
                5 -> filteredList.sortedBy {

                    it.cafe.rating
                }

                else -> filteredList
            }

        // =========================
        // ④ RecyclerViewに表示
        // =========================

        cafeAdapter.updateData(
            sortedList
        )
    }

    // =========================
    // 都道府県Spinnerを更新
    // =========================

    private fun updatePrefectureSpinner() {

        val prefectures =
            cafeList
                .mapNotNull {
                    getPrefecture(it.cafe.address)
                }
                .distinct()
                .sorted()

        val items =
            mutableListOf("都道府県")

        items.addAll(prefectures)

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item_small,
            items
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerPrefecture.adapter =
            adapter

        // 現在選択している都道府県が
        // 登録されている場合は、そのまま選択
        if (items.contains(currentPrefecture)) {

            spinnerPrefecture.setSelection(
                items.indexOf(currentPrefecture)
            )

        } else {

            // 登録されていない場合は
            // 「都道府県」に戻す
            currentPrefecture =
                "都道府県"

            spinnerPrefecture.setSelection(
                0
            )
        }
    }

    // =========================
    // 住所から都道府県を取得
    // =========================

    private fun getPrefecture(
        address: String
    ): String? {

        val prefectures = listOf(

            "北海道",

            "青森県",
            "岩手県",
            "宮城県",
            "秋田県",
            "山形県",
            "福島県",

            "茨城県",
            "栃木県",
            "群馬県",
            "埼玉県",
            "千葉県",
            "東京都",
            "神奈川県",

            "新潟県",
            "富山県",
            "石川県",
            "福井県",
            "山梨県",
            "長野県",

            "岐阜県",
            "静岡県",
            "愛知県",
            "三重県",

            "滋賀県",
            "京都府",
            "大阪府",
            "兵庫県",
            "奈良県",
            "和歌山県",

            "鳥取県",
            "島根県",
            "岡山県",
            "広島県",
            "山口県",

            "徳島県",
            "香川県",
            "愛媛県",
            "高知県",

            "福岡県",
            "佐賀県",
            "長崎県",
            "熊本県",
            "大分県",
            "宮崎県",
            "鹿児島県",
            "沖縄県"
        )

        return prefectures.firstOrNull {

            address.startsWith(it)
        }
    }
}