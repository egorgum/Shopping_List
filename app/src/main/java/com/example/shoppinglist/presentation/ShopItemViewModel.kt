package com.example.shoppinglist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.*

class ShopItemViewModel:ViewModel() {
    private val repo = ShopListRepositoryImpl
    private val getShopItemUseCase = GetShopItemUseCase(repo)
    private val addShopItemUseCase = AddShopItemUseCase(repo)
    private val editShopItemUseCase = EditShopItemUseCase(repo)

    //Ошибки в полях
    private val _errorInputName = MutableLiveData<Boolean>()
    val errorInputName:LiveData<Boolean>
        get() = _errorInputName
    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount:LiveData<Boolean>
        get() = _errorInputCount

    //Элемент списка
    private val _shopItem= MutableLiveData<ShopItem>()
    val shopItem:LiveData<ShopItem>
        get() = _shopItem

    //Разрешение закрыть экран
    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen:LiveData<Unit>
        get() =_shouldCloseScreen

    fun getShopItem(id: Int){
       _shopItem.value = getShopItemUseCase.getShopItem(id = id)
    }

    fun addShopItem(inputName: String?, inputCount: String?){
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name,count)
        if (fieldsValid){
            val shopItem = ShopItem(name, count, true)
            addShopItemUseCase.addShopItem(shopItem = shopItem)
            finishScreen()
        }
    }

    fun editShopItem(inputName: String?, inputCount: String?){
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name,count)
        if (fieldsValid){
            _shopItem.value?.let {
                val item = it.copy(name = name, count = count)
                editShopItemUseCase.editShopItem(shopItem = item)
                finishScreen()
            }

        }
    }
    //Правильный формат имени
    private fun parseName(name:String?):String{
        return name?.trim() ?:""
    }
    //Правильный формат количества
    private fun parseCount(count: String?): Int{
        return try {
            count?.trim()?.toInt() ?: 0
        }catch (e: Exception){
            0
        }
    }

    //Правильность введеных значений
    private fun validateInput(name: String, count: Int):Boolean{
        var result = true
        if (name.isBlank()){
            _errorInputName.value = true
            result = false
        }
        if (count == 0){
            _errorInputCount.value = true
            result = false
        }
        return result
    }

     fun resetErrorInputName(){
        _errorInputName.value = false
    }

     fun resetErrorInputCount(){
        _errorInputCount.value = false
    }

    private fun finishScreen(){
        _shouldCloseScreen.value = Unit
    }
}