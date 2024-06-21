package ae.giftery.api.v2.client.model.products


data class FieldDto(
    var type: String,
    var code: String,
    var name: String,
    var description: String? = null,
    var validationRule: String? = null
)