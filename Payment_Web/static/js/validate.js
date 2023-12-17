var validRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
const jpe = ".jpeg";
const png = ".png";
const gif = ".gif";
const svg = ".svg";
const jpg = ".jpg"

document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');
    var today = new Date();
    const firstName = document.getElementById('fName');
    const lastName = document.getElementById('lName');
    const ima = document.getElementById('previewBut');
    const firstNameRe = document.getElementById('rec-fName');
    const lastNameRe = document.getElementById('rec-lName');
    const emailInput = document.getElementById('emailField');
    const smsInput = document.getElementById('Phone');
    const cardNum = document.getElementById('card');
    const expirationDate = document.getElementById('expirationField');
    const ccv = document.getElementById('CCVField');
    const amount = document.getElementById('amountField');
    const term = document.getElementById('termField');
    const message = document.getElementById('messageBox');
    parseFloat(amount.value).toFixed(2);

    form.addEventListener('submit', (e) => {
        console.log(document.getElementById('previewBut').value);
        if (firstName.value === '' || firstName.value == null) {
            e.preventDefault();
            window.confirm("First name for sender is missing");
        }
        if (lastName.value === '' || lastName.value == null) {
            e.preventDefault();
            window.confirm("Last name for sender is missing");
        }
        if (ima.value === '' || ima.value == null) {
            e.preventDefault();
            window.confirm("Please upload an image");
        }
        else if (ima.value !== '') {
            if ((!ima.value.includes(jpe) || !ima.value.includes(png) || !ima.value.includes(gif) || !ima.value.includes(svg) || !ima.value.includes(jpg)) === false) {
                e.preventDefault();
                window.confirm("Image with .jpeg, .jpg, .png, .gif, .svg are allow");
            }
        }
        if (firstNameRe.value === '' || firstNameRe.value == null) {
            e.preventDefault();
            window.confirm("First name for recipient is missing");
        }
        if (lastNameRe.value === '' || lastNameRe.value == null) {
            e.preventDefault();
            window.confirm("Last name for recipient is missing");
        }
        if (message.value.length < 10) {
            e.preventDefault();
            window.confirm("Please provide message with at least 10 characters long");
        }
        if (document.getElementById('emails').checked) {
            if (!emailInput.value.match(validRegex)) {
                e.preventDefault();
                window.confirm("Please provide a valid email address");
            }
        }
        if (document.getElementById('SMS').checked) {
            if (smsInput.value.length !== 12 || smsInput.value === '' || smsInput == null) {
                e.preventDefault();
                window.confirm("Please provide phone number");
            }
        }
        if (cardNum.value === '' || cardNum.value === null) {
            if (18 < cardNum.value.length > 19) {
                e.preventDefault();
                window.confirm("Your card number is incorrect please check it againt");
            }
            else {
                e.preventDefault();
                window.confirm("Please provide card number");
            }
        }
        if (expirationDate.value === '' || expirationDate.value == null) {
            e.preventDefault();
            window.confirm("Please provide expiration date");
        }
        else if (expirationDate.value.slice(0, 4) < today.getFullYear() || expirationDate.value.slice(0, 4) == today.getFullYear()) {
            if (expirationDate.value.slice(5, 7) - 1 < today.getMonth()) {
                e.preventDefault();
                window.confirm("Your card is expire");
            }
        }
        if (ccv.value === '' || ccv.value < 3 || ccv.value.length > 4) {
            e.preventDefault();
            window.confirm("Please provide CCV");
        }
        if (amount.value === '' || amount.value == null) {
            e.preventDefault();
            window.confirm("Please provide the amount");
        }
        if (term.checked == false) {
            e.preventDefault();
            window.confirm("Please check the Term and Condition box");
        }
    });
});

const previewImage = (event) => {
    const imageFiles = event.target.files;

    const imageFilesLength = imageFiles.length;

    if (imageFilesLength > 0) {
        const imageSrc = URL.createObjectURL(imageFiles[0]);

        const imagePreviewElement = document.querySelector("#preView");

        imagePreviewElement.src = imageSrc;
    }
}

function dash() {
    const phoneNumber = document.getElementById('Phone');

    let str = phoneNumber.value;
    if (str.length === 3) {
        str += "-";
    }
    else if (str.length === 7) {
        str += "-";
    }
    phoneNumber.value = str;

}

function dash_card() {
    const cardNumber = document.getElementById('card'); 

    let cardString = cardNumber.value; 

    if(cardString.length === 4) {
        cardString += "-";
    }
    else if(cardString.length === 9) {
        cardString += "-";
    }
    else if(cardString.length === 14) {
        cardString += "-";
    }

    cardNumber.value = cardString; 
}

