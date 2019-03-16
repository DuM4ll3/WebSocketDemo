package ferraz.trade.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.tinder.scarlet.WebSocket
import ferraz.trade.app.api.TradeService
import ferraz.trade.app.api.model.Subscribe

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tradeService = TradeService.create()
        val BASF_SUBSCRIBE = Subscribe("DE000BASF111")

        tradeService.observeWebSocketEvent()
            .filter { it is WebSocket.Event.OnConnectionOpened<*> }
            .subscribe { tradeService.sendSubscribe(BASF_SUBSCRIBE) }

        tradeService.observeStock()
            .subscribe({ stock ->
                Log.d("STOCK_OBSERVER", "Stock price is ${stock.price}")
            })
    }
}
