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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
import java.io.File
import java.util.stream.Collectors

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var showTopAndStatusBar = mutableStateOf(true)
    private lateinit var windowInsetsController : WindowInsetsControllerCompat

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

        //system bars
        windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            val navController = rememberNavController()
            val myViewModel: HomeViewModel = hiltViewModel()
            val backStackEntryState = navController.currentBackStackEntryAsState()

            val scaffoldState = rememberBottomSheetScaffoldState()
            val showBottomSheet = remember { mutableStateOf(false) }

            val galleryLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument(),
                onResult = { uri ->
                    if ( uri != null ) {
                        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        val fileInputStream = contentResolver.openInputStream(uri)
                        val mimeType = contentResolver.getType(uri)?.split("/") ?: listOf("","")
                        if (mimeType[0] != "image")
                            return@rememberLauncherForActivityResult
                        val fileExtension = mimeType[1]
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
                            if (showTopAndStatusBar.value) {
                                windowInsetsController.show(WindowInsetsCompat.Type.statusBars())
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
                                    onDeleteSelectedCategory = {
                                        return@MyTopBar myViewModel.deleteSelectedCategory()
                                                               },
                                    onDeleteSelectedItem = {
                                        myViewModel.deleteSelectedItems()
                                        actionModeEnabled.value = false
                                    },
                                    onShareSelectedItem = {
                                        val selectedItemUri = myViewModel.getSelectedItemsFilePathAndUnselect()
                                        shareSelectedItems(selectedItemUri)
                                    }
                                )
                            } else
                                windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
                        }
                    },

                    sheetContent = {
                        Box {
                            ItemBottomSheet(
                                item = myViewModel.selectedItemToShow.collectAsState().value,
                                onSaveChangesToDB = { item -> myViewModel.saveItemChanges(item) },
                                onDeleteSelectedItem = {
                                    myViewModel.selectedItemToShow.value?.let {
                                        myViewModel.unselectAllItemsInActionMode()
                                        myViewModel.selectItemInActionMode(it)
                                        myViewModel.deleteSelectedItems()
                                    }
                                }
                            )
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
                            showTopBar=showTopAndStatusBar,
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

    override fun onResume() {
        super.onResume()
        if (! showTopAndStatusBar.value)
            windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
    }

    private fun shareSelectedItems( selectedFilesPath : List<String> ) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.setType("image/*")
        val urisList = selectedFilesPath
            .stream()
            .map { path -> FileProvider.getUriForFile(this, "com.mego.clothy.fileProvider", File(path) ) }
            .collect(Collectors.toList())

        intent.putExtra(Intent.EXTRA_STREAM, ArrayList(urisList) )
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity( Intent.createChooser(intent, getString(R.string.share_image)) )
    }
}