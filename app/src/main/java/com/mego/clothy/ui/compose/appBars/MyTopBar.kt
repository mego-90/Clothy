package com.mego.clothy.ui.compose.appBars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.mego.clothy.R
import com.mego.clothy.ui.navigation.MyScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    title:String,
    currentScreenRoute:String,
    canNavigateUp:Boolean,
    actionModeEnabled:Boolean,
    onNavigateUp:()->Unit,
    onOpenCamera:()->Unit,
    onImportImageFromDevice:()->Unit,
    onAddCategory:()->Unit,
    onDeleteSelectedCategory:()->Boolean,
    onDeleteSelectedItem : ()-> Unit,
    onShareSelectedItem : ()->Unit) {

    var showDeleteItemsConfirmationDialog by remember { mutableStateOf(false) }
    var showDeleteCategoryConfirmation by remember { mutableStateOf(false) }
    var showMoreSettingMenu by remember { mutableStateOf(false) }
    var showDeletionNotAllowedDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = title) },
        actions = {
            // Action Mode
            if (actionModeEnabled) {

                // Share Selected Items
                IconButton(
                    onClick = {
                        onShareSelectedItem()
                    }
                ) {
                    Icon(imageVector = Icons.Default.Share ,contentDescription ="" )
                }

                // Delete Selected Items
                IconButton(
                    onClick = {
                        showDeleteItemsConfirmationDialog = true
                    }
                ) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription ="" )
                }
            }
            // Not in ActionMode
            else {
                if (currentScreenRoute == MyScreen.CATEGORY_GALLERY.route) {
                    //Camera
                    IconButton(onClick = { onOpenCamera() }) {
                        Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = "")
                    }
                    //Import images
                    IconButton(onClick = { onImportImageFromDevice() }) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "")
                    }
                    //More Menu
                    ExposedDropdownMenuBox(expanded = showMoreSettingMenu, onExpandedChange = { showMoreSettingMenu = !showMoreSettingMenu }){
                        
                        IconButton(onClick = { showMoreSettingMenu= true }, modifier = Modifier.menuAnchor() ) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "")
                        }

                        DropdownMenu(
                            expanded = showMoreSettingMenu,
                            onDismissRequest = { showMoreSettingMenu = false })
                        {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(R.string.delete_category)) },
                                onClick = { showDeleteCategoryConfirmation = true },
                                leadingIcon = { Icon( imageVector = Icons.Default.DeleteForever, contentDescription = "" ) }
                            )
                        }
                    }
                }
                if (currentScreenRoute == MyScreen.Home.route)
                    IconButton(onClick = { onAddCategory() }) {
                        Icon(imageVector = Icons.Default.PostAdd, contentDescription = "")
                    }
            }
        },
        navigationIcon = {
            if (canNavigateUp)
                IconButton(onClick = { onNavigateUp() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
            }
        },
    )

    if (showDeleteCategoryConfirmation) {
        DeleteConfirmationDialog(
            dialogText = stringResource(R.string.delete_current_category_dialog_text),
            onCancel = {
                showDeleteCategoryConfirmation = false
                showMoreSettingMenu = false
            },
            onConfirm = {
                val deleted = onDeleteSelectedCategory()
                showDeleteCategoryConfirmation = false
                showMoreSettingMenu = false

                if (deleted)
                    onNavigateUp()
                else
                    showDeletionNotAllowedDialog = true
            }
        )

    }

    if (showDeletionNotAllowedDialog)
        DeleteCategoryIsDisallowed(
            onDismiss = { showDeletionNotAllowedDialog = false }
        )

    if (showDeleteItemsConfirmationDialog)
        DeleteConfirmationDialog(
            dialogText = stringResource( R.string.delete_selected_items_dialog_text),
            onCancel = { showDeleteItemsConfirmationDialog = false },
            onConfirm = {
                onDeleteSelectedItem()
                showDeleteItemsConfirmationDialog = false
            }
        )
}


@Composable
fun DeleteConfirmationDialog(
    dialogText:String,
    onCancel:()->Unit,
    onConfirm:()->Unit) {
    AlertDialog(
        title = { Text(text = stringResource(R.string.delete_confirmation))},
        text = { Text(text = dialogText) },
        icon = { Icon(imageVector = Icons.Default.Delete, contentDescription = "", tint = Color.Red)},
        confirmButton = { TextButton(onClick = { onConfirm() }) { Text(text = stringResource(id = R.string.ok))}},
        dismissButton = {TextButton(onClick = { onCancel() }) { Text(text = stringResource(id = R.string.cancel))}},
        onDismissRequest = { onCancel() }
    )
}

@Composable
fun DeleteCategoryIsDisallowed( onDismiss:()->Unit ) {
    AlertDialog(
        title = { Text(text = stringResource(R.string.deletion_not_allowed))},
        text = { Text(text = stringResource(R.string.deletion_not_allowed_message)) },
        icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "", tint = Color.Red)},
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() },
                content = { Text(text = stringResource(id = R.string.ok)) }
            )
        }
    )
}