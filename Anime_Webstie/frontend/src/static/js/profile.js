import api from './APIClient.js';

var usr_id = 0; 

console.log("profile.js working!");

// let lightnovelImagePath = '../images/istockphoto-165818983-612x612.jpg'

document.addEventListener('DOMContentLoaded', () => {
    let logoutButton = document.querySelector("#logout-button");
    
    logoutButton.addEventListener('click', () => {
        api.logOut().then(() => {
            window.location = "/";
        });
    });

    api.getCurrentUser().then(data => {
        console.log(data);
    
        // Personalize homepage
    
        document.title = data.username;
    
        let avatar = document.querySelector("#pfp");
    
        let avatarPrefix = 'images/avatars/'
    
        if (data.avatar == "avatar1") {
            avatar.src = avatarPrefix + '1.webp';
        } else if (data.avatar == "avatar2") {
            avatar.src = avatarPrefix + '2.png';
        } else if (data.avatar == "avatar3") {
            avatar.src = avatarPrefix + '3.jpg';
        } else if (data.avatar == "avatar4") {
            avatar.src = avatarPrefix + '4.jpg';
        } else if (data.avatar == "avatar5") {
            avatar.src = avatarPrefix + '5.avif';
        }
    
        let fullname = document.querySelector("#fullname");
        fullname.innerText = data.first_name + " " + data.last_name;
    
        // Show history
        api.getHistory(data.id).then(media => {
            console.log(media);

            if (media.length == 0) {
                let contentArea = document.querySelector("#content");

                let messageArea = document.createElement("div");
                messageArea.classList.add("message-area");

                let emptyHistoryMsg = document.createElement("p");
                emptyHistoryMsg.innerText = "History is empty.";
                emptyHistoryMsg.classList.add("empty-message");

                messageArea.appendChild(emptyHistoryMsg)
                contentArea.appendChild(messageArea);
            }
    
            media.forEach(item => {
                console.log(item);
    
                let mediaArea = document.querySelector("#media");
                
                let media = document.createElement("div");
                media.classList.add("my-media");
    
                let mediaDisplay = document.createElement("a");
                
                let mediaCover = document.createElement("img");
                mediaCover.classList.add("cover");
                let mediaTitle = document.createElement("p");
                mediaTitle.classList.add("title");
    
                if (item.his_media_type == "anime") {
                    mediaDisplay.href = '/anime/' + item.his_media_id;
                    mediaCover.src = item.his_cover; 
                    mediaTitle.innerText = item.his_media_title; 
                    
                } else if (item.his_media_type == "manga") {
                    mediaDisplay.href = '/anime/' + item.his_media_id;
                    mediaCover.src = item.his_cover; 
                    mediaTitle.innerText = item.his_media_title; 
    
                } else if (item.his_media_type == "light novel") {
                    mediaDisplay.href = '/anime/' + item.his_media_id;
                    mediaCover.src = item.his_cover; 
                    mediaTitle.innerText = item.his_media_title; 
                }
    
                mediaDisplay.appendChild(mediaCover);
                mediaDisplay.appendChild(mediaTitle);
    
                media.appendChild(mediaDisplay);
    
                mediaArea.appendChild(media);
            });
       });
    }); 
});

