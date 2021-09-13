let mysqlService=require('../services/mysql_operation.service')
let config=require('../config.json')

let objectToExecute={};
objectToExecute.config=config.mysql_database_config
objectToExecute.query='Select * from personas'

// mysqlService.getConection(config.mysql_database_config)
// .then(console.log)
// .catch(console.log)

mysqlService.executeQuery(objectToExecute)
.then(arr=>{
    console.log(arr[0].nombre)
})
.catch(console.log)