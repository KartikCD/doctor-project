const mongoose = require("mongoose");

const sensed = new mongoose.Schema({
  ecg: String,
  pulse: String,
  temperature: String,
  humidity: String,
  timestp: String,
  name: String,
});

module.exports = Sensed = mongoose.model("Sensed", sensed);
