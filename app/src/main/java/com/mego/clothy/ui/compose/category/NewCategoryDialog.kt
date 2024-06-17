package com.mego.clothy.ui.compose.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mego.clothy.R
import com.mego.clothy.domain.Category

const val CATEGORY_NAME_MAX_CHARS = 12

val icons = listOf(
        R.drawable.coat,
        R.drawable.jacket,
        R.drawable.suit,
        R.drawable.dress,
        R.drawable.sweater,
        R.drawable.tshirt_f,
        R.drawable.tshirt_m,
        R.drawable.rich,
        R.drawable.skirt,
        R.drawable.boot,
        R.drawable.shoes_f,
        R.drawable.accessories,
        R.drawable.bag,
        R.drawable.sun_glasses,
        R.drawable.makeup_1,
        R.drawable.makeup_2,
        R.drawable.cap,
        R.drawable.trousers,
        R.drawable.tie
    )

@Composable
fun NewCategoryDialog( onSaveRequest:(category:Category)->Unit, onDismissRequest:()->Unit ) {

    var selectedIcon by remember { mutableIntStateOf(icons[0]) }

    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        var inputtedCategoryName by remember { mutableStateOf("") }
        Card {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.add_new_category),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top=8.dp)
                )

                var isErrorInName by remember { mutableStateOf(false) }
                OutlinedTextField(
                    modifier = Modifier.padding(16.dp),
                    value = inputtedCategoryName,
                    onValueChange = {
                        inputtedCategoryName = it
                        isErrorInName = inputtedCategoryName.isEmpty() || inputtedCategoryName.length>CATEGORY_NAME_MAX_CHARS
                                    },
                    placeholder = { Text(text = stringResource(R.string.insert_category_name)) },
                    singleLine = true,
                    isError = isErrorInName,
                    supportingText = {
                        if (isErrorInName)
                            Text(text = stringResource(R.string.must_be_between_0_and_characters, CATEGORY_NAME_MAX_CHARS))
                                     },
                    trailingIcon = {
                        IconButton(onClick = { inputtedCategoryName = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "")
                        }
                    }
                )

                //Icons
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(3),
                    modifier = Modifier.height(150.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items= icons) {icon ->
                        if (icon != selectedIcon)
                            OutlinedIconButton(
                                onClick = { selectedIcon = icon },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(painter = painterResource(id = icon),
                                    contentDescription = "",
                                    modifier = Modifier.padding(6.dp)
                                    )
                            }
                        else
                            FilledIconButton(
                                onClick = {  },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(painter = painterResource(id = icon),
                                    contentDescription = "",
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                    }

                }
                
                //Bottom Buttons Bar
                Row (
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, bottom = 8.dp)) {

                    TextButton(onClick = { onDismissRequest() }) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            if (inputtedCategoryName.isEmpty() || inputtedCategoryName.length >CATEGORY_NAME_MAX_CHARS)
                                return@TextButton
                            onSaveRequest(Category(inputtedCategoryName,selectedIcon))
                        }
                    ) {
                        Text(stringResource(R.string.add))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun NewCategoryDialogPreview() {
    NewCategoryDialog(onSaveRequest = {}, onDismissRequest = {})
}