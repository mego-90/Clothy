package com.mego.clothy.ui.compose.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.mego.clothy.domain.Item

@Composable
fun CategoryGalleryScreen(
    items : List<Item>,
    actionModeEnabled:MutableState<Boolean>,
    onSelectItem:(Item)->Int,
    onUnselectItem:(Item)->Int,
    onOpenItem: (Item)-> Unit
) {

    Column {
        CategoryItems(
            categoryItemsList=items,
            actionModeEnabled=actionModeEnabled,
            onSelectItem=onSelectItem,
            onUnselectItem=onUnselectItem,
            onOpenItem = onOpenItem)
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ItemDetailsInCategory(
    item: Item,
    actionModeEnabled: MutableState<Boolean>,
    onSelectItem:(Item)->Int,
    onUnselectItem:(Item)->Int,
    onOpenItem: (Item)-> Unit
) {
    //Key used in Remember because when deleting item it will not update itemChecked because it is remembered
    var itemChecked by remember(item.isSelected) { mutableStateOf( item.isSelected) }

    Box(
        modifier = Modifier.fillMaxHeight().combinedClickable (
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(),
            onClick = {
                      if (actionModeEnabled.value) {
                          val countOfSelectedItems = if (itemChecked) onUnselectItem(item) else onSelectItem(item)
                          itemChecked = item.isSelected
                          if (countOfSelectedItems == 0) actionModeEnabled.value = false
                      } else
                          onOpenItem(item)

            },
            onLongClick = {
                actionModeEnabled.value = true
                onSelectItem(item)
                itemChecked = item.isSelected
            })

    ) {
        Card(modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp) ) {
            Box {
                GlideImage(
                    model = item.imagePath,
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Center)
                )
            }
        }

        if (actionModeEnabled.value){
            Checkbox(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(32.dp),
                checked = itemChecked,
                colors = CheckboxDefaults
                    .colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        checkmarkColor = Color.White,
                        uncheckedColor = Color.White
                    ),
                onCheckedChange = {
                    itemChecked = it
                    item.isSelected = itemChecked
                    if (itemChecked) onSelectItem(item) else onUnselectItem(item)
                }
            )
        }

    }
}

@Composable
private fun CategoryItems(
    categoryItemsList:List<Item>,
    actionModeEnabled:MutableState<Boolean>,
    onSelectItem: (Item) -> Int,
    onUnselectItem: (Item) -> Int,
    onOpenItem: (Item)-> Unit ) {

    LazyVerticalGrid(columns = GridCells.Fixed(3) ) {
        items(items = categoryItemsList) {
            ItemDetailsInCategory(
                item = it,
                actionModeEnabled=actionModeEnabled,
                onSelectItem = onSelectItem,
                onUnselectItem = onUnselectItem,
                onOpenItem = onOpenItem
            )
        }
    }
}