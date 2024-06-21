package ae.giftery.api.v2.client

import ae.giftery.api.v2.client.constants.ProductResponseType
import ae.giftery.api.v2.client.model.ResponseDto
import ae.giftery.api.v2.client.model.accounts.AccountDto
import ae.giftery.api.v2.client.model.auth.AuthResultDto
import ae.giftery.api.v2.client.model.currencies.ExchangeRateDto
import ae.giftery.api.v2.client.model.products.ProductDto
import ae.giftery.api.v2.client.model.transaction.CancellationResultDto
import ae.giftery.api.v2.client.model.transaction.OrderResultApiV2Dto
import ae.giftery.api.v2.client.model.transaction.ReserveRequestDto
import ae.giftery.api.v2.client.model.transaction.ReserveResultDto
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class GifteryV2Connector(
    private val config: GifteryV2Configuration
) {
    private val jsonObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val client: HttpClient = HttpClient
        .newBuilder()
        .build()

    fun auth(): ResponseDto<AuthResultDto> {
        val login = config.login
        val password = config.password
        return send(
            token = null,
            method = "POST",
            path = "/auth",
            digestWithoutTime = login + password,
            body = mapOf(
                "login" to login,
                "password" to password,
            ),
            resultTypeRef = object : TypeReference<ResponseDto<AuthResultDto>>() {})
    }

    fun refreshToken(refreshToken: UUID): ResponseDto<AuthResultDto> {
        return send(
            token = null,
            method = "POST",
            path = "/auth/refresh/$refreshToken",
            digestWithoutTime = "$refreshToken",
            resultTypeRef = object : TypeReference<ResponseDto<AuthResultDto>>() {})
    }

    fun getAccounts(token: String): ResponseDto<List<AccountDto>> {
        return send(
            token = token,
            method = "GET",
            path = "/accounts",
            digestWithoutTime = "",
            resultTypeRef = object : TypeReference<ResponseDto<List<AccountDto>>>() {},
        )
    }

    fun getCurrencyRate(token: String, currencyFrom: String, currencyTo: String): ResponseDto<ExchangeRateDto> {
        return send(
            token = token,
            method = "GET",
            path = "/exchange-rates",
            digestWithoutTime = "",
            resultTypeRef = object : TypeReference<ResponseDto<ExchangeRateDto>>() {},
            params = mapOf(
                "currencyFrom" to currencyFrom,
                "currencyTo" to currencyTo,
            )
        )
    }

    fun getProducts(
        token: String,
        responseType: ProductResponseType = ProductResponseType.SHORT,
        currency: String? = null
    ): ResponseDto<List<ProductDto>> {
        return send(
            token = token,
            method = "GET",
            path = "/products",
            digestWithoutTime = "",
            resultTypeRef = object : TypeReference<ResponseDto<List<ProductDto>>>() {},
            params = mapOf(
                "responseType" to responseType.value,
                "currency" to currency,
            )
        )
    }

    fun getProductById(token: String, productId: Int, currency: String? = null): ResponseDto<ProductDto> {
        return send(
            token = token,
            method = "GET",
            path = "/products/$productId",
            digestWithoutTime = "",
            resultTypeRef = object : TypeReference<ResponseDto<ProductDto>>() {},
            params = mapOf(
                "currency" to currency,
            )
        )
    }

    fun reserve(token: String, reserveRequest: ReserveRequestDto): ResponseDto<ReserveResultDto> {
        val fieldsAsStr = reserveRequest.fields
            ?.map { it.key + "=" + it.value }
            ?.sorted()
            ?.joinToString(",")
            ?: ""
        val digestWithoutTime = "" + reserveRequest.itemId + fieldsAsStr

        return send(
            token = token,
            method = "POST",
            path = "/operations/reserve",
            body = reserveRequest,
            digestWithoutTime = digestWithoutTime,
            resultTypeRef = object : TypeReference<ResponseDto<ReserveResultDto>>() {},
        )
    }

    fun confirm(token: String, transactionUUID: UUID): ResponseDto<OrderResultApiV2Dto> {
        return send(
            token = token,
            method = "POST",
            path = "/operations/$transactionUUID/confirm",
            digestWithoutTime = "" + transactionUUID,
            resultTypeRef = object : TypeReference<ResponseDto<OrderResultApiV2Dto>>() {},
        )
    }

    fun cancel(token: String, transactionUUID: UUID): ResponseDto<CancellationResultDto> {
        return send(
            token = token,
            method = "POST",
            path = "/operations/$transactionUUID/cancel",
            digestWithoutTime = "" + transactionUUID,
            resultTypeRef = object : TypeReference<ResponseDto<CancellationResultDto>>() {},
        )
    }

    fun getStatus(token: String, transactionUUID: UUID): ResponseDto<OrderResultApiV2Dto> {
        return send(
            token = token,
            method = "GET",
            path = "/operations/$transactionUUID",
            digestWithoutTime = "" + transactionUUID,
            resultTypeRef = object : TypeReference<ResponseDto<OrderResultApiV2Dto>>() {},
        )
    }

    private fun <T> send(
        token: String?,
        method: String,
        path: String,
        digestWithoutTime: String = "",
        body: Any? = null,
        resultTypeRef: TypeReference<T>,
        params: Map<String, String?> = mapOf()
    ): T {

        val time = System.currentTimeMillis()
        val signature = makeSign(config.secretKey, "" + time + digestWithoutTime)

        val paramsAsString = params
            .filter { (_, value) -> value != null }
            .map { (key, value) -> key + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8) }
            .joinToString("&", "?")

        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(config.serverUrl + "/api/v2" + path + paramsAsString))
            .method(method, getJsonPublisher(body))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("signature", signature)
            .header("time", "$time")

        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return jsonObjectMapper.readValue(response.body(), resultTypeRef)
    }

    private fun getJsonPublisher(content: Any?): HttpRequest.BodyPublisher? {
        if (content == null) {
            return HttpRequest.BodyPublishers.noBody()
        }
        return HttpRequest.BodyPublishers.ofByteArray(jsonObjectMapper.writeValueAsBytes(content))
    }

    private fun makeSign(secretKey: String, digest: String): String {
        val decodedSecretKey = Base64.getDecoder().decode(secretKey)
        val messageAsByteArray = digest.toByteArray(StandardCharsets.UTF_8)

        val sha256HMAC = Mac.getInstance("HmacSHA256")
        sha256HMAC.init(SecretKeySpec(decodedSecretKey, "HmacSHA256"))
        val bytes = sha256HMAC.doFinal(messageAsByteArray)

        return Base64.getEncoder().encodeToString(bytes)
    }
}