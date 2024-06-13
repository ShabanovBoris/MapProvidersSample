import android.content.Context
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import ru.bosha.mapssample.maps.core.AwesomeMap
import ru.bosha.mapssample.maps.providers.google.GoogleMapsProvider
import ru.bosha.mapssample.maps.providers.yandex.YandexMapsProvider

interface MapProvider {

    fun provide(
        holder: FrameLayout,
        lifecycleOwner: LifecycleOwner? = null,
        interactive: Boolean = false,
        movable: Boolean = false,
        onMapLoaded: (AwesomeMap) -> Unit
    )
}

fun MapProvider(context: Context, vendor: MapVendor): MapProvider {
    return when (vendor) {
        MapVendor.YandexMap -> YandexMapsProvider(context)
        MapVendor.Google -> GoogleMapsProvider(context)
        else -> YandexMapsProvider(context) // your default provider
    }
}