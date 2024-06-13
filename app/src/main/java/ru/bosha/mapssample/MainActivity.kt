package ru.bosha.mapssample

import MapProvider
import MapVendor
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.bosha.mapssample.databinding.ActivityMainBinding
import ru.bosha.mapssample.maps.core.AwesomeMap
import ru.bosha.mapssample.maps.core.Location

val defaultVendor = MapVendor.Google // set the map vendor with di

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var mapProvider: MapProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mapProvider = MapProvider(this, defaultVendor)
        mapProvider?.provide(
            holder = binding.mapHolder,
            lifecycleOwner = this,
            interactive = true
        ) { map ->
            onMapReady(map)
            map.onMarkerClick(::onMapMarkerClick)
        }

        binding.button.setOnClickListener {
            MapListDialogFragment().show(supportFragmentManager, MapListDialogFragment::class.simpleName)
        }
    }

    private fun onMapReady(map: AwesomeMap) {
        val googleWasHere = Location(55.746735, 37.624702)
        map.moveCamera(googleWasHere, zoomLevel = 16f)
        map.addMarker(googleWasHere)
    }

    private fun onMapMarkerClick(id: Long) {
        // ...
    }
}