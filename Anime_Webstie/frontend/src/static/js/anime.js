import api from './APIClient.js';

// type, mediaId, title, cover, placeHolder, usrId
let animeData = {
    type: "",
    mediaId: "",
    title: "",
    cover: "",
    holder: 1,
    usrId: ""
}

var liked = false;

api.getCurrentUser().then(user => {

    animeData.usrId = user.id;
    console.log("userId: " + JSON.stringify(user));
    console.log("userId: " + user.id);

    api.getFavorites(user.id).then(favorites => {

        favorites.forEach(media => {

            if (media.fav_media_id == animeData.mediaId) {
                likeButton.src = "../images/liked.png"
                liked = true;
            }

            console.log("favorties: " + JSON.stringify(media));
        })

    })

}); 

document.addEventListener('DOMContentLoaded', () => {
    let path = document.location.pathname;
    let animeId = path.split('/anime/')[1];

    animeData.type = "anime";
    animeData.mediaId = animeId;
    // console.log(animeId);

    api.getAnimeById(animeId).then(anime => {
        console.log(anime);

        document.title = anime.title;
        animeData.title = anime.title;

        let animeCover = document.querySelector("#cover-image");
        animeCover.src = anime.image;
        animeData.cover = anime.image;

        let animeTitle = document.querySelector("#title");
        animeTitle.innerText = anime.title + " (" + anime.releaseDate + ")";

        let animeEpisodes = document.querySelector("#volume-count");
        animeEpisodes.innerText = "Episodes: " + anime.episodes.length;

        let animeDescription = document.querySelector("#description");
        animeDescription.innerText = anime.description.split('\n')[0];

        // Add episodes
        anime.episodes.forEach(episode => {
            let animeLink = document.createElement("a");
            animeLink.href = episode.url;
            animeLink.target = "_blank";

            let animeEpisode = document.createElement("div");
            animeEpisode.classList.add("episode");

            let animeEpisodeText = document.createElement("h3");
            animeEpisodeText.innerText = "Watch Episode " + episode.number;
            animeEpisodeText.classList.add("episode-text");

            let animeEpisodeLinkIcon = document.createElement("img");
            animeEpisodeLinkIcon.src = "../images/follow.png";
            animeEpisodeLinkIcon.classList.add("link-icon");

            animeEpisode.appendChild(animeEpisodeText);
            animeEpisode.appendChild(animeEpisodeLinkIcon);

            animeLink.appendChild(animeEpisode);

            let animeEpisodeList = document.querySelector("#volumes");
            animeEpisodeList.appendChild(animeLink);
        });

        api.addToHistory(animeData).then(data => {
            console.log("history: " + data)
        })

    });


});

let likeButton = document.querySelector("#add-fav");

likeButton.addEventListener('click', () => {

    if (liked) {

        api.removeFromFavorites(animeData.mediaId).then(data => {
            console.log("removed from favorites");
            likeButton.src = "../images/like.png";
        })

        liked = false;

    } else {
        api.addToFavorites(animeData).then(data => {

            console.log("This is data: " + data);
            likeButton.src = "../images/liked.png";
    
        });

        liked = true;

    }

});

  