const redis = require('redis').createClient(process.env.REDIS_PORT, process.env.REDIS_HOST);

redis.on('error', function (err) {
    console.log("Redis error : " + err);
});

module.exports = redis;