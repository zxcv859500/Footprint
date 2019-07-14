const router = require('express').Router();
const controller = require('../../../controller/index');
const upload = require('../../../multer');
const auth = require('../../../middlewares/auth');

router.use('/write/typea', auth);
router.post('/write/typea', upload.single('photo'), (req, res, next) => {
    const data = {
        title: req.body.title,
        content: req.body.content,
        author: req.decoded.username,
        picture: (req.file) ? req.file.filename : "",
        latitude: req.body.latitude,
        longitude: req.body.longitude,
        road: req.body.road
    };

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