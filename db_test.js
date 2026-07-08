const mysql = require('mysql2/promise');

async function test() {
  const connection = await mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '1234',
    database: 'do_shopping'
  });

  const [rows] = await connection.execute('SELECT id, order_number FROM orders');
  console.log("Orders:", rows);
  
  process.exit(0);
}
test();
