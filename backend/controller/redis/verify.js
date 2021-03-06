const client = require('../redisfile');

module.exports = {
  async create(phone) {
    function randomRange(n1, n2) {
      return Math.floor( (Math.random() * (n2 - n1 + 1)) + n1 );
    }
    const verificationNumber = randomRange(10000, 99999);
    client.set(phone, verificationNumber, 'EX', 3 * 60);

    return verificationNumber;
  },

  async verify(phone) {
    const {promisify} = require('util');
    const getAsync = promisify(client.get).bind(client);

    return await getAsync(phone);
  }
};