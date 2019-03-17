package ferraz.trade.app.model

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ferraz.trade.app.api.model.Stock
import ferraz.trade.app.databinding.ListItemBinding

class StockAdapter(private  val stocks: List<Stock>): RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemBinding = ListItemBinding.inflate(layoutInflater, parent, false)

        return StockViewHolder(binding)
    }

    override fun getItemCount(): Int = stocks.size
    override fun onBindViewHolder(holder: StockViewHolder, position: Int) =  holder.bind(stocks[position])

    inner class StockViewHolder(private val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Stock) {
            binding.item = data
            binding.executePendingBindings()
        }
    }
}