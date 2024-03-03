//import the database
const pool = require('../../db');
//import queries.js
const queries = require('./queries');

//======================Controllers=================
//1.
const getUser = (req,res) =>{
    pool.query(queries.getUser, (error,results) =>{
        if(error) throw error;
        res.status(200).json(results.rows);
    });
};

//2.
const addUser = (req,res) =>{
    const {email,password,username,dateCreated} = req.body;

    // Check if email is valid/exist
    pool.query(queries.checkEmailExists, [email],(error, results)=>{
        console.log("result length = " + results.rows.length)
        if(results.rows.length >= 1){ // if(0 = no email, 1 = got email)
            res.status(409).send("{\"message\": \"Email already exists.\"}")
        }else{
            pool.query(queries.addUser, [email,password,username,dateCreated], (error,results) =>{
                if(error) throw error;
    
                res.setHeader('content-type','application/json')
                res.status(201).send("{\"message\": \"User data added\"}");
                console.log("Users Created");
            })
        }
    });
};

const getStudentbyEmail = (req,res)=>{
    const email = req.params.email;
    
    pool.query(queries.getStudentbyEmail, [email], (error, results) =>{
        if (error) throw error
        res.status(200); //.json("{"+results.rows+"}");
        console.log(results.rows)

        if(!results.rows.length){
            res.status(400);
        }else{
            //check password
            pool.query(queries.checkPassword, [email,password],(error,results)=>{
                if(!results.rows.length){
                    res.status(400);
                }else{
                    res.status(200)
                }
            });
        }
    });

};

const loginUser = (req,res) =>{
    const {email,password} = req.body;

    //Check if email and password is valid
    pool.query(queries.checkPassword, [email,password],(error,results)=>{
        if(!results.rows.length){
            res.status(400).send();
            console.log("FAIL");
        }else{
            pool.query(queries.getUserID, [email,password], (error,results)=>{
                res.status(200).send(results.rows[0]);
                console.log("LOGIN" + results.rows);
            });
        }
    });
};

const rateUser = (req,res) =>{
    const {AccountID,feedbackType, comments, rating} = req.body;
    
    console.log(AccountID);
    console.log(feedbackType)
    console.log(comments)
    console.log(rating)
    pool.query(queries.addFeedback,[AccountID,feedbackType,comments,rating], (error,results)=>{
        if(error) throw error;
    
        res.setHeader('content-type','application/json')
        res.status(201).send("{\"message\": \"Feedback added\"}");
        console.log("Feedback Created");
        console.log(results.rows)
    })
}


const getUsernameByID = (req,res) =>{
    const id = req.params.id;
    console.log("gello")

    pool.query(queries.getUsernameByID, [id], (error,results) =>{
        if(error) throw error;
        res.status(200).send(results.rows[0])
        console.log(results.rows);
    });
}

const getGame = (req,res) =>{
    const id = req.params.id;
    
    pool.query(queries.getGame, [id], (error,results) =>{
        if(error) throw error;
        res.status(200).send(results.rows[0])
        console.log(results.rows)
    });
}


const addGame = (req,res) =>{
    const {accountID} = req.body;

    pool.query(queries.addGame, [accountID], (error,results) =>{
        if (error) throw error;
        res.status(201).send("Noice")
        console.log("Game Added")
    });
}

const updateGame = (req,res) =>{
    const id = req.params.id;
    const {score} = req.body; //from client

    pool.query(queries.updateGame, [score,id] ,(error,results)=>{
        if (error) throw error;
        res.status(200).send("Game Updated Successfully!");
    });
};


const addPlaylist = (req,res) => {
    const {playlistName, playlistDate,accountID} = req.body;

    pool.query(queries.addPlaylist, [playlistName,playlistDate,accountID],(error,results) =>{
        if (error) throw error;
        res.status(201).send("Playlist Added")
        console.log("Playlist Added")
    });
}

const getPlaylist = (req,res) => {
    const id = req.params.id;

    pool.query(queries.getPlaylist, [id], (error,results) =>{
        if(error) throw error;
        res.status(200).send(results.rows)
        console.log(results.rows);
    });
}

const deletePlaylist = (req,res) => {
    const id = req.params.id;
    pool.query(queries.deletePlaylist, [id], (error,results) =>{
        if (error) throw error;
        res.status(200).send("Playlist removed successfully!");
    });
}

const addSongPlaylist = (req,res) => {
    const {playlistID, playlistSongRef, playlistArtistRef} = req.body;

    pool.query(queries.addSongPlaylist, [playlistID,playlistSongRef,playlistArtistRef],(error,results) =>{
        if (error) throw error;
        res.status(201).send("Song Added");
        console.log("Song Added");
    });

}

const getSpecificPlaylist = (req,res) => {
    const id = req.params.id;

    pool.query(queries.getSpecificPlaylist, [id], (error,results) =>{
        if (error) throw error;
        res.status(200).send(results.rows)
        console.log(results.rows)
    });
}

const getPlaylistMeta = (req,res) => {
    const id = req.params.id;
    pool.query(queries.getPlaylistMeta, [id], (error,results) =>{
        if (error) throw error;
        res.status(200).send(results.rows)
        console.log(results.rows)
    });
}

const deleteSong = (req,res) =>{
    const playlistSongRef = req.params.playlistSongRef;
    pool.query(queries.deleteSong, [playlistSongRef],(error,results)=>{
        if (error) throw error;
        res.status(200).send("Song removed successfully");
    });
}

const updatePlaylist = (req,res) =>{
    const id =req.params.id;
    const {playlistName,playlistImage} = req.body;

    pool.query(queries.updatePlaylist, [playlistName,playlistImage,id], (error,results) =>{
        if (error) throw error;
        res.status(200).send("Playlist Updated Successfully!");
    })
}

const updateUsername = (req,res) =>{
    const id = req.params.id;
    const{username} =req.body;

    pool.query(queries.updateUsername, [username, id], (error,results) =>{
        if (error) throw error;
        res.status(200).send("Username Updated Successfully!")
    })
}

const updateProfileImage = (req,res) =>{
    const id = req.params.id;
    const{imagePath} = req.body;
    
    pool.query(queries.checkProfileImage, [id], (error,results) =>{
        if(!results.rows.length){
            pool.query(queries.addProfileImage, [id,imagePath],(error,results) =>{
                if (error) throw error;
                res.status(201).send("Profile Image Added");
                console.log("Image Added");
            });
        } else{
            pool.query(queries.updateProfileImage, [imagePath,id], (error,results) =>{
                if (error) throw error;
                res.status(200).send("Profile Image Updated Successfully!");
            })
        }
    })
}

const getProfileImage = (req,res) =>{
    const id = req.params.id;
    
    pool.query(queries.checkProfileImage, [id],(error,results)=>{
        if (error) throw error;
        res.status(200).send(results.rows)
        console.log("Noice")
    })
}


const updateAbout = (req,res) =>{
    const {aboutVersion, aboutEmail, aboutNumber, aboutInfo} = req.body;
    pool.query(queries.updateAbout, [aboutVersion,aboutEmail,aboutNumber,aboutInfo],(error,results)=>{
        if (error) throw error;
        res.status(200).send(results.rows)
        console.log("about is the updated")
    });
}

const getFeedback = (req,res) =>{
    pool.query(queries.getFeedback, (error,results) =>{
        if(error) throw error;
        res.status(200).json(results.rows);
    })
}

const getAbout = (req,res) =>{
    pool.query(queries.getAbout, (error,results) =>{
        if(error) throw error;
        res.status(200).json(results.rows);
    });
};

const deleteFeedback = (req,res) =>{
    const id = req.params.id;
    pool.query(queries.deleteFeedback, [id], (error,results) =>{
        if (error) throw error;
        res.status(200).send("Feedback Successfully Removed!")
    })
}

const getQuery = (req,res) =>{
    const id = req.params.id;
    pool.query(queries.getQuery,[id], (error,results)=>{
        if(error) throw error;
        res.status(200).json(results.rows);
    })
}

const saveQuery = (req,res) =>{
    const {accountID, query} = req.body;
    pool.query(queries.saveQuery, [accountID,query], (error,results)=>{
        if (error) throw error;
        res.status(201).send("Query Added");
    });
}
//======================END==========================

module.exports = {
    getUser,
    addUser,
    getStudentbyEmail,
    loginUser,
    rateUser,
    getUsernameByID,
    addGame,
    getGame,
    updateGame,
    addPlaylist,
    getPlaylist,
    deletePlaylist,
    addSongPlaylist,
    getSpecificPlaylist,
    getPlaylistMeta,
    deleteSong,
    updatePlaylist,
    updateUsername,
    updateProfileImage,
    getProfileImage,
    getAbout,
    updateAbout,
    getFeedback,
    deleteFeedback,
    getQuery,
    saveQuery
};