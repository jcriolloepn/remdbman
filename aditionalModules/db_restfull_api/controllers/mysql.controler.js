const mysqlService=require('../services/mysql_operation.service')
const mysqlCOntroller={};

mysqlCOntroller.testConnection=(req,res)=>{
    console.log(req.body)
    mysqlService.getConection(req.body)
    .then(_=>res.send({"response":"ok"}))
    .catch(_=>res.send({"response":"error"}))
}

mysqlCOntroller.executeQuery=(req,res)=>{
    let queryObject={}
    queryObject.config={}
    queryObject.config.host=req.body.host
    queryObject.config.user=req.body.user
    queryObject.config.password=req.body.password
    queryObject.config.database=req.body.database
    queryObject.config.port=req.body.port
    queryObject.config.supportBigNumbers=req.body.supportBigNumbers
    queryObject.config.bigNumberStrings=req.body.bigNumberStrings
    queryObject.query=req.body.query

    console.log(queryObject)
    mysqlService.executeQuery(queryObject)
    .then(result=>{
        console.log({"response":result})
        res.send({"response":result})
    })
    .catch(_=>{
        console.log({"response":"error"})
        res.send({"response":"error"})
    })
}
module.exports=mysqlCOntroller