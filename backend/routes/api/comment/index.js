const router = require('express').Router();
const controller = require('../../../controller/mysql');
const auth = require('../../../middlewares/auth');

router.use('/:id/write', auth);
router.post('/:id/write', (req, res, next) => {
    const { nickname } = req.decoded;
    const postId = req.params.id;
    const { content } = req.body;

    if (!content || content.trim() === '') {
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

router.use('/:id/delete', auth);
router.get('/:id/delete', (req, res, next) => {
    const { nickname } = req.decoded;
    const commentId = req.params.id;

    controller.comment.delete(nickname, commentId)
        .then(() => {
            res.status(200).json({
                Message: "Delete complete"
            })
        })
        .catch((err) => {
            res.status(409).json({
                Error: err.message
            })
        })
});

router.use('/:id/edit', auth);
router.post('/:id/edit', (req, res, next) => {
    const { nickname } = req.decoded;
    const commentId = req.params.id;
    const { content } = req.body;

    if (!content) {
        res.status(409).json({
            Error: "Content required"
        })
    }

    controller.comment.edit(nickname, commentId, content)
        .then(() => {
            res.status(200).json({
                author: nickname,
                content: content
            })
        })
        .catch((err) => {
            res.status(409).json({
                Error: err.message
            })
        })
});

router.use('/:id/like', auth);
router.get('/:id/like', (req, res, next) => {
    const commentId = req.params.id;
    const { username } = req.decoded;

    controller.like.commentLikeHandle(commentId, username)
        .then(() => {
            res.status(200).json({
                commentId: commentId,
                username: username
            })
        })
        .catch(() => {
            res.status(409).json({
                Error: err.message
            })
        })
});

router.use('/:id/like/cancel', auth);
router.get('/:id/like', (req, res, next) => {
    const commentId = req.params.id;
    const { username } = req.decoded;

    controller.like.commentLikeCancel(commentId, username)
        .then(() => {
            res.status(200).json({
                message: "Comment like cancel complete"
            })
        })
        .catch((err) => {
            res.status(409).json({
                Error: err.message
            })
        })
});

module.exports = router;