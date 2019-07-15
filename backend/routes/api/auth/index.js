const router = require('express').Router();
const controller = require('../../../controller/index');

router.post('/register', (req, res) => {
    const {username, password, nickname} = req.body;

    if (username.length <= 0 || username.length > 16 || password.length <= 0 || password.length > 16) {
        res.status(409).json({
            error: "username or password too long"
        });
    }

    controller.user.create(username, password, nickname)
        .then((result) => {
            res.json({
                message: "register success"
            });
        })
        .catch((err) => {
            res.status(500).send(err);
        })
});

router.post('/login', (req, res) => {
    const {username, password} = req.body;

    controller.user.login(username, password)
        .then((result) => {
            res.json({
                token: result
            });
        })
        .catch((err) => {
            console.log(err);
            res.status(500).json({
                error: err.message
            });
        })
});

module.exports = router;