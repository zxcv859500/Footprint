const knex = require('../knexfile');

module.exports = {
    async create(data) {
        if (data.type !== '1') {
            const count = await knex.count('postId as cnt')
                .from('post')
                .joinRaw('natural join markerApply natural join marker')
                .where({
                    latitude: data.latitude,
                    longitude: data.longitude,
                    type: data.type
                });
            if (count[0].cnt >= 1) {
                throw new Error("Type A or Type C can only exist by one");
            }
        }
        let userId = await knex('user')
            .select('userId')
            .where('user.nickname', data.author);
        userId = userId[0].userId;

        let pictureId = await knex('picture')
            .insert({
                path: data.picture
            })
            .returning('pictureId');
        pictureId = pictureId[0];

        let postId = await knex('post')
            .insert({
                title: data.title,
                content: data.content,
                date: data.date,
                type: data.type,
                userId: userId,
                pictureId: pictureId
            })
            .returning('postId');
        postId = postId[0];
        const marker = await knex.count('markerId as cnt')
            .from('marker')
            .where({
                latitude: data.latitude,
                longitude: data.longitude
            })
            .map(r => ({
                cnt: r.cnt
            }));
        let markerId;
        if (marker[0].cnt <= 0) {
            markerId = await knex('marker')
                .insert({
                    latitude: data.latitude,
                    longitude: data.longitude,
                    road: data.road
                })
                .returning('markerId');
        } else {
            markerId = await knex('marker')
                .select('markerId')
                .where({
                    latitude: data.latitude,
                    longitude: data.longitude
                })
                .map((result) => {
                    return result.markerId
                });
        }
        markerId = markerId[0];
        await knex('markerApply')
            .insert({
                markerId: markerId,
                postId: postId
            });
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
                .joinRaw('natural join user')
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

    async getPost(params) {
        const {postId, userId} = params;
        let flag = false;

        const count = await knex.count('userId as cnt')
            .from('likeApply')
            .where('postId', postId)
            .andWhere('userId', userId)
            .map((result) => {
                return result.cnt;
            });

        if (count[0] >= 1) flag = true;

        let post = await knex.select('title', 'content', 'nickname', 'pictureId', 'like', 'date', 'nickname')
            .from('post')
            .joinRaw('natural join user')
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

        post[0].likeFlag = flag;

        return post[0];
    },

    async edit(data) {
        const post = await knex('post')
            .select('nickname')
            .count('postId as cnt')
            .joinRaw('natural join user')
            .where('postId', data.postId)
            .map(r => ({
                cnt: r.cnt,
                author: r.nickname
            }));

        if (post[0].cnt <= 0) {
            throw new Error("Post doesn't exist");
        } else if (post[0].author !== data.author) {
            throw new Error("This user is not author of this post");
        } else {
            await knex('post')
                .where('postId', data.postId)
                .update({
                    title: data.title,
                    content: data.content
                });
        }
    },

    async delete(data) {
        const post = await knex('post')
            .select('nickname', 'type')
            .count('postId as cnt')
            .joinRaw('natural join user')
            .where('postId', data.postId)
            .map(r => ({
                cnt: r.cnt,
                type: r.type,
                author: r.nickname
            }));

        if (post[0].cnt <= 0) {
            throw new Error("Post doesn't exist");
        } else if (data.previlage === 0 && post[0].author !== data.author) {
            throw new Error("This user is not author of this post");
        } else {
            this.deleteComment(data.postId);
            if (post[0].type === 0) {
                const markerId = await knex('markerApply')
                    .select('markerId')
                    .where('postId', data.postId)
                    .map((result) => {
                        return result.markerId
                    });

                const postIds = await knex('markerApply')
                    .select('postId')
                    .where('markerId', markerId[0])
                    .map((result) => {
                        return result.postId
                    });

                await knex('marker')
                    .where('markerId', markerId[0])
                    .del();

                await knex('markerApply')
                    .where('markerId', markerId[0])
                    .del();

                for (const id of postIds) {
                    await knex('post')
                        .where('postId', id)
                        .del();
                    this.deleteComment(id);
                }

            }
            await knex('post')
                .where('postId', data.postId)
                .del();
        }
    },

    async deleteComment(postId) {
        const commentId = await knex('commentApply')
            .select('commentId')
            .where('postId', postId)
            .map((result) => {
                return result.commentId;
            });

        for (const id of commentId) {
            await knex('comment')
                .where('commentId', id)
                .del();

            await knex('commentApply')
                .where('commentId', id)
                .del();
        }
    }
};
