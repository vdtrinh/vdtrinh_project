var validRegexPhone = /^\d\d\d-\d\d\d-\d\d\d\d*$/;
const checkPhone = function(phone) {
    if(!phone.match(validRegexPhone)) {
        return false; 
    }
    else {
        return true; 
    }
}; 

module.exports = checkPhone; 