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

module.exports = router;