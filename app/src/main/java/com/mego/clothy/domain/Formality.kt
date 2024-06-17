package com.mego.clothy.domain

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups2
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.ui.graphics.vector.ImageVector
import com.mego.clothy.R

enum class Formality (@StringRes val titleRes:Int, val icon: ImageVector) {
    UNSPECIFIED(R.string.formality_unspecified,Icons.Default.PersonOff),
    FORMAL(R.string.formality_formal, Icons.Default.Groups2),
    CASUAL(R.string.formality_casual, Icons.Default.SelfImprovement),
    SPORT(R.string.formality_sport, Icons.Default.SportsMartialArts)
}