const checkName = function(object, object2) {
    let lastNameCheck = object.toLowerCase(); 
    let firstNameCheck = object2.toLowerCase(); 
    if(lastNameCheck == "dent") {
        if(firstNameCheck == "stuart" || firstNameCheck == "stu") {
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

module.exports = checkName; 