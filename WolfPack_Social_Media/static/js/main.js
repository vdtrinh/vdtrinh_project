import api from '/js/APIClient.js';

let username = localStorage.getItem('username');
let userId = localStorage.getItem('userId');




var currUser;

api.current(username).then(currentUser => {
    const currentUse = document.getElementById('userName');
    currentUse.innerHTML = "@" + currentUser.username;

    const userAvatar = document.getElementById('userAvatar');
    userAvatar.src = currentUser.avatar;

    const linkProfile = document.getElementById('profile');
    const redirectLink = document.createElement('a');
    redirectLink.href = "/profile?id=" + currentUser.id;
    redirectLink.innerHTML = "Profile";
    linkProfile.appendChild(redirectLink);
});

showHowl();

function emptyHowl() {
    const display = document.getElementById('post-content');
    display.innerHTML = "";
}

function showHowl() {
    emptyHowl();
    api.howls(userId).then(howls => {
        const result = document.getElementById('post-content');
        howls.sort((a, b) => {
            return new Date(b.datetime) - new Date(a.datetime);
        });
        howls.forEach(howl => {
            result.append(makeContent(howl));
        });
    });
}


const postButton = document.getElementById('button');
const postContent = document.getElementById('howlMessage');

postButton.addEventListener('click', (e) => {
    let postDate = new Date();
    postDate = postDate.toJSON();

    let postHowl = {
        "userId": parseInt(userId),
        "datetime": postDate,
        "text": postContent.value
    }

    api.postHowl(userId, postHowl).then(postHowl => {
        postContent = "";
        showHowl();
    });
});


function makeContent(howling) {
    const returnItem = document.createElement('div');
    returnItem.classList.add('row');

    const followerContainer = document.createElement('div');
    followerContainer.classList.add('col-2');


    const followerPicture = document.createElement('img');
    followerPicture.src = howling.user.avatar;
    followerPicture.classList.add('rounded-circle');
    followerPicture.setAttribute('id', 'userPic');
    followerContainer.appendChild(followerPicture);


    const followerNameContainer = document.createElement('div');
    followerNameContainer.classList.add('col-2');

    const followerName = document.createElement('p');
    followerName.innerHTML = howling.user.first_name + ' ' + howling.user.last_name;
    followerNameContainer.appendChild(followerName);


    const followerUserNameContainer = document.createElement('div');
    followerUserNameContainer.classList.add('col-2');


    const followerUserName = document.createElement('a');
    followerUserName.href = "/profile?id=" + howling.userId;
    followerUserName.innerHTML = howling.user.username;
    followerUserNameContainer.appendChild(followerUserName);


    const emptyDiv = document.createElement('div');
    emptyDiv.classList.add('col-2');
    const emptyDiv2 = document.createElement('div');
    emptyDiv2.classList.add('col-1');

    const followerDateTimeContainer = document.createElement('div');
    followerDateTimeContainer.classList.add('col');
    followerDateTimeContainer.classList.add('flex-row-reverse');
    followerDateTimeContainer.innerHTML = normalDateTime(howling.datetime);

    const followerHowler = document.createElement('div');
    followerHowler.classList.add('row');

    const followerHowlerContent = document.createElement('p');
    followerHowlerContent.innerHTML = howling.text;
    followerHowler.appendChild(followerHowlerContent);

    const emptyRow = document.createElement('div');
    emptyRow.classList.add('row');
    emptyRow.classList.add('border-bottom');

    returnItem.appendChild(followerContainer);
    returnItem.appendChild(followerNameContainer);
    returnItem.appendChild(followerUserNameContainer);
    returnItem.appendChild(emptyDiv);
    returnItem.appendChild(emptyDiv2);
    returnItem.appendChild(followerDateTimeContainer);
    returnItem.appendChild(followerHowler);
    returnItem.appendChild(emptyRow);

    return returnItem;
};

// https://stackoverflow.com/questions/20518516/how-can-i-get-time-from-iso-8601-time-format-in-javascript
// https://stackoverflow.com/questions/1643320/get-month-name-from-date
function normalDateTime(dateTime) {
    let monthAndDate = new Date(dateTime);
    let date = monthAndDate.getDate();
    let time = new Date(dateTime).toLocaleTimeString("en", { timeStyle: 'short', hour12: true, timeZone: 'UTC' });
    return monthAndDate.toLocaleString("en-US", { month: 'short' }) + " " + date + ", " + time;
}; 
