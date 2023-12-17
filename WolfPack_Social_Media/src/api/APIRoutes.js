const express = require('express');
const apiRouter = express.Router(); 

let users = require('../data/users.json');
let follows = require('../data/follows.json'); 
let howls = require('../data/howls.json');

apiRouter.use(express.json());

apiRouter.get('/users/:username', (req, res) => {
    const username = req.params.username; 
    let user = users.find(user => user.username == username); 
    if(user) {
        res.json(user); 
    }
    else {
        res.status(404).json({error: 'User not found'});
    }
}), 

apiRouter.get('/current/users/:username', (req, res) => {
    const currUser = req.params.username; 
    let currentUser = users.find(curUser => curUser.username == currUser); 
    if(currentUser) {
        res.json(currentUser); 
    }
    else {
        res.status(404).json({error: 'User not found'});
    }
}),

apiRouter.get('/getUsersById/users/:userId', (req, res) => {
    const userId = req.params.userId; 
    let user = users.find(user => user.id == userId); 
    if(user) {
        res.json(user);
    } else {
        res.status(404).json({error: 'User not found'});
    }
}),

apiRouter.get('/follows/:userId/howl', (req, res) => {
    const currentId = req.params.userId; 
    let listFollow = follows[currentId].following; 

    const listOfFollower = howls.filter(howl => listFollow.includes(howl.userId) || howl.userId == currentId); 
    const findById = (id) => users.find(user => user.id == id); 
    listOfFollower.forEach(howl => howl['user'] = findById(howl.userId)  || findById(currentId)); 

    if(listOfFollower) {
        res.json(listOfFollower); 
    }
    else{
        res.status(404).json({error: 'User not found' });
    }
}), 

apiRouter.get('/users/:userId/howl', (req, res) => {
    const profileId = req.params.userId; 
    const profileHowl = howls.filter(howl => howl.userId == profileId); 
    
    const findById = (id) => users.find(user => user.id == id); 
    profileHowl.forEach(howl => howl['user'] = findById(howl.userId)); 

    if(profileHowl){
        res.json(profileHowl);
    } 
    else{
        res.status(404).json({error: 'User not found' });
    }
}),

apiRouter.get('/follows/:userId', (req, res) => {
    let userId = req.params.userId; 
    let userList = follows[userId].following; 
    const listFollowing = users.filter(user => userList.includes(user.id)); 

    if(listFollowing) {
        res.json(listFollowing); 
    }
    else {
        res.status(404).json({error: 'User not found' });
    }
}),

apiRouter.post('/postHowl/:id/howls', (req, res) => {
    let userPostId = req.params.id; 
    let howlContent = req.body; 

    let oldId = howls.slice(-1); 
    let newId = oldId[0].id; 
    newId += 1;
    
    let newHowl = {
        "id": newId, 
        "userId": userPostId, 
        "datetime": howlContent.datetime,
        "text": howlContent.text
    }
    howls.push(newHowl); 
    res.json(newHowl); 
}), 

apiRouter.delete('/:unfollowUser/:currentUser/follows', (req, res) => {
    let currentUserId = req.params.currentUser; 
    let unfollowUser = req.params.unfollowUser;
    let newList = [];

    for(let i = 0; i < follows[currentUserId].following.length; i++) {
        if(follows[currentUserId].following[i] != unfollowUser) {
            newList.push(follows[currentUserId].following[i]); 
        }
    }

    follows[currentUserId].following = newList; 

    if(follows[currentUserId].following) {
        res.json(follows[currentUserId].following);
    }
    else {
        res.status(404).json({error: 'Can not unfollow' });
    }
}), 

apiRouter.post('/followUser/:currentUserId/follows', (req, res) => {
    let currentUserId = req.params.currentUserId; 
    let followUser = req.body; 
    follows[currentUserId].following.push(followUser.id);

    let newList = follows[currentUserId].following; 

    if(newList) {
        res.json(newList); 
    }
    else {
        res.status(404).json({error: 'User not found' });
    }
})

module.exports = apiRouter; 