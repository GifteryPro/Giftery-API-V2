package ae.giftery.api.v2.client.constants

enum class FieldValidationCode {
    MISSING,
    REDUNDANT,
    EMPTY,
    NOT_A_NUMBER,
    OUT_OF_RANGE,
    STEP_ERROR,
    REGEX_ERROR,
    LENGTH_ERROR,
}