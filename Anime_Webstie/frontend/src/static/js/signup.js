import api from './APIClient.js';

let firstName;
let lastName;
let username;
let password;
let avatar = "";

let password2;

// api.getCurrentUser().then(user => {
//     // If user is already authenicated, redirect to discover page.
//     console.log("User authenticated!");
//     document.location = "/discover";
// }).catch(err => {
//     // console.log(err);

//     // Do nothing - allow user to login.
// });

document.addEventListener('DOMContentLoaded', () => {
    let nextButton = document.querySelector('#next');

    nextButton.addEventListener('click', e => {
        console.log(nextButton);

        firstName = document.querySelector("#first-name").value;
        lastName = document.querySelector("#last-name").value;

        console.log("First name", firstName);
        console.log("Last name", lastName);

        let moveOn = validateNames(firstName, lastName);

        console.log("Can we move on?", moveOn);

        // Display picture "page"
        if (moveOn) {
            // Remove elements from previous "page"

            let nameArea = document.querySelector("#name-area");
            nameArea.style.display = "none";

            nextButton.style.display = "none";

            // Display elements for picture "page"

            let pictureArea = document.querySelector("#picture-area");
            pictureArea.style.display = "block";

            let nextButton2 = document.querySelector("#next2");
            nextButton2.style.display = "block";

            // Choose profile picture

            let avatar1 = document.querySelector("#avatar-1");
            let avatar2 = document.querySelector("#avatar-2");
            let avatar3 = document.querySelector("#avatar-3");
            let avatar4 = document.querySelector("#avatar-4");
            let avatar5 = document.querySelector("#avatar-5");
          
            avatar1.addEventListener('click', () => {
                avatar1.style.opacity = "1";
                
                avatar2.style.opacity = "0.5";
                avatar3.style.opacity = "0.5";
                avatar4.style.opacity = "0.5";
                avatar5.style.opacity = "0.5";

                avatar = "avatar1";
            });

            avatar2.addEventListener('click', () => {
                avatar2.style.opacity = "1";
                
                avatar1.style.opacity = "0.5";
                avatar3.style.opacity = "0.5";
                avatar4.style.opacity = "0.5";
                avatar5.style.opacity = "0.5";

                avatar = "avatar2";
            });

            avatar3.addEventListener('click', () => {
                avatar3.style.opacity = "1";
                
                avatar1.style.opacity = "0.5";
                avatar2.style.opacity = "0.5";
                avatar4.style.opacity = "0.5";
                avatar5.style.opacity = "0.5";

                avatar = "avatar3";
            });

            avatar4.addEventListener('click', () => {
                avatar4.style.opacity = "1";
                
                avatar1.style.opacity = "0.5";
                avatar2.style.opacity = "0.5";
                avatar3.style.opacity = "0.5";
                avatar5.style.opacity = "0.5";

                avatar = "avatar4";
            });

            avatar5.addEventListener('click', () => {
                avatar5.style.opacity = "1";
                
                avatar1.style.opacity = "0.5";
                avatar2.style.opacity = "0.5";
                avatar3.style.opacity = "0.5";
                avatar4.style.opacity = "0.5";

                avatar = "avatar5";
            });

            let pictureErrorMsg = document.querySelector("#picture-error");

            nextButton2.addEventListener('click', () => {
                if (avatar != "") {
                    pictureErrorMsg.style.display = "none";

                    pictureArea.style.display = "none";
                    nextButton2.style.display = "none";

                    let credentialsArea = document.querySelector("#credentials-area");
                    credentialsArea.style.display = "block";

                    let submitButton = document.querySelector("#submit");
                    submitButton.style.display = "block";

                    submitButton.addEventListener('click', () => {
                        username = document.querySelector("#username").value;
                        password = document.querySelector("#password").value;
                        password2 = document.querySelector("#repeated-password").value;

                        moveOn = validatePasswords(password, password2);

                        console.log("Can we move on?", moveOn);

                        let usernameErrorMsg = document.querySelector("#username-error");

                        api.signUp(firstName, lastName, username, password, avatar).then(newUser => {
                            
                            console.log(newUser);
                            window.location = "/discover";
                        }).catch(err => { // Check to see if error is thrown for duplicate username
                            console.log(err);
                            usernameErrorMsg.innerText = "That username is taken.";
                            usernameErrorMsg.style.display = "block";
                        });
                    });
                } else {
                    pictureErrorMsg.innerText = "Please select an avatar.";
                    pictureErrorMsg.style.display = "block";
                }
            });            
        }
    });
});

function validateNames(fName, lName) {
    let valid = true;

    let fNameErrorMsg = document.querySelector("#fname-error");
    let lNameErrorMsg = document.querySelector("#lname-error");
    
    // Check if first name exists and follows regex pattern

    if (!fName || fName == "" || fName == null) {
        valid = false;

        fNameErrorMsg.innerText = "Please provide a first name.";
        fNameErrorMsg.style.display = "block";
    } else if (!(/^[a-zA-Z-. ]*$/.test(fName))) { // Valid names include upper or lowercase letters, dashes, periods or spaces
        valid = false;

        fNameErrorMsg.innerText = "First name should consist only of letters, spaces, dashes, and periods.";
        fNameErrorMsg.style.display = "block";
    } else {
        fNameErrorMsg.style.display = "none";
    }

    // Check if last name exists and follows regex pattern

    if (!lName || lName == "" || lName == null) {
        valid = false;

        lNameErrorMsg.innerText = "Please provide a last name.";
        lNameErrorMsg.style.display = "block";
    } else if (!(/^[a-zA-Z-. ]*$/.test(lName))) { // Valid names include upper or lowercase letters, dashes, periods or spaces
        valid = false;

        lNameErrorMsg.innerText = "Last name should consist only of letters, spaces, dashes, and periods.";
        lNameErrorMsg.style.display = "block";
    } else {
        lNameErrorMsg.style.display = "none";
    }

    return valid;
}

function validatePasswords(password1, password2) {
    let valid = true;

    let passwordErrorMsg = document.querySelector("#password-error");

    if (password1 != password2) {
        passwordErrorMsg.innerText = "Passwords must match!";
        passwordErrorMsg.style.display = "block";
        
        valid = false;
    } else {
        passwordErrorMsg.style.display = "none";
    }

    return valid;
}