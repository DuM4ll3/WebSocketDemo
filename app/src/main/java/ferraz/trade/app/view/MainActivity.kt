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

    private val adapter = StockAdapter()
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = adapter

        val BASF = Subscribe("DE000BASF111")
        val SIEMENS = Subscribe("DE0007236101")

        val tradeService = TradeService.create()
        tradeService.observeWebSocketEvent()
            .filter { it is WebSocket.Event.OnConnectionOpened<*> }
            .subscribe {
                tradeService.sendSubscribe(BASF)
                tradeService.sendSubscribe(SIEMENS)
            }
            .addTo(disposable)

        tradeService.observeStock()
            .subscribeOn(Schedulers.computation())
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .distinctUntilChanged { t1, t2 -> t1.isin == t2.isin }
            .buffer(2)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleResult, ::handleError)
            .addTo(disposable)
    }

    private fun handleResult(stocks: List<Stock>) {
        adapter.stocks = stocks
    }

    private fun handleError(error: Throwable) {
        Log.d("ERROR", error.localizedMessage)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
