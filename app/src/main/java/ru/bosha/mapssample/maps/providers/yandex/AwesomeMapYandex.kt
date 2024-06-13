package ru.bosha.mapssample.maps.providers.yandex

import MapHelper
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.ScreenRect
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.bosha.mapssample.maps.core.AwesomeMap
import ru.bosha.mapssample.maps.core.CameraEventListener
import ru.bosha.mapssample.maps.core.Location
import ru.bosha.mapssample.maps.core.MapCircle
import ru.bosha.mapssample.maps.core.MapMarker
import ru.bosha.mapssample.maps.core.toLocation
import ru.bosha.mapssample.maps.core.toPoint

class AwesomeMapYandex(
    private val mapView: MapView
) : AwesomeMap {

    override val helper: MapHelper
        get() = object : MapHelper {
            override fun getBoundsCenter(locations: List<Location>): Location {
                val polyline = Polyline(locations.map { Point(it.latitude, it.longitude) })
                val target = map.cameraPosition(Geometry.fromPolyline(polyline)).target
                return target.toLocation()
            }
        }
    private val map get() = mapView.mapWindow.map
    private val context get() = mapView.context
    override val defaultZoom: Float = 16f
    override val target get() = map.cameraPosition.target.toLocation()
    override val zoom get() = map.cameraPosition.zoom


    private var markerClickListener: (Long) -> Unit = {}
    private val mapObjectTapListener = MapObjectTapListener { mapObject, _ ->
        val id = mapObject.userData as? Long
        id?.let(markerClickListener)
        true
    }

    private var cameraEventListener: CameraEventListener? = null
    private val cameraListener: CameraListener =
        CameraListener { map, cameraPosition, cameraUpdateReason, finished ->
            if (finished) {
                cameraEventListener?.onCameraIdleListener()
                return@CameraListener
            } else {
                cameraEventListener?.onMoveListener()
            }
            if (cameraUpdateReason == CameraUpdateReason.GESTURES) cameraEventListener?.onGestureListener()
        }

    init {
        map.mapObjects.addTapListener(mapObjectTapListener)
        map.addCameraListener(cameraListener)
    }

    override fun applyPaddings(bottom: Int?, logoBottom: Int?) {
        logoBottom?.let {
            map.logo.setPadding(Padding(0, logoBottom))
        }

        bottom?.let {
            mapView.let {
                val rect = ScreenRect(
                    ScreenPoint(0f, 0f),
                    ScreenPoint(
                        it.mapWindow.width().toFloat(),
                        it.mapWindow.height() - bottom.toFloat(),
                    )
                )
                try {
                    mapView.mapWindow?.focusRect = rect
                } catch (e: Exception) {
                    // если передаем невалидные значения - просто игнорим
                }
            }
        }
    }

    override fun zoomIn() {
        val currentCameraPosition = map.cameraPosition
        val newZoom = currentCameraPosition.zoom + 1
        val newCameraPosition = CameraPosition(
            currentCameraPosition.target,
            newZoom,
            currentCameraPosition.azimuth,
            currentCameraPosition.tilt
        )
        map.move(
            newCameraPosition,
            Animation(Animation.Type.SMOOTH, defaultZoomDuration),
            null
        )
    }

    override fun zoomOut() {
        val currentCameraPosition = map.cameraPosition
        val newZoom = currentCameraPosition.zoom - 1
        val newCameraPosition = CameraPosition(
            currentCameraPosition.target,
            newZoom,
            currentCameraPosition.azimuth,
            currentCameraPosition.tilt
        )
        map.move(
            newCameraPosition,
            Animation(Animation.Type.SMOOTH, defaultZoomDuration),
            null
        )
    }

    override fun addMarker(
        location: Location,
        id: Long?
    ): MapMarker = map.let { yaMap ->
        val placemark = yaMap.mapObjects.addPlacemark().apply {
            geometry = location.toPoint()
        }
        return object : MapMarker {
            override var zIndex: Float
                get() = placemark.zIndex
                set(value) {
                    placemark.zIndex = value
                }
            override var location: Location
                get() = placemark.geometry.toLocation()
                set(value) {
                    placemark.geometry = value.toPoint()
                }
            override var id: Long
                set(value) {
                    placemark.userData = value
                }
                get() = placemark.userData as Long


            override fun setImage(bitmap: Bitmap, anchor: Pair<Float, Float>?) {
                val imageProvider = ImageProvider.fromBitmap(bitmap)
                placemark.apply {
                    setIcon(imageProvider)
                    setIconStyle(IconStyle().apply {
                        anchor?.let {
                            this.anchor = PointF(anchor.first, anchor.second)
                        }
                    })
                }
            }

            override fun remove() {
                yaMap.mapObjects.remove(placemark)
            }
        }
    }

    override fun addCircle(
        context: Context,
        position: Location,
        currentRange: Double,
        @ColorRes circleColor: Int,
        stroke: Boolean
    ): MapCircle {
        val circle = Circle(Point(position.latitude, position.longitude), currentRange.toFloat())
        val mapCircle = map.mapObjects.addCircle(circle).apply {
            strokeColor = if (stroke) Color.WHITE else Color.TRANSPARENT
            strokeWidth = 4f
            fillColor = ContextCompat.getColor(context, circleColor)
        }

        return object : MapCircle {
            override fun remove() {
                map.mapObjects.remove(mapCircle)
            }

            override var radius: Float
                get() = mapCircle.geometry.radius
                set(value) {
                    try {
                        mapCircle.geometry = Circle(position.toPoint(), value)
                    } catch (e: Exception) {
                        // Nothing
                    }
                }
            override var color: Int
                get() = mapCircle.fillColor
                set(value) {
                    mapCircle.fillColor = value
                }

            override val center: Location = mapCircle.geometry.center.toLocation()
        }
    }

    override fun addPolyline(locations: List<Location>, colorRes: Int, width: Float) {
        val polyline = Polyline(locations.map { it.toPoint() })
        map.mapObjects.addPolyline(polyline).apply {
            strokeWidth = width
            setStrokeColor(ContextCompat.getColor(context, colorRes))
        }
    }


    override fun moveCamera(
        location: Location,
        zoomLevel: Float?,
        zoomRange: Float?,
        isAnimated: Boolean
    ) {
        val rangePosition = zoomRange?.let {
            val circle = Circle(location.toPoint(), zoomRange)
            map.cameraPosition(Geometry.fromCircle(circle))
        }
        val pointPosition = CameraPosition(
            location.toPoint(),
            zoomLevel ?: map.cameraPosition.zoom,
            map.cameraPosition.azimuth,
            map.cameraPosition.tilt
        )
        if (isAnimated) {
            map.move(
                rangePosition ?: pointPosition,
                Animation(Animation.Type.SMOOTH, defaultAnimateDuration),
                null
            )
        } else {
            map.move(rangePosition ?: pointPosition)
        }
    }

    override fun moveCameraWithBounds(
        locations: List<Location>, isAnimated: Boolean
    ) {
        val polyline = Polyline(locations.map { it.toPoint() })
        val position = map.cameraPosition(Geometry.fromPolyline(polyline))
        map.move(position)
    }

    override fun moveCameraWithBounds(
        locations: List<Location>,
        width: Int,
        height: Int,
        startPadding: Float
    ) {
        val rect =
            ScreenRect(
                ScreenPoint(startPadding, startPadding),
                ScreenPoint(width.toFloat(), height.toFloat())
            )
        val polyline = Polyline(locations.map { it.toPoint() })

        val position = map.cameraPosition(Geometry.fromPolyline(polyline), rect)
        map.move(position, Animation(Animation.Type.SMOOTH, defaultAnimateDuration), null)
    }

    override fun onMarkerClick(callback: (id: Long) -> Unit) {
        markerClickListener = callback
    }

    override fun setCameraListener(listener: CameraEventListener) {
        cameraEventListener = listener
    }

    override fun onStart() {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        mapView.onStop()
    }

    private companion object {
        const val defaultAnimateDuration = 0.5f
        const val defaultZoomDuration = 0.3f
    }
}