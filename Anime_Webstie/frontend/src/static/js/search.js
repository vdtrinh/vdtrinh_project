import api from './APIClient.js';

const searchButton = document.querySelector('#search-button');

searchButton.addEventListener('click', () => {

    const search = document.getElementById("searchbar").value.toLowerCase();
    if (search !== '') {
        api.searchAnime(search).then(anime => {
            let animeInfo = anime.results;
    
            let animeList = document.querySelector("#animes");
    
            document.querySelector("#anime-header").style.visibility = "visible";
    
            animeInfo.forEach(anime => {
    
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
            })
        });
    
    
        api.searchLightNovel(search).then(lightNovels => {
            let lightNovelInfo = lightNovels.results;
    
            let LNList = document.querySelector("#lightNovel");
    
            document.querySelector("#LN-header").style.visibility = "visible";
    
            for (let i = 0; i < 20; i++) {
                let LNDisplay = document.createElement("a");
                LNDisplay.classList.add("lightNovel");
                LNDisplay.href = '/lightNovel/' + lightNovelInfo[i].item.id;
    
                let LNCover = document.createElement("img");
                LNCover.src = "../images/istockphoto-165818983-612x612.jpg";
                LNCover.classList.add("LN-cover");
    
                let LNTitle = document.createElement("p");
                LNTitle.innerText = lightNovelInfo[i].item.title;
                LNTitle.classList.add("LN-title");
    
                LNDisplay.appendChild(LNCover);
                LNDisplay.appendChild(LNTitle);
    
                LNList.appendChild(LNDisplay);
            }
    
        });
    
        api.searchManga(search).then(mangas => {
            let mangaInfo = mangas.results;

            let mangaList = document.querySelector("#manga");

            document.querySelector("#manga-header").style.visibility = "visible";

            for (let i = 0; i < 20; i++) {
                api.getMangabyId(mangaInfo[i].id).then(manga => {

                    let mangaDisplay = document.createElement("a");
                    mangaDisplay.classList.add("manga");
                    mangaDisplay.href = '/manga/' + manga.id;            
                
                    let mangaCover = document.createElement("img");
                    mangaCover.src = manga.image;
                    mangaCover.classList.add("manga-cover");
    
                    let mangaTitle = document.createElement("p");
                    mangaTitle.innerText = manga.title;
                    mangaTitle.classList.add("manga-title");
    
                    mangaDisplay.appendChild(mangaCover);
                    mangaDisplay.appendChild(mangaTitle);
    
                    mangaList.appendChild(mangaDisplay);

                });
            }
        });
    }

    // displayHistory(search);
});

// function displayHistory(recentSearch) {

//     console.log("bruh");

//     const history = document.getElementById("history");

//     let historyDisplay = document.createElement("div");
//     historyDisplay.classList.add("title")

//     let historyTitle = document.createElement("h2");
//     historyTitle.innerText = recentSearch;

//     let deleteButton = document.createElement("img");
//     deleteButton.classList.add("delete-button");
//     deleteButton.src = "../images/delete.png"

//     historyDisplay.appendChild(historyTitle);
//     historyDisplay.appendChild(deleteButton);

//     history.appendChild(historyDisplay);

// }
