const knex = require('../knexfile');

module.exports = {
    async list() {
        const marker = await knex('marker')
            .distinct('type')
            .select('latitude', 'longitude')
            .joinRaw('natural join markerApply natural join post')
            .map(r => ({
                type: r.type,
                latitude: r.latitude,
                longitude: r.longitude
            }));
        let result = [];
        marker.forEach((element) => {
             const index = result.findIndex((res) => {
                 return res.latitude === element.latitude && res.longitude === element.longitude
             });
             if (index === -1) {
                 result.push({
                     latitude: element.latitude,
                     longitude: element.longitude,
                     type: element.type
                 })
             } else if (result[index].type <= element.type) {
                 result[index] = {
                     latitude: element.latitude,
                     longitude: element.longitude,
                     type: element.type
                 };
             }
        });
        return result;
    }
};
