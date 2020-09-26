package com.example.madlevel4task1.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madlevel4task1.R
import com.example.madlevel4task1.database.ProductRepository
import com.example.madlevel4task1.models.Product
import kotlinx.android.synthetic.main.fragment_shopping_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ShoppingListFragment : Fragment() {

    private val products = arrayListOf<Product>()
    private val productAdapter = ProductAdapter(products)
    private lateinit var productRepository: ProductRepository
    private val mainScope = CoroutineScope(Dispatchers.Main)

    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productRepository = ProductRepository(requireContext())
        getShoppingListFromDatabase()

        initViews()
    }

    // Setup the view with the recyclerview
    private fun initViews() {

        btn_add_product.setOnClickListener {
            showAddProductdialog()
        }

        viewManager = LinearLayoutManager(activity)
        rvProducts.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
//        createItemTouchHelper().attachToRecyclerView(rvProducts)

        rvProducts.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = productAdapter
        }

    }

    // This method is called when the user clicks on the fab and it opens an Android AlertDialog
    // where the user can enter the product details in the inputs and add the product
    @SuppressLint("InflateParams")
    private fun showAddProductdialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.add_product_dialog_title))
        val dialogLayout = layoutInflater.inflate(R.layout.add_product_dialog, null)
        val productName = dialogLayout.findViewById<EditText>(R.id.et_product_name)
        val amount = dialogLayout.findViewById<EditText>(R.id.et_amount)

        builder.setView(dialogLayout)
        builder.setPositiveButton(R.string.dialog_ok_btn) { _: DialogInterface, _: Int ->
            addProduct(productName, amount)
        }
        builder.show()
    }


    private fun getShoppingListFromDatabase() {
        // Get the products from the database using the product repository and add them to the products array list
        mainScope.launch {
            val shoppingList = withContext(Dispatchers.IO) { // IO dispatcher because we're using database operations
                productRepository.getAllProducts()
            }
            this@ShoppingListFragment.products.clear()
            this@ShoppingListFragment.products.addAll(shoppingList)
            this@ShoppingListFragment.productAdapter.notifyDataSetChanged()
        }
    }

    // Creates a product and adds it to the recyclerview
    private fun addProduct(txtName: EditText, txtAmount: EditText) {
        if (validateFields(txtName, txtAmount)) {
            mainScope.launch {
                val product = Product(
                    name = txtName.text.toString(),
                    amount = txtAmount.text.toString().toInt()
                )

                // Insert the product to the database using the repo
                // Again we use the IO dispatcher for db actions because we don't want to do db actions on the main thread
                withContext(Dispatchers.IO) {
                    productRepository.insertProduct(product)
                }

                getShoppingListFromDatabase()

            }
        }
    }

    /**
     * Create a touch helper to recognize when a user swipes an item from a recycler view.
     * An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
     * and uses callbacks to signal when a user is performing these actions.
     */
//    private fun createItemTouchHelper(): ItemTouchHelper {
//
//        // Callback which is used to create the ItemTouch helper. Only enables left swipe.
//        // Use ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) to also enable right swipe.
//        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//
//            // Enables or Disables the ability to move items up and down.
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                return false
//            }
//
//            // Callback triggered when a user swiped an item.
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val position = viewHolder.adapterPosition
//                val productToDelete = shoppingList[position]
//                mainScope.launch {
//                    withContext(Dispatchers.IO) {
//                        productRepository.deleteProduct(productToDelete)
//                    }
//                    getShoppingListFromDatabase()
//                }
//            }
//        }
//        return ItemTouchHelper(callback)
//    }

    // Validates if the input fields are blank or not
    private fun validateFields(txtName: EditText, txtAmount: EditText) : Boolean {
        if (txtName.text.toString().isBlank() || txtAmount.text.isBlank()) {
            Toast.makeText(activity, "Input field(s) are blank! Please check and try again.", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

}