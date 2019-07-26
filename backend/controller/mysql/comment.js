const knex = require('../knexfile');

module.exports = {
    async write(author, postId, content) {
        const date = new Date();

        const postCnt = await knex.count('postId as cnt')
            .from('post')
            .where('postId', postId)
            .map((result) => {
                return result.cnt;
            });

        if (postCnt <= 0) {
            throw new Error("Post doesn't exist");
        } else {
            const userId = await knex('verify.js')
                .select('userId')
                .where('nickname', author)
                .map((result) => {
                    return result.userId
                });

            const commentId = await knex('comment')
                .insert({
                    content: content,
                    date: date,
                    userId: userId
                })
                .returning('commentId');
            await knex('commentApply')
                .insert({
                    commentId: commentId[0],
                    postId: postId
                })
        }
    },

    async delete(author, commentId) {
        const comment = await knex('comment')
            .select('nickname')
            .joinRaw('natural join user')
            .count('commentId as cnt')
            .where('commentId', commentId)
            .map(r => ({
                cnt: r.cnt,
                author: r.nickname
            }));

        if (comment[0].cnt <= 0) {
            throw new Error("Comment doesn't exist");
        } else if (comment[0].author !== author) {
            throw new Error("This user is not author of this comment");
        } else {
            await knex('comment')
                .where('commentId', commentId)
                .del();

            await knex('commentApply')
                .where('commentId', commentId)
                .del();
        }
    },

    async edit(author, commentId, content) {
        const comment = await knex('comment')
            .select('nickname')
            .joinRaw('natural join user')
            .count('commentId as cnt')
            .where('commentId', commentId)
            .map(r => ({
                cnt: r.cnt,
                author: r.author
            }));

        if (comment[0].cnt <= 0) {
            throw new Error("Comment doesn't exist");
        } else if (comment[0].author !== author) {
            throw new Error("This user is not author of this comment");
        } else {
            await knex('comment')
                .where('commentId', commentId)
                .update('content', content);
        }
    }
};