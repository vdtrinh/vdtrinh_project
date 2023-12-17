const mysql = require('mysql'); 

let connection; 

exports.getDatabaseConnection = () => {
    if(!connection) {
        connection = mysql.createConnection({
            host: process.env.DB_HOST, 
            port: process.env.DB_PORT, 
            user: process.env.MYSQL_USER,
            password: process.env.MYSQL_PASSWORD, 
            database: process.env.MYSQL_DATABASE, 
            charset: process.env.DB_CHARSET
        })
    }
    return connection; 
};

exports.query = (query, params = []) => {
    return new Promise((resolve, reject) => {
        if(!connection) {
            connection = exports.getDatabaseConnection(); 
        }
        connection.connect(function (err) {
            if(err) {
                reject(err); 
                return ("NOT CONNECTED!"); 
            }
            else {
                connection.query(query, params, (err, results, fields) => {
                    if(err) {
                        reject(err);
                        return ("CONNECTED BUT ERROR"); 
                    }
                    resolve({
                        results: results, 
                        fields: fields
                    })
                })
            }
        });
    });
};

exports.close = () => {
    if(connection) {
        connection.end();
        connection = null; 
    }
};