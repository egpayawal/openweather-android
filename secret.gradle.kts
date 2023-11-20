val secretDebug by extra(
    listOf(
        Pair("fileName", "debug.keystore"),
        Pair("storePassword", "a7GcaxwxfO"),
        Pair("keyPassword", "a7GcaxwxfO"),
        Pair("keyAlias", "debugkeystorealias")
    )
)

val secretProd by extra(
    listOf(
        Pair("fileName", ""),
        Pair("storePassword", ""),
        Pair("keyPassword", ""),
        Pair("keyAlias", "")
    )
)

val stagingApi by extra(
    listOf(
        Pair("BASE_URL", "https://api.openweathermap.org")
    )
)

val releaseApi by extra(
    listOf(
        Pair("BASE_URL", "https://api.openweathermap.org")
    )
)

val openWeather by extra(
    listOf(
        Pair("open_weather_app_id", "")
    )
)
