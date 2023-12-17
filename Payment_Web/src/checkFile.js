const checkPicture = function(picFile) {
    if(picFile == null || picFile == undefined) {
        return false;
    }
    else {
        return true; 
    }
};

module.exports = checkPicture; 