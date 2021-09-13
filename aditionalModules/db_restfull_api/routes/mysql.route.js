const express = require('express');
const router=express.Router();
const mysqlController=require('../controllers/mysql.controler')

router.post('/mysql/getConnection',mysqlController.testConnection);
router.post('/mysql/executeQuery',mysqlController.executeQuery);

module.exports = router;