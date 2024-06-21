package ae.giftery.api.v2.client.model.accounts

import java.math.BigDecimal

class AccountDto(
    var id: Int,
    var balance: BigDecimal,
    var creditLimit: BigDecimal,
    var currency: String
) {
    override fun toString(): String {
        return "AccountDto(id=$id, balance=$balance, creditLimit=$creditLimit, currency='$currency')"
    }
}