package ferraz.trade.app.api.model

data class Stock (
    val isin: String,
    val price: Double,
    val bid: Double,
    val ask: Double
)