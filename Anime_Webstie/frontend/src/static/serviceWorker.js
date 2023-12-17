function log(...data) {
  console.log("SWv1.0", ...data);
}

log("SW Script executing");

const STATIC_CACHE_NAME = 'otakhub-static-v0';

self.addEventListener('install', event => {
  log('install', event);
  event.waitUntil(
    caches.open(STATIC_CACHE_NAME).then(cache => {
      return cache.addAll([
        '/offline',
        //CSS
        '/css/animeHome.css',
        '/css/common.css',
        '/css/discover.css',
        '/css/favorites.css',
        '/css/lightNovel.css',
        '/css/lightNovelHome.css',
        '/css/mangaChapter.css',
        '/css/mangaHome.css',
        '/css/media.css',
        '/css/mediaHomePage.css',
        '/css/profile.css',
        '/css/search.css',
        //Images
        '/images/back.png',
        '/images/delete.png',
        '/images/favorites-selected.png',
        '/images/favorites.png',
        '/images/follow.png',
        '/images/istockphoto-165818983-612x612.jpg',
        '/images/like.png',
        '/images/liked.png',
        '/images/logout.png',
        '/images/media-selected.png',
        '/images/media.png',
        '/images/profile-selected.png',
        '/images/profile.png',
        '/images/search-selected.png',
        '/images/search.png',
        '/images/settings.png',
        '/images/user.png',
        '/images/avatars/1.webp',
        '/images/avatars/2.png',
        '/images/avatars/3.jpg',
        '/images/avatars/4.jpg',
        '/images/avatars/5.avif',
        '/images/icons/android-chrome-192x192.png',
        '/images/icons/android-chrome-512x512.png',
        '/images/icons/apple-touch-icon.png',
        '/images/icons/browserconfig.xml',
        '/images/icons/favicon-16x16.png',
        '/images/icons/favicon-32x32.png',
        '/images/icons/favicon.ico',
        '/images/icons/mstile-150x150.png',
        '/images/icons/safari-pinned-tab.svg',
        '/images/anime-stills/1.jpg',
        '/images/anime-stills/2.jpg',
        '/images/anime-stills/3.jpg',
        '/images/anime-stills/4.jpg',
        '/images/anime-stills/5.jpg',
        '/images/lightNovel-stills/1.jpeg',
        '/images/lightNovel-stills/2.jpg',
        '/images/lightNovel-stills/3.jpg',
        '/images/lightNovel-stills/4.jpg',
        '/images/lightNovel-stills/5.jpg',
        '/images/manga-stills/1.png',
        '/images/manga-stills/2.jpg',
        '/images/manga-stills/3.jpg',
        '/images/manga-stills/4.jpg',
        '/images/manga-stills/5.jpg',
        //Scripts
        '/js/anime.js',
        '/js/AnimeHomePage.js',
        '/js/APIClient.js',
        '/js/common.js',
        '/js/discover.js',
        '/js/favorites.js',
        '/js/HTTPClient.js',
        '/js/lightNovel.js',
        '/js/lightNovelHomePage.js',
        '/js/manga.js',
        '/js/mangaChapter.js',
        '/js/MangaHomePage.js',
        '/js/profile.js',
        '/js/search.js',
        //Templates
        '/templates/anime.html',
        '/templates/animeHomePage.html',
        '/templates/discover.html',
        '/templates/favorites.html',
        '/templates/lightNovel.html',
        '/templates/lightNovelHomePage.html',
        '/templates/manga.html',
        '/templates/mangaChapter.html',
        '/templates/mangaHomePage.html',
        '/templates/offline.html',
        '/templates/profile.html',
        '/templates/search.html',
      ]);
    })
  );
});

self.addEventListener('activate', event => {
  log('activate', event);
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.filter(cacheName => {
          return cacheName.startsWith('otakhub-') && cacheName != STATIC_CACHE_NAME;
        }).map(cacheName => {
          return caches.delete(cacheName);
        })
      );
    })
  );
});

self.addEventListener('fetch', event => {
  var requestUrl = new URL(event.request.url);
  //Treat API calls (to our API) differently
  if (requestUrl.origin === location.origin && requestUrl.pathname.startsWith('/api')) {
    //If we are here, we are intercepting a call to our API
    if (event.request.method === "GET") {
      //Only intercept (and cache) GET API requests
      event.respondWith(
        cacheFirst(event.request)
      );
    }
  }
  else {
    //If we are here, this was not a call to our API
    event.respondWith(
      cacheFirst(event.request)
    );
  }

});


function cacheFirst(request) {
  return caches.match(request)
    .then(response => {
      //Return a response if we have one cached. Otherwise, get from the network
      return response || fetchAndCache(request);
    })
    .catch(error => {
      return caches.match('/offline');
    })
}



function fetchAndCache(request) {
  return fetch(request).then(response => {
    var requestUrl = new URL(request.url);
    //Cache everything except login
    if (response.ok && !requestUrl.pathname.startsWith('/login')) {
      caches.open(STATIC_CACHE_NAME).then((cache) => {
        cache.put(request, response);
      });
    }
    return response.clone();
  });
}



self.addEventListener('message', event => {
  log('message', event.data);
  if (event.data.action === 'skipWaiting') {
    self.skipWaiting();
  }
});  