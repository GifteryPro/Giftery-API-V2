package ae.giftery.api.v2.client.model.transaction

import ae.giftery.api.v2.client.constants.FieldValidationCode
import java.math.BigDecimal
import java.util.*

class ReserveResultDto(

    var transactionUUID: UUID? = null,
    var price: BigDecimal? = null,
    var currency: String? = null,
    var fieldsError: Map<String, FieldValidationCode>? = null,
) {

    override fun toString(): String {
        return "ReserveResultDto(transactionUUID=$transactionUUID, price=$price, currency='$currency', fieldsError=$fieldsError)"
    }
}