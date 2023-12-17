import api from './APIClient.js';

// api.getCurrentUser().then(user => {
//     // If user is already authenicated, redirect to discover page.
//     console.log("User authenticated!");
//     document.location = "/discover";
// }).catch(err => {
//     // console.log(err);

//     // Do nothing - allow user to login.
// });

const loginButton = document.querySelector('#login-button'); 
const username = document.querySelector('#username');
const password = document.querySelector('#password'); 

loginButton.addEventListener('click', (e) => {
    api.logIn(username.value, password.value).then(userData => {
        document.location = "/discover";
    }).catch((err) => {
        console.log("An error has occurred", err);
    }); 
});