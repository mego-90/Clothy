package com.mego.clothy.ui.compose.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.mego.clothy.domain.Category
import com.mego.clothy.domain.Item
import com.mego.clothy.ui.compose.itemDialog.DisplayImageDialog
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomeScreen(
    categories : List<Category>,
    itemsInCategories:Map<Long, StateFlow<List<Item>>>,
    onSelectCategory: (Long) -> Unit,
    onAddImageToCategory:(Long)-> Unit,
    onEditImage:(Item)->Unit) {

    Column {

        CategoriesTopCircles(categories = categories, onSelectCategory)

        Spacer( modifier = Modifier.height(8.dp) )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { category ->
                item {
                    CategoryRowItems(
                        category = category,
                        listOfItems = itemsInCategories[category.id]?.collectAsState()?.value.orEmpty(),
                        onSelectCategory = onSelectCategory,
                        onAddImageToCategory = onAddImageToCategory,
                        onEditImage = onEditImage
                    )
                }
            }
        }
    }
}


@Composable
fun CategoriesTopCircles(categories: List<Category>, onSelectCategory :(Long) -> Unit) {

    LazyRow {
        items(items = categories) { category ->
            Card (
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke( 1.dp, MaterialTheme.colorScheme.primary ),
                onClick = { onSelectCategory(category.id) },
                modifier = Modifier.padding(start = 12.dp)
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {

                    Icon(
                            painter = painterResource(id = category.iconResourceId),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp).size(48.dp),
                            contentDescription = ""
                        )

                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ItemCard(item:Item, onEditImage:(Item)->Unit) {
    val showImageInDialog = remember { mutableStateOf (false)}

    GlideImage (model = item.imagePath,
        contentDescription = "cloth item",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .size(128.dp)
            .clickable {
                showImageInDialog.value = true
            }
    )

    if (showImageInDialog.value)
        DisplayImageDialog(
            item = item,
            onDismiss = {showImageInDialog.value=false},
            onEdit = {
                onEditImage(item)
                showImageInDialog.value = false
            }
        )
}

@Composable
private fun CategoryRowItems(
    category: Category, listOfItems : List<Item>,
    onSelectCategory :(Long) -> Unit,
    onAddImageToCategory:(Long)-> Unit,
    onEditImage:(Item)->Unit
)
{

    Card(
        colors = CardDefaults.cardColors().copy(contentColor = MaterialTheme.colorScheme.secondary),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .padding(top=16.dp)
            .clickable {
                onSelectCategory(category.id)
            }
    )
    {
        Column() {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top=8.dp) )
            {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "show all",
                    color = Color.Blue,
                    style = MaterialTheme.typography.bodySmall
                )

                Icon(Icons.Default.ChevronRight,
                    contentDescription = "",
                    tint = Color.Blue,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(12.dp) )

            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyRow(Modifier.padding(start=8.dp, bottom=8.dp),horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items = listOfItems, key = {it.id}) { cloth ->
                    ItemCard(cloth, onEditImage)
                }
                item {
                    CaptureItemToCategoryCard(categoryId = category.id, onClick = onAddImageToCategory)
                }
            }
        }
    }
}

@Composable
fun CaptureItemToCategoryCard( categoryId:Long, onClick:(id:Long)->Unit ) {
    Card(
        border = BorderStroke(
            width=1.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .size(128.dp)
            .clickable {
                onClick(categoryId)
            }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }
    }
}