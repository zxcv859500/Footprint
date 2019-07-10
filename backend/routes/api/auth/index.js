const router = require('express').Router();
const controller = require('../../../controller/index');

router.post('/register', (req, res) => {
    const {username, password} = req.body;

    if (username.length <= 0 || username.length > 16 || password.length <= 0 || password.length > 16) {
        res.status(409).json({
            error: "username or password too long"
        });
    }

    controller.user.create(username, password)
        .then((result) => {
            res.json({
                message: "register success"
            });
        })
        .catch((err) => {
            res.status(500).send(err);
        })
});

module.exports = router;