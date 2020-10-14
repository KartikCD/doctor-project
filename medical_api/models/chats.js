const mongoose = require("mongoose");

const conversation = new mongoose.Schema({
  _id: mongoose.Schema.Types.ObjectId,
  participants: [
    {
      type: mongoose.Schema.Types.ObjectId,
      required: true,
      refPath: "onModel"
    },
    { type: mongoose.Schema.Types.ObjectId, required: true, refPath: "onModel" }
  ],
  onModel: {
    type: String,
    required: true,
    enum: ["Doctors", "AstDoctors"]
  }
});

const Message = new mongoose.Schema({
  _id: [{ type: mongoose.Schema.Types.ObjectId, ref: "conversation" }],
  sender: String,
  content: String,
  time_created: new Date()
});

module.exports = conversation = mongoose.model("conversation", conversation);
module.exports = Message = mongoose.model("Message", Message);
