const router = require('express').Router();
const controller = require('../../../controller');

router.get('/list', (req, res, next) => {
    controller.marker.list()
        .then((result) => {
            res.status(200).send(result);
        })
        .catch((err) => {
            res.status(409).json({
                Error: err.message
            })
        })
});

module.exports = router;