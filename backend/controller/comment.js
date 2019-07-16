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
            const commentId = await knex('comment')
                .insert({
                    content: content,
                    author: author,
                    date: date
                })
                .returning('commentId');
            await knex('commentApply')
                .insert({
                    commentId: commentId[0],
                    postId: postId
                })
        }
    }
}