import ru.bosha.mapssample.maps.core.Location

interface MapHelper {
    fun getBoundsCenter(locations: List<Location>) : Location
}