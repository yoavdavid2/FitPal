package com.example.fitpal3.utils

import android.content.Context
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlin.math.roundToInt
import androidx.core.graphics.createBitmap

object MarkerIconFactory {
    fun createPinnedIcon(
        context: Context,
        @DrawableRes baseRes: Int,
        @DrawableRes overlayRes: Int,
        baseTint: Int,
        overlayTint: Int? = null,
        sizeDp: Float = 46f,
        overlayScale: Float = 0.55f,
        overlayYOffsetDp: Float = -2f,
    ): BitmapDescriptor {
        val density = context.resources.displayMetrics.density
        val sizePx = (sizeDp * density).roundToInt()

        val base = ContextCompat.getDrawable(context, baseRes)
            ?: return BitmapDescriptorFactory.defaultMarker()
        val overlay = ContextCompat.getDrawable(context, overlayRes)
            ?: return BitmapDescriptorFactory.defaultMarker()

        // Tint base
        val baseWrapped = DrawableCompat.wrap(base.mutate())
        DrawableCompat.setTint(baseWrapped, baseTint)

        // Tint overlay (optional; if null keep original)
        val overlayWrapped = DrawableCompat.wrap(overlay.mutate())
        overlayTint?.let { DrawableCompat.setTint(overlayWrapped, it) }

        val bitmap = createBitmap(sizePx, sizePx)
        val canvas = Canvas(bitmap)

        baseWrapped.setBounds(0, 0, sizePx, sizePx)
        baseWrapped.draw(canvas)

        val overlaySizePx = (sizePx * overlayScale).roundToInt()
        val centerX = sizePx / 2
        val centerY = sizePx / 2 + (overlayYOffsetDp * density).roundToInt()

        val left = centerX - overlaySizePx / 2
        val top = centerY - overlaySizePx / 2
        val right = left + overlaySizePx
        val bottom = top + overlaySizePx

        overlayWrapped.setBounds(left, top, right, bottom)
        overlayWrapped.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}