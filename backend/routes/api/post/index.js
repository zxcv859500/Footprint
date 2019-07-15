const router = require('express').Router();
const controller = require('../../../controller/index');
const upload = require('../../../multer');
const auth = require('../../../middlewares/auth');

router.use('/write/typea', auth);
router.post('/write/typea', upload.single('picture'), (req, res, next) => {
    if(!req.filename) {
        res.status(403).json({
            error: "No picture data error"
        })
    }
    const data = {
        title: req.body.title,
        content: req.body.content,
        author: req.decoded.username,
        picture: req.file.filename,
        latitude: req.body.latitude,
        longitude: req.body.longitude,
        road: req.body.road,
        date: new Date()
    };

    console.log(data);

    controller.post.create(data)
        .then(() => {
            res.status(200).json({
                message: "Posting success"
            });
        })
        .catch((err) => {
            res.status(500).json({
                error: err.message
            });
        })
});

module.exports = router;