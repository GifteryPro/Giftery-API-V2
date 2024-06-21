package ae.giftery.api.v2.client.model.products

import java.math.BigDecimal

data class ProductItemDto(
    var id: Int,
    var name: String,
    var description: String? = null,
    var rrp: BigDecimal,
    var rrpCurrency: String,
    var price: BigDecimal,
    var priceCurrency: String,
    var exchangeRate: BigDecimal? = null,
    var inStock: Long? = null,
)