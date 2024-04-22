
package com.accionmfb.omnix.core.commons;

public interface ResponseCode {
    String SUCCESS = "00";
    String RECORD_ALREADY_EXIST = "02";
    String RECORD_NOT_FOUND = "03";
    String RECORD_NOT_ACTIVE = "04";
    String BAD_VALUE_FORMAT = "07";
    String USER_SESSION_APP_USER_MISMATCH = "93";
    String USER_SESSION_NOT_FOUND = "94";
    String TAMPERED_TOKEN = "95";
    String UNAUTHENTICATED = "95";
    String UNAUTHORIZED = "96";
    String FORBIDDEN_RESOURCE = "97";
    String FORBIDDEN_APP_USER = "98";

    String SUCCESS_CODE="00";
    String RECORD_INUSE_CODE="04";
    String REQUEST_PROCESSING="05";
    String FAILED_TRANSACTION="06";
    String FAILED_MODEL="07";
    String FORMAT_EXCEPTION="08";
    String ID_EXPIRED="09";
    String CUSTOMER_DISABLED="10";
    String PASSWORD_PIN_MISMATCH="11";
    String CUSTOMER_BOARDED="12";
    String CUSTOMER_TIER_REACHED="13";
    String CUSTOMER_NUMBER_MISSING="14";
    String CUSTOMER_NUMBER_MOBILE_MISMATCH="15";
    String MOBILE_NUMBER_CUSTOMER_NAME_MISMATCH="16";
    String ACCOUNT_CUSTOMER_MISMATCH="17";
    String IRREVERSIBLE_TRANSACTION="18";
    String NO_PRIMARY_ACCOUNT="19";
    String INSUFFICIENT_BALANCE="20";
    String NO_CALLBACK_SERVICE="21";
    String SERVICE_UNAVAILABLE="22";
    String CORRUPT_DATA="23";
    String SAME_ACCOUNT="24";
    String ACTIVE_LOAN_EXIST="25";
    String OUT_OF_RANGE="26";
    String NAME_MISMATCH="27";
    String INVALID_TYPE="28";
    String DUPLICATE_CUSTOMER_RECORD="29";
    String OTP_REQUIRED="30";
    String OTP_INVALID="31";
    String NO_ROLE="32";
    String TRANSACTION_BRANCH_MISMATCH="33";
    String LIMIT_EXCEEDED="34";
    String LOAN_DECLINED="35";
    String LOAN_CUSTOMER_MISMATCH="36";
    String DOB_MISMATCH="37";
    String IMEI_MISMATCH="38";
    String INCORRECT_SECURITY_ANSWER = "40";
    String THIRD_PARTY_FAILURE="96";
    String INVALID_CREDENTIALS="97";
    String NOT_LOGGED_IN="98";
    String APP_USER_INACTIVE="95";
    String HTTP_ERROR="94";
    String INTERNAL_SERVER_ERROR="99";
    String BAD_REQUEST="400";
    String REVERSE_CODE="98";
}
