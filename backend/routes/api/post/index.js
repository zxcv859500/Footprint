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

   if (!latitude || !longitude) {
       res.status(409).json({
           Error: "Latitude and longitude required"
       })
   }

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

    controller.like.likeHandle(postId, username)
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

router.use('/:id/like/cancel', auth);
router.get('/:id/like/cancel', (req, res, next) => {
    const postId = req.params.id;
    const {username} = req.decoded;

    controller.like.likeCancel(postId, username)
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

router.use('/:id/comment', auth);
router.post('/:id/comment', (req, res, next) => {
    const { nickname } = req.decoded;
    const postId = req.params.id;
    const { content } = req.body;

    if (content.trim() === '') {
        res.status(409).json({
            Error: "Content empty"
        })
    } else {
        controller.comment.write(nickname, postId, content)
            .then(() => {
                res.status(200).json({
                    nickname: nickname,
                    postId: postId,
                    content: content
                })
            })
            .catch((err) => {
                res.status(409).json({
                    Error: err.message
                })
            })
    }
});

router.use('/:id/edit', auth);
router.post('/:id/edit', (req, res, next) => {
    const data = {
        author: req.decoded.nickname,
        postId: req.params.id,
        title: req.body.title,
        content: req.body.content
    };

    if (!data.title || !data.content || data.title.trim() === '' || data.content.trim() === '') {
        res.status(409).json({
            Error: "Empty title or content"
        })
    }

    controller.post.edit(data)
        .then(() => {
            res.status(200).json({
                postId: data.postId,
                title: data.title,
                content: data.content
            })
        })
        .catch((err) => {
            res.status(409).json({
                Error: err.message
            })
        })
});

router.use('/:id/delete', auth);
router.get('/:id/delete', (req, res, next) => {
    const data = {
        author: req.decoded.nickname,
        postId: req.params.id
    };

    controller.post.delete(data)
        .then(() => {
            res.status(200).json({
                message: "Delete complete"
            })
        })
        .catch((err) => {
            res.status(409).json({
                Error: err.message
            })
        })
});

module.exports = router;