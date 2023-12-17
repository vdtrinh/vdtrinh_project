var validRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;

const checkEmail = function(email) {
    if(!email.match(validRegex)) {
        return false;
    }
    else {
        return true; 
    }
}; 

module.exports = checkEmail; 