import api from './APIClient.js';

// type, mediaId, title, cover, placeHolder, usrId
let mangaData = {
    type: "",
    mediaId: "",
    title: "",
    cover: "",
    holder: 1,
    usrId: ""
}

var liked = false;

api.getCurrentUser().then(user => {

    mangaData.usrId = user.id;
    console.log("userId: " + JSON.stringify(user));
    console.log("userId: " + user.id);

    api.getFavorites(user.id).then(favorites => {

        favorites.forEach(media => {

            if (media.fav_media_id == mangaData.mediaId) {
                likeButton.src = "../images/liked.png"
                liked = true;
            }

            console.log("favorties: " + JSON.stringify(media));
        })

    })

}); 

document.addEventListener('DOMContentLoaded', () => {
    let path = document.location.pathname;
    let mangaId = path.split('/manga/')[1];
    mangaData.type = "manga";
    mangaData.mediaId = mangaId;
    console.log('id', mangaId);

    api.getMangabyId(mangaId).then(manga => {
        console.log(manga);

        document.title = manga.title;
        mangaData.title = manga.title;

        let mangaCover = document.querySelector("#cover-image");
        mangaCover.src = manga.image;
        mangaData.cover = manga.image;

        let mangaTitle = document.querySelector("#title");

        if (manga.releaseDate && manga.releaseDate != null) {
            mangaTitle.innerText = manga.title + " (" + manga.releaseDate + ")";
        } else {
            mangaTitle.innerText = manga.title;
        }        

        let mangaChapters = document.querySelector("#volume-count");
        mangaChapters.innerText = "Chapters: " + manga.chapters.length;

        let mangaDescription = document.querySelector("#description");
        mangaDescription.innerText = manga.description.en.split('\n')[0];

        let chapterNum = 0;

        api.addToHistory(mangaData).then(data => {
            console.log("history: " + data)
        });  

        // Add chapters
        manga.chapters.forEach(chapter => {
            chapterNum++;

            let mangaLink = document.createElement("a");
            mangaLink.href = "/manga/" + mangaId + "/" + chapter.id;

            let mangaChapter = document.createElement("div");
            mangaChapter.classList.add("episode");

            let mangaChapterText = document.createElement("h3");
            mangaChapterText.innerText = "Read Chapter " + chapterNum + ": " + chapter.title;
            mangaChapterText.classList.add("episode-text");

            let mangaChapterLinkIcon = document.createElement("img");
            mangaChapterLinkIcon.src = "../images/follow.png";
            mangaChapterLinkIcon.classList.add("link-icon");

            mangaChapter.appendChild(mangaChapterText);
            mangaChapter.appendChild(mangaChapterLinkIcon);

            mangaLink.appendChild(mangaChapter);

            let mangaChapterList = document.querySelector("#volumes");
            mangaChapterList.appendChild(mangaLink);
        });

    });

});

let likeButton = document.querySelector("#add-fav");

likeButton.addEventListener('click', () => {

    if (liked) {

        api.removeFromFavorites(mangaData.mediaId).then(data => {
            console.log("removed from favorites");
            likeButton.src = "../images/like.png";
        })

        liked = false;

    } else {
        api.addToFavorites(mangaData).then(data => {

            console.log("This is data: " + data);
            likeButton.src = "../images/liked.png";
    
        });

        liked = true;

    }
});