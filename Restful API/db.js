const Pool = require('pg').Pool;

const pool = new Pool({
    user: "postgres",
    host: "localhost",
    database: "fyp",
    password: "L0l0k123",
    port: 5433,
});

module.exports = pool;