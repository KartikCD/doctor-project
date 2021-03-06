const express = require("express");

const Doctors = require("../models/doctorRegistration");
const AstDoctors = require("../models/assistantDoctors");
const Patients = require("../models/patients");
const Sensed = require("../models/sensed");
// const { Mongoose } = require("mongoose");
const mailService = require("../utilities/mailer");

const api = express.Router();

api.post("/registration", async (req, res) => {
  const details = {
    Name: req.body.Name,
    date_of_birth: req.body.date_of_birth,
    doctor_license_number: req.body.doctor_license_number,
    PhoneNumber: req.body.PhoneNumber,
    password: req.body.password,
    email: req.body.email,
  };

  console.log(details);

  const checkForDuplicate = await Doctors.exists({
    email: req.body.email,
  });
  if (checkForDuplicate === true) {
    res.status(200).json({
      datasaved: "Email Already Exist",
      status: "fail",
    });
  } else {
    const doctor_regis = new Doctors(details);
    doctor_regis
      .save()
      .then((data) => {
        mailService.transporter.sendMail(
          mailService.verificationEmailTemplate(
            "k25dave@gmail.com",
            data.email,
            data._id,
            "doctorVerification"
          ),
          (err, info) => {
            if (err) {
              console.log(err);
              return err;
            }
            if (info.messageId) {
              console.log(info);
              console.log(info.messageId);
            }
          }
        );
      })
      .then(
        res.status(200).json({
          datasaved: "data saved success",
          status: "success",
        })
      )
      .catch((err) => console.log(err));
  }
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

api.post("/astDoctorReg", async (req, res) => {
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
  const checkForDuplicate = await AstDoctors.exists({
    email: req.body.email,
  });
  if (checkForDuplicate === true) {
    res.status(200).json({
      datasaved: "Email Already Exist",
      status: "fail",
    });
  } else {
    const saveAssistant = new AstDoctors(astDoctorReg);
    saveAssistant["Doctors"].push(doctorid);
    saveAssistant
      .save()
      .then((data) => {
        console.log(data);
        console.log(
          mailService.verificationEmailTemplate(
            "k25dave@gmail.com",
            data.email,
            data._id,
            "assistantDoctorVerification"
          )
        );
        mailService.transporter.sendMail(
          mailService.verificationEmailTemplate(
            "k25dave@gmail.com",
            data.email,
            data._id,
            "assistantDoctorVerification"
          ),
          (err, info) => {
            if (err) {
              console.log(err);
              return err;
            }
            if (info.messageId) {
              console.log(info);
              console.log(info.messageId);
            }
          }
        );
      })
      .then(
        res.status(200).json({
          datasaved: "data saved success",
          status: "success",
        })
      )
      .then(
        Doctors.findOneAndUpdate(
          {
            _id: doctorid,
          },
          {
            $push: {
              assistantDoctor: saveAssistant._id,
            },
          },
          {
            new: true,
          },
          (err, result) => {
            console.log(err);
            console.log(result);
          }
        )
      )
      .catch((err) => console.log(err));
  }
});

api.post("/assistantPatientReg", async (req, res, next) => {
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

  const checkForDuplicate = await Patients.exists({
    email: req.body.pemail,
  });
  if (checkForDuplicate === true) {
    res.status(200).json({
      datasaved: "Email Already Exist",
      status: "fail",
    });
  } else {
    const savePatient = new Patients(patient);
    savePatient["assistantDoctor"].push(astDoctorId);

    savePatient
      .save()
      .then((data) => {
        console.log(data);
        console.log(
          mailService.verificationEmailTemplate(
            "k25dave@gmail.com",
            data.email,
            data._id,
            "patientVerification"
          )
        );
        mailService.transporter.sendMail(
          mailService.verificationEmailTemplate(
            "k25dave@gmail.com",
            data.pemail,
            data._id,
            "patientVerification"
          ),
          (err, info) => {
            if (err) {
              console.log(err);
              return err;
            }
            if (info.messageId) {
              console.log(info);
              console.log(info.messageId);
            }
          }
        );
      })
      .then(
        res.status(200).json({
          datasaved: "data saved success",
          status: "success",
        })
      )
      .then(
        AstDoctors.findOneAndUpdate(
          {
            _id: astDoctorId,
          },
          {
            $push: {
              Patients: savePatient._id,
            },
          },
          {
            new: true,
          },
          (err, result) => {
            console.log(err);
            console.log(result);
          }
        )
      )
      .catch((err) => console.log(err));
  }
});

api.post("/patientReg", async (req, res) => {
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

  const checkForDuplicate = await Patients.exists({
    email: req.body.pemail,
  });
  if (checkForDuplicate === true) {
    res.status(200).json({
      datasaved: "Email Already Exist",
      status: "fail",
    });
  } else {
    const savePatient = new Patients(patient);
    savePatient["Doctors"].push(doctorid);

    savePatient
      .save()
      .then((data) => {
        console.log(data);
        console.log(
          mailService.verificationEmailTemplate(
            "k25dave@gmail.com",
            data.email,
            data._id,
            "patientVerification"
          )
        );
        mailService.transporter.sendMail(
          mailService.verificationEmailTemplate(
            "k25dave@gmail.com",
            data.pemail,
            data._id,
            "patientVerification"
          ),
          (err, info) => {
            if (err) {
              console.log(err);
              return err;
            }
            if (info.messageId) {
              console.log(info);
              console.log(info.messageId);
            }
          }
        );
      })
      .then(
        Doctors.findOneAndUpdate(
          {
            _id: doctorid,
          },
          {
            $push: {
              Patients: savePatient._id,
            },
          },
          {
            new: true,
          },
          (err, result) => {
            console.log(err);
            console.log(result);
          }
        )
      )
      .then(
        res.status(200).json({
          datasaved: "data saved success",
          status: "success",
        })
      )
      .catch((err) => console.log(err));
  }
});

api.post("/Login", (req, res) => {
  const logincred = {
    email: req.body.email,
    password: req.body.password,
    type: req.body.type,
  };

  // console.log(logincred);
  if (logincred["type"] === "Doctor") {
    Doctors.findOne({
      email: logincred["email"],
    })
      .then((doctor) => {
        if (!doctor) {
          res.status(200).json({
            status: "FAILURE",
            message: "doctor not available",
            id: null,
            name: null,
            phone: null,
            email: null,
          });
        }
        if (doctor.verification === true) {
          if (doctor.password === logincred["password"]) {
            res
              .status(200)
              .cookie("id", doctor._id.toString())
              .cookie("type", "Doctor")
              .json({
                status: "SUCCESS",
                message: "success",
                id: doctor._id,
                name: doctor.Name,
                phone: doctor.PhoneNumber.toString(),
                email: doctor.email.toString(),
              });
          } else {
            res.status(200).json({
              status: "FAILURE",
              message: "password incorrect",
              id: null,
              name: null,
              phone: null,
              email: null,
            });
          }
        } else if (doctor.verification === false) {
          res.status(200).json({
            status: "FAIL",
            message: "unverified",
            id: null,
            name: null,
            phone: null,
            email: null,
          });
        }
      })
      .catch((err) => {
        console.log(err);
      });
  }
  if (logincred["type"] === "assistantDoctor") {
    AstDoctors.findOne({
      email: logincred["email"],
    })
      .then((assistantDoctor) => {
        if (!assistantDoctor) {
          res.status(200).json({
            status: "FAILURE",
            message: "Assistant Doctor not available",
            id: null,
            name: null,
            phone: null,
            email: null,
          });
        }
        if (assistantDoctor.verification === true) {
          if (assistantDoctor.password === logincred["password"]) {
            res
              .status(200)
              .cookie("id", assistantDoctor._id.toString())
              .cookie("type", "assistantDoctor")
              .json({
                status: "SUCCESS",
                message: "success",
                id: assistantDoctor._id,
                name: assistantDoctor.Name,
                phone: assistantDoctor.PhoneNumber.toString(),
                email: assistantDoctor.email.toString(),
              });
          } else {
            res.status(200).json({
              status: "FAILURE",
              message: "password incorrect",
              id: null,
              name: null,
              phone: null,
              email: null,
            });
          }
        } else if (assistantDoctor.verification === false) {
          res.status(200).json({
            status: "FAIL",
            message: "unverified",
            id: null,
            name: null,
            phone: null,
            email: null,
          });
        }
      })
      .catch((err) => {
        console.log(err);
      });
  }
  if (logincred["type"] === "Patients") {
    Patients.findOne({
      pemail: logincred["email"],
    })
      .then((patient) => {
        if (!patient) {
          res.status(200).json({
            status: "FAILURE",
            message: "Patient not available",
            id: null,
            name: null,
            phone: null,
            email: null,
          });
        }
        if (patient.verification === true) {
          if (patient.password === logincred["password"]) {
            res
              .status(200)
              .cookie("id", patient._id.toString())
              .cookie("type", "Patients")
              .json({
                status: "SUCCESS",
                message: "success",
                id: patient._id,
                name: patient.Name,
                phone: patient.PhoneNumber.toString(),
                email: patient.pemail.toString(),
              });
          } else {
            res.status(200).json({
              status: "FAILURE",
              message: "password incorrect",
              id: null,
              name: null,
              phone: null,
              email: null,
            });
          }
        } else if (patient.verification === false) {
          res.status(200).json({
            status: "FAIL",
            message: "unverified",
            id: null,
            name: null,
            phone: null,
            email: null,
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
        res.status(400).json({
          message: "Doctor not found",
        });
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
        res.status(400).json({
          message: "Doctor not found",
        });
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
        res.status(400).json({
          message: "Doctor not found",
        });
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
          res.status(400).json({
            message: "No Patients found.",
          });
        }
      } else {
        res.status(400).json({
          message: "No Patients found.",
        });
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
          res.status(400).json({
            message: "No Patients found.",
          });
        }
      } else {
        res.status(400).json({
          message: "No Patients found.",
        });
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
        res.status(400).json({
          message: "No Patients found.",
        });
      }
    } catch (err) {
      next(err);
    }
  }
});

api.get("/doctorVerification/:id", (req, res) => {
  const doctorId = {
    id: req.params.id,
  };
  Doctors.findByIdAndUpdate(
    {
      _id: doctorId.id,
    },
    {
      verification: true,
    },
    {
      new: true,
    }
  )
    .then((data) => {
      if (data.verification) {
        res.render("../views/verification");
      } else {
        res.send("Link Invalid");
      }
    })
    .catch((err) => {
      console.log(err);
    });
});

api.get("/patientVerification/:id", (req, res) => {
  const patientId = {
    id: req.params.id,
  };
  Patients.findByIdAndUpdate(
    {
      _id: patientId.id,
    },
    {
      verification: true,
    },
    {
      new: true,
    }
  )
    .then((data) => {
      if (data.verification) {
        res.render("../views/verification");
      } else {
        res.send("Link Invalid");
      }
    })
    .catch((err) => {
      console.log(err);
    });
});

api.get("/assistantDoctorVerification/:id", (req, res) => {
  const assistantDoctorId = {
    id: req.params.id,
  };
  AstDoctors.findByIdAndUpdate(
    {
      _id: assistantDoctorId.id,
    },
    {
      verification: true,
    },
    {
      new: true,
    }
  )
    .then((data) => {
      if (data.verification) {
        res.render("../views/verification");
      } else {
        res.send("Link Invalid");
      }
    })
    .catch((err) => {
      console.log(err);
    });
});

api.get("/getDetails", (req, res) => {
  const id = req.cookies.id;
  const type = req.cookies.type;
  if (id === undefined || type === undefined) {
    res.send(`
      <h1>INVALID AUTHENTICATION ERROR</h1>
      <br>
      <a href="/apiv1/medical/loginPage">Click here to login</a>
    
    `);
  }
  if (type === "Doctor") {
    Doctors.findById({
      _id: id,
    })
      .populate("Patients", [
        "Name",
        "date_of_birth",
        "PhoneNumber",
        "Disease",
        "healthDescription",
        "pemail",
        "verification",
      ])
      .populate("assistantDoctor", [
        "Name",
        "date_of_birth",
        "doctor_license_number",
        "PhoneNumber",
        "email",
      ])
      .then((data) => {
        console.log(data);
        res.render("../views/info", {
          type: "Doctor",
          info: data,
        });
      });
  }
  if (type === "assistantDoctor") {
    AstDoctors.findById({
      _id: id,
    })
      .populate("Patients", [
        "Name",
        "date_of_birth",
        "PhoneNumber",
        "Disease",
        "healthDescription",
        "pemail",
        "verification",
      ])
      .populate("Doctors", [
        "Name",
        "date_of_birth",
        "doctor_license_number",
        "PhoneNumber",
        "email",
      ])
      .then((data) => {
        console.log(data);
        res.render("../views/info", {
          type: "assistantDoctor",
          info: data,
        });
      });
  }
  if (type === "Patients") {
    Patients.findById({
      _id: id,
    })
      .populate("Doctors", [
        "Name",
        "date_of_birth",
        "doctor_license_number",
        "PhoneNumber",
        "email",
      ])
      .populate("assistantDoctors", [
        "Name",
        "date_of_birth",
        "doctor_license_number",
        "PhoneNumber",
        "email",
      ])
      .then((data) => {
        console.log(data);
        res.render("../views/info", {
          type: "Patients",
          info: data,
        });
      });
  }
});

api.get("/detailsmobile", (req, res, next) => {
  const id = req.query.id;
  const type = req.query.type;
  if (type === "Doctor") {
    Doctors.findById({
      _id: id,
    })
      .populate("Patients", [
        "Name",
        "date_of_birth",
        "PhoneNumber",
        "Disease",
        "healthDescription",
        "pemail",
        "verification",
      ])
      .populate("assistantDoctor", [
        "Name",
        "date_of_birth",
        "doctor_license_number",
        "PhoneNumber",
        "email",
      ])
      .then((data) => {
        console.log(data);
        res.render("../views/info1", {
          type: "Doctor",
          info: data,
        });
      });
  }
  if (type === "assistantDoctor") {
    AstDoctors.findById({
      _id: id,
    })
      .populate("Patients", [
        "Name",
        "date_of_birth",
        "PhoneNumber",
        "Disease",
        "healthDescription",
        "pemail",
        "verification",
      ])
      .populate("Doctors", [
        "Name",
        "date_of_birth",
        "doctor_license_number",
        "PhoneNumber",
        "email",
      ])
      .then((data) => {
        console.log(data);
        res.render("../views/info1", {
          type: "assistantDoctor",
          info: data,
        });
      });
  }
  if (type === "Patients") {
    Patients.findById({
      _id: id,
    })
      .populate("Doctors", [
        "Name",
        "date_of_birth",
        "doctor_license_number",
        "PhoneNumber",
        "email",
      ])
      .populate("assistantDoctors", [
        "Name",
        "date_of_birth",
        "doctor_license_number",
        "PhoneNumber",
        "email",
      ])
      .then((data) => {
        console.log(data);
        res.render("../views/info1", {
          type: "Patients",
          info: data,
        });
      });
  }
});

api.get("/loginPage", (req, res) => {
  res.clearCookie("token").clearCookie("id").render("../views/login");
});

api.get("/register", (req, res) => {
  res.render("../views/doctor_registration");
});

module.exports = api;
