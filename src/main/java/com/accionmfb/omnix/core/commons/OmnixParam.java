package com.accionmfb.omnix.core.commons;

import com.accionmfb.omnix.core.annotation.RequiredOmnixParam;

@RequiredOmnixParam
public enum OmnixParam {
    OMNIX_ENCRYPTION_ALGORITHM,
    APP_USER_TOKEN_EXPIRATION_IN_HR,
    ID_TOKEN_EXPIRATION_IN_MIN,
    ADMIN_USER_EXPIRATION_IN_MIN,
}
