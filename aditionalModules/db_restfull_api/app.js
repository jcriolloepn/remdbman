const express=require('express');
const app=express();
const {port}=require('./config.json');
const { use } = require('./routes/mysql.route');
const bodyParser=require('body-parser');

//routes definition
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:false}));
const mysqlRoute=require('./routes/mysql.route')
app.use('/',mysqlRoute)

app.listen(port, () => console.log(`listening on http://localhost:${port}`));
