const multer = require('multer');
const path = require('path');
const uploadDir = path.join(__dirname, '../public/uploads');
const config = require('./config');
const crypto = require('crypto');

const storage = multer.diskStorage({
    destination: (req, file, callback) => {
        callback(null, uploadDir);
    },
    filename: (req, file, callback) => {
        const encrypted = crypto.createHmac('sha1', config.secret)
            .update(file.originalname)
            .digest('base64');
        const ext = path.extname(file.originalname);
        const fname = encrypted + ext;
        callback(null, fname);
    }
});

module.exports = multer(
    {
        storage: storage
    }
);

