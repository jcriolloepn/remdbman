package com.criollojose.remdbman.beans

data class MysqlConfig (var host:String,
                   var user:String,
                   var password:String,
                   var database:String,
                   var port:String="3306",
                   var supportBigNumbers:Boolean=true,
                   var bigNumberStrings:Boolean=true,){
    constructor():this("","","","","")
}