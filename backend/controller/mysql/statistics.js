const knex = require('../knexfile');

module.exports = {
	async list() {
		const roads = await knex('marker')
			.distinct('road')
			.map((result) => {
				return result.road;
			});

		builder = []

		for (road of roads) {
			const typeA = await knex.count('markerId as cnt')
				.from('marker')
				.joinRaw('natural join post')
				.where('type', 0)
				.map((result) => {
					return result.cnt;
				});

			const typeB = await knex.count('markerId as cnt')
				.from('marker')
				.joinRaw('natural join post')
				.where('type', 1)
				.map((result) => {
					return result.cnt;
				});

			const typeC = await knex.count('markerId as cnt')
				.from('marker')
				.joinRaw('natural join post')
				.where('type', 2)
				.map((result) => {
					return result.cnt;
				});
			builder.push({
				road: road,
				typeA: typeA[0],
				typeB: typeB[0],
				typeC: typeC[0]
			});
		}
		return builder;
	}
}
