package ae.giftery.api.v2.client.model

import ae.giftery.api.v2.client.constants.StatusCode

open class ResponseDto<T : Any>(
    var status: StatusCode,
    var statusCode: Int,
    var data: T? = null,
    var message: String? = null,
) {

    override fun toString(): String {
        return "ResponseDto(" +
                "status=$status, " +
                "statusCode=$statusCode, " +
                "message=$message, " +
                "data=$data)"
    }

}