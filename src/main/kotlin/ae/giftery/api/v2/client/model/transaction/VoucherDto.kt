package ae.giftery.api.v2.client.model.transaction

import java.time.OffsetDateTime

data class VoucherDto(
    var pin: String,
    var serialNumber: String? = null,
    var expiration: OffsetDateTime? = null
)
