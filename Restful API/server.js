console.log('Hello World')
// npm init -y - package.json
// npm i --save express - install express
// npm i pg - install postgres

const express = require('express');
const userRoutes = require('./src/user/routes');

const app = express();
const port = 3000;

//add a middleware
app.use(express.json());

app.get('/', (req,res) =>{
    res.send("Hello World!");
});

app.use('/api/v1/users', userRoutes);

app.listen(port, () => console.log('App Listening on port' + port));