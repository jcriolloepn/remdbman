package com.criollojose.remdbman.utils

import java.util.regex.Pattern

object Constants{
    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    const val PASS_LENGTH=8;
    const val LOGIN_KEY = "LOGIN_KEY"
    const val PASSWORD_KEY = "PASSWORD_KEY"
    const val EXTRA_LOGIN="EXTRA_LOGIN"
    const val URL_MYSQL_DBCLIENT="http://192.168.11.140:3000/mysql"
    const val EXTRA_KEY_MYSQL_CNX_NAME="KEY_MYSQL_CNX_NAME"

}