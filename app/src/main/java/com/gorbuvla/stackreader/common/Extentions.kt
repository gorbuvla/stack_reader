@file:JvmName("ExtensionUtils")

package com.gorbuvla.stackreader.common

import android.os.Build
import android.text.Html
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by vlad on 20/08/2017.
 */

fun Long.toStringDate() : String {
    val sdf = SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.GERMANY)
    return sdf.format(Date(this*1000))
}

fun Int.toStackPoints() : String {
    return "$this ${if (this == 1) "point" else "points"}"
}

fun String.fromHTML() : String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
        Html.fromHtml(this).toString()
    }
}
