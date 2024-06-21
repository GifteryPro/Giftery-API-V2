package ae.giftery.api.v2.client.model.auth

import java.util.*


class AuthResultDto(
    var accessToken: String,
    var refreshToken: UUID,
) {
    override fun toString(): String {
        return "AuthResultDto(accessToken='$accessToken', refreshToken=$refreshToken)"
    }
}