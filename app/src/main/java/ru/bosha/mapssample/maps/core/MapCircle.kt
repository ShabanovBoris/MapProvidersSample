package ru.bosha.mapssample.maps.core

interface MapCircle {
    var radius: Float
    var color: Int

    val center: Location

    fun remove()
}