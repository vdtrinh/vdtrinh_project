import api from './APIClient.js';

document.addEventListener('DOMContentLoaded', () => {
    api.getAnime().then(animeInput => {
        // Pretty print JSON
        let animeString = JSON.stringify(animeInput.results, null, 2);
        console.log(animeString);

        let animes = animeInput.results;

        let animeList = document.querySelector("#animes");

        // Render anime displays
        animes.forEach(anime => {
            // <a class = "anime" href = "<anime.id>">
            //      <img src = "<anime.image>" class = "anime-cover">
            //      <p class = "anime-title"><anime.title></p>
            // </a>

            let animeDisplay = document.createElement("a");
            animeDisplay.classList.add("anime");
            animeDisplay.href = '/anime/' + anime.id;

            let animeCover = document.createElement("img");
            animeCover.src = anime.image;
            animeCover.classList.add("anime-cover");

            let animeTitle = document.createElement("p");
            animeTitle.innerText = anime.title;
            animeTitle.classList.add("anime-title");

            animeDisplay.appendChild(animeCover);
            animeDisplay.appendChild(animeTitle);

            animeList.appendChild(animeDisplay);
        });
    });    
});

const searchButton = document.querySelector('#search-button');

searchButton.addEventListener('click', () => {

    const search = document.getElementById("searchbar").value.toLowerCase();
    if (search !== '') {
        api.searchAnime(search).then(anime => {
            let animeInfo = anime.results;
    
            let animeList = document.querySelector("#animes");

            animeList.innerHTML = "";
        
            animeInfo.forEach(anime => {
    
                let animeDisplay = document.createElement("a");
                animeDisplay.classList.add("anime");
                animeDisplay.href = '/anime/' + anime.title;
    
                let animeCover = document.createElement("img");
                animeCover.src = anime.image;
                animeCover.classList.add("anime-cover");
    
                let animeTitle = document.createElement("p");
                animeTitle.innerText = anime.title;
                animeTitle.classList.add("anime-title");
    
                animeDisplay.appendChild(animeCover);
                animeDisplay.appendChild(animeTitle);
    
                animeList.appendChild(animeDisplay);
            })
        });
    }
});