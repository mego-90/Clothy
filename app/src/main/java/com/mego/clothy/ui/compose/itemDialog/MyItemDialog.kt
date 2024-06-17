package com.mego.clothy.ui.compose.itemDialog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.mego.clothy.domain.Item

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun DisplayImageDialog(
    item: Item,
    onDismiss:()->Unit,
    onEdit:(itemId:Long) ->Unit) {

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var maxOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var updatedSize by remember { mutableStateOf(IntSize.Zero) }

    Dialog(onDismissRequest = { onDismiss()}) {
        Box(modifier =  Modifier.fillMaxWidth(0.95f)){
            Card(modifier = Modifier.fillMaxWidth()) {
                GlideImage(
                    model = item.imagePath,
                    contentScale = ContentScale.FillWidth,
                    contentDescription = "",
                    alignment = Alignment.TopStart,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .layout { measurable, constraints ->
                            val img = measurable.measure(constraints)
                            layout(img.width, img.height) {
                                img.place(0, 0)
                            }
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale *= zoom
                                scale = scale.coerceIn(1f, 2f)
                                // Update the offset to implement panning when zoomed.
                                offset =
                                    if (scale == 1f)
                                        Offset(0f, 0f)
                                    else
                                        Offset(
                                            (offset.x + pan.x).coerceIn(-maxOffset.x, maxOffset.x),
                                            (offset.y + pan.y)
                                                .coerceIn(-maxOffset.y, maxOffset.y)
                                        )

                            }
                        }
                        .onSizeChanged {
                            updatedSize = it
                            //maxOffset = Offset (it.width.times(scale).minus(it.width), it.height.times(scale).minus(it.height))
                            maxOffset = Offset(it.center.x.toFloat() / 4, it.center.y.toFloat() / 4)
                        }
                        .combinedClickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(),
                            onClick = {},
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
                )
            }

            //Close
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                onClick = { onDismiss() }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "", tint = Color.White)
            }

            //Expand
            IconButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = { onEdit(item.id) }) {
                Icon(imageVector = Icons.Default.Fullscreen, contentDescription = "", tint = Color.White)
            }
        }
    }
}