package com.example.shoe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoe.model.Product


class ProductsAdapter(
    private val products: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    // ViewHolder class nested inside ProductsAdapter
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.iProduct)
        val productName: TextView = itemView.findViewById(R.id.nProduct)
        val productPrice: TextView = itemView.findViewById(R.id.pProduct)

        // Method to bind product data to the ViewHolder
        fun bind(product: Product) {
            productImage.setImageResource(product.image)
            productName.text = product.name
            // Format and display the product price using toVND()
            productPrice.text = product.price.toVND()

            // Set click listener for the product item
            itemView.setOnClickListener {
                onProductClick(product)
            }
        }
    }

    // Inflate item layout and create ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    // Bind data to the ViewHolder at the given position
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product) // Bind the product data to the holder
    }

    // Return the total count of items
    override fun getItemCount(): Int {
        return products.size
    }
}
