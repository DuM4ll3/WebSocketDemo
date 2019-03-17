package ferraz.trade.app.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import ferraz.trade.app.R
import ferraz.trade.app.api.model.Stock
import ferraz.trade.app.api.model.Subscribe
import ferraz.trade.app.model.StockAdapter
import ferraz.trade.app.viewModel.MainViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /*
     * Not doing DI here, but in a production app Dagger or other DI framework could be used
     */
    private val viewModel = MainViewModel()
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView.layoutManager = LinearLayoutManager(this)

        /*
         * subscribe to the stream of events
         */
        viewModel.observeStream()
            .subscribe(::handleResult, ::handleError)
            .addTo(disposable)

        /*
         * adds a new stock to the stream
         */
        actionButton.setOnClickListener { addStock() }
    }

    private fun handleResult(stocks: List<Stock>) {
        listView.adapter = StockAdapter(stocks)
    }

    private fun handleError(error: Throwable) {
        Log.d("ERROR", error.localizedMessage)
    }

    private fun addStock() {
        val SIEMENS = Subscribe("DE0007236101")
        viewModel.add(SIEMENS)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
