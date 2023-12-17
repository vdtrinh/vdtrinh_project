const db = require('./DBConnection');
const History = require('./History');

function getHistoryById(hisId) {
    return db.query('SELECT * FROM history WHERE his_id=?', [hisId]).then(({results}) => {
        if(results[0]) {
            db.close(); 
            return new History(results[0]);
        }
    });
}

function createHistory(data) {
    return db.query('INSERT INTO history (his_media_type, his_media_id, his_media_title, his_cover, usr_id) VALUES (?, ?, ?, ?, ?)',
    [data.type, data.mediaId, data.title, data.cover, data.usrId]).then(({results}) => {
        db.close(); 
        return results.insertId; 
    });
}

function getUserHistory(usrId) {
    return db.query('SELECT * FROM history WHERE usr_id=?', [usrId]).then(({results}) => {
        db.close(); 
        return results.map(data => new History(data));
    });
}

module.exports = {
    getHistoryById: getHistoryById,
    createHistory: createHistory,
    getUserHistory: getUserHistory
};