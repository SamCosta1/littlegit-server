'use strict';

const fs = require('fs');
const settingsPath = '../src/main/resources/settings.json';
// Get the application settings
const settings = require(settingsPath);

// Get the config from the environment variables
const dbPass = process.env["LITTLEGIT_DB_PASS"];
const dbUser = process.env["LITTLEGIT_DB_USER"];
const dbHost = process.env["LITTLEGIT_DB_HOST"];

const dbSettings = settings.db;

if (!dbSettings.host) {
    dbSettings.host = dbHost;
}

if (!dbSettings.user) {
    dbSettings.user = dbUser;
}

if (!dbSettings.password) {
    dbSettings.password = dbPass;
}

fs.writeFile(settingsPath, JSON.stringify(settings, null, 3));

const dbConfig = {
    config: dbSettings
};

fs.writeFile("database.json", JSON.stringify(dbConfig, null, 3));