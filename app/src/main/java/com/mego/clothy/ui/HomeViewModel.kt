package com.mego.clothy.ui

import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mego.clothy.MyApplication
import com.mego.clothy.data.ItemsRepository
import com.mego.clothy.domain.Category
import com.mego.clothy.domain.Item
import com.mego.clothy.fileNameFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

const val SELECTED_CATEGORY_KEY = "selected_category"

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor (
    private val savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository ) : ViewModel() {

    private val selectedCategoryIDStateFlow : StateFlow<Long?> = savedStateHandle.getStateFlow(SELECTED_CATEGORY_KEY,null)

    lateinit var selectedCategoryStateFlow : StateFlow<Category?>

    var categoriesStateFlow : StateFlow<List<Category>> = MutableStateFlow<List<Category>>(mutableListOf())

    var itemsStateFlow : StateFlow<Map<Long,StateFlow<List<Item>>>> = MutableStateFlow(mutableMapOf<Long,StateFlow<List<Item>>>())

    private val actionModeSelectedItemsList = mutableListOf<Item>()

    var selectedItemToShow : Item? = null

    init {
        viewModelScope.launch {

            categoriesStateFlow = getCategories().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList() )

            selectedCategoryStateFlow = selectedCategoryIDStateFlow
                .mapLatest { categoriesStateFlow.value.firstOrNull { cate -> cate.id == it } }
                .filterNotNull()
                .stateIn(viewModelScope, SharingStarted.Eagerly, null)

            itemsStateFlow = categoriesStateFlow
                .mapLatest { cateList ->
                    val itemsInCate = mutableMapOf< Long,StateFlow<List<Item>> >()
                    cateList.forEach {
                        itemsInCate[it.id] = getItemsOfCategory(it).stateIn(viewModelScope)//(it).first()
                    }
                    itemsInCate
                }
                .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap<Long,StateFlow<List<Item>>>().toMutableMap() )
        }

    }


    fun setSelectedCategoryID(selectedCategoryID : Long ) {
        savedStateHandle[SELECTED_CATEGORY_KEY] = selectedCategoryID
    }

    private fun getCategories() =
        itemsRepository.getAllCategories()

    private fun getItemsOfCategory(category : Category) =
        itemsRepository.getAllItemsInCategory(category)

    fun addNewCategory( category: Category ) {
        viewModelScope.launch {
            itemsRepository.addNewCategory(category)
        }
    }

    fun selectItemInActionMode(item : Item) : Int {
        item.isSelected = true
        actionModeSelectedItemsList.add(item)
        return actionModeSelectedItemsList.size
    }

    fun unselectItemInActionMode(item : Item) : Int {
        item.isSelected = false
        actionModeSelectedItemsList.remove(item)
        return actionModeSelectedItemsList.size
    }

    fun unselectAllItemsInActionMode() {
        actionModeSelectedItemsList.forEach { it.isSelected = false }
        actionModeSelectedItemsList.clear()
    }

    fun deleteSelectedCategory() {
        viewModelScope.launch {
            selectedCategoryStateFlow.value?.let {
                itemsStateFlow.value[it.id]?.value?.forEach { item ->
                    itemsRepository.deleteItem(item)
                }
                itemsRepository.deleteCategory(it)
            }
        }
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            actionModeSelectedItemsList.forEach {item ->
                File(item.imagePath).takeIf { it.isFile }?.delete()
                itemsRepository.deleteItem(item)
            }
            actionModeSelectedItemsList.clear()
        }
    }


    fun addImageDataToDB(tempImageUri: Uri) {
        val imageFile = tempImageUri.toFile()

        viewModelScope.launch{

            imageFile.takeIf { it.exists() }
                ?.apply {

                    selectedCategoryStateFlow?.value?.let {
                    val item =
                        Item(
                            categoryID = it.id,
                            imagePath = imageFile.path,
                            //formality = "",
                            //suitableWeather = "",
                            colorHex = ""
                            //brand = "",
                            //notes = ""
                        )
                    itemsRepository.addItemToCategory(it, item)
                    }
                }
        }

    }

    fun importImageToAppFolder(inputStream:InputStream, fileExtension:String?) {
        val fileName = "${SimpleDateFormat(fileNameFormat, Locale.ENGLISH).format( System.currentTimeMillis() ) }.${fileExtension}"
        val importedFile = File(MyApplication.savedImagesFolderPath, fileName)
        viewModelScope.launch{

            val buffer = ByteArray(1024)
            val fos = FileOutputStream(importedFile)
            while ( inputStream.read(buffer) > 0)
                fos.write(buffer)

            inputStream.close()
            fos.close()

            addImageDataToDB(importedFile.toUri())
        }
    }

}