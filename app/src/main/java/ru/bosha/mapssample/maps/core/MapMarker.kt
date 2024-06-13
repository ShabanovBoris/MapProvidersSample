package ru.bosha.mapssample.maps.core

import android.graphics.Bitmap
import ru.bosha.mapssample.maps.core.Location

interface MapMarker {
    var zIndex: Float
    var location: Location
    var id: Long
    fun setImage(bitmap: Bitmap, anchor: Pair<Float, Float>? = null)
    fun remove()
}