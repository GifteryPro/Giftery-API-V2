package ae.giftery.api.v2.client

import ae.giftery.api.v2.client.constants.ProductResponseType
import ae.giftery.api.v2.client.constants.StatusCode
import ae.giftery.api.v2.client.model.transaction.FieldDto
import ae.giftery.api.v2.client.model.transaction.ReserveRequestDto
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun main() {

    val configuration = GifteryV2Configuration(
        serverUrl = "https://api-stg.giftery.pro:7443",
        login = "*******",
        password = "*******",
        secretKey = "*******"
    )

    val gifteryV2Connector = GifteryV2Connector(configuration)

    // Auth
    val auth = gifteryV2Connector.auth()
    if (auth.status != StatusCode.SUCCESS) {
        println("Auth error: $auth")
        return
    } else {
        println("Auth result: $auth")
    }
    println("")

    // Re-auth
    val reAuth = gifteryV2Connector.refreshToken(auth.data!!.refreshToken)
    if (reAuth.status != StatusCode.SUCCESS) {
        println("Re-auth error: $reAuth")
        return
    }else {
        println("Re-auth result: $reAuth")
    }
    println("")

    // Get accounts
    val accountsResponse = gifteryV2Connector.getAccounts(reAuth.data!!.accessToken)
    println("Accounts: $accountsResponse")
    accountsResponse.data!!.forEach {
        println("\t$it")
    }
    println("")

    //Get products
    val productsResponse = gifteryV2Connector.getProducts(
        token = reAuth.data!!.accessToken,
        currency = "AED",
        responseType = ProductResponseType.SHORT
    )
    println("Products: ${productsResponse.status}")
    productsResponse.data!!.forEach {
        println("\t$it")
    }
    println("")

    //Get products by id
    val productByIdResponse = gifteryV2Connector.getProductById(
        token = reAuth.data!!.accessToken,
        productId = 1264,
        currency = "AED",
    )
    val productByIdData = productByIdResponse.data!!
    println("Product #${productByIdData.id}: ${productByIdData.name} ${productByIdData.fields}")
    productByIdData.items.forEach {
        println("\t$it")
    }
    println("")

    //Get exchange rate
    val currencyRateResponse = gifteryV2Connector.getCurrencyRate(
        token = reAuth.data!!.accessToken,
        currencyFrom = "USD",
        currencyTo = "AED",
    )
    println("Exchange rate: $currencyRateResponse")
    println("")

    // Reserve voucher + confirm
    val reserveDtuRequest = ReserveRequestDto(
        clientTime = OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
        itemId = 6409,
        fields = listOf(FieldDto("account_number", "SUCCESS"))
    )
    val reserveDtuResponse = gifteryV2Connector.reserve(reAuth.data!!.accessToken, reserveDtuRequest)
    println("Reserve result: $reserveDtuResponse")

    val confirmDtuResponse = gifteryV2Connector.confirm(reAuth.data!!.accessToken, reserveDtuResponse.data?.transactionUUID!!)
    println("Confirm result: $confirmDtuResponse")

    val dtuStatusResponse = gifteryV2Connector.getStatus(reAuth.data!!.accessToken, reserveDtuResponse.data?.transactionUUID!!)
    println("Status: $dtuStatusResponse")


}