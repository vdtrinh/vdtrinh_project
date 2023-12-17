import api from './APIClient.js';

document.addEventListener('DOMContentLoaded', () => {
    let path = document.location.pathname;
    let mangaId = path.split('/')[2];
    let chapterId = path.split('/')[3];

    console.log("Manga id: ", mangaId);
    console.log("Chapter id: ", chapterId);

    api.getMangaChapter(mangaId, chapterId).then(chapterData => {
        chapterData.forEach(panel => {
            let panelSlot = document.createElement("img");
            panelSlot.src = panel.img;
            panelSlot.classList.add("panel-slot");

            let main = document.querySelector("main");
            main.appendChild(panelSlot);
        })
    });

    let backButton = document.querySelector("#back-button");

    backButton.addEventListener('click', () => {
        document.location = '/manga/' + mangaId;
    });
});