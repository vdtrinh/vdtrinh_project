import api from './APIClient.js';

// type, mediaId, title, cover, placeHolder, usrId
let lightNovelData = {
    type: "",
    mediaId: "",
    title: "",
    cover: "",
    holder: 1,
    usrId: ""
}

var liked = false;

api.getCurrentUser().then(user => {

    lightNovelData.usrId = user.id;
    console.log("userId: " + JSON.stringify(user));
    console.log("userId: " + user.id);

    api.getFavorites(user.id).then(favorites => {

        favorites.forEach(media => {

            if (media.fav_media_id == lightNovelData.mediaId) {
                likeButton.src = "../images/liked.png"
                liked = true;
            }

            console.log("favorties: " + JSON.stringify(media));
        })

    })

}); 

document.addEventListener('DOMContentLoaded', () => {
    let path = document.location.pathname;
    let lightNovelId = path.split('/lightNovel/')[1];
    lightNovelData.type = "light novel";
    lightNovelData.mediaId = lightNovelId;
    lightNovelData.cover = "../images/istockphoto-165818983-612x612.jpg"

    console.log('id: ', lightNovelId);

    api.getLighNovelById(lightNovelId).then(lightNovel => {
        console.log(lightNovel);

        document.title = lightNovel.novel.title;
        lightNovelData.title = lightNovel.novel.title;

        let lightNovelTitle = document.querySelector("#title");
        lightNovelTitle.innerText = lightNovel.novel.title;

        let lightNovelChapters = document.querySelector("#volume-count");
        lightNovelChapters.innerText = "Chapters: " + lightNovel.novel.chapters.total;

        let lightNovelDescription = document.querySelector("#description");
        lightNovelDescription.innerHTML = lightNovel.novel.description;

        console.log("Getting chapter info...");

        let chapterNum = 0;

        console.log("Sleeping...");

        sleep(1001).then(() => {
            console.log("Sleep over.");

            // Add chapters
            api.getLightNovelChapters(lightNovelId).then(chapterData => {
                console.log(chapterData);

                chapterData.chapters.forEach(chapter => {
                    chapterNum++;

                    let lightNovelLink = document.createElement("a");
                    lightNovelLink.href = chapter.url;
                    lightNovelLink.target = '_blank';

                    let lightNovelChapter = document.createElement("div");
                    lightNovelChapter.classList.add("episode");

                    let lightNovelChapterText = document.createElement("h3");
                    lightNovelChapterText.innerText = "Read Chapter " + chapterNum + ": " + chapter.title;
                    lightNovelChapterText.classList.add("episode-text");

                    let lightNovelChapterLinkIcon = document.createElement("img");
                    lightNovelChapterLinkIcon.src = "../images/follow.png";
                    lightNovelChapterLinkIcon.classList.add("link-icon");

                    lightNovelChapter.appendChild(lightNovelChapterText);
                    lightNovelChapter.appendChild(lightNovelChapterLinkIcon);

                    lightNovelLink.appendChild(lightNovelChapter);

                    let lightNovelChapterList = document.querySelector("#volumes");
                    lightNovelChapterList.appendChild(lightNovelLink);
                });
            });
        });

        api.addToHistory(lightNovelData).then(data => {
            console.log("history: " + data)
        });
    });

});

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

let likeButton = document.querySelector("#add-fav");

likeButton.addEventListener('click', () => {

    if (liked) {

        api.removeFromFavorites(lightNovelData.mediaId).then(data => {
            console.log("removed from favorites");
            likeButton.src = "../images/like.png";
        })

        liked = false;

    } else {
        api.addToFavorites(lightNovelData).then(data => {

            console.log("This is data: " + data);
            likeButton.src = "../images/liked.png";
    
        });

        liked = true;

    }
});