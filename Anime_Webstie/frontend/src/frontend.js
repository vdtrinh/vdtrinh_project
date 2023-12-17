const express = require('express');
const frontendRouter = express.Router();

const html_dir = __dirname + '/static/templates/';

frontendRouter.get('/', (req, res) => {
    res.sendFile(`${html_dir}index.html`);
});

frontendRouter.get('/login', (req, res) => {
    res.sendFile(`${html_dir}login.html`);
});

frontendRouter.get('/discover', (req, res) => {
    res.sendFile(`${html_dir}discover.html`);
});

frontendRouter.get('/profile', (req, res) => {
    res.sendFile(`${html_dir}profile.html`);
});

frontendRouter.get('/animeHomePage', (req, res) => {
    res.sendFile(`${html_dir}animeHomePage.html`);
});

frontendRouter.get('/mangaHomePage', (req, res) => {
    res.sendFile(`${html_dir}mangaHomePage.html`);
});

frontendRouter.get('/lightNovelHomePage', (req, res) => {
    res.sendFile(`${html_dir}lightNovelHomePage.html`);
});

frontendRouter.get('/search', (req, res) => {
    res.sendFile(`${html_dir}search.html`);
});

frontendRouter.get('/favorites', (req, res) => {
    res.sendFile(`${html_dir}favorites.html`);
});

frontendRouter.get('/anime/:id', (req, res) => {
    res.sendFile(`${html_dir}anime.html`);
});

frontendRouter.get('/manga/:id', (req, res) => {
    res.sendFile(`${html_dir}manga.html`);
});

frontendRouter.get('/lightNovel/:id', (req, res) => {
    res.sendFile(`${html_dir}lightNovel.html`);
});

frontendRouter.get('/manga/:mangaId/:mangaChapterId', (req, res) => {
    res.sendFile(`${html_dir}mangaChapter.html`);
});

frontendRouter.get('/signup', (req, res) => {
    res.sendFile(`${html_dir}signup.html`);
});

frontendRouter.get('/offline', (req,  res) => {
    res.sendFile(`${html_dir}offline.html`);
});

module.exports = frontendRouter; 