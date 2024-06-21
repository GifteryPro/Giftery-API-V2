package ae.giftery.api.v2.client

data class GifteryV2Configuration(
    var serverUrl : String,
    val login: String,
    val password: String,
    val secretKey: String
)