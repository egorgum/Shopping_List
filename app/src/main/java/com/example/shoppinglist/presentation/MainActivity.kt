package com.example.shoppinglist.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
class MainActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener {


    private var shopItemContainer: FragmentContainerView? = null //Контейнер фрагмента
    private lateinit var viewModel: MainViewModel
    private lateinit var myAdapter: ShopListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shopItemContainer = findViewById(R.id.shop_item_container)
        setupRecyclerView()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val buttonAddItem = findViewById<FloatingActionButton>(R.id.btAdd)
        //Запуск активтити или фрагмента
        buttonAddItem.setOnClickListener {
            if (isOnePainMode()){
                val intent = ShopItemActivity.newIntentAddItem(this)
                startActivity(intent)
            }
            else{
                launchFragment(ShopItemFragment.newInstanceAddItem())
            }
        }
        viewModel.shopList.observe(this){
            myAdapter.submitList(it)
        }
    }

    //Вертикальный или горизонтальный экран
    private fun isOnePainMode():Boolean{
        return shopItemContainer == null
    }

    private fun launchFragment(fragment: Fragment){
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.shop_item_container, fragment)
            .addToBackStack(null)
            .commit()
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

    //Переход на экран изменений
    private fun setupClickListener() {
        myAdapter.shopItemClick = {
            if (isOnePainMode()){
                val intent = ShopItemActivity.newIntentEditItem(this,it.id)
                startActivity(intent)
            }
            else{
                launchFragment(ShopItemFragment.newInstanceEditItem(it.id))
            }

        }
    }

    //Смена состояния элемента
    private fun setupLongClickListener() {
        myAdapter.shopItemLongClick = {
            viewModel.changeStateShopItem(it)
        }
    }

    //Реализация интерфейса фрагмента
    override fun onEditingFinished() {
        Toast.makeText(this@MainActivity,"Success", Toast.LENGTH_SHORT).show()
        supportFragmentManager.popBackStack()
    }
}