const knex = require('../knexfile');

module.exports = {
	async list() {
		const roads = await knex('marker')
			.distinct('road')
			.map((result) => {
				return result.road;
			});

		const builder = [];

		for (const road of roads) {
			let data = {
				road: road,
				typeA: 0,
				typeB: 0,
				typeC: 0
			};
			const markerIds = await knex.select("markerId")
				.from('marker')
				.where('road', road)
				.map((result) => {
					return result.markerId
				});
			for (const markerId of markerIds) {
				const typeA = await knex.count('markerId as cnt')
					.from('marker')
					.joinRaw('natural join markerApply')
					.joinRaw('natural join post')
					.where('type', 0)
					.andWhere('road', road)
					.map((result) => {
						return result.cnt;
					});

				const typeB = await knex.count('markerId as cnt')
					.from('marker')
					.joinRaw('natural join markerApply')
					.joinRaw('natural join post')
					.where('type', 1)
					.andWhere('road', road)
					.map((result) => {
						return result.cnt;
					});

				const typeC = await knex.count('markerId as cnt')
					.from('marker')
					.joinRaw('natural join markerApply')
					.joinRaw('natural join post')
					.where('type', 2)
					.andWhere('road', road)
					.map((result) => {
						return result.cnt;
					});
				if (typeC[0] >= 1) {
					data.typeC += 1;
				} else if (typeB[0] >= 1) {
					data.typeB += 1;
				} else {
					data.typeA += 1;
				}
			}
			builder.push(data);
		}
		return builder;
	}
}
