const router = require('express').Router();
const auth = require('./auth');
const post = require('./post');
const comment = require('./comment');
const marker = require('./marker');
const picture = require('./picture');
const statistics = require('./statistics');

router.use('/auth', auth);
router.use('/post', post);
router.use('/comment', comment);
router.use('/marker', marker);
router.use('/picture', picture);
router.use('/statistics', statistics);

module.exports = router;
