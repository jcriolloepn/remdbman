package com.criollojose.remdbman.beans

data class MysqlConnectionMap (
    var conections:Map<String,MysqlConfig>
    ){
    constructor():this (mapOf<String,MysqlConfig>("" to MysqlConfig("","","","","")))
}