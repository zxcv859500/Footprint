const router = require('express').Router();
const controller = require('../../../controller/mysql');
const auth = require('../../../middlewares/auth');

router.post('/register', (req, res) => {
    const {username, password, nickname, phone} = req.body;

    if (!username || !password || !nickname || !phone) {
        res.status(409).json({
            error: "Username, password, nickname, phone required"
        })
    }

    if (!phone.match(/^\d{3}-\d{3,4}-\d{4}$/)) {
        res.status(409).json({
            error: "Unsuccessful phone number"
        })
    }

    if (username.length <= 0 || username.length > 16 || password.length <= 0 || password.length > 16) {
        res.status(409).json({
            error: "username or password too long"
        });
    }

    controller.user.create(username, password, nickname, phone)
        .then((result) => {
            res.json({
                message: "register success"
            });
        })
        .catch((err) => {
            res.status(500).json({
                error: err.message
            });
        })
});

router.post('/login', (req, res) => {
    const {username, password} = req.body;

    if (!username || !password) {
        res.status(409).json({
            Error: "Username and password required"
        })
    }

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

router.use('/check', auth);
router.get('/check', (req, res) => {
    res.status(200).json({
        message: "token authorized"
    });
});

module.exports = router;