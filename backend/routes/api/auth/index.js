const router = require('express').Router();
const controller = require('../../../controller/mysql');
const service = require('../../../service/');
const auth = require('../../../middlewares/auth');
const redis = require('../../../controller/redis');

router.post('/register', (req, res) => {
    const {username, password, nickname, phone} = req.body;

    if (!username || !password || !nickname || !phone) {
        res.status(409).json({
            error: "Username, password, nickname, phone required"
        })
    } else if (!phone.match(/^\d{3}-\d{3,4}-\d{4}$/)) {
        res.status(409).json({
            error: "Unsuccessful phone number"
        })
    } else if (username.length <= 0 || username.length > 16 || password.length <= 0 || password.length > 16) {
        res.status(409).json({
            error: "username or password too long"
        });
    } else {
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
    }
});

router.post('/login', (req, res) => {
    const {username, password} = req.body;

    if (!username || !password) {
        res.status(409).json({
            Error: "Username and password required"
        })
    } else {
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
    }
});

router.use('/check', auth);
router.get('/check', (req, res) => {
    res.status(200).json({
        message: "token authorized"
    });
});

router.post('/cert', async (req, res) => {
    const { phone } = req.body;

    if (!phone) {
        res.status(409).json({
            error: "Phone number required"
        });
    } else if (!phone.match(/^\d{3}-\d{3,4}-\d{4}$/)) {
        res.status(409).json({
            error: "Unsuccessful phone number"
        })
    } else if (!await controller.user.verifyPhone(phone)) {
        res.status(409).json({
            error: "Phone number already exists"
        })
    } else {
        service.verify.sms.sendVerificationNumber(phone)
            .then(() => {
                res.status(200).json({
                    message: "Verification number has send to your phone"
                })
            })
            .catch((err) => {
                res.status(409).json({
                    error: err.message
                })
            })
    }
});

router.post('/verify', (req, res) => {
    const { phone, verify } = req.body;

    if (!phone || !verify) {
        res.status(409).json({
            error: "Phone number or verify number required"
        })
    } else if (!phone.match(/^\d{3}-\d{3,4}-\d{4}$/)) {
        res.status(409).json({
            error: "Unsuccessful phone number"
        })
    } else {
        redis.verify.verify(phone)
            .then((result) => {
                if (verify === result) {
                    res.status(200).json({
                        message: "Verification complete"
                    })
                } else {
                    throw new Error("Verification failed")
                }
            })
            .catch((err) => {
                res.status(409).json({
                    error: err.message
                })
            })
    }
});

module.exports = router;