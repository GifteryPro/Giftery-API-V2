package ae.giftery.api.v2.client.model

import ae.giftery.api.v2.client.constants.StatusCode

open class ResponseDto<T : Any>(
    var status: StatusCode,
    var data: T? = null,
) {

    override fun toString(): String {
        return "ResponseDto(status=$status, data=$data)"
    }

}