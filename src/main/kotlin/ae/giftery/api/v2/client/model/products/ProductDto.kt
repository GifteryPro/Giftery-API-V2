package ae.giftery.api.v2.client.model.products


data class ProductDto(
    var id: Int,
    var type: String,
    var name: String,
    var country: String,
    var brand: String? = null,
    var category: String? = null,
    var description: String? = null,
    var instruction: String? = null,
    var items: List<ProductItemDto> = listOf(),
    var fields: List<FieldDto> = listOf()
)