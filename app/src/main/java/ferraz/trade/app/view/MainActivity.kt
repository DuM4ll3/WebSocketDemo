package ferraz.trade.app.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.tinder.scarlet.WebSocket
import ferraz.trade.app.R
import ferraz.trade.app.api.TradeService
import ferraz.trade.app.api.model.Stock
import ferraz.trade.app.api.model.Subscribe
import ferraz.trade.app.model.StockAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView.layoutManager = LinearLayoutManager(this)

        val tradeService = TradeService.create()
        val BASF_SUBSCRIBE = Subscribe("DE000BASF111")

        tradeService.observeWebSocketEvent()
            .filter { it is WebSocket.Event.OnConnectionOpened<*> }
            .subscribe { tradeService.sendSubscribe(BASF_SUBSCRIBE) }
            .addTo(disposable)

        tradeService.observeStock()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe(::handleResult, ::handleError)
            .addTo(disposable)
    }

    private fun handleResult(stock: Stock) {
        val stocks = listOf(stock)
        val adapter = StockAdapter(stocks)
        listView.adapter = adapter

        Log.d("STOCK_OBSERVER", "Stock price is ${stock.price}")
    }

    private fun handleError(error: Throwable) {
        Log.d("ERROR", error.localizedMessage)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
