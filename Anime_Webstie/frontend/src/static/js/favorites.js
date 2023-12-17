import api from './APIClient.js';

let currentUser = "";

api.getCurrentUser().then(user => {

    currentUser = user.id;
    console.log("userId: " + JSON.stringify(user));
    console.log("userId: " + user.id);

    api.getFavorites(user.id).then(favorites => {

        if (favorites.length == 0) {
            let contentArea = document.querySelector("#favorites");

            let messageArea = document.createElement("div");
            messageArea.classList.add("message-area");

            let emptyHistoryMsg = document.createElement("p");
            emptyHistoryMsg.innerText = "No favorites... so far.";
            emptyHistoryMsg.classList.add("empty-message");

            messageArea.appendChild(emptyHistoryMsg)
            contentArea.appendChild(messageArea);
        }

        favorites.forEach(media => {

            console.log("favorties: " + JSON.stringify(media));

            if (media.fav_media_type == "anime") {
                populateAnimeFavorites(media);
            } else if (media.fav_media_type == "light novel") {
                populateLNFavorites(media);
            } else {
                populateMangaFavorites(media);
            }

        })

    })

});

document.addEventListener('DOMContentLoaded', () => {

    let animeFavorites = document.querySelector("#animes");
    let mangaFavorites = document.querySelector("#mangas");
    let LNFavorites = document.querySelector("#light_Novels");

    let animeFilter = document.querySelector("#anime-filter");

    animeFilter.addEventListener('click', () => {

        animeFavorites.style.display = "flex";
        mangaFavorites.style.display = "none";
        LNFavorites.style.display = "none";

        animeFilter.classList.add("filter-selected");
        mangaFilter.classList.remove("filter-selected");
        lightnovelFilter.classList.remove("filter-selected");

    });

    let mangaFilter = document.querySelector("#manga-filter");

    mangaFilter.addEventListener('click', () => {

        animeFavorites.style.display = "none";
        mangaFavorites.style.display = "flex";
        LNFavorites.style.display = "none";

        mangaFilter.classList.add("filter-selected");
        animeFilter.classList.remove("filter-selected");
        lightnovelFilter.classList.remove("filter-selected");

    });

    let lightnovelFilter = document.querySelector("#lightnovel-filter");

    lightnovelFilter.addEventListener('click', () => {

        animeFavorites.style.display = "none";
        mangaFavorites.style.display = "none";
        LNFavorites.style.display = "flex";

        lightnovelFilter.classList.add("filter-selected");
        animeFilter.classList.remove("filter-selected");
        mangaFilter.classList.remove("filter-selected");

    });
});

function populateMangaFavorites(manga) {

    let mangaList = document.querySelector("#mangas");

    let mangaDiv = document.createElement("div");
    mangaDiv.classList.add("media-div");

    let mangaDisplay = document.createElement("a");
    mangaDisplay.classList.add("media");
    mangaDisplay.href = '/manga/' + manga.fav_media_id;

    let mangaCover = document.createElement("img");
    mangaCover.src = manga.fav_cover;
    mangaCover.classList.add("cover-image");

    let mangaTitle = document.createElement("p");
    mangaTitle.innerText = manga.fav_media_title;
    mangaTitle.classList.add("favorite-title");

    let favButton = document.createElement("button");
    favButton.classList.add("like-button");

    let favPic = document.createElement("img");
    favPic.src = "../images/liked.png";

    favButton.addEventListener('click', () => {
        favPic.src = "../images/like.png";
        api.removeFromFavorites(manga.fav_media_id)

    })

    // mangaList
    //  - mangaDiv 
    //      - mangaDisplay (flex, row)
    //          - mangaCover
    //          - mangaTitle
    //      -favButton
    //          - favPic

    favButton.appendChild(favPic);

    mangaDisplay.appendChild(mangaCover);
    mangaDisplay.appendChild(mangaTitle);

    mangaDiv.appendChild(mangaDisplay);
    mangaDiv.appendChild(favButton);

    mangaList.appendChild(mangaDiv);

}

function populateAnimeFavorites(anime) {

    let animeList = document.querySelector("#animes");

    let animeDiv = document.createElement("div");
    animeDiv.classList.add("media-div");

    let animeDisplay = document.createElement("a");
    animeDisplay.classList.add("media");
    animeDisplay.href = '/anime/' + anime.fav_media_id;

    let animeCover = document.createElement("img");
    animeCover.src = anime.fav_cover;
    animeCover.classList.add("cover-image");

    let animeTitle = document.createElement("p");
    animeTitle.innerText = anime.fav_media_title;
    animeTitle.classList.add("favorite-title");

    let favButton = document.createElement("button");
    favButton.classList.add("like-button");

    let favPic = document.createElement("img");
    favPic.src = "../images/liked.png";

    favButton.addEventListener('click', () => {
        favPic.src = "../images/like.png";
        api.removeFromFavorites(anime.fav_media_id)

    })

    favButton.appendChild(favPic);

    animeDisplay.appendChild(animeCover);
    animeDisplay.appendChild(animeTitle);

    animeDiv.appendChild(animeDisplay);
    animeDiv.appendChild(favButton);

    animeList.appendChild(animeDiv);

}

function populateLNFavorites(lightNovel) {

    let LNList = document.querySelector("#light_Novels");

    let LNDiv = document.createElement("div");
    LNDiv.classList.add("media-div");

    let LNDisplay = document.createElement("a");
    LNDisplay.classList.add("media");
    LNDisplay.href = '/lightNovels/' + lightNovel.fav_media_id;

    let LNCover = document.createElement("img");
    LNCover.src = lightNovel.fav_cover;
    LNCover.classList.add("cover-image");

    let LNTitle = document.createElement("p");
    LNTitle.innerText = lightNovel.fav_media_title;
    LNTitle.classList.add("favorite-title");

    let favButton = document.createElement("button");
    favButton.classList.add("like-button");

    let favPic = document.createElement("img");
    favPic.src = "../images/liked.png";

    favButton.addEventListener('click', () => {
        favPic.src = "../images/like.png";
        api.removeFromFavorites(lightNovel.fav_media_id)

    })

    favButton.appendChild(favPic);

    LNDisplay.appendChild(LNCover);
    LNDisplay.appendChild(LNTitle);

    LNDiv.appendChild(LNDisplay);
    LNDiv.appendChild(favButton);

    LNList.appendChild(LNDiv);

}