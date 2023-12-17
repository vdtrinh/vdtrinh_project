import HTTPClient from "./HTTPClient.js";

//https://rapidapi.com/hefesto-technologies-hefesto-technologies-default/api/anime-manga-and-novels-api/

const API_BASE = '/api';

export default {
  getAnime: () => {
    return HTTPClient.get(API_BASE+'/media/animes')
  },

  searchAnime:(animeTitle) => {
    return HTTPClient.get(API_BASE+`/media/searchAnime/${animeTitle}`)
  },

  getAnimeById: (animeId) => {
    return HTTPClient.get(API_BASE+`/media/animes/${animeId}`)
  },

  getAnimeEpisode: (animeId, animeEpisodeId) => {
    return HTTPClient.get(API_BASE+`/media/animes/${animeId}/${animeEpisodeId}`)
  },

  getManga: () => {
    return HTTPClient.get(API_BASE+'/media/mangas')
  },

  searchManga: (mangaTitle) => {
    return HTTPClient.get(API_BASE+`/media/searchManga/${mangaTitle}`)
  },

  getMangabyId: (mangaId) => {
    return HTTPClient.get(API_BASE+`/media/mangas/${mangaId}`)
  },

  getMangaChapter: (mangaId, mangaChapter) => {
    return HTTPClient.get(API_BASE+`/media/mangas/${mangaId}/${mangaChapter}`)
  }, 

  getLighNovel: () => {
    return HTTPClient.get(API_BASE+`/media/lightNovels`)
  },

  searchLightNovel: (lightNovelTitle) => {
    return HTTPClient.get(API_BASE+`/media/searchLightNovel/${lightNovelTitle}`)
  },

  getLighNovelById: (lightNovelId) => {
    return HTTPClient.get(API_BASE+`/media/lightNovels/${lightNovelId}`)
  },

  getLightNovelChapters: (lightNovelId) => {
    console.log('ATTEMPTING TO ACCESS ', API_BASE+`/media/lightNovels/${lightNovelId}/chapters`);
    return HTTPClient.get(API_BASE+`/media/lightNovels/${lightNovelId}/chapters`)
  }, 

  getLighNovelChapter: (lightNovelId, lightNovelChapter) => {
    return HTTPClient.get(API_BASE+`/media/lightNovels/${lightNovelId}/${lightNovelChapter}`)
  },

  getFavorites: (userId) => {
    return HTTPClient.get(API_BASE+`/favorites/users/${userId}`)
  }, 

  getFavoriteMedia: (mediaId) => {
    return HTTPClient.get(API_BASE+`/favorites/${mediaId}`)
  },

  getFavoriteMediaType: (userId, mediaType) => {
    return HTTPClient.get(API_BASE+`/favorites/${userId}/${mediaType}`)
  },
// Make sure media has all fields
// type, mediaId, title, cover, usrId
  addToFavorites: (media) => {
    return HTTPClient.post(API_BASE+`/favorites/insert`, media)
  }, 

  removeFromFavorites: (mediaID) => {
    return HTTPClient.delete(API_BASE+`/favorites/remove/${mediaID}`)
  }, 

  getHistory: (userId) => {
    return HTTPClient.get(API_BASE+`/history/userHistory/${userId}`)
  }, 
// Make sure media has all fields
// type, mediaId, title, cover, usrId
  addToHistory: (media) => {
    return HTTPClient.post(API_BASE+`/history/createHistory`, media)
  }, 

  signUp: (firstName, lastName, username, password, avatar) => {
    let data = {
      first_name: firstName,
      last_name: lastName,
      username: username,
      password: password,
      avatar: avatar
    }

    return HTTPClient.post(API_BASE + '/users/signup', data);
  },

  logIn: (username, password) => {
    let data = {
      username: username,
      password: password
    }
    return HTTPClient.post(API_BASE + '/users/login', data);
  },

  logOut: () => {
    return HTTPClient.post(API_BASE+'/users/logout', {});
  },

  getCurrentUser: () => {
    return HTTPClient.get(API_BASE+'/users/current'); 
  }
};