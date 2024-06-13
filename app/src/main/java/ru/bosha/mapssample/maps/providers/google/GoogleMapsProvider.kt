package ru.bosha.mapssample.maps.providers.google

import MapProvider
import android.content.Context
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import kotlinx.coroutines.launch
import ru.bosha.mapssample.maps.core.AwesomeMap

class GoogleMapsProvider(private val context: Context) : MapProvider {

    override fun provide(
        holder: FrameLayout,
        lifecycleOwner: LifecycleOwner?,
        interactive: Boolean,
        movable: Boolean,
        onMapLoaded: (AwesomeMap) -> Unit
    ) {
        holder.removeAllViews()
        val options = GoogleMapOptions().apply {
            liteMode(!interactive)
            compassEnabled(false)
            mapToolbarEnabled(false)
        }
        val mapView = MapView(context, options).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            isClickable = interactive
        }
        holder.addView(mapView)

        lifecycleOwner?.lifecycleScope?.launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                mapView.onCreate(null)
                mapView.onResume()
                val map = mapView.awaitMap()
                val awesomeMap = AwesomeMapGoogle(map, mapView)
                map.awaitMapLoad()
                onMapLoaded(awesomeMap)
                map.setOnMarkerClickListener(awesomeMap)
                map.isBuildingsEnabled = false
            }
        }
        addLifecycleObserver(lifecycleOwner, mapView)
    }

    private fun addLifecycleObserver(
        lifecycleOwner: LifecycleOwner?,
        mapView: MapView
    ) {
        lifecycleOwner?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START -> mapView.onStart()
                    Lifecycle.Event.ON_STOP -> mapView.onStop()
                    else -> Unit
                }
            }
        })
    }

}