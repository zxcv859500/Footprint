const router = require('express').Router();
const controller = require('../../../controller/index');
const upload = require('../../../multer');
const auth = require('../../../middlewares/auth');

router.use('/write', auth);
router.post('/write', upload.single('picture'), (req, res, next) => {
    if(req.filename) {
        res.status(403).json({
            error: "No picture data error"
        })
    }
    const data = {
        title: req.body.title,
        content: req.body.content,
        author: req.decoded.nickname,
        picture: req.file.filename,
        latitude: req.body.latitude,
        longitude: req.body.longitude,
        road: req.body.road,
        type: req.body.type,
        date: new Date()
    };

    controller.post.create(data)
        .then(() => {
            res.status(200).send(data);
        })
        .catch((err) => {
            res.status(500).json({
                error: err.message
            });
        })
});

router.post('/list', (req, res, next) => {
   const { latitude, longitude } = req.body;

   controller.post.getList(latitude, longitude)
       .then((result) => {
           res.status(200).send(result);
       })
       .catch((err) => {
           res.status(500).json({
               Error: err.message
           });
       })
});

router.get('/:id', (req, res, next) => {
    const postId = req.params.id;

    controller.post.getPost(postId)
        .then((result) => {
            res.status(200).send(result);
        })
        .catch((err) => {
            res.status(500).json({
                Error: err.message
            });
        })
});

router.use('/:id/like', auth);
router.get('/:id/like', (req, res, next) => {
    const postId = req.params.id;
    const {username} = req.decoded;

    controller.post.likeHandle(postId, username)
        .then(() => {
            res.status(200).json({
                postId: postId,
                username: username
            })
        })
        .catch((err) => {
            res.status(409).json({
                Error: err.message
            })
        })
});

module.exports = router;