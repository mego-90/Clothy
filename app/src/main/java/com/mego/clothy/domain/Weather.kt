package com.mego.clothy.domain

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.CloudCircle
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.mego.clothy.R

enum class Weather (@StringRes val titleRes:Int, val icon: ImageVector ) {
    UNSPECIFIED(R.string.weather_unspecified, Icons.Default.CloudCircle),
    HOT(R.string.weather_hot, Icons.Default.WbSunny),
    WARM(R.string.weather_warm, Icons.Default.WbCloudy),
    COLD(R.string.weather_cold, Icons.Default.AcUnit)
}