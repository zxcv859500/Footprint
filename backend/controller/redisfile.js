const redis = require('redis').createClient();

redis.on('error', function (err) {
    console.log("Redis error : " + err);
});

module.exports = redis;