const https = require('https');

/**
 * Provide an event that contains the following keys:
 *
 *   - operation: one of the operations in the switch statement below
 *   - tableName: required for operations that interact with DynamoDB
 *   - payload: a parameter to pass to the operation being performed
 */
exports.handler = async (event) => {
    console.log('hello');
    console.log('Received event: ' + JSON.stringify(event, null, 2));

    const options = {
        host: '',
        port: 443,
        path: '/.json',
        method: 'POST'
    };

    const request = https.request(options, (result) => {
       console.log(result.statusCode);

       result.on('data', function(data) {
        process.stdout.write(data);
       });
    });

    const body = JSON.stringify({
      foo: 'bar'
    });

    request.end(body);

    request.on('error', (error) => {
      console.error(error);
    });

    return { 'yo': 'hey' };

    // const operation = event.operation;
    // const payload = event.payload;
    // if (event.tableName) {
    //     payload.TableName = event.tableName;
    // }

    // switch (operation) {
    //     case 'read':
    //         return await dynamo.get(payload).promise();
    //     default:
    //         return await dynamo.get(payload).promise();
    // }
};
