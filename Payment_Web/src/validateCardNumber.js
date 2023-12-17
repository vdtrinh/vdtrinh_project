var validRegexVisa = /^4[0-9]{12}(?:[0-9]{3})?$/;
var validRegexMaster = /^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$/;
var validRegexDiscover = /^6(?:011|5[0-9]{2})[0-9]{12}$/;
var validRegexAmerican = /^3[47][0-9]{13}$/;
const checkCardNum = function (objectCardNum, cardType) {
    let result = objectCardNum.replaceAll('-', '');

    if (cardType === 'Visa') {
        if (objectCardNum.slice(0, 1) !== '4') {
            return false;
        }
        else {
            if (!result.match(validRegexVisa)) {
                return false;
            }
            else {
                return true;
            }
        }
    } else if (cardType === 'Master') {
        if (51 <= parseInt(objectCardNum.slice(0, 2)) <= 55) {
            if (result.match(validRegexMaster)) {
                return true;
            }
            else {
                return false;
            }
        } else {
            return false;
        }
    } else if(cardType === 'Discover') {
        if(parseInt(objectCardNum.slice(0, 4)) == 6011) {
            if(result.match(validRegexDiscover)) {
                return true;
            }
            else {
                return false;
            }
        } else {
            return false; 
        }
    } else if(cardType === 'American Express') {
        if(parseInt(objectCardNum.slice(0, 2)) == 37) {
            if(result.match(validRegexAmerican)) {
                return true;
            }
            else{
                return false;
            }
        }
        else {
            return false;
        }
    } else {
        return false;
    }
};
module.exports = checkCardNum;