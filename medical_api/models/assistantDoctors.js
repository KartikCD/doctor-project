const mongoose = require("mongoose");

const assistantDoctor = new mongoose.Schema({
  Name: String,
  date_of_birth: String,
  doctor_license_number: String,
  email: String,
  PhoneNumber: Number,
  password: String,
  Patients: [{ type: mongoose.Schema.Types.ObjectId, ref: "Patients" }],
  verification : {
    type : Boolean,
    default : false
  },
  Doctors: [
    { type: mongoose.Schema.Types.ObjectId, ref: "Doctors", default: [] },
  ],
});

module.exports = AstDoctors = mongoose.model("AstDoctors", assistantDoctor);
