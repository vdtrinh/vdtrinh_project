let animeStillPrefix = 'images/anime-stills/';

let animeStills = [
    '1.jpg',
    '2.jpg',
    '3.jpg',
    '4.jpg',
    '5.jpg'
];

let mangaStillPrefix = 'images/manga-stills/';

let mangaStills = [
    '1.png',
    '2.jpg',
    '3.jpg',
    '4.jpg',
    '5.jpg'
];

let lightNovelStillPrefix = 'images/lightNovel-stills/';

let lightNovelStills = [
    '1.jpeg',
    '2.jpg',
    '3.jpg',
    '4.jpg',
    '5.jpg'
];

document.addEventListener('DOMContentLoaded', () => {
    // Animes
    
    let animePreview = document.querySelector("#anime-preview");

    let randomAnimeNum = Math.floor(Math.random() * animeStills.length);
    let randomAnime = animeStills[randomAnimeNum];
    animePreview.src = animeStillPrefix + randomAnime;

    // Mangas

    let mangaPreview = document.querySelector("#manga-preview");

    let randomMangaNum = Math.floor(Math.random() * mangaStills.length);
    let randomManga = mangaStills[randomMangaNum];
    mangaPreview.src = mangaStillPrefix + randomManga;

    // Light novels

    let lightNovelPreview = document.querySelector("#lightNovel-preview");

    let lightNovelNum = Math.floor(Math.random() * lightNovelStills.length);
    let randomLightNovel = lightNovelStills[lightNovelNum];
    lightNovelPreview.src = lightNovelStillPrefix + randomLightNovel;
});