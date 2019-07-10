const knex = require('../knexfile');
const crypto = require('crypto');
const config = require('../config');
const jwt = require('jsonwebtoken');

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

    async login(username, password) {
        let actualPwd;
        const encrypted = crypto.createHmac('sha1', config.secret)
            .update(password)
            .digest('base64');

        try {
            const user = await knex('user AS u')
                .select('u.password')
                .where('u.username', username);
            actualPwd = user[0].password;
        } catch(err) {
            throw err;
        }

        if (encrypted === actualPwd) {
             return await jwt.sign(
                {
                    username: username
                },
                config.secret,
                {
                    expiresIn: '7d'
                }
            );
        } else {
            throw new Error("Wrong password");
        }
    }
};