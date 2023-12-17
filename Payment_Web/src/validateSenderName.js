const checkSenderName = function (objectFirstName, objectLastName) {
    if(objectFirstName === '' || objectFirstName == null) {
        return false; 
    }
    if(objectLastName === '' || objectLastName == null) {
        return false; 
    }
    else{ 
        return true;
    }
};

module.exports = checkSenderName; 