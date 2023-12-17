var today = new Date(); 
var validRegexExp = new RegExp(/^\d{4}-\d{2}$/);
const checkExp= function(objectExp) {
    if(!objectExp.match(validRegexExp)) {
        return false; 
    }
    else if(objectExp.slice(0, 4) < today.getFullYear()) {
        return false;
    }
    else if(objectExp.slice(0, 4) == today.getFullYear()) {
        if(objectExp.slice(5, 7) - 1 < today.getMonth()) {
            return false; 
        }
        else {
            return true; 
        }
    }
    else {
        return true; 
    }
};

module.exports = checkExp; 