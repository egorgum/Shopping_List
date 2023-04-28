package com.example.shoppinglist.presentation

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

    fun changeStateShopItem(shopItem: ShopItem){
        val newItem = shopItem.copy(enabled = !shopItem.enabled)
        editShopItemUseCase.editShopItem(newItem)
    }


}