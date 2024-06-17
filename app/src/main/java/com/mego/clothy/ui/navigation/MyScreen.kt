package com.mego.clothy.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImageSearch
import com.mego.clothy.R

sealed class MyScreen ( val route:String, @StringRes val label:Int, val materialIconName:String, val showTopBar:Boolean ) {

    object Home : MyScreen("HOME", R.string.home_screen, Icons.Default.Home.name , true)

    object CATEGORIES : MyScreen("CATEGORIES", R.string.categories_screen, Icons.Default.Category.name , true)

    object MyCamera : MyScreen("My_CAMERA", R.string.my_camera_screen, Icons.Default.Camera.name ,false)

    object CATEGORY_GALLERY : MyScreen("CATEGORY_GALLEY"/*{categoryID}"*/, R.string.category_gallery_screen, Icons.Default.Image.name, true)

    object MyItemDetails : MyScreen("My_ITEM_DETAILS", R.string.my_camera_screen, Icons.Default.ImageSearch.name ,false)
    /*
    {
        fun routeWithCategoryID(categoryID:Long) =
            "CATEGORY_GALLEY/{$categoryID}"
    }
    */

    companion object{
        fun getScreenByRoute(route: String?) : MyScreen {
            when (route) {
                "HOME" -> return Home
                "CATEGORIES" -> return CATEGORIES
                "My_CAMERA" -> return MyCamera
                "CATEGORY_GALLERY" -> return CATEGORY_GALLERY
                "My_ITEM_DETAILS" -> return MyItemDetails
            }
            return Home
        }
    }
}