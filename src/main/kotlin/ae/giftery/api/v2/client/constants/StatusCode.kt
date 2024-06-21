package ae.giftery.api.v2.client.constants

enum class StatusCode(
    val code: Int
) {

    SUCCESS(0),
    WAITING_FOR_CONFIRMATION(1),
    IN_PROCESS(2),
    CANCELED(3),
    ERROR(4),
    ALREADY_CONFIRMED(10),
    ALREADY_CANCELED(11),

    NO_VOUCHERS(-1),
    ACCOUNT_NOT_FOUND(-2),
    ACCOUNT_NOT_SPECIFIED(-3),
    ITEM_NOT_SPECIFIED(-4),
    ITEM_NOT_FOUND(-5),
    CURRENCY_RATE_NOT_FOUND(-6),
    NO_MONEY(-7),
    CLIENT_TIME_NOT_SPECIFIED(-8),
    WRONG_QUANTITY(-9),
    WRONG_DATA(-10),
    PROVIDER_ERROR(-11),

    USER_LOCKED(-100),
    BAD_CREDENTIAL(-101),
    TOKEN_EXPIRED(-102),
    TOKEN_MISSING(-103),
    INVALID_TOKEN(-104),
    INVALID_SIGNATURE(-105),
    NOT_FOUND(-106),
    BAD_REQUEST(-107),

    INTERNAL_ERROR(-10000),
}