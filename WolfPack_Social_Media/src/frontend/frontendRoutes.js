const express = require('express'); 
const path = require('path'); 

const frontendRouter = express.Router();

// const html_dir = path.resolve(__dirname + '../../templates/'); 

frontendRouter.get('/', (req, res) => {
    let index = path.join(__dirname, "../../templates/login.html");
    res.sendFile(index); 
}); 

frontendRouter.get('/main', (req,res) => {
    let main = path.join(__dirname, "../../templates/main.html");
    res.sendFile(main); 
});

frontendRouter.get('/profile', (req, res) => {
    let profile = path.join(__dirname, "../../templates/profile.html");
    res.sendFile(profile); 
})

module.exports = frontendRouter; 