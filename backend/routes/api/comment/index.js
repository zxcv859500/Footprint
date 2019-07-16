const router = require('express').Router();
const controller = require('../../../controller/index');
const auth = require('../../../middlewares/auth');

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

module.exports = router;