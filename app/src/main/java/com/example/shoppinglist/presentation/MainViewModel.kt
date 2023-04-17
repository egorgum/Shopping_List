package com.example.shoppinglist.presentation

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.*

class MainViewModel: ViewModel() {

    private val repo = ShopListRepositoryImpl

    private val getShopListUseCase = GetShopListUseCase(repo)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repo)
    private  val editShopItemUseCase = EditShopItemUseCase(repo)

    val shopList = getShopListUseCase.getShopList()



    fun deleteShopItem(shopItem: ShopItem){
        deleteShopItemUseCase.deleteShopItem(shopItem)
    }

    fun editShopItem(shopItem: ShopItem){
        Log.d(
            ContentValues.TAG, "" +
                "name - ${shopItem.name}\n" +
                "count - ${shopItem.count}\n" +
                "enabled - ${shopItem.enabled}")
    }

    fun changeStateShopItem(shopItem: ShopItem){
        val newItem = shopItem.copy(enabled = !shopItem.enabled)
        editShopItemUseCase.editShopItem(newItem)
    }


}