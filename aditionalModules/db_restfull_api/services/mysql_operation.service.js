const path = require('path');
const mysql = require('mysql');
const config = require('../config.json');
const mysqlService = {};
//method for test connection
mysqlService.getConection = (connectionObject) => {
    return new Promise((resolve, reject) => {
        connection = mysql.createConnection(connectionObject);
        connection.connect(function (err) {
            if (err) {
                reject('error connecting: ' + err.stack);
            }else{
                connection.end((error) => {
                    if (error) {
                        reject('error end connection: ' + err.stack);
                    }else{
                        console.log('end conection')
                        resolve('CONECTED');
                    }
                    
                });
            }
        });
    })
}
//method for execute query
/**
 * 
 * @param {*} objectToExecute contains config for database, query
 * @returns 
 */
mysqlService.executeQuery=(objectToExecute)=>{
    return new Promise((resolve, reject) => {
        var connection = mysql.createConnection(objectToExecute.config);
        var error = '';
        var query_str = objectToExecute.query;
        connection.query(query_str, (err, rows) => {
            if (err) {
                error += 'QUERY ERROR: ' + err;
            }
            connection.end((err) => {
                if (err) {
                    error += 'CONNECTION END ERROR' + err;
                }
                if (error.length > 0) {
                    reject(error);
                }
                console.log('coneccion mysql cerrada')
                resolve(rows);
            });
        });
    });
}

module.exports = mysqlService;
