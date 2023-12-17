const checkAmount = function(objectAmount) {
    if(isNaN(objectAmount)) {
        return false;
    }
    else {
        return true; 
    }
};

module.exports = checkAmount; 