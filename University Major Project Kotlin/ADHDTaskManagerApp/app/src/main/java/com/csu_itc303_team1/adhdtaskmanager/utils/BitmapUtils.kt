package com.csu_itc303_team1.adhdtaskmanager.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import android.view.ViewTreeObserver


fun blurBitmap(bitmap: Bitmap, applicationContext: Context, radius: Float = 10f): Bitmap? {
    return try {
        val rs = RenderScript.create(applicationContext)
        val input = Allocation.createFromBitmap(rs, bitmap)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setRadius(radius)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(bitmap)
        rs.destroy()

        bitmap
    } catch (e: Exception) {
        null
    }
}

fun takeScreenshot(view: View): Bitmap {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

fun captureScreenshotWhenReady(view: View, action: (Bitmap) -> Unit) {
    val observer = view.viewTreeObserver
    observer.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            // Ensure the listener is removed to prevent multiple callbacks
            if (observer.isAlive) {
                observer.removeOnPreDrawListener(this)
            } else {
                view.viewTreeObserver.removeOnPreDrawListener(this)
            }

            action(takeScreenshot(view))
            return true
        }
    })
}