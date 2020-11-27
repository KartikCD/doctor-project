const express = require("express");
const app = express();
const mongoose = require("mongoose");
var bodyParser = require("body-parser");
const routes = require("./routes/routes");
const cookieParser = require("cookie-parser")

app.set("view engine","ejs")

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.use(cookieParser())

var DBCONFIG = "mongodb://localhost/CHAT";

mongoose.connect(DBCONFIG, { useNewUrlParser: true });
let database = mongoose.connection;

database.on("error", console.error.bind(console, "connection error:"));
database.once("open", () => {
  console.log("MongoDB is connected 12345");
});

//Socket Connection Script

// Routes of API

app.use("/apiv1/medical/", routes);

app.listen(4000, "0.0.0.0", () => {
  console.log("Well Done, Now I am Listening");
});

app.use(express.static(__dirname));
