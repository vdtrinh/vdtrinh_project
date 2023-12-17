const jwt = require('jsonwebtoken');

const TOKEN_COOKIE_NAME = "Otakhub_User_Token";

const API_SECRET = process.env.API_SECRET_KEY;

exports.generateToken = (req, res, user) => {
    let data = {
        user: user
    }

    const token = jwt.sign(data, API_SECRET);

    res.cookie(TOKEN_COOKIE_NAME, token, {
        httpOnly: true,
        secure: true
    });
};

exports.removeToken = (req, res) => {
    res.cookie(TOKEN_COOKIE_NAME, "", {
        httpOnly: true,
        secure: true,
        maxAge: -360000
    });
};

exports.TokenMiddleware = (req, res, next) => {

    let token = null;
    if (!req.cookies[TOKEN_COOKIE_NAME]) {
        //No cookie, so let's check Authorization header
        const authHeader = req.get('Authorization');
        if (authHeader && authHeader.startsWith("Bearer ")) {
            //Format should be "Bearer token" but we only need the token
            token = authHeader.split(" ")[1];
        }
    }
    else { //We do have a cookie with a token
        token = req.cookies[TOKEN_COOKIE_NAME]; //Get session Id from cookie
    }

    if (!token) { // If we don't have a token
        res.status(401).json({ error: 'Not authenticated' });
        return;
    }

    //If we've made it this far, we have a token. We need to validate it

    try {
        const decoded = jwt.verify(token, API_SECRET);
        req.user = decoded.user;
        next(); //Make sure we call the next middleware
    }
    catch (err) { //Token is invalid
        res.status(401).json({ error: 'Not authenticated' });
        return;
    }

};