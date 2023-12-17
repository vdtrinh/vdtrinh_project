const express = require('express');
const apiRouter = express.Router();
apiRouter.use(express.json());
const axios = require('axios').default
const cookieParser = require('cookie-parser');
apiRouter.use(cookieParser());
// import axios from "axios";
const crypto = require('crypto');

const db = require('./DBConnection');

const UserDAO = require('./UserDAO.js');
const { TokenMiddleware, generateToken, removeToken } = require('./TokenMiddleware.js');
const FavoriteDAO = require('./FavoriteDAO.js');
const HistoryDAO = require('./HistoryDAO.js');

//https://docs.consumet.org/rest-api/Manga/mangadex/get-manga-chapter-pages
//https://rapidapi.com/tuefekci/api/web-novel-api
//https://api.mangadex.org/docs/guide/find-manga/

// Gets the top airing anime that we can display and use the id for 
// https://docs.consumet.org/rest-api/Anime/gogoanime/get-top-airing-anime
apiRouter.get('/media/animes', TokenMiddleware, (req, res) => {

    const url = "https://api.consumet.org/anime/gogoanime/top-airing";
    const data = async () => {
        try {
            const { data } = await axios.get(url, { params: { page: 15 } });
            return data;
        } catch (err) {
            throw new Error(err.message);
        }
    }

    data().then(result => {
        res.json(result);
    }).catch(err => {
        throw new Error(err.message);
    });

    console.log(data);
});

//searching for anime
// https://docs.consumet.org/rest-api/Anime/gogoanime/search
apiRouter.get('/media/searchAnime/:animeTitle', TokenMiddleware, (req, res) => {
    const url = "https://api.consumet.org/anime/gogoanime/" + req.params.animeTitle + "?page=2";
    const data = async () => {
        try {
            const { data } = await axios.get(url, { params: { page: 2 } });
            return data;
        } catch (err) {
            throw new Error(err.message);
        }
    };

    data().then(result => {
        res.json(result);
    }).catch(err => {
        throw new Error(err.message);
    });

    console.log(data);
});


// Uses the anime id to get the anime info as well as the anime episodes information. The anime information will also display the anime episode id that we will use to display the episode
// This api call is used to display the information and all the episodes of the anime
// https://docs.consumet.org/rest-api/Anime/gogoanime/get-anime-info
apiRouter.get('/media/animes/:animeId', TokenMiddleware, (req, res) => {
    // Using the example ID of "spy-x-family".
    const url = "https://api.consumet.org/anime/gogoanime/info/" + req.params.animeId;
    const data = async () => {
        try {
            const { data } = await axios.get(url);
            return data;
        } catch (err) {
            throw new Error(err.message);
        }
    };

    data().then(result => {
        res.json(result);
    }).catch(err => {
        throw new Error(err.message);
    });


    console.log(data);
});

// The user will pass in the specific anime episdoe id that is used to display the anime episode to the user
// The anime id here is useless and is only used to for the dev to further document the actions
// https://docs.consumet.org/rest-api/Anime/gogoanime/get-anime-episode-streaming-links
apiRouter.get('/media/animes/:animeId/:animeEpisodeId', TokenMiddleware, (req, res) => {
    const url = "https://api.consumet.org/anime/gogoanime/watch/" + req.params.animeEpisodeId;
    const data = async () => {
        try {
            const { data } = await axios.get(url, { params: { server: "gogocdn" } });
            return data;
        } catch (err) {
            throw new Error(err.message);
        }
    };

    data().then(result => {
        res.json(result);
    }).catch(err => {
        throw new Error(err.message);
    });

    console.log(data);
})

// We will use this call to just get the manga id and the title so that we can use it for the manga info and chapters
// The use of this is that the consumet api website doesn't have an api call to get mutliple mangas so we will use this for now as the id and consumet manga id are basically the same
// Thus interchangeable. Use the slug of this
// https://rapidapi.com/hefesto-technologies-hefesto-technologies-default/api/anime-manga-and-novels-api/
apiRouter.get('/media/mangas', TokenMiddleware, (req, res) => {

    const url = "https://api.mangadex.org/manga/random";
    const data = async () => {
        try {
            const { data } = await axios.get(url);
            return data;
        } catch (err) {
            throw new Error(err.message);
        }
    };

    data().then(result => {
        res.json(result);
    }).catch(err => {
        throw new Error(err.message);
    });

});

apiRouter.get('/media/searchManga/:mangaTitle', TokenMiddleware, (req, res) => {
    const url = "https://api.consumet.org/manga/mangadex/" + req.params.mangaTitle;
    const data = async () => {
        try {
            const { data } = await axios.get(url);
            return data;
        } catch (err) {
            throw new Error(err.message);
        }
    };

    data().then(result => {
        res.json(result);
    }).catch(err => {
        throw new Error(err.message);
    });
});

// Using the manga id we can get a manga info page that we can display to the users. 
// https://docs.consumet.org/rest-api/Manga/mangapark-v2/get-manga-info
apiRouter.get('/media/mangas/:mangaId', TokenMiddleware, (req, res) => {
    const url = "https://api.consumet.org/manga/mangadex/info/" + req.params.mangaId;
    const data = async () => {
        try {
            const { data } = await axios.get(url);
            return data;
        } catch (err) {
            throw new Error(err.message);
        }
    };

    data().then(result => {
        res.json(result);
    }).catch(err => {
        throw new Error(err.message);
    });


    console.log(data);
});

// This will return the specific manga chapter that the user will be able to read the manga chapter
// https://docs.consumet.org/rest-api/Manga/mangapark-v2/get-manga-chapter-pages
apiRouter.get('/media/mangas/:mangaId/:mangaChapter', TokenMiddleware, (req, res) => {
    const url = "https://api.consumet.org/manga/mangadex/read/" + req.params.mangaChapter;
    const data = async () => {
        try {
            const { data } = await axios.get(url);
            return data;
        } catch (err) {
            throw new Error(err.message);
        }
    };


    data().then(result => {
        res.json(result);
    }).catch(err => {
        throw new Error(err.message);
    });

    console.log(data);
});

// This will return a list of list of popular light novels that will show info that we can use to display that specific light novel
// https://rapidapi.com/tuefekci/api/web-novel-api
apiRouter.get('/media/lightNovels', TokenMiddleware, (req, res) => {

    const options = {
        method: 'GET',
        url: 'https://web-novel-api.p.rapidapi.com/novels/0',
        headers: {
            'X-RapidAPI-Key': process.env.X_RapidAPI_Key,
            'X-RapidAPI-Host': 'web-novel-api.p.rapidapi.com'
        }
    };

    axios.request(options).then(function (response) {
        console.log(response.data);
        res.json(response.data)
    }).catch(function (error) {
        console.error(error);
    });
});

apiRouter.get('/media/searchLightNovel/:lightNovelTitle', TokenMiddleware, (req, res) => {
    const options = {
        method: 'GET',
        url: 'https://web-novel-api.p.rapidapi.com/search/' + req.params.lightNovelTitle,
        headers: {
            'X-RapidAPI-Key': process.env.X_RapidAPI_Key,
            'X-RapidAPI-Host': 'web-novel-api.p.rapidapi.com'
        }
    };

    axios.request(options).then(function (response) {
        console.log(response.data);
        res.json(response.data);
    }).catch(function (error) {
        console.error(error);
    });
});

// Gets the info of the specific light novel 
// https://rapidapi.com/tuefekci/api/web-novel-api
apiRouter.get('/media/lightNovels/:lightNovelId', TokenMiddleware, (req, res) => {

    const options = {
        method: 'GET',
        url: 'https://web-novel-api.p.rapidapi.com/novel/' + req.params.lightNovelId,
        headers: {
            'X-RapidAPI-Key': process.env.X_RapidAPI_Key,
            'X-RapidAPI-Host': 'web-novel-api.p.rapidapi.com'
        }
    };

    axios.request(options).then(function (response) {
        console.log(response.data);
        res.json(response.data);
    }).catch(function (error) {
        console.error(error);
    });
});

// Gets the info of the specific light novel 
// https://rapidapi.com/tuefekci/api/web-novel-api
apiRouter.get('/media/lightNovels/:lightNovelId/chapters', TokenMiddleware, (req, res) => {

    const options = {
        method: 'GET',
        url: 'https://web-novel-api.p.rapidapi.com/novel/' + req.params.lightNovelId + '/chapters/cmVhZGxpZ2h0bm92ZWwubWU=',
        // url: 'https://web-novel-api.p.rapidapi.com/novel/fa102782f605163ddc1b3341709fd70221b4e23b/chapters/cmVhZGxpZ2h0bm92ZWwubWU=',
        headers: {
            'X-RapidAPI-Key': process.env.X_RapidAPI_Key,
            'X-RapidAPI-Host': 'web-novel-api.p.rapidapi.com'
        }
    };

    axios.request(options).then(function (response) {
        console.log(response.data);
        res.json(response.data);
    }).catch(function (error) {
        console.error(error);
    });
});

// Gets the chapter of the specific light novel 
// https://rapidapi.com/tuefekci/api/web-novel-api
apiRouter.get('/media/lightNovels/:lightNovelId/chapters', TokenMiddleware, (req, res) => {
    const options = {
        method: 'GET',
        url: 'https://web-novel-api.p.rapidapi.com/novel/' + req.params.lightNovelId + '/chapters/cmVhZGxpZ2h0bm92ZWwubWU=',
        // url: 'https://web-novel-api.p.rapidapi.com/novel/fa102782f605163ddc1b3341709fd70221b4e23b/chapters/cmVhZGxpZ2h0bm92ZWwubWU=',
        headers: {
            'X-RapidAPI-Key': process.env.X_RapidAPI_Key,
            'X-RapidAPI-Host': 'web-novel-api.p.rapidapi.com'
        }
    };

    axios.request(options).then(function (response) {
        console.log(response.data);
        res.json(response.data);
    }).catch(function (error) {
        console.error(error);
    });
});

// Gets the chapter of the specific light novel 
// https://rapidapi.com/tuefekci/api/web-novel-api
apiRouter.get('/media/lightNovels/:lightNovelId/chapter/:lightNovelChapter', TokenMiddleware, (req, res) => {

    const options = {
        method: 'GET',
        url: 'https://web-novel-api.p.rapidapi.com/novel/' + req.params.lightNovelId + "/chapter/" + req.params.lightNovelChapter,
        headers: {
            'X-RapidAPI-Key': process.env.X_RapidAPI_Key,
            'X-RapidAPI-Host': 'web-novel-api.p.rapidapi.com'
        }
    };

    axios.request(options).then(function (response) {
        console.log(response.data);
        res.json(response.data);
    }).catch(function (error) {
        console.error(error);
    });
});

apiRouter.post('/favorites/insert', TokenMiddleware, (req, res) => {
    let favMedia = req.body;

    FavoriteDAO.addToFavorites(favMedia).then(data => {
        FavoriteDAO.getFavoriteById(data).then(result => {
            res.json(result);
        });
    });
});

apiRouter.delete('/favorites/remove/:mediaId', TokenMiddleware, (req, res) => {
    FavoriteDAO.removeFavorite(req.params.mediaId).then(data => {
        res.status(200).json("Done");
    });
})

apiRouter.get('/favorites/:mediaId', TokenMiddleware, (req, res) => {
    FavoriteDAO.getFavoriteById(req.params.favId).then(data => {
        res.json(data);
    });
});

apiRouter.get('/favorites/users/:usrId', TokenMiddleware, (req, res) => {
    FavoriteDAO.getFavoriteByUserId(req.params.usrId).then(data => {
        res.json(data);
    });
});

apiRouter.post('/history/createHistory', TokenMiddleware, (req, res) => {
    let hist = req.body;
    HistoryDAO.createHistory(hist).then(data => {
        HistoryDAO.getHistoryById(data).then(result => {
            res.json(result);
        });
    });
});

apiRouter.get('/history/userHistory/:userId', TokenMiddleware, (req, res) => {
    HistoryDAO.getUserHistory(req.params.userId).then(data => {
        res.json(data);
    });
});

apiRouter.post('/users/signup', (req, res) => {
    let info = req.body;
    UserDAO.checkUsername(info.username).then(data => {
        if(data.length == 0) {
            salt().then(secretSalt => {
                hashPassword(info.password, secretSalt).then(pass => {
                    UserDAO.createUser(info, pass, secretSalt).then(data => {
                        UserDAO.getUserById(data).then(user => {
                            let result = user; 
        
                            generateToken(req, res, user);
        
                            res.json(result);
                        });
                    });
                });
            });
        }
        else {
            res.status(400).json({error: 'username is existed'});
        }
    });
});

const salt = () => {
    return new Promise((resolve, reject) => {
        crypto.randomBytes(28, (err, key) => {
            if (err) {
                reject("Error: " + err);
            }
            else {
                resolve(key.toString('hex'));
            }
        });
    });
};

const hashPassword = (password, salt) => {
    return new Promise((resolve, reject) => {

        crypto.pbkdf2(password, salt, 100000, 64, 'sha512', (err, key) => {
            resolve(key.toString('hex'));
        });

    });
};

apiRouter.post('/users/login', (req, res) => {

    if (req.body.username && req.body.password) {
        UserDAO.getUserByCredentials(req.body.username, req.body.password).then(user => {
            let result = {
                user: user
            }

            generateToken(req, res, user);

            res.json(result);
        }).catch(err => {
            res.status(400).json({ error: err });
        });
    }
    else {
        res.status(401).json({ error: 'Not authenticated' });
    }
});

apiRouter.get('/users/current', TokenMiddleware, (req, res) => {
    res.json(req.user);
});

apiRouter.post('/users/logout', (req, res) => {
    removeToken(req, res);
    res.json({ success: true });
});

module.exports = apiRouter