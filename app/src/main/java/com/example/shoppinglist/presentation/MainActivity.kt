package com.example.shoppinglist.presentation

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var myAdapter: ShopListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.shopList.observe(this){
            myAdapter.submitList(it)
        }
    }
    private fun setupRecyclerView(){
        val rvShopList = findViewById<RecyclerView>(R.id.rv)
        with(rvShopList){
            myAdapter = ShopListAdapter()
            adapter = myAdapter
            //Установка максимальных значений для пуллов viewHolder
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.ENABLED,
                ShopListAdapter.MAX_PULL_SIZE)
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.NOT_ENABLED,
                ShopListAdapter.MAX_PULL_SIZE)
        }
        //Обработки действий с элементами
        setupLongClickListener()
        setupClickListener()
        setupSwipeListener(rvShopList)
    }

    //Удаление при свайпе
    private fun setupSwipeListener(rvShopList: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = myAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteShopItem(item)
            }

        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }

    //Изменение элемента
    private fun setupClickListener() {
        myAdapter.shopItemClick = {
            viewModel.editShopItem(it)
        }
    }

    //Смена состояния элемента
    private fun setupLongClickListener() {
        myAdapter.shopItemLongClick = {
            viewModel.changeStateShopItem(it)
        }
    }

}