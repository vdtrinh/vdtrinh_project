import api from '/js/APIClient.js'; 

let currentUser = localStorage.getItem('username'); 
var currentId = localStorage.getItem('userId'); 

api.current(currentUser).then(currentUser => {
    const currentUse = document.getElementById('userName');
    currentUse.innerHTML = "@" + currentUser.username;

    const userAvatar = document.getElementById('userAvatar');
    userAvatar.src = currentUser.avatar;

    const linkProfile = document.getElementById('profile');
    const redirectLink = document.createElement('a');
    redirectLink.href = "/main";
    redirectLink.innerHTML = "Home";
    linkProfile.appendChild(redirectLink);
});

const query = location.search; 
let parameters = new URLSearchParams(query); 
let profileId = parameters.get('id'); 

api.getUserById(profileId).then(profile => {
   const profileContent = document.getElementById('content');
   profileContent.append(displayProfile(profile)); 
});

function displayProfile(profileUser) {
    const outerContainer = document.createElement('div');
    outerContainer.classList.add('row');

    const pictureContainer = document.createElement('div');
    pictureContainer.classList.add('col-2'); 

    const profileAvatar = document.createElement('img'); 
    profileAvatar.src = profileUser.avatar; 
    profileAvatar.classList.add('rounded-circle');
    profileAvatar.setAttribute('id', 'userPic'); 

    pictureContainer.appendChild(profileAvatar); 

    const nameContainer = document.createElement('div');
    nameContainer.classList.add('col-5');

    const userNameProfile = document.createElement('a'); 
    userNameProfile.href = "/profile?id=" + profileUser.id; 
    userNameProfile.innerHTML = "@" + profileUser.username; 

    const nameProfile = document.createElement('h6'); 
    nameProfile.innerHTML = profileUser.first_name + " " + profileUser.last_name; 

    nameContainer.appendChild(userNameProfile);
    nameContainer.appendChild(nameProfile); 

    const followButtonContainer = document.createElement('div');
    followButtonContainer.classList.add('col'); 

    const followButton = document.createElement('button');
    followButton.setAttribute('type', 'button');
    followButton.setAttribute('id', 'follow-but');

    followButtonContainer.appendChild(followButton);

    let userFollow = false; 

    if(profileUser.id == currentId) {
        followButton.style.display = "none";
    } else {
        api.userId(currentId).then(curUserList => {
            curUserList.forEach(followId => {
                if(profileUser.id == followId.id) {
                    userFollow = true; 
                }
            })
            if(userFollow) {
                followButton.innerHTML = "Unfollow";
            }
            else {
                followButton.innerHTML = "Follow";
            }
        })
    }

    followButton.addEventListener('click', (e) => {
        if(userFollow) {
            followButton.innerHTML = "Follow"; 
            userFollow = false; 

            api.getUserById(profileUser.id).then(unfollow => {
                let userUnfollow = unfollow; 
                api.userUnfollow(profileUser.id, currentId); 
                api.userId(currentId)
            })
        }
        else {
            followButton.innerHTML = "Unfollow"; 
            userFollow = true; 

            api.getUserById(profileUser.id).then(following => {
                api.followUser(currentId, following); 
                api.userId(currentId); 
            })
        }
    });

    outerContainer.appendChild(pictureContainer);
    outerContainer.appendChild(nameContainer);
    outerContainer.appendChild(followButtonContainer);

    return outerContainer; 
};

api.userId(profileId).then(profileIdList => {
    const mostOuterContainer = document.getElementById('listOfFollower');
    profileIdList.forEach(profileFollow => {
        mostOuterContainer.append(displayFollowList(profileFollow)); 
    })
});

function displayFollowList(profileFollow) {
    const container = document.createElement('div');

    const spanContainer = document.createElement('span');

    const imgTag = document.createElement('img'); 
    imgTag.src = profileFollow.avatar; 
    imgTag.classList.add('rounded-circle'); 
    imgTag.setAttribute('id', 'userPic');

    const profileFollowUserName = document.createElement('a');
    profileFollowUserName.href = "/profile?id=" + profileFollow.id; 
    profileFollowUserName.setAttribute('id', 'followerGap'); 
    profileFollowUserName.innerHTML = profileFollow.username; 

    const profileFollowName = document.createElement('span'); 
    profileFollowName.setAttribute('id', 'followerGap'); 
    profileFollowName.innerHTML = profileFollow.first_name + " " + profileFollow.last_name; 


    spanContainer.appendChild(imgTag); 
    spanContainer.appendChild(profileFollowUserName);
    spanContainer.appendChild(profileFollowName);

    container.appendChild(spanContainer); 

    return container; 
};

api.howlByUserId(profileId).then(allHowl => {
    const displayHowl = document.getElementById('howlContentProfileId'); 
    allHowl.sort((a,b) => {
        return new Date(b.datetime) - new Date(a.datetime);
    });

    allHowl.forEach(howl => {
        displayHowl.append(makeContent(howl)); 
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

function normalDateTime(dateTime) {
    let monthAndDate = new Date(dateTime);
    let date = monthAndDate.getDate();
    let time = new Date(dateTime).toLocaleTimeString("en", { timeStyle: 'short', hour12: true, timeZone: 'UTC' });
    return monthAndDate.toLocaleString("en-US", { month: 'short' }) + " " + date + ", " + time;
}; 