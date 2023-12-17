const db = require('./DBConnection');
const User = require('./User');
const crypto = require('crypto');

function getUserByCredentials(username, password) {
    return db.query(`
    SELECT * 
    FROM user
    WHERE usr_username = ?`, [username]).then(({ results }) => {
        const user = new User(results[0]);
        if (user) {
            db.close();
            return user.validatePassword(password);
        }
        else {
            db.close(); 
            throw new Error("No such user");
        }
    });
}

function createUser(data, secretPass, secretSalt) {
    return db.query('INSERT INTO user (usr_first_name, usr_last_name, usr_username, usr_password, usr_salt, usr_avatar) VALUES (?, ?, ?, ?, ?, ?)',
        [data.first_name, data.last_name, data.username, secretPass, secretSalt, data.avatar]).then(({ results }) => {
            db.close();
            return results.insertId;
        });
}

function getUserById(usrId) {
    return db.query('SELECT * FROM user WHERE usr_id=?', [usrId]).then(({results}) => {
        if(results[0]) {
            db.close(); 
            return new User(results[0]);
        }
    });
}

function checkUsername(username) {
    return db.query('SELECT * FROM user WHERE usr_username=?', [username]).then(({results}) => {
        if(results.length === 0) {
            db.close(); 
            return results; 
        }
        else {
            db.close();
            return new User(results[0]);
        }
    });
}

module.exports = {
    getUserByCredentials: getUserByCredentials,
    createUser: createUser,
    getUserById: getUserById,
    checkUsername: checkUsername
}; 