const knex = require('../knexfile');
const crypto = require('crypto');
const config = require('../config');

module.exports = {
    async create(username, password) {
        const encrypted = crypto.createHmac('sha1', config.secret)
            .update(password)
            .digest('base64');

        try {
            return await knex('user')
                .insert({username: username, password: encrypted});
        } catch(err) {
            throw err;
        }
    },

    async verify(username, password) {
        const encrypted = crypto.createHmac('sha1', config.secret)
            .update(password)
            .digest('base64');

        const actualPwd = await knex('user AS u')
            .select('u.password')
            .where('u.username', username);

        return encrypted === actualPwd;
    }
};