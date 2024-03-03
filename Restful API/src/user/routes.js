const {Router} = require('express');

const controller = require('./controller');

const router = Router();

//===============ROUTES==============
//GET,POST,DELETE,PUT

//Test - GET USER info
router.get("/", controller.getUser);

router.post("/", controller.addUser);

router.get("/:email", controller.getStudentbyEmail);

router.post("/login", controller.loginUser);

router.post("/rate", controller.rateUser);

router.get("/id/:id", controller.getUsernameByID); 

router.get("/game/:id", controller.getGame);

router.post("/game", controller.addGame);

router.put("/game/:id", controller.updateGame);

router.post("/playlist", controller.addPlaylist)

router.get("/playlist/:id", controller.getPlaylist)

router.delete("/playlist/:id", controller.deletePlaylist)

router.get("/playlist/:id/specific", controller.getSpecificPlaylist)

router.post("/addsongplaylist", controller.addSongPlaylist)

router.get("/getPlaylistMeta/:id", controller.getPlaylistMeta)

router.delete("/playlist/:playlistSongRef/song", controller.deleteSong)

router.put("/playlist/:id", controller.updatePlaylist)

router.put("/id/:id", controller.updateUsername)

router.put("/idImg/:id", controller.updateProfileImage)

router.get("/idImg/:id", controller.getProfileImage)

router.get("/abouts/s", controller.getAbout)

router.put("/abouts", controller.updateAbout)

router.get("/feedback/get", controller.getFeedback)

router.delete("/feedback/:id", controller.deleteFeedback)

router.get("/query/:id", controller.getQuery)

router.post("/query", controller.saveQuery)

//=================END===============

module.exports=router;