
enum class MapVendor {
        YandexMap,
        Google,
        TwoGIS;

        companion object {
            fun getByName(typeName: String, default: MapVendor): MapVendor {
                return MapVendor.entries.find { it.name == typeName } ?: default
            }
        }
    }

