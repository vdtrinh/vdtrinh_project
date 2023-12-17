const express = require('express'); 
const multer = require('multer'); 


const app = express(); 
const PORT = 80; 

const html_path = __dirname + '/templates/'; 

app.use(express.static('static'));
app.use(express.urlencoded({extended: true}));
app.use(express.json());

app.get('/', (req, res) => {
    res.sendFile(html_path + 'form.html');
});

const checkName = require('./src/validateName.js'); 

const checkCardNum = require('./src/validateCardNumber.js');

const checkExp = require('./src/validateExp.js'); 

const checkCCV = require('./src/validateCCV.js'); 

const checkAmount = require('./src/validateAmount.js'); 

const checkBox = require('./src/validateTerm.js'); 

const checkMessage = require('./src/validateMessage.js');

const checkSenderName = require('./src/validateSenderName');

const checkPhone = require('./src/validatePhone'); 

const checkEmail = require('./src/validateEmail'); 

var storageI = multer.diskStorage({
    destination: 'static/uploads', 
    filename: function(req, file, cb) {
        cb(null, file.originalname); 
    }
});

const upload = multer({storage: storageI});


app.post('/formdata', upload.single('uploadImage'),(req, res) => {
    var nameValidate = checkName(req.body['last-name-rec'], req.body['first-name-rec']);

    var email = checkEmail(req.body['rec-email']); 

    var phone = checkPhone(req.body['rec-phone-num']); 

    var cardValidate = checkCardNum(req.body['Number'], req.body['cards']); 

    var expValidate = checkExp(req.body['exp']); 

    var ccv = checkCCV(req.body['CCV']);

    var amountValidate = checkAmount(req.body['amount']); 

    var term = checkBox(req.body['checkB']);

    var message = checkMessage(req.body['message']);

    var senderName = checkSenderName(req.body['senderFirstName'], req.body['SenderLastName']); 
    
    if(senderName == true && nameValidate == true && message == true && cardValidate == true &&
        expValidate == true && ccv == true && amountValidate == true && term == true) {
            if(req.body['type'] === 'SMS') {
                if(phone == true) {
                    if(req.file !== undefined) {
                        res.sendFile(html_path + 'successForm.html');
                    } else {
                        res.sendFile(html_path + 'errorForm.html');
                    }
                } 
                else {
                    res.sendFile(html_path + 'errorForm.html');
                }
            } else if(req.body['type'] === 'emails') {
                if(email == true) {
                    if(req.file !== undefined) {
                        res.sendFile(html_path + 'successForm.html');
                    } else {
                        res.sendFile(html_path + 'errorForm.html');
                    }
                } else {
                    res.sendFile(html_path + 'errorForm.html');
                }
            } else if(req.body['rec-email'] !== '' && req.body['rec-phone-num'] !== '') {
                if(email == true && phone == true ) {
                    if(req.file !== undefined) {
                        res.sendFile(html_path + 'successForm.html');
                    } else {
                        res.sendFile(html_path + 'errorForm.html');
                    }
                } else {
                    res.sendFile(html_path + 'errorForm.html');
                }
            } else if (req.body['rec-email'] !== '' && req.body['rec-phone-num'] === ''){
                if(email == true) {
                    if(req.file !== undefined) {
                        res.sendFile(html_path + 'successForm.html');
                    } else {
                        res.sendFile(html_path + 'errorForm.html');
                    }
                } else {
                    res.sendFile(html_path + 'errorForm.html');
                }
            } else if(req.body['rec-email'] === '' && req.body['rec-phone-num'] !== '') {
                if(phone == true) {
                    if(req.file !== undefined) {
                        res.sendFile(html_path + 'successForm.html');
                    } else {
                        res.sendFile(html_path + 'errorForm.html');
                    }
                } else {
                    res.sendFile(html_path + 'errorForm.html');
                }
            } else if(req.body['type'] === 'DNN') {
                if(req.file !== undefined) {
                    res.sendFile(html_path + 'successForm.html');
                } else {
                    res.sendFile(html_path + 'errorForm.html');
                }
            }
        } else {
            res.sendFile(html_path + 'errorForm.html');
        }  
});

app.listen(PORT, () => console.log(`Server listening on port: ${PORT}`));