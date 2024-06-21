package ae.giftery.api.v2.client.model.currencies

import java.math.BigDecimal

data class ExchangeRateDto(
    var currencyFrom: String,
    var currencyTo: String,
    var rate: BigDecimal? = null,
)