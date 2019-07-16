const knex = require('../knexfile');

module.exports = {
    async list() {
        const marker = knex('marker')
            .select('latitude', 'longitude')
            .map(r => ({
                latitude: r.latitude,
                longitude: r.longitude
            }))
        return marker;
    }
};
