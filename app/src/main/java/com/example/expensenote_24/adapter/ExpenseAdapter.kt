package com.example.expensenote_24.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.expensenote_24.R
import com.example.expensenote_24.databinding.ItemExpenseBinding
import com.example.expensenote_24.model.ExpenseItem
import java.text.DecimalFormat

class ExpenseAdapter(
    private var items: List<ExpenseItem>,
    private val onDeleteClick: (ExpenseItem) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private val decimalFormat = DecimalFormat("#,##0.00")

    fun updateData(newItems: List<ExpenseItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ExpenseItem) {
            binding.tvTitle.text = item.title
            val subtitle = "${item.category} • ${item.date}"
            binding.tvCategoryDate.text = subtitle

            if (item.note.isNotBlank()) {
                binding.tvNote.visibility = View.VISIBLE
                binding.tvNote.text = item.note
            } else {
                binding.tvNote.visibility = View.GONE
            }

            val context = binding.root.context
            val formattedAmount = decimalFormat.format(item.amount)

            if (item.type == ExpenseItem.TYPE_INCOME) {
                binding.tvAmount.text = "+฿$formattedAmount"
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.income_green))
            } else {
                binding.tvAmount.text = "-฿$formattedAmount"
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.expense_red))
            }

            // Set Category Icon
            val iconRes = when (item.category) {
                "อาหาร/เครื่องดื่ม" -> R.drawable.ic_food
                "เดินทาง/น้ำมัน" -> R.drawable.ic_transport
                "ช้อปปิ้ง" -> R.drawable.ic_shopping
                "บันเทิง/พักผ่อน" -> R.drawable.ic_entertainment
                "ค่าน้ำ/ค่าไฟ/บิล" -> R.drawable.ic_bills
                "เงินเดือน/โบนัส" -> R.drawable.ic_salary
                else -> R.drawable.ic_other
            }
            binding.imgCategory.setImageResource(iconRes)

            binding.btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }
}
