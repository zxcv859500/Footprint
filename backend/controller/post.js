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

    async getPost(latitude, longitude) {
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
            const post = await knex.select('title', 'content', 'nickname', 'like', 'pictureId', 'date', 'type')
                .from('post')
                .joinRaw('natural join pictureApply natural join picture')
                .joinRaw('natural join postApply natural join user')
                .where({
                    postId: postId
                })
                .map(r => ({
                    title: r.title,
                    content: r.content,
                    author: r.author,
                    like: r.like,
                    pictureId: r.pictureId,
                    date: r.date,
                    type: r.type
                }));
            posts.push(await post[0]);
        }
        return posts;
    }
};