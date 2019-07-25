const knex = require('../../knexfile');
const path = require('path');
const picturePath = path.join(__dirname, '../', '/public/uploads');

module.exports = {
    async get(params) {
        const picture = await knex('picture')
            .select('path')
            .where('pictureId', params.pictureId)
            .map((result) => {
                return result.path;
            });
        if (!picture[0]) {
            throw new Error("Picture doesn't exist");
        } else {
            return path.join(picturePath, picture[0]);
        }
    }
};
