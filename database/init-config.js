'use strict';

const fs = require('fs');
const settingsPath = '../src/main/resources/settings.json';
// Get the application settings
const settings = require(settingsPath);

// Get the config from the environment variables
const dbPass = process.env["LITTLEGIT_DB_PASS"];
const dbUser = process.env["LITTLEGIT_DB_USER"];
const dbHost = process.env["LITTLEGIT_DB_HOST"];
const redisHost = process.env["LITTLEGIT_REDIS_HOST"];

const dbSettings = settings.db;

if (dbHost) {
    dbSettings.host = dbHost;
}

if (dbUser) {
    dbSettings.user = dbUser;
}

if (dbPass) {
    dbSettings.password = dbPass;
}

if (redisHost) {
    settings.redis.host = redisHost;
}

fs.writeFile(settingsPath, JSON.stringify(settings, null, 3));

const dbConfig = {
    config: dbSettings
};

fs.writeFile("database.json", JSON.stringify(dbConfig, null, 3));