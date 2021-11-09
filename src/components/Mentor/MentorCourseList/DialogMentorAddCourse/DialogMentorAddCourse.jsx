import React, { useState } from "react";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import TextField from "@mui/material/TextField";
import { DialogActions } from "@mui/material";
import { MdClose } from "react-icons/md";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select from "@mui/material/Select";
import InputLabel from "@mui/material/InputLabel";

import "./DialogMentorAddCourse.scss";
import { connect } from "react-redux";

const DialogMentorAddCourse = ({
  isDialogOpen,
  handleDialogClose,
  setCourses,
  courses,
  token,
}) => {
  const [courseName, setCourseName] = useState("");
  const [difficulty, setDifficulty] = useState("");
  const [description, setDescription] = useState("");

  const convertLevel = (data) => {
    if (data === "NOVICE") return "Новичок";
    if (data === "EXPERIENCED") return "Опытный";
    if (data === "ADVANCED") return "Продвинутый";
    if (data === "PROFESSIONAL") return "Профессионал";
  };

  const handleDifficultyChange = (event) => {
    setDifficulty(event.target.value);
  };

  const handleDescriptionChange = (event) => {
    setDescription(event.target.value);
  };

  const handleSaveCourse = () => {
    var myHeaders = new Headers();
    myHeaders.append("Authorization", token);
    myHeaders.append("Content-Type", "application/json");

    var raw = JSON.stringify({
      courseName: courseName,
      skillLevel: difficulty,
      description: description,
    });

    var requestOptions = {
      method: "POST",
      headers: myHeaders,
      body: raw,
      redirect: "follow",
    };

    fetch("http://localhost:8080/courses/current", requestOptions)
      .then((response) => response.json())
      .then((result) => console.log(result))
      .catch((error) => console.log("error", error));

    setCourses((prevState) => [
      ...prevState,
      {
        id: courses.length,
        courseName: courseName,
        description: description,
        skillLevel: convertLevel(difficulty),
      },
    ]);
    handleDialogClose();
  };

  const handleChangeCourseName = (event) => {
    setCourseName(event.target.value);
  };

  return (
    <Dialog open={isDialogOpen} onClose={handleDialogClose}>
      <DialogTitle>
        <p className="dialog-add-course-title">
          Добавление курса
          <MdClose
            className="dialog-add-course-title-icon"
            onClick={handleDialogClose}
          />
        </p>
      </DialogTitle>
      <form>
        <DialogContent>
          <section className="dialog-add-course-content">
            <TextField
              className="dialog-add-course-content-input"
              value={courseName}
              onChange={handleChangeCourseName}
              id="outlined-basic"
              label="Название курса"
              variant="outlined"
            />
            <div className="dialog-add-course-blank-div" />
            <TextField
              className="dialog-add-course-content-input"
              value={description}
              onChange={handleDescriptionChange}
              id="outlined-basic"
              label="Описание"
              variant="outlined"
            />
            <div className="dialog-add-course-blank-div" />

            <FormControl fullWidth>
              <InputLabel id="demo-simple-select-label">Сложность</InputLabel>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={difficulty}
                label="Сложность"
                onChange={handleDifficultyChange}
              >
                <MenuItem value={"NOVICE"}>Новичок</MenuItem>
                <MenuItem value={"EXPERIENCED"}>Опытный</MenuItem>
                <MenuItem value={"ADVANCED"}>Продвинутый</MenuItem>
                <MenuItem value={"PROFESSIONAL"}>Профессионал</MenuItem>
              </Select>
            </FormControl>
          </section>
        </DialogContent>
        <DialogActions>
          <button
            disabled={
              courseName.length === 0 ||
              difficulty.length === 0 ||
              description.length === 0
            }
            type="button"
            className="dialog-add-course-button"
            onClick={handleSaveCourse}
          >
            ДОБАВИТЬ КУРС
          </button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

export default connect(mapStateToProps)(DialogMentorAddCourse);
