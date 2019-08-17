const router = require('express').Router();
const controller = require('../../../controller/myseql');

router.get('/list', (req, res) => {
	controller.statistics.list()
		.then((result) => {
			res.status(200).send(result);
		})
		.catch((err) => {
			res.status(500).json({
				error: err.message
			})
		})
}
