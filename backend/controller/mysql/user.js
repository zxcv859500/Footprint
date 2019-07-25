const knex = require('../../knexfile');
const crypto = require('crypto');
const config = require('../../config');
const jwt = require('jsonwebtoken');

module.exports = {
    async create(username, password, nickname, phone) {
        const count = await knex.count('userId as cnt')
            .from('user')
            .where('username', username)
            .orWhere('nickname', nickname)
            .orWhere('phone', phone)
            .map((result) => {
                return result.cnt
            });

        if (count >= 1) {
            throw new Error("Username or nickname or phone already exists");
        }

        const encrypted = crypto.createHmac('sha1', config.secret)
            .update(password)
            .digest('base64');

        try {
            return await knex('user')
                .insert({username: username, password: encrypted, nickname: nickname, phone: phone});
        } catch(err) {
            throw err;
        }
    },

    async login(username, password) {
        let actualPwd;
        let nickname;
        const encrypted = crypto.createHmac('sha1', config.secret)
            .update(password)
            .digest('base64');

        try {
            const user = await knex('user AS u')
                .select('*')
                .where('u.username', username);
            actualPwd = user[0].password;
            nickname = user[0].nickname;
        } catch(err) {
            throw err;
        }
        if (encrypted === actualPwd) {
             return await jwt.sign(
                {
                    username: username,
                    nickname: nickname
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