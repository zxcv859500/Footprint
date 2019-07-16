const knex = require('../knexfile');

module.exports = {
    async create(data) {
        try {
            if (data.type !== 1) {
                const count = await knex.count('postId as CNT')
                    .from('post')
                    .joinRaw('natural join markerApply natural join marker')
                    .where({
                        latitude: data.latitude,
                        longitude: data.longitude
                    });
                console.log(count[0].CNT);
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

    }
};