package ru.bosha.mapssample.maps.core

import MapHelper
import android.content.Context
import androidx.annotation.ColorRes

interface AwesomeMap {

    val defaultZoom: Float
    val zoom: Float
    val target: Location
    val helper: MapHelper

    fun addMarker(location: Location, id: Long? = null): MapMarker?

    fun addCircle(
        context: Context,
        position: Location,
        currentRange: Double,
        @ColorRes circleColor: Int,
        stroke: Boolean = false
    ): MapCircle

    fun addPolyline(
        locations: List<Location>,
        @ColorRes colorRes: Int,
        width: Float
    )

    fun moveCamera(
        location: Location,
        zoomLevel: Float? = null,
        zoomRange: Float? = null,
        isAnimated: Boolean = true
    )

    fun moveCameraWithBounds(locations: List<Location>, isAnimated: Boolean = true)

    /**
     * @param startPadding паддинг начала коррдинат слева вверху, 0.0 по умолчанию
     * @param height высота
     * @param width ширина
     */
    fun moveCameraWithBounds(
        locations: List<Location>,
        width: Int,
        height: Int,
        startPadding: Float
    )

    fun onMarkerClick(callback: (Long) -> Unit)

    fun setCameraListener(listener: CameraEventListener)

    fun onStart()
    fun onStop()
    fun applyPaddings(bottom: Int? = null, logoBottom: Int? = null)

    fun zoomIn()

    fun zoomOut()
}