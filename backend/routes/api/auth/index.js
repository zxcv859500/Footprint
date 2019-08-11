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

router.use('/edit', auth);
router.post('/edit', (req, res) => {
   const { password, nickname } = req.body;
   const { userId } = req.decoded;
   const params = {
       password: password,
       nickname: nickname,
       userId: userId
   };

   if (!password || !nickname) {
       res.status(409).json({
           error: "New nickname and new password required"
       });
   } else {
       controller.user.edit(params)
           .then(() => {
               res.status(200).json({
                   params
               });
           })
           .catch((err) => {
               res.status(409).json({
                   error: err.message
               })
           })
   }
});

router.use('/secession', auth);
router.get('/secession', (req, res) => {
   const { userId } = req.decoded;

   controller.user.secession({ userId: userId })
       .then(() => {
           res.status(200).json({
               message: "Secession complete"
           })
       })
       .catch((err) => {
           res.status(409).json({
               error: err.message
           })
       })
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

router.post('/find/username', (req, res) => {
   const {phone} = req.body;

   if (!phone) {
       res.status(409).json({
           error: 'Phone number required'
       })
   } else if (!phone.match(/^\d{3}-\d{3,4}-\d{4}$/)) {
       res.status(409).json({
           error: "Unsuccessful phone number"
       });
   } else {
       controller.user.findUsername({phone: phone})
           .then((result) => {
               res.status(200).json({
                   "username": result
               });
           })
           .catch(() => {
               res.status(409).json({
                   "error": "Unregistered phone number or some error occur"
               })
           })
   }
});

router.post('/find/password/verify', (req, res) => {
    const {username} = req.body;

    if (!username) {
        res.status(409).json({
            error: 'Username required'
        })
    } else {
        controller.user.findPasswordVerify({username: username})
            .then((result) => {
                res.status(200).json({
                    "phone": result
                });
            })
            .catch(() => {
                res.status(409).json({
                    "error": "Unregistered username or some error occur"
                })
            })
    }
});

router.post('/find/password', (req, res) => {
    const {username, newPassword} = req.body;

    if (!username || !newPassword) {
        res.status(409).json({
            error: "Username and newPassword required"
        })
    } else {
        controller.user.findPassword({username: username, newPassword: newPassword})
            .then(() => {
                res.status(200).json({
                    "username": username,
                    "new password": newPassword
                })
            })
            .catch(() => {
                res.status(409).json({
                    error: "Unregistered username or invalid password"
                })
            })
    }
});

router.post('/find/cert', async (req, res) => {
    const { phone } = req.body;

    if (!phone) {
        res.status(409).json({
            error: "Phone number required"
        });
    } else if (!phone.match(/^\d{3}-\d{3,4}-\d{4}$/)) {
        res.status(409).json({
            error: "Unsuccessful phone number"
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

router.post('/find/verify', (req, res) => {
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