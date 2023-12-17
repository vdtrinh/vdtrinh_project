const API_BASE = '/api'; 

const HTTPClient = {
    get: (url) => {
        return fetch(`${API_BASE}${url}`)
        .then(res => {
            if(res.ok) {
                return res.json(); 
            }
            throw new Error('Network response was not ok.');
        })
        .then(obj => {
            console.log(obj); 
            return obj; 
        })
        .catch(err => console.log(err)); 
    },

    post: (url, data) => {
        return fetch(`${API_BASE}${url}`, {
            method: 'POST', 
            body: JSON.stringify(data), 
            headers: {
                'Content-Type': 'application/json'
            }, 
        }).then(res => {
             res.json();
        }).then(data => console.log(data)) 
    }, 

    delete: (url) => {
        return fetch(`${API_BASE}${url}`, {
            method: 'DELETE',
            headers: {}
        }).then(res => {
            if(res.ok) {
                return res.json(); 
            }
            throw new Error('Network response was not ok.');
        }).then(obj => {
            console.log(obj); 
            return obj;
        }).catch(err => console.log(err)); 
    }
};

export default {
    login: (username) => {
        return HTTPClient.get(`/users/${username}`); 
    },

    current: (current) => {
        return HTTPClient.get(`/current/users/${current}`); 
    }, 

    userId: (usId) => {
        return HTTPClient.get(`/follows/${usId}`); 
    },

    getUserById: (userId) => {
        return HTTPClient.get(`/getUsersById/users/${userId}`); 
    }, 

    howls: (currentId) => {
        return HTTPClient.get(`/follows/${currentId}/howl`); 
    }, 

    postHowl: (id, content) => {
        return HTTPClient.post(`/postHowl/${id}/howls`, content); 
    }, 

    howlByUserId: (profileId) => {
        return HTTPClient.get(`/users/${profileId}/howl`); 
    },

    userUnfollow: (unfollowUser, currentUser) => {
        return HTTPClient.delete(`/${unfollowUser}/${currentUser}/follows`); 
    },

    followUser: (currentUserId, followUserId) => {
        return HTTPClient.post(`/followUser/${currentUserId}/follows`, followUserId); 
    }
};