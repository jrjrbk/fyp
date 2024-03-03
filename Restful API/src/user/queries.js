//==============Queries==============
const getUser = "SELECT * FROM users";

const getStudentbyEmail = "SELECT * FROM users WHERE email=$1";

const checkPassword = "SELECT * FROM users WHERE email=$1 AND password=$2";

const getUserID = "SELECT \"accountID\" from users WHERE email = $1 and password= $2";

const checkEmailExists = "SELECT u FROM users u WHERE u.email = $1";

const addUser = 'INSERT INTO users (email,password,username, "dateCreated") VALUES ($1,$2,$3,$4)';

const addFeedback = 'INSERT INTO feedback ("accountID", "feedbackType", "comments", rating) VALUES ($1,$2,$3,$4)';

const getUsernameByID = 'SELECT username FROM users WHERE \"accountID\"=$1';

const getGame = 'SELECT * FROM game WHERE \"accountID\"=$1';

const addGame = 'INSERT INTO game (\"accountID\") VALUES ($1)';

const updateGame = "UPDATE game SET score = $1 WHERE \"accountID\" = $2";

const addPlaylist = "INSERT INTO playlist (\"playlistName\",\"playlistDate\",\"accountID\") VALUES ($1,$2,$3)";

const getPlaylist = "SELECT * FROM playlist WHERE \"accountID\"=$1";

const deletePlaylist = "DELETE FROM playlist WHERE \"playlistID\"=$1";

const addSongPlaylist = "INSERT INTO playlist_metadata (\"playlistID\",\"playlistSongRef\",\"playlistArtistRef\") VALUES ($1,$2,$3)";

const getSpecificPlaylist = "SELECT * FROM playlist WHERE \"playlistID\" = $1";

const getPlaylistMeta = "SELECT * FROM playlist_metadata WHERE \"playlistID\" = $1";

const deleteSong = "DELETE FROM playlist_metadata WHERE \"playlistSongRef\"=$1";

const updatePlaylist = "UPDATE playlist SET \"playlistName\" = $1, \"playlistImage\" = $2 WHERE \"playlistID\" = $3";

const updateUsername = "UPDATE users SET \"username\" =$1 WHERE \"accountID\" =$2";

const updateProfileImage = "UPDATE \"userImg\" SET \"imagePath\" = $1 WHERE \"accountID\" = $2";

const checkProfileImage = "SELECT * FROM \"userImg\" WHERE \"accountID\" = $1";

const addProfileImage = "INSERT INTO \"userImg\" (\"accountID\",\"imagePath\") VALUES ($1,$2)";

const getAbout = "SELECT * FROM \"about\"";

const updateAbout = "UPDATE about SET \"aboutVersion\" = $1,\"aboutEmail\" =$2,\"aboutNumber\"=$3,\"aboutInfo\"=$4";

const getFeedback = "SELECT * FROM feedback"; 

const deleteFeedback = "DELETE FROM feedback WHERE \"feedbackID\" = $1";

const getQuery = "SELECT * FROM search WHERE \"accountID\" = $1 ";

const saveQuery = "INSERT INTO \"search\" (\"accountID\",\"query\") VALUES ($1,$2)";

//==============END==================

module.exports = {
    getUser,
    checkEmailExists,
    addUser,
    getStudentbyEmail,
    checkPassword,
    getUserID,
    addFeedback,
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
    checkProfileImage,
    addProfileImage,
    getAbout,
    updateAbout,
    getFeedback,
    deleteFeedback,
    getQuery,
    saveQuery
};