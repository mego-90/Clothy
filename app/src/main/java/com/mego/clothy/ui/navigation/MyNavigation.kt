package com.mego.clothy.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.mego.clothy.ui.HomeViewModel
import com.mego.clothy.ui.compose.camera.CameraScreen
import com.mego.clothy.ui.compose.category.CategoryGalleryScreen
import com.mego.clothy.ui.compose.home.HomeScreen
import com.mego.clothy.ui.compose.itemDialog.ItemDetailsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNavigation(
    navController: NavHostController,
    myViewModel : HomeViewModel,
    showTopBar : MutableState<Boolean>,
    showBottomSheet : MutableState<Boolean>,
    bottomSheetState: SheetState,
    actionModeEnabled : MutableState<Boolean> ) {

    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = MyScreen.Home.route
    )
    {

        navController.addOnDestinationChangedListener { _, navDestination: NavDestination, _ ->
            //manage top bar
            val selectedScreen = MyScreen.getScreenByRoute(navDestination.route)
            showTopBar.value = selectedScreen.showTopBar
            showBottomSheet.value = selectedScreen is MyScreen.MyItemDetails

        }

        composable(route = MyScreen.Home.route) {
            HomeScreen(
                categories = myViewModel.categoriesStateFlow.collectAsState().value,
                itemsInCategories = myViewModel.itemsStateFlow.collectAsState().value,
                onSelectCategory = {
                    myViewModel.setSelectedCategoryID(it)
                    navController.navigate(MyScreen.CATEGORY_GALLERY.route)
                },
                onAddImageToCategory = {
                    myViewModel.setSelectedCategoryID(it)
                    navController.navigate(MyScreen.MyCamera.route)
                },
                onEditImage = {
                    myViewModel.setSelectedCategoryID(it.categoryID)
                    myViewModel.setSelectedItemToShow(it)
                    navController.navigate(MyScreen.MyItemDetails.route)
                }

            )
        }

        composable (route=MyScreen.CATEGORY_GALLERY.route , ) {
            val selectedCategoryID = myViewModel.selectedCategoryStateFlow.collectAsState().value?.id
            CategoryGalleryScreen(
                items = myViewModel.itemsStateFlow.collectAsState().value[selectedCategoryID]?.collectAsState()?.value.orEmpty(),
                actionModeEnabled = actionModeEnabled,
                onSelectItem = {item ->  myViewModel.selectItemInActionMode(item) },
                onUnselectItem = {item ->  myViewModel.unselectItemInActionMode(item) },
                onOpenItem = { item ->
                    myViewModel.setSelectedItemToShow(item)
                    navController.navigate(MyScreen.MyItemDetails.route)
                }
            )

        }

        composable(route = MyScreen.CATEGORIES.route) {

        }

        composable(route = MyScreen.MyCamera.route) {
            CameraScreen(
                onSaveImageRequest = {tempFileUri -> myViewModel.addImageDataToDB(tempFileUri) },
                onCancelRequest = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(route=MyScreen.MyItemDetails.route){
            val selectedCategoryID = myViewModel.selectedCategoryStateFlow.collectAsState().value?.id
            ItemDetailsScreen(
                items = myViewModel.itemsStateFlow.collectAsState().value[selectedCategoryID]?.collectAsState()?.value.orEmpty(),
                selectedItem = myViewModel.selectedItemToShow.collectAsState().value,
                bottomSheetState = bottomSheetState,
                onChangeShownItem = {item -> myViewModel.setSelectedItemToShow(item) },
                onBackIconClick = {
                    if (bottomSheetState.currentValue == SheetValue.Expanded ) {
                        coroutineScope.launch {
                            bottomSheetState.partialExpand()
                        }
                    } else
                        navController.navigateUp()
                }
            )
        }
    }

    BackHandler(enabled = actionModeEnabled.value) {
        actionModeEnabled.value = false
        myViewModel.unselectAllItemsInActionMode()
    }

    BackHandler(enabled = (bottomSheetState.currentValue == SheetValue.Expanded )   ) {
        coroutineScope.launch {
            bottomSheetState.partialExpand()
        }
    }
}