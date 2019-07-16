const router = require('express').Router();
const auth = require('./auth');
const post = require('./post');
const comment = require('./comment');
const marker = require('./marker');

router.use('/auth', auth);
router.use('/post', post);
router.use('/comment', comment);
router.use('/marker', marker);

module.exports = router;