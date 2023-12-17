import api from '/js/APIClient.js';


const form = document.querySelector('form');
const input = document.getElementById('username');
form.addEventListener('submit', (e) => {
    api.login(input.value).then(userInfo => {
        if(userInfo !== undefined) {
            console.log(userInfo);

            localStorage.setItem('username', userInfo.username); 
            localStorage.setItem('userId', userInfo.id); 

            document.location = "/main";
        }
        else{
            alert("User doesn't exist"); 
            return; 
        }
    })
});






