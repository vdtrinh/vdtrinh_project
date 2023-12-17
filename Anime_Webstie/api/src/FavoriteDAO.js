const db = require('./DBConnection');
const Favorite = require('./Favorite');

function getFavoriteById(favId) {
    return db.query('SELECT * FROM favorite WHERE fav_id=?', [favId]).then(({results}) => {
        if(results[0]) {
            db.close(); 
            return new Favorite(results[0]);
        }
    });
}

function getFavoriteByMediaId(mediaId) {
    return db.query('SELECT * FROM favorite WHERE fav_media_id=?', [mediaId]).then(({results}) => {
        if(results[0]) {
            db.close();
            return new Favorite(results[0]);  
        }
    });
}

function getFavoriteByMediaType(mediaType, usrId) {
    return db.query('SELECT * FROM favorite WHERE fav_media_type=? AND usr_id=?', [mediaType, usrId]).then(({results}) => {
        if(results[0]) {
            db.close();
            return new Favorite(results[0]);  
        }
    });
}

function addToFavorites(data) {
    return db.query('INSERT INTO favorite (fav_media_type, fav_media_id, fav_media_title, fav_cover, fav_holder, usr_id) VALUES (?, ?, ?, ?, ?, ?)', 
    [data.type, data.mediaId, data.title, data.cover, data.holder, data.usrId]).then(({results}) => {
        db.close(); 
        return results.insertId; 
    });
}

function getFavoriteByUserId(usrId) {
    return db.query('SELECT * FROM favorite WHERE usr_id=?', [usrId]).then(({results}) => {
        db.close(); 
        return results.map(data => new Favorite(data));
    });
}

function removeFavorite(mediaId) {
    return db.query('DELETE FROM favorite WHERE fav_media_id=?', [mediaId]).then(({results}) => {
        return; 
    });
}

module.exports = {
    getFavoriteById: getFavoriteById,
    addToFavorites: addToFavorites,
    getFavoriteByMediaType: getFavoriteByMediaType,
    getFavoriteByUserId: getFavoriteByUserId,
    removeFavorite: removeFavorite,
    getFavoriteByMediaId: getFavoriteByMediaId
};