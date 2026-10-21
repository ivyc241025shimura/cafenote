package com.example.mycafenote

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun View.applyStatusBarPadding() {
    val initialTopPadding = paddingTop

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->

        val statusBarInsets =
            insets.getInsets(WindowInsetsCompat.Type.statusBars())

        view.setPadding(
            view.paddingLeft,
            initialTopPadding + statusBarInsets.top,
            view.paddingRight,
            view.paddingBottom
        )

        insets
    }

    ViewCompat.requestApplyInsets(this)
}

/**
 * ヘッダー用
 * ステータスバーの高さ分だけ中身を下げる
 */
fun View.applyHeaderStatusBarPadding() {

    val initialTopPadding = paddingTop

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->

        val statusBarInsets =
            insets.getInsets(WindowInsetsCompat.Type.statusBars())

        view.setPadding(
            view.paddingLeft,
            initialTopPadding + statusBarInsets.top,
            view.paddingRight,
            view.paddingBottom
        )

        insets
    }

    ViewCompat.requestApplyInsets(this)
}