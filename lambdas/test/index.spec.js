// export default env => {
//   const required = ['BUCKET', 'REGION'];
//   const missing = [];
//
//   required.forEach(reqVar => {
//     if (!env[reqVar]) {
//       missing.push(reqVar);
//     }
//   });
//
//   return missing;
// };

const { handler } = require('.');

describe('lambda handler', () => {

  test('test lambda handler', async () => {
    const result = await handler();
    console.info(result);
    // console.info(actualValue, 'yo');
    // expect(3).toBe(3);
  });

});
