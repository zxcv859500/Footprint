const knex = require('../knexfile');

module.exports = {

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
    },

    async likeCancel(postId, username) {
        const postCnt = await knex('post')
            .count('postId as cnt')
            .where('postId', postId)
            .map((result) => {
                return result.cnt
            });

        if (postCnt <= 0) {
            throw new Error("Post doesn't exist");
        } else {
            const userId = await knex('user')
                .select('userId')
                .where('username', username)
                .map((result) => {
                    return result.userId
                });
            const likeCnt = await knex('likeApply')
                .count('postId as cnt')
                .where({
                    postId: postId,
                    userId: userId
                })
                .map((result) => {
                    return result.cnt
                });
            if (likeCnt <= 0) {
                throw new Error("This user have never been liked this post");
            } else {
                await knex('likeApply')
                    .where({
                        postId: postId,
                        userId: userId
                    })
                    .del();

                await knex('post')
                    .where('postId', postId)
                    .decrement('like', 1);
            }
        }
    },

    async commentLikeHandle(commentId, username) {
        const userId = await knex('user')
            .select('userId')
            .where('username', username)
            .map((result) => {
                return result.userId
            });

        const like = await knex
            .count('commentId as cnt')
            .from('commentLike')
            .where({
                commentId: commentId,
                userId: userId
            });

        const comment = await knex
            .count('commentId as cnt')
            .from('comment')
            .where({
                commentId: commentId
            });

        if (like[0].cnt >= 1) {
            throw new Error("Already liked this comment");
        } else if (comment[0].cnt <= 0) {
            throw new Error("Comment doesn't exist");
        } else {
            await knex('commentLike')
                .insert({
                    userId: userId[0],
                    commentId: commentId
                });
            await knex('comment')
                .where('commentId', commentId)
                .increment('like', 1);
        }
    },

    async commentLikeCancel(commentId, username) {
        const commentCnt = await knex('comment')
            .count('commentId as cnt')
            .where('commentId', commentId)
            .map((result) => {
                return result.cnt
            });

        if (commentCnt <= 0) {
            throw new Error("Comment doesn't exist");
        } else {
            const userId = await knex('user')
                .select('userId')
                .where('username', username)
                .map((result) => {
                    return result.userId
                });
            const likeCnt = await knex('commentLike')
                .count('commentId as cnt')
                .where({
                    commentId: commentId,
                    userId: userId
                })
                .map((result) => {
                    return result.cnt
                });
            if (likeCnt <= 0) {
                throw new Error("This user have never been liked this post");
            } else {
                await knex('commentLike')
                    .where({
                        commentId: commentId,
                        userId: userId
                    })
                    .del();

                await knex('comment')
                    .where('commentId', commentId)
                    .decrement('like', 1);
            }
        }
    }
};