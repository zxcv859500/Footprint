const knex = require('../knexfile');

module.exports = {
    async create(data) {
        try {
            if (data.type !== '1') {
                const count = await knex.count('postId as CNT')
                    .from('post')
                    .joinRaw('natural join markerApply natural join marker')
                    .where({
                        latitude: data.latitude,
                        longitude: data.longitude,
                        type: data.type
                    });
                if (count[0].CNT >= 1) {
                    throw new Error("Type A or Type C can only exist by one");
                }
            }

            let postId = await knex('post')
                .insert({
                    title: data.title,
                    content: data.content,
                    date: data.date,
                    type: data.type
                })
                .returning('postId');
            postId = postId[0];

            let markerId = await knex('marker')
                .insert( {
                    latitude: data.latitude,
                    longitude: data.longitude,
                    road: data.road
                })
                .returning('markerId');
            markerId = markerId[0];

            let pictureId = await knex('picture')
                .insert({
                    path: data.picture
                })
                .returning('pictureId');
            pictureId = pictureId[0];

            let userId = await knex('user')
                .select('userId')
                .where('user.nickname', data.author);
            userId = userId[0].userId;

            await knex('postApply')
                .insert({
                    postId: postId,
                    userId: userId
                });
            await knex('pictureApply')
                .insert({
                    pictureId: pictureId,
                    postId: postId
                });
            await knex('markerApply')
                .insert({
                    markerId: markerId,
                    postId: postId
                });
        } catch(err) {
            throw err;
        }
    },

    async getList(latitude, longitude) {
        let posts = [];
        const postIds = await knex.select('postId')
            .from('post')
            .joinRaw('natural join markerApply natural join marker')
            .where({
                latitude: latitude,
                longitude: longitude
            })
            .map((result) => {
                return result.postId;
            });

        for (const postId of postIds) {
            const post = await knex.select('title', 'nickname', 'pictureId', 'like', 'date', 'type', 'postId')
                .from('post')
                .joinRaw('natural join pictureApply natural join picture')
                .joinRaw('natural join postApply natural join user')
                .where({
                    postId: postId
                })
                .map(r => ({
                    postId: r.postId,
                    title: r.title,
                    author: r.nickname,
                    pictureId: r.pictureId,
                    like: r.like,
                    date: r.date,
                    type: r.type
                }));
            posts.push(await post[0]);
        }
        return posts;
    },

    async getPost(postId) {
        const post = await knex.select('title', 'content', 'nickname', 'pictureId', 'like', 'date', 'nickname')
            .from('post')
            .joinRaw('natural join pictureApply natural join picture')
            .joinRaw('natural join postApply natural join user')
            .where({
                postId: postId
            })
            .map(r=> ({
                title: r.title,
                content: r.content,
                author: r.nickname,
                pictureId: r.pictureId,
                like: r.like,
                date: r.date,
            }));
        return post[0];
    },

    async likeHandle(postId, username) {
        const userId = await knex('user')
            .select('userId')
            .where('username', username)
            .map((result) => {
                return result.userId
            });

        const like = await knex
            .count('postId as cnt')
            .from('likeApply')
            .where({
                postId: postId,
                userId: userId
            });

        const post = await knex
            .count('postId as cnt')
            .from('post')
            .where({
                postId: postId
            });

        if (like[0].cnt >= 1) {
            throw new Error("Already liked this post");
        } else if (post[0].cnt <= 0) {
            throw new Error("Post doesn't exist");
        } else {
            await knex('likeApply')
                .insert({
                    userId: userId[0],
                    postId: postId
                });
            await knex('post')
                .where('postId', postId)
                .increment('like', 1);
        }
    }
};