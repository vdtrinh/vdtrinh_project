const checkMessage = function(objectMessage) {
    if(objectMessage.length < 10) {
        return false;
    }
    else {
        return true; 
    }
};

module.exports = checkMessage;