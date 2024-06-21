package ae.giftery.api.v2.client.model.transaction

class ReserveRequestDto(
    var clientTime: String,
    var itemId: Int,
    var accountId: Int? = null,
    var reference: String? = null,
    var fields: List<FieldDto>? = null,
)