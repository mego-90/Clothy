package com.mego.clothy.ui.compose.itemDialog

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.mego.clothy.R
import com.mego.clothy.domain.Formality
import com.mego.clothy.domain.Item
import com.mego.clothy.domain.Weather
import com.mego.clothy.ui.compose.appBars.DeleteConfirmationDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailsScreen(
    items:List<Item>,
    selectedItem: Item?,
    onBackIconClick: () -> Unit,
    onChangeShownItem : (Item) -> Unit,
    bottomSheetState : SheetState) {

    Column{
        ImageBoxSection(
            items = items,
            onBackIconClick = onBackIconClick,
            onChangeShownItem = onChangeShownItem,
            selectedItem = selectedItem,
            bottomSheetState = bottomSheetState
            )
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun ImageBoxSection(
    items:List<Item>,
    onBackIconClick:()->Unit,
    onChangeShownItem : (Item)->Unit,
    selectedItem: Item?,
    bottomSheetState : SheetState) {

    val coroutineScope = rememberCoroutineScope()
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var maxOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var updatedSize by remember { mutableStateOf(IntSize.Zero) }
    val pagerState = rememberPagerState(
        pageCount = { items.size },
        initialPage = if (selectedItem != null) items.indexOf(selectedItem).coerceAtLeast(0) else 0
    )
    var scrollingAllowed by remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = pagerState.currentPage) {
        bottomSheetState.partialExpand()
        onChangeShownItem(items[pagerState.currentPage])
        focusManager.clearFocus()
    }

    Box (modifier = Modifier
        .fillMaxWidth()
    ){
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = scale == 1f && scrollingAllowed,
            contentPadding = PaddingValues(horizontal = 8.dp),
            verticalAlignment = Alignment.Top,
            pageSpacing = 8.dp,
            modifier = Modifier
                .transformable(
                    state = rememberTransformableState(onTransformation = { zoom, pan, _ ->

                        scale *= zoom
                        scale = scale.coerceIn(1f, 2f)

                        // Update the offset to implement panning when zoomed.
                        offset =
                            if (scale == 1f)
                                Offset(0f, 0f)
                            else
                                Offset(
                                    (offset.x + pan.x).coerceIn(
                                        -maxOffset.x,
                                        maxOffset.x
                                    ),
                                    (offset.y + pan.y)
                                        .coerceIn(-maxOffset.y, maxOffset.y)
                                )
                    }
                    ),
                    canPan = { scale > 1f },
                    enabled = true,
                )
            //key = { /*TODO*/ },

        ) { page ->
            Box(modifier = Modifier) {
                Card {
                    GlideImage(
                        model = items[page].imagePath,
                        contentScale = ContentScale.Crop,
                        contentDescription = "",
                        modifier = Modifier
                            .pointerInteropFilter(onTouchEvent = { event ->
                                if (event.pointerCount > 1) {
                                    //to make it stop on center of image
                                    scrollingAllowed = false
                                    scrollingAllowed = true
                                }
                                false
                            }
                            )

                            .onSizeChanged {
                                updatedSize = it
                                maxOffset =
                                    Offset(it.center.x.toFloat() / 4, it.center.y.toFloat() / 4)
                            }
                            .combinedClickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,//rememberRipple(),
                                onClick = {
                                    if (bottomSheetState.currentValue == SheetValue.Expanded)
                                        coroutineScope.launch {
                                            bottomSheetState.partialExpand()
                                        }
                                },
                                onDoubleClick = {
                                    scale = 1f
                                    offset = Offset(0f, 0f)
                                }
                            )
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                            .fillMaxWidth()
                    )
                }
                //Back Button
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                    onClick = { onBackIconClick() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemBottomSheet(
    item:Item?,
    onSaveChangesToDB:(Item)->Unit,
    onDeleteSelectedItem:()->Unit ) {

    if (item==null)
        return

    //weather
    val selectedWeather = Weather.valueOf(item.suitableWeather)
    var weatherOptionsExpended by remember(item) { mutableStateOf(false) }

    //formality
    val selectedFormality = Formality.valueOf(item.formality)
    var formalityOptionsExpended by remember(item) { mutableStateOf(false) }

    Box {
        Column {
            //Upper Row
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ){

                //weather
                ExposedDropdownMenuBox(
                    expanded = weatherOptionsExpended,
                    onExpandedChange = { weatherOptionsExpended = !weatherOptionsExpended })
                {
                    IconButton(
                        modifier = Modifier
                            .menuAnchor()
                            .size(32.dp),
                        onClick = { weatherOptionsExpended = true }) {
                        Icon(
                            imageVector = selectedWeather.icon,
                            contentDescription = "",
                            tint= MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = weatherOptionsExpended,
                        onDismissRequest = { weatherOptionsExpended = false }) {
                        Weather.entries.forEach {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = it.titleRes)) },
                                modifier = if (selectedWeather == it) Modifier.background(
                                    MaterialTheme.colorScheme.background
                                ) else Modifier,
                                leadingIcon = {
                                    Icon(
                                        imageVector = it.icon,
                                        contentDescription = "",
                                        tint= MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    item.suitableWeather = it.name
                                    weatherOptionsExpended= false
                                    onSaveChangesToDB(item)
                                })
                        }
                    }
                }

                //formality
                ExposedDropdownMenuBox(
                    expanded = formalityOptionsExpended,
                    onExpandedChange = { formalityOptionsExpended = !formalityOptionsExpended })
                {
                    IconButton(
                        modifier = Modifier
                            .menuAnchor()
                            .size(32.dp),
                        onClick = { formalityOptionsExpended = true }) {
                        Icon(
                            imageVector = selectedFormality.icon,
                            contentDescription = "",
                            tint= MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = formalityOptionsExpended,
                        onDismissRequest = { formalityOptionsExpended = false }) {
                        Formality.entries.forEach {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = it.titleRes)) },
                                modifier = if (selectedFormality == it) Modifier.background(
                                    MaterialTheme.colorScheme.background
                                ) else Modifier,
                                leadingIcon = {
                                    Icon(
                                        imageVector = it.icon,
                                        contentDescription = "",
                                        tint= MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    item.formality = it.name
                                    formalityOptionsExpended=false
                                    onSaveChangesToDB(item)
                                })
                        }
                    }
                }

                // Delete
                var showDeleteItemsConfirmationDialog by remember { mutableStateOf(false) }
                IconButton(
                    modifier = Modifier.size(32.dp),
                    onClick = { showDeleteItemsConfirmationDialog = true }) {
                    Icon (
                        imageVector = Icons.Default.Delete,
                        contentDescription = "",
                        tint= MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp))
                }

                if (showDeleteItemsConfirmationDialog)
                    DeleteConfirmationDialog(
                        dialogText = stringResource( R.string.delete_selected_items_dialog_text),
                        onCancel = { showDeleteItemsConfirmationDialog = false },
                        onConfirm = {
                            onDeleteSelectedItem()
                            showDeleteItemsConfirmationDialog = false
                        }
                    )

                //Like
                var itemLiked by remember(item) { mutableStateOf(item.liked) }
                IconButton(
                    modifier=Modifier.size(32.dp) ,
                    onClick = {
                        itemLiked = ! itemLiked
                        item.liked = itemLiked
                        onSaveChangesToDB(item)
                    }) {
                    if (itemLiked)
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "",
                            tint= MaterialTheme.colorScheme.primary
                        )
                    else
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "",
                            tint = Color.Red
                        )
                }

                //Share Button
                val context = LocalContext.current
                IconButton(
                    modifier=Modifier.size(32.dp) ,
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.setType("image/*")
                        val fileToShare = File(item.imagePath)
                        val fileUri = FileProvider.getUriForFile(context, "com.mego.clothy.fileProvider", fileToShare )
                        intent.putExtra(Intent.EXTRA_STREAM, fileUri )
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        context.startActivity( Intent.createChooser(intent, context.getString(R.string.share_image)) )
                    }
                )
                {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "",
                        tint= MaterialTheme.colorScheme.primary
                    )
                }
            }

            // expandable section
            var inputChanges by remember(item) { mutableStateOf(false) }
            val focusManager = LocalFocusManager.current

            //brand
            var brandInput by remember(item) { mutableStateOf(item.brand) }
            var brandInEditState by remember(item) { mutableStateOf(false) }
            val brandFocusRequester = FocusRequester()
            OutlinedTextField(
                modifier= Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .focusRequester(brandFocusRequester)
                    .onFocusChanged { focus -> brandInEditState = focus.isFocused },
                value = TextFieldValue(text = brandInput, selection = TextRange(brandInput.length)),
                onValueChange = {
                    brandInput=it.text
                    item.brand=brandInput
                    inputChanges = true
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                label = { Text(stringResource(id = R.string.brand) ) },
                singleLine = true,
                placeholder = { Text(stringResource(id = R.string.enter_brand) ) },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (brandInEditState)
                                focusManager.clearFocus()
                            else
                                brandFocusRequester.requestFocus() }
                    ) {
                        val icon = if (brandInEditState) Icons.Default.Check else Icons.Default.Edit
                        Icon(imageVector = icon, contentDescription = "")
                    }
                }
            )

            //notes
            var notesInput by remember(item) { mutableStateOf(item.notes) }
            var notesInEditState by remember(item) { mutableStateOf(false) }
            val notesFocusRequester = FocusRequester()
            OutlinedTextField(
                modifier= Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .focusRequester(notesFocusRequester)
                    .onFocusChanged { focus -> notesInEditState = focus.isFocused } ,
                value = TextFieldValue(text = notesInput, selection = TextRange(notesInput.length)),
                onValueChange = {
                    notesInput=it.text
                    item.notes = notesInput
                    inputChanges = true
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                label = { Text(stringResource(id = R.string.notes) ) },
                minLines = 3,
                placeholder = { Text(stringResource(id = R.string.enter_notes) ) },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (notesInEditState)
                                focusManager.clearFocus()
                            else
                                notesFocusRequester.requestFocus()
                        }
                    ) {
                        val icon = if (notesInEditState) Icons.Default.Check else Icons.Default.Edit
                        Icon(imageVector = icon, contentDescription = "")
                    }
                }
            )

            //Save Changes of Brand Or Notes to DB after delay
            if (inputChanges){
                LaunchedEffect(item.brand, item.notes) {
                    delay(400)
                    onSaveChangesToDB(item)
                    inputChanges = false
                }
            }
        }
    }
}