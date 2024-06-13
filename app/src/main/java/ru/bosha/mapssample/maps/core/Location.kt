package ru.bosha.mapssample.maps.core

import com.google.android.gms.maps.model.LatLng
import com.yandex.mapkit.geometry.Point

data class Location(val latitude: Double, val longitude: Double)
data class LocationBounds(val locations: List<Location>)


fun Location.toLatLng(): LatLng  = LatLng(latitude, longitude)
fun LatLng.toLocation(): Location = Location(latitude, longitude)

fun Location.toPoint(): Point  = Point(latitude, longitude)
fun Point.toLocation(): Location = Location(latitude, longitude)