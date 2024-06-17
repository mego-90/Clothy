package com.mego.clothy.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mego.clothy.R
import com.mego.clothy.ui.compose.appBars.MyTopBar
import com.mego.clothy.ui.compose.category.NewCategoryDialog
import com.mego.clothy.ui.compose.itemDialog.ItemBottomSheet
import com.mego.clothy.ui.navigation.MyNavigation
import com.mego.clothy.ui.navigation.MyScreen
import com.mego.clothy.ui.theme.ClothyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val requestCameraPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                //TODO
            } else {
                Toast.makeText(this, R.string.camera_permission_denied,Toast.LENGTH_LONG).show()
            }
    }

    fun requestCameraPerm() {
        requestCameraPermLauncher.launch( Manifest.permission.CAMERA )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()
            val myViewModel: HomeViewModel = hiltViewModel()
            val backStackEntryState = navController.currentBackStackEntryAsState()
            var showTopBar = remember { mutableStateOf(true) }

            val scaffoldState = rememberBottomSheetScaffoldState()//bottomSheetState = myBottomSheetState)
            val showBottomSheet = remember { mutableStateOf(false) }

            val galleryLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument(),
                onResult = { uri ->
                    if ( uri != null ) {
                        // download file into temp then use old method
                        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        val fileInputStream = contentResolver.openInputStream(uri)
                        val fileExtension =
                            uri
                            .lastPathSegment
                            ?.split(".")
                            ?.last()
                            ?.takeIf { it.length == 3 || it.length == 4 }
                        if (fileInputStream != null) {
                            myViewModel.importImageToAppFolder(fileInputStream, fileExtension)
                        }
                    }
                }
            )

            val previousBackStackEntry = remember(backStackEntryState.value) {
                navController.previousBackStackEntry
            }
            //TODO pass backStack to TopBar
            val topBarTitle = remember (backStackEntryState.value){
                if (navController.currentDestination?.route == MyScreen.CATEGORY_GALLERY.route)
                    myViewModel.selectedCategoryStateFlow?.value?.name.orEmpty()
                else
                    getString(R.string.app_name)
            }

            var mustShowAddCategoryDialog by remember { mutableStateOf(false) }
            val actionModeEnabled = remember { mutableStateOf (false) }

            ClothyTheme {
                //Scaffold (
                BottomSheetScaffold (
                    scaffoldState = scaffoldState,
                    topBar =
                    {
                        Box{
                            if (showTopBar.value)
                                MyTopBar(title = topBarTitle,
                                    canNavigateUp = previousBackStackEntry != null,
                                    actionModeEnabled = actionModeEnabled.value,
                                    onNavigateUp = {
                                        if (actionModeEnabled.value) {
                                            myViewModel.unselectAllItemsInActionMode()
                                            actionModeEnabled.value = false
                                        } else
                                            navController.navigateUp()
                                    },
                                    currentScreenRoute = backStackEntryState.value?.destination?.route.orEmpty(),
                                    onOpenCamera = { navController.navigate(MyScreen.MyCamera.route) },
                                    onAddCategory = { mustShowAddCategoryDialog = true },
                                    onImportImageFromDevice = { galleryLauncher.launch(arrayOf("image/*")) },
                                    onDeleteSelectedCategory = { myViewModel.deleteSelectedCategory() },
                                    onDeleteSelectedItem = {
                                        myViewModel.deleteSelectedItems()
                                        actionModeEnabled.value = false
                                    })
                        }
                    },

                    sheetContent = {
                        Box {
                            ItemBottomSheet(item = myViewModel.selectedItemToShow)
                        }
                    },
                    sheetPeekHeight = if (showBottomSheet.value) 92.dp else 0.dp
                )
                { innerPadding ->
                    Box(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()) {
                        MyNavigation(
                            navController = navController,
                            myViewModel = myViewModel,
                            actionModeEnabled = actionModeEnabled,
                            showTopBar=showTopBar,
                            bottomSheetState = scaffoldState.bottomSheetState,
                            showBottomSheet = showBottomSheet)
                    }
                    if (mustShowAddCategoryDialog)
                        NewCategoryDialog(
                            onDismissRequest = { mustShowAddCategoryDialog = false},
                            onSaveRequest = {
                                myViewModel.addNewCategory(it)
                                mustShowAddCategoryDialog = false} )
                }
            }
        }
    }
}