const mongoose = require("mongoose");

const patient = new mongoose.Schema({
  Name: String,
  date_of_birth: String,
  Disease: String,
  healthDescription: String,
  PhoneNumber: Number,
  password: String,
  pemail: String,
  Doctors: [{ type: mongoose.Schema.Types.ObjectId, ref: "Doctors" }],
  assistantDoctor: [
    { type: mongoose.Schema.Types.ObjectId, ref: "AstDoctors" },
  ],
});

module.exports = Patients = mongoose.model("Patients", patient);
