const knex = require('../knexfile');
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
        let actualPwd,
            nickname,
            userId,
            previlage,
            phone;

        const encrypted = crypto.createHmac('sha1', config.secret)
            .update(password)
            .digest('base64');

        try {
            const user = await knex('user AS u')
                .select('*')
                .where('u.username', username);
            actualPwd = user[0].password;
            nickname = user[0].nickname;
            userId = user[0].userId;
            previlage = user[0].previlage;
            phone = user[0].phone;
        } catch(err) {
            throw err;
        }
        if (encrypted === actualPwd) {
             return await jwt.sign(
                {
                    userId: userId,
                    username: username,
                    nickname: nickname,
                    previlage: previlage,
                    phone: phone
                },
                config.secret,
                {
                    expiresIn: '7d'
                }
            );
        } else {
            throw new Error("Wrong password");
        }
    },

    async verifyPhone(phone) {
        const count = await knex.count('userId as cnt')
            .from('user')
            .where('phone', phone)
            .map((result) => {
                return result.cnt;
            });

        return count[0] <= 0;
    },

    async edit(params) {
        const { password, nickname, userId } = params;

        const encrypted = crypto.createHmac('sha1', config.secret)
            .update(password)
            .digest('base64');

        return await knex('user').update({
            nickname: nickname,
            password: encrypted
        })
            .where('userId', userId);
    },

    async secession(params) {
        const { userId } = params;

        return await knex('user')
            .where('userId', userId)
            .del();
    },

    async findUsername(params) {
        const {phone} = params;

        const username = await knex('user')
            .select('username')
            .where('phone', phone)
            .map((result) => {
                return result.username;
            });

        return username[0];
    },

    async findPasswordVerify(params) {
        const {username} = params;

        const phone = await knex('user')
            .select('phone')
            .where('username', username)
            .map((result) => {
                return result.phone;
            });

        return phone[0];
    },

    async findPassword(params) {
        const {username, newPassword} = params;

        const encrypted = crypto.createHmac('sha1', config.secret)
            .update(newPassword)
            .digest('base64');

        return await knex('user')
            .update('password', encrypted)
            .where('username', username);
    }
};