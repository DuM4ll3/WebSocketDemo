package ferraz.trade.app.viewModel

import com.tinder.scarlet.WebSocket
import ferraz.trade.app.api.TradeService
import ferraz.trade.app.api.model.Stock
import ferraz.trade.app.api.model.Subscribe
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

interface ViewModel {
    fun observeStream(): Flowable<List<Stock>>
    fun add(subscribe: Subscribe)
}

class MainViewModel(private val tradeService: TradeService = TradeService.create()) : ViewModel {

    private val disposable = CompositeDisposable()

    init {
        val BASF = Subscribe("DE000BASF111")
        val BAYER = Subscribe("DE000BAY0017")

        tradeService.observeWebSocketEvent()
            .filter { it is WebSocket.Event.OnConnectionOpened<*> }
            .subscribe {
                tradeService.sendSubscribe(BASF)
                tradeService.sendSubscribe(BAYER)
            }
            .addTo(disposable)
    }

    override fun add(subscribe: Subscribe) {
        tradeService.sendSubscribe(subscribe)
    }

    override fun observeStream(): Flowable<List<Stock>> {
        return tradeService.observeStock()
            .subscribeOn(Schedulers.computation())
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .distinctUntilChanged { t1, t2 -> t1.isin == t2.isin }
            .buffer(2)
            .observeOn(AndroidSchedulers.mainThread())
    }
}