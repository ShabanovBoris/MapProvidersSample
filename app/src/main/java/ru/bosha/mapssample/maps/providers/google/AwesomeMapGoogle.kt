package ru.bosha.mapssample.maps.providers.google

import MapHelper
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.ktx.addMarker
import ru.bosha.mapssample.maps.core.AwesomeMap
import ru.bosha.mapssample.maps.core.CameraEventListener
import ru.bosha.mapssample.maps.core.Location
import ru.bosha.mapssample.maps.core.MapCircle
import ru.bosha.mapssample.maps.core.MapMarker
import ru.bosha.mapssample.maps.core.toLatLng
import ru.bosha.mapssample.maps.core.toLocation

class AwesomeMapGoogle(
    private var map: GoogleMap,
    private var mapView: com.google.android.gms.maps.MapView
) : AwesomeMap, OnMarkerClickListener {

    override val helper: MapHelper
        get() = object : MapHelper {
            override fun getBoundsCenter(locations: List<Location>): Location {
                val center = LatLngBounds.Builder().apply {
                    locations.forEach {
                        include(it.toLatLng())
                    }
                }.build().center
                return center.toLocation()
            }
        }

    private val context get() = mapView.context
    override val defaultZoom: Float = 16f
    override val target: Location get() = map.cameraPosition.target.toLocation()
    override val zoom: Float get() = map.cameraPosition.zoom
    private var markerClickListener: (Long) -> Unit = {}

    var mapType: Int
        get() = map.mapType
        set(value) {
            map.mapType = value
        }


    override fun addMarker(location: Location, id: Long?): MapMarker? {
        val marker = map.addMarker {
            position(location.toLatLng())
        } ?: return null
        return object : MapMarker {
            override var zIndex: Float
                get() = marker.zIndex
                set(value) {
                    marker.zIndex = value
                }
            override var location: Location
                get() = marker.position.toLocation()
                set(value) {
                    marker.position = value.toLatLng()
                }
            override var id: Long
                get() = marker.tag as Long
                set(value) {
                    marker.tag = value
                }

            override fun setImage(bitmap: Bitmap, anchor: Pair<Float, Float>?) {
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
                anchor?.let {
                    marker.setAnchor(anchor.first, anchor.second)
                } ?: run {
                    marker.setAnchor(0.5f, 0.5f)
                }
            }

            override fun remove() {
                marker.remove()
            }
        }
    }

    override fun addCircle(
        context: Context,
        position: Location,
        currentRange: Double,
        circleColor: Int,
        stroke: Boolean
    ): MapCircle {
        val circleOptions = CircleOptions().apply {
            center(position.toLatLng())
            radius(currentRange)
            strokeWidth(4f)
            if (stroke) {
                strokeColor(Color.WHITE)
            } else {
                strokeColor(Color.TRANSPARENT)
            }
            fillColor(ContextCompat.getColor(context, circleColor))
        }
        val circle = map.addCircle(circleOptions)
        return object : MapCircle {
            override fun remove() {
                circle.remove()
            }

            override var radius: Float
                get() = circle.radius.toFloat()
                set(value) {
                    circle.radius = value.toDouble()
                }
            override var color: Int
                get() = circle.fillColor
                set(value) {
                    circle.fillColor = value
                }

            override val center: Location = circle.center.toLocation()
        }
    }

    override fun addPolyline(locations: List<Location>, colorRes: Int, width: Float) {
        val polyline = PolylineOptions()
            .width(width)
            .color(ContextCompat.getColor(context, colorRes))

        locations.forEach {
            polyline.add(it.toLatLng())
        }
        map.addPolyline(polyline)
    }

    override fun moveCamera(location: Location, zoomLevel: Float?, zoomRange: Float?, isAnimated: Boolean) {
        val rangePosition = zoomRange?.let {
            CameraUpdateFactory.newLatLngZoom(
                location.toLatLng(),
                zoomRange
            )
        }

        val defaultZoom = if (zoom < defaultZoom) 14f else zoom
        map.animateCamera(
            rangePosition ?: CameraUpdateFactory.newLatLngZoom(
                location.toLatLng(),
                zoomLevel ?: defaultZoom
            )
        )
    }

    override fun moveCameraWithBounds(locations: List<Location>, isAnimated: Boolean) {
        val bounds = LatLngBounds.Builder().apply {
            locations.forEach {
                include(LatLng(it.latitude, it.longitude))
            }
        }.build()
        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, 0)
        )
    }

    override fun moveCameraWithBounds(
        locations: List<Location>,
        width: Int,
        height: Int,
        startPadding: Float
    ) {

        val bounds = LatLngBounds.Builder().apply {
            locations.forEach {
                include(LatLng(it.latitude, it.longitude))
            }
        }.build()
        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                width,
                height,
                startPadding.toInt()
            )
        )
    }

    override fun onMarkerClick(callback: (Long) -> Unit) {
        markerClickListener = callback
    }

    override fun setCameraListener(listener: CameraEventListener) {
        map.setOnCameraIdleListener { listener.onCameraIdleListener() }
        map.setOnCameraMoveListener { listener.onMoveListener() }
        map.setOnCameraMoveStartedListener {
            if (it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                listener.onGestureListener()
            }
        }
    }

    override fun onStart() {
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
    }

    override fun applyPaddings(bottom: Int?, logoBottom: Int?) {
        bottom?.let {
            map.setPadding(0, 0, 0, bottom)
        }
    }

    override fun zoomIn() {
        map.animateCamera(CameraUpdateFactory.zoomIn(), 300, null)
    }

    override fun zoomOut() {
        map.animateCamera(CameraUpdateFactory.zoomOut(), 300, null)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val id = marker.tag as Long
        markerClickListener(id)
        return true
    }
}