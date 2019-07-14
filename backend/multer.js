const multer = require('multer');
const path = require('path');
const uploadDir = path.join(__dirname, '../uploads');

const storage = multer.diskStorage({
    destination: (req, file, callback) => {
        callback(null, uploadDir);
    },
    filename: (req, file, callback) => {
        const ext = path.extname(file.originalname);
        const basename = path.basename(file.originalname, ext);
        const fname = basename + Date.now().valueOf() + ext;
        callback(null, fname);
    }
});

module.exports = multer(
    {
        storage: storage
    }
);

