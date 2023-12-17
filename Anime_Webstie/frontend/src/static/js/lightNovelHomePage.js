import api from './APIClient.js';

document.addEventListener('DOMContentLoaded', () => {
    api.getLighNovel().then(lightNovelsData => {

        let lightNovels = lightNovelsData.novels;

        console.log(lightNovels);

        let novelId = Math.floor(Math.random() * 200)

        let LNList = document.querySelector("#lightNovels");

        for (let i = 0; i < 20; i++) {
            let LNDisplay = document.createElement("a");
            LNDisplay.classList.add("lightNovel");
            LNDisplay.href = '/lightNovel/' + lightNovels[novelId].id;

            let LNCover = document.createElement("img");
            LNCover.src = "../images/istockphoto-165818983-612x612.jpg";
            LNCover.classList.add("lightNovel-cover");

            let LNTitle = document.createElement("p");
            LNTitle.innerText = lightNovels[novelId].title;
            LNTitle.classList.add("lightNovel-title");

            LNDisplay.appendChild(LNCover);
            LNDisplay.appendChild(LNTitle);

            LNList.appendChild(LNDisplay);

            novelId = Math.floor(Math.random() * 200)
        }
    });
});

const searchButton = document.querySelector('#search-button');

searchButton.addEventListener('click', () => {

    const search = document.getElementById("searchbar").value.toLowerCase();
    if (search !== '') {
        
        api.searchLightNovel(search).then(lightNovels => {
            let lightNovelInfo = lightNovels.results;
    
            let LNList = document.querySelector("#lightNovels");

            LNList.innerHTML = "";
        
            for (let i = 0; i < 15; i++) {
                let LNDisplay = document.createElement("a");
                LNDisplay.classList.add("lightNovel");
                LNDisplay.href = '/lightNovel/' + lightNovelInfo[i].item.id;
    
                let LNCover = document.createElement("img");
                LNCover.src = "../images/istockphoto-165818983-612x612.jpg";
                LNCover.classList.add("lightNovel-cover");
    
                let LNTitle = document.createElement("p");
                LNTitle.innerText = lightNovelInfo[i].item.title;
                LNTitle.classList.add("lightNovel-title");
    
                LNDisplay.appendChild(LNCover);
                LNDisplay.appendChild(LNTitle);
    
                LNList.appendChild(LNDisplay);
            }
    
        });
    }
});