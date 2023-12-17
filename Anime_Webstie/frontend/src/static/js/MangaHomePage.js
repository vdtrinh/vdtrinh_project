import api from './APIClient.js';

document.addEventListener('DOMContentLoaded', () => {
    let mangaList = document.querySelector("#mangas");

    let featuredMangas = [
        '1bddb3cb-44c8-471a-9133-f9589fb1c879',
        '47029dd2-1b20-457c-aa80-6ef0c431cc9a',
        '8c7a252c-abbe-40b7-b0aa-0a9ca6f70032',
        '1d815a62-83bf-426d-9801-c55171810bc3',
        '6e3938ae-f079-41cc-a192-d6fb283b301a',
        '00293f4b-8ccc-4f65-95ef-b1a0c32d5ac8',
        'e3668b69-6122-4f69-b3a5-dcf1927159d8',
        'a0b8d5fe-e49c-4f43-a0b2-9ebeea920233',
        '53c628b3-d1f5-43aa-8df3-080034285cb4'
    ];

    featuredMangas.forEach(featuredManga => {
        api.getMangabyId(featuredManga).then(mangaById => {
            console.log(mangaById);

            let mangaDisplay = document.createElement("a");
            mangaDisplay.classList.add("manga");
            mangaDisplay.href = '/manga/' + mangaById.id;            
        
            let mangaCover = document.createElement("img");
            mangaCover.src = mangaById.image;
            mangaCover.classList.add("manga-cover");

            let mangaTitle = document.createElement("p");
            mangaTitle.innerText = mangaById.title;
            mangaTitle.classList.add("manga-title");

            mangaDisplay.appendChild(mangaCover);
            mangaDisplay.appendChild(mangaTitle);

            mangaList.appendChild(mangaDisplay);
        });
    });  
});

const searchButton = document.querySelector('#search-button');

searchButton.addEventListener('click', () => {

    const search = document.getElementById("searchbar").value.toLowerCase();
    if (search !== '') {
        api.searchManga(search).then(mangas => {
            let mangaInfo = mangas.results;

            let mangaList = document.querySelector("#mangas");
            
            mangaList.innerHTML = "";

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
});