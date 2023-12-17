const checkCCV = function(objectCCV) {
    if(isNaN(objectCCV)) {
        return false; 
    }
    else if(objectCCV.length < 3 || objectCCV.length > 4) {
        return false; 
    }
    else {
        return true; 
    }
};

module.exports = checkCCV; 