const controller = require('../../../controller/mysql');
const router = require('express').Router();

router.get('/:id', (req, res, next) => {
    const data = {
        pictureId: req.params.id
    };

    controller.picture.get(data)
        .then((result) => {
            res.status(200).sendFile(result);
        })
        .catch((err) => {
            res.status(409).json({
                Error: err.message
            })
        })
});

module.exports = router;