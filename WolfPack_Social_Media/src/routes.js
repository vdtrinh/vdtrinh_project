
const express = require('express');
const router = express.Router(); 

const apiRouter = require('./api/APIRoutes.js'); 
const frontendRouter = require('./frontend/frontendRoutes.js'); 


router.use(frontendRouter); 
router.use('/api', apiRouter); 



module.exports = router; 