package com.savr.paging3kotlin.utils

import android.view.View

fun View.visibleWhen(show: Boolean) {
    if (show) this.visibility = View.VISIBLE
    else this.visibility = View.GONE
}