package com.example.shoppinglist.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem

class ShopListAdapter: ListAdapter<ShopItem, ShopItemViewHolder>(ShopItemDiffCallback()) {

    //Нажатие на элемент
    var shopItemLongClick: ((ShopItem) -> Unit)? = null
    //Длинное нажатие на элемент
    var shopItemClick: ((ShopItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        //Выбираем нужный layout
        val layout = when (viewType){
            ENABLED -> R.layout.enabled_element
            NOT_ENABLED -> R.layout.not_enabled_element
            else -> throw RuntimeException("Unknown viewType: $viewType")
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        //Установка текстов и обработка действий
        with(holder){
            val shopItem = getItem(position)
            tvName.text = shopItem.name
            tvCount.text = shopItem.count.toString()

            view.setOnLongClickListener {
                shopItemLongClick?.invoke(shopItem)
                true
            }

            view.setOnClickListener {
                shopItemClick?.invoke(shopItem)
            }
        }
    }

    //Получение типа view для нужного макета
    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.enabled){
            ENABLED
        } else NOT_ENABLED
    }

    companion object{
        //Значения ViewType
        const val ENABLED = 0
        const val NOT_ENABLED = 1
        //Максимальная длинна пула viewHolder
        const val MAX_PULL_SIZE = 5
    }
}