package com.example.shoppinglist.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputLayout

class ShopItemFragment: Fragment() {

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener
    private lateinit var viewModel: ShopItemViewModel
    private lateinit var tilName: TextInputLayout
    private lateinit var tilCount: TextInputLayout
    private lateinit var edName: EditText
    private lateinit var edCount: EditText
    private lateinit var btSave: Button

    private var screenMode: String = MODE_UNKNOWN
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    //Проверка на реализацию интерфейса в активити
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener){
            onEditingFinishedListener = context
        }
        else{
            throw RuntimeException("Activity mast implement OnEditingFinishedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_item,container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        initViews(view)
        addNameChangedListener()
        addCountChangedListener()
        launchRightScreenMode()
        observeErrorInputCount()
        observeErrorInputName()
        observeShouldCloseScreen()
    }

    private fun observeShouldCloseScreen(){
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner){
            onEditingFinishedListener.onEditingFinished()
        }
    }

    private fun  observeErrorInputCount(){
        viewModel.errorInputCount.observe(viewLifecycleOwner){
            val message =
                if (it){
                    getString(R.string.error_input_count)
                }
                else{
                    null
                }
            tilCount.error = message
        }
    }

    private fun observeErrorInputName(){
        viewModel.errorInputName.observe(viewLifecycleOwner){
            val message =
                if (it){
                    getString(R.string.error_input_name)
                }
                else{
                    null
                }
            tilName.error = message
        }
    }

    private fun launchRightScreenMode(){
        when(screenMode){
            MODE_EDIT -> launchEditMode()
            MODE_ADD -> launchAddMode()
        }
    }

    private fun addNameChangedListener(){
        edName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun addCountChangedListener(){
        edCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun launchAddMode(){
        btSave.setOnClickListener {
            viewModel.addShopItem(edName.text?.toString(), edCount.text?.toString())
        }
    }

    private fun launchEditMode(){
        viewModel.getShopItem(shopItemId)
        viewModel.shopItem.observe(viewLifecycleOwner){
            edName.setText(it.name)
            edCount.setText(it.count.toString())
        }
        btSave.setOnClickListener {
            viewModel.editShopItem(edName.text?.toString(), edCount.text?.toString())
        }

    }


    //Какой режим нужно открыть(Создание или редактирование)
    private fun parseParams(){
        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)){
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = args.getString(SCREEN_MODE)
        if (mode!= MODE_ADD && mode != MODE_EDIT){
            throw RuntimeException("Unknown screen mode $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT){
            if (!args.containsKey(SHOP_ITEM_ID)){
                throw RuntimeException("Param shopItemId is absent")
            }
        }
        shopItemId = args.getInt(SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
    }

    private fun initViews(view: View){
        tilName = view.findViewById(R.id.tilName)
        tilCount = view.findViewById(R.id.tilCount)
        edName = view.findViewById(R.id.edName)
        edCount = view.findViewById(R.id.edCount)
        btSave = view.findViewById(R.id.btSave)
    }

    //Интерфейс, реализуемый в активити
    interface OnEditingFinishedListener{
        fun onEditingFinished()
    }

    companion object{
        private const val SCREEN_MODE = "extra_mode"
        private const val SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""

        fun newInstanceAddItem(): ShopItemFragment{
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment{
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, shopItemId)
                }
            }
        }
    }
}