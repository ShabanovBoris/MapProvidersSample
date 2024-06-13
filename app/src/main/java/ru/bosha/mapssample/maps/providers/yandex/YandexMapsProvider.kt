package ru.bosha.mapssample.maps.providers.yandex

import MapProvider
import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.MapLoadedListener
import com.yandex.mapkit.mapview.MapView
import ru.bosha.mapssample.databinding.MapkitViewBinding
import ru.bosha.mapssample.maps.core.AwesomeMap

class YandexMapsProvider(private val context: Context) : MapProvider {

    private val yaMapLoadedListeners: MutableList<MapLoadedListener> = mutableListOf()

    override fun provide(
        holder: FrameLayout,
        lifecycleOwner: LifecycleOwner?,
        interactive: Boolean,
        movable: Boolean,
        onMapLoaded: (AwesomeMap) -> Unit
    ) {
        holder.removeAllViews()
        val mapView = if (movable) {
            val mapkitViewBinding = MapkitViewBinding.inflate(LayoutInflater.from(holder.context), holder, true)
            mapkitViewBinding.root
        } else {
            MapView(context)
                .apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    holder.addView(this)
                }
        }
        mapView.onStart()
        MapKitFactory.getInstance().onStart()

        val map = AwesomeMapYandex(mapView)
        val innerLoadListener = MapLoadedListener { onMapLoaded(map) }
        yaMapLoadedListeners.add(innerLoadListener) // храним ссылку на колбек
        mapView.mapWindow.map.setMapLoadedListener(innerLoadListener)
        mapView.setNoninteractive(!interactive)
        mapView.mapWindow.map.set2DMode(true)

        lifecycleOwner?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START -> map.onStart()
                    Lifecycle.Event.ON_STOP -> map.onStop()
                    else -> Unit
                }
            }
        })
    }
}

