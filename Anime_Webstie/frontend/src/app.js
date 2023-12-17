const express = require('express');
const app = express();
const PORT = process.env.PORT;

const router = require('./frontend'); 

// Designate the static folder as serving static resources
app.use(express.static(__dirname + '/static/'));

// const frontendRoutes = require('./frontend/src/static/frontend.js'); 

app.use(router); 

// As our server to listen for incoming connections
app.listen(PORT, () => console.log(`Server listening on port: ${PORT}`));