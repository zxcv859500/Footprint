const redis = require('../../controller/redis');
const config = require('../../config');
const request = require('request-promise');
const util = require('util');

module.exports = {
    async sendVerificationNumber(phone) {
        const verificationNumber = await redis.verify.create(phone);
        const uri = util.format("https://api-sens.ncloud.com/v1/sms/services/%s/messages", config.smsService.serviceId);
        const to = phone.replace(/-/gi, '');
        const content = util.format("[발바닥 앱 인증] 인증번호는 %s 입니다.", verificationNumber);

        const options ={
            uri: uri,
            method: 'POST',
            json: true,
            headers: {
                'Content-Type': 'application/json',
                'x-ncp-auth-key': config.smsService.api,
                'x-ncp-service-secret': config.smsService.secret
            },
            body: {
                type: "SMS",
                contentType: "COMM",
                from: config.smsService.phone,
                content: content,
                to: [
                    to
                ]
            }
        };

        return await request(options);
    }
};