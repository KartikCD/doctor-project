const express = require("express");

const Doctors = require("../models/doctorRegistration");
const AstDoctors = require("../models/assistantDoctors");
const Patients = require("../models/patients");
const Sensed = require("../models/sensed");
const { Mongoose } = require("mongoose");

const api = express.Router();

api.post("/registration", (req, res) => {
  const doctor_regis = new Doctors({
    Name: req.body.Name,
    date_of_birth: req.body.date_of_birth,
    doctor_license_number: req.body.doctor_license_number,
    PhoneNumber: req.body.PhoneNumber,
    password: req.body.password,
    email: req.body.email,
  });

  doctor_regis
    .save()
    .then(
      res.status(200).json({
        datasaved: "data saved success",
        status: "success",
      })
    )
    .catch((err) => console.log(err));
});

api.post("/sensedData", (req, res) => {
  const sens = new Sensed({
    ecg: req.body.ecg,
    pulse: req.body.pulse,
    temperature: req.body.temperature,
    humidity: req.body.humidity,
    timestp: req.body.timestp,
    name: req.body.name,
  });

  sens
    .save()
    .then(
      res.status(200).json({
        status: "success",
        message: "Data inserted successfully.",
      })
    )
    .catch((err) => console.log(err));
});

api.post("/astDoctorReg", (req, res) => {
  const doctorid = req.body.doctorid;
  console.log(doctorid);
  const astDoctorReg = {
    Name: req.body.Name,
    date_of_birth: req.body.date_of_birth,
    doctor_license_number: req.body.doctor_license_number,
    PhoneNumber: req.body.PhoneNumber,
    password: req.body.password,
    email: req.body.email,
  };
  const saveAssistant = new AstDoctors(astDoctorReg);
  saveAssistant["Doctors"].push(doctorid);
  saveAssistant
    .save()
    .then(
      res.status(200).json({
        datasaved: "data saved success",
        status: "success",
      })
    )
    .then(
      Doctors.findOneAndUpdate(
        { _id: doctorid },
        { $push: { assistantDoctor: saveAssistant._id } },
        { new: true },
        (err, result) => {
          console.log(err);
          console.log(result);
        }
      )
    )
    .catch((err) => console.log(err));
});

api.post("/assistantPatientReg", (req, res, next) => {
  const astDoctorId = req.body.doctorid;
  const patient = {
    Name: req.body.Name,
    date_of_birth: req.body.date_of_birth,
    Disease: req.body.Disease,
    healthDescription: req.body.healthDescription,
    PhoneNumber: req.body.PhoneNumber,
    password: req.body.password,
    pemail: req.body.pemail,
  };

  const savePatient = new Patients(patient);
  savePatient["assistantDoctor"].push(astDoctorId);

  savePatient
    .save()
    .then(
      res.status(200).json({
        datasaved: "data saved success",
        status: "success",
      })
    )
    .then(
      AstDoctors.findOneAndUpdate(
        { _id: astDoctorId },
        { $push: { Patients: savePatient._id } },
        { new: true },
        (err, result) => {
          console.log(err);
          console.log(result);
        }
      )
    )
    .catch((err) => console.log(err));
});

api.post("/patientReg", (req, res) => {
  const doctorid = req.body.doctorid;
  const patient = {
    Name: req.body.Name,
    date_of_birth: req.body.date_of_birth,
    Disease: req.body.Disease,
    healthDescription: req.body.healthDescription,
    PhoneNumber: req.body.PhoneNumber,
    password: req.body.password,
    pemail: req.body.pemail,
  };

  const savePatient = new Patients(patient);
  savePatient["Doctors"].push(doctorid);

  savePatient
    .save()
    .then(
      res.status(200).json({
        datasaved: "data saved success",
        status: "success",
      })
    )
    .then(
      Doctors.findOneAndUpdate(
        { _id: doctorid },
        { $push: { Patients: savePatient._id } },
        { new: true },
        (err, result) => {
          console.log(err);
          console.log(result);
        }
      )
    )
    .catch((err) => console.log(err));
});

api.post("/Login", (req, res) => {
  const logincred = {
    Name: req.body.Name,
    password: req.body.password,
    type: req.body.type,
  };
  console.log(logincred);
  if (logincred["type"] === "Doctor") {
    Doctors.findOne({ Name: logincred["Name"] })
      .then((doctor) => {
        if (!doctor) {
          res.status(200).json({
            status: "FAILURE",
            message: "doctor not available",
            id: null,
            name: null,
            phone: null,
          });
        }
        if (doctor.password === logincred["password"]) {
          res.status(200).json({
            status: "SUCCESS",
            message: "success",
            id: doctor._id,
            name: doctor.Name,
            phone: doctor.PhoneNumber.toString(),
          });
        } else {
          res.status(200).json({
            status: "FAILURE",
            message: "password incorrect",
            id: null,
            name: null,
            phone: null,
          });
        }
      })
      .catch((err) => {
        console.log(err);
      });
  }
  if (logincred["type"] === "assistantDoctor") {
    AstDoctors.findOne({ Name: logincred["Name"] })
      .then((assistantDoctor) => {
        if (!assistantDoctor) {
          res.status(200).json({
            status: "FAILURE",
            message: "Assistant Doctor not available",
            id: null,
            name: null,
            phone: null,
          });
        }
        if (assistantDoctor.password === logincred["password"]) {
          res.status(200).json({
            status: "SUCCESS",
            message: "success",
            id: assistantDoctor._id,
            name: assistantDoctor.Name,
            phone: assistantDoctor.PhoneNumber.toString(),
          });
        } else {
          res.status(200).json({
            status: "FAILURE",
            message: "password incorrect",
            id: null,
            name: null,
            phone: null,
          });
        }
      })
      .catch((err) => {
        console.log(err);
      });
  }
  if (logincred["type"] === "Patients") {
    Patients.findOne({ Name: logincred["Name"] })
      .then((patient) => {
        if (!patient) {
          res.status(200).json({
            status: "FAILURE",
            message: "Patient not available",
            id: null,
            name: null,
            phone: null,
          });
        }
        if (patient.password === logincred["password"]) {
          res.status(200).json({
            status: "SUCCESS",
            message: "success",
            id: patient._id,
            name: patient.Name,
            phone: patient.PhoneNumber.toString(),
          });
        } else {
          res.status(200).json({
            status: "FAILURE",
            message: "password incorrect",
            id: null,
            name: null,
            phone: null,
          });
        }
      })
      .catch((err) => {
        console.log(err);
      });
  }
});

api.get("/getsensordata", (req, res) => {
  Sensed.find({}).then((data) => {
    var response = {};
    response["sensed"] = data;
    res.header("Access-Control-Allow-Origin", "*");
    res.header(
      "Access-Control-Allow-Headers",
      "Origin, X-Requested-With, Content-Type, Accept"
    );
    res.status(200).json(response);
  });
});

api.post("/chatpanel", async (req, res, next) => {
  const ids = {
    id: req.body.doctor_id,
    type: req.body.type,
  };
  console.log(ids);
  if (ids.type === "Doctor") {
    try {
      const doctor = await Doctors.findById(ids.id)
        .populate("assistantDoctor", "Name")
        .populate("Patients", "Name")
        .select("Name");
      if (doctor) {
        res.status(200).json(doctor);
      } else {
        res.status(400).json({ message: "Doctor not found" });
      }
    } catch (err) {
      next(err);
    }
  } else if (ids.type === "assistantDoctor") {
    try {
      const doctor = await AstDoctors.findById(ids.id)
        .populate("Doctors", "Name")
        .populate("Patients", "Name")
        .select("Name");
      if (doctor) {
        res.status(200).json(doctor);
      } else {
        res.status(400).json({ message: "Doctor not found" });
      }
    } catch (err) {
      next(err);
    }
  } else if (ids.type === "Patients") {
    try {
      const doctor = await Patients.findById(ids.id)
        .populate("Doctors", "Name")
        .populate("assistantDoctor", "Name")
        .select("Name");
      if (doctor) {
        res.status(200).json(doctor);
      } else {
        res.status(400).json({ message: "Doctor not found" });
      }
    } catch (err) {
      next(err);
    }
  }
});

api.get("/", (req, res, next) => {
  const response = {
    message: "Welcome to doctor app.",
  };
  res.status(200).json(response);
});

api.post("/getpatient", async (req, res, next) => {
  const response = {
    id: req.body.doctor_id,
    type: req.body.type,
  };
  console.log(response);
  if (response.type === "Doctor") {
    try {
      const doctor = await Doctors.findOne({
        _id: response.id,
      }).populate("Patients");
      console.log(doctor);
      if (doctor) {
        if (doctor["Patients"].length > 0) {
          res.status(200).json({
            pname: doctor["Patients"][0].Name,
            pemail: doctor["Patients"][0].pemail,
            dname: doctor.Name,
            demail: doctor.email,
          });
        } else {
          res.status(400).json({ message: "No Patients found." });
        }
      } else {
        res.status(400).json({ message: "No Patients found." });
      }
    } catch (err) {
      next(err);
    }
  } else if (response.type === "assistantDoctor") {
    try {
      const doctor = await AstDoctors.findOne({
        _id: response.id,
      }).populate("Patients");
      console.log(doctor);
      if (doctor) {
        if (doctor["Patients"].length > 0) {
          res.status(200).json({
            pname: doctor["Patients"][0].Name,
            pemail: doctor["Patients"][0].pemail,
            dname: doctor.Name,
            demail: doctor.email,
          });
        } else {
          res.status(400).json({ message: "No Patients found." });
        }
      } else {
        res.status(400).json({ message: "No Patients found." });
      }
    } catch (err) {
      next(err);
    }
  } else if (response.type === "Patients") {
    try {
      const doctor = await Patients.findOne({
        _id: response.id,
      })
        .populate("Doctors")
        .populate("assistantDoctor");
      console.log(doctor);
      let demail = "";
      let dname = "";
      if (doctor["Doctors"].length > 0) {
        demail = doctor["Doctors"][0].email;
        dname = doctor["Doctors"][0].Name;
      } else {
        demail = doctor["assistantDoctor"][0].email;
        dname = doctor["assistantDoctor"][0].Name;
      }
      if (doctor) {
        res.status(200).json({
          pname: doctor.Name,
          demail: demail,
          dname: dname,
          pemail: doctor.pemail,
        });
      } else {
        res.status(400).json({ message: "No Patients found." });
      }
    } catch (err) {
      next(err);
    }
  }
});

module.exports = api;
