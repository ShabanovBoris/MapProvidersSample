package ru.bosha.mapssample.maps.core

interface CameraEventListener {
    fun onCameraIdleListener()
    fun onMoveListener()
    fun onGestureListener()
}


fun AwesomeMap.setCameraListener(
    onCameraIdleListener: () -> Unit = {},
    onMoveListener: () -> Unit = {},
    onGestureListener: () -> Unit = {},
) {
    setCameraListener(object : CameraEventListener {
        override fun onCameraIdleListener() {
            onCameraIdleListener()
        }

        override fun onMoveListener() {
            onMoveListener()
        }

        override fun onGestureListener() {
            onGestureListener()
        }
    })
}