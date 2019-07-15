const knex = require('../knexfile');

module.exports = {
    async create(data) {
        try {
            let postId = await knex('post')
                .insert({
                    title: data.title,
                    content: data.content,
                    date: data.date
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