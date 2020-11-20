const mongoose = require("mongoose");

const doctorModels = new mongoose.Schema({
  Name: String,
  date_of_birth: String,
  email: String,
  doctor_license_number: String,
  PhoneNumber: Number,
  password: String,
  Patients: [{ type: mongoose.Schema.Types.ObjectId, ref: "Patients" }],
  assistantDoctor: [
    { type: mongoose.Schema.Types.ObjectId, ref: "AstDoctors" },
  ],
  verification : {
    type : Boolean,
    default : false
  }
});

module.exports = Doctors = mongoose.model("Doctors", doctorModels);
