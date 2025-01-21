package com.example.shoe

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoe.model.CartItem

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val listener: CartItemListener? = null
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    interface CartItemListener {
        fun onQuantityChanged(item: CartItem, newQuantity: Int)
        fun onRemoveItem(item: CartItem)
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productTitle)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productSize: TextView = itemView.findViewById(R.id.productSize)
        val productColor: TextView = itemView.findViewById(R.id.productColor)
        val productQuantity: EditText = itemView.findViewById(R.id.productQuantity)
        val btnIncrease: ImageButton = itemView.findViewById(R.id.btnIncrease)
        val btnDecrease: ImageButton = itemView.findViewById(R.id.btnDecrease)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        holder.productName.text = item.name
        holder.productPrice.text = "Giá: ${item.price.toVND()}"
        holder.productSize.text = "Size: ${item.size}"
        holder.productColor.text = "Màu: ${item.color}"
        holder.productQuantity.setText(item.quantity.toString())

        holder.productImage.setImageResource(item.imageResId)

        holder.btnIncrease.setOnClickListener {
            item.quantity++
            holder.productQuantity.setText(item.quantity.toString())
            listener?.onQuantityChanged(item, item.quantity)
        }

        holder.btnDecrease.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                holder.productQuantity.setText(item.quantity.toString())
                listener?.onQuantityChanged(item, item.quantity)
            }
        }

        holder.btnRemove.setOnClickListener {
            listener?.onRemoveItem(item)
            val positionToRemove = cartItems.indexOf(item)
            cartItems.removeAt(positionToRemove)
            notifyItemRemoved(positionToRemove)
            notifyItemRangeChanged(positionToRemove, cartItems.size)
        }
    }

    override fun getItemCount(): Int = cartItems.size
}
