import React, { useEffect, useState } from "react";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import { MdClose } from "react-icons/md";

import { connect } from "react-redux";

import "./StudentMentorCourseApplyDialog.scss";

const StudentMentorCourseApplyDialog = React.memo(
  ({ isDialogOpen, handleDialogClose, token, userProfile }) => {
    const [courses, setCourses] = useState([]);

    const handleChooseCourse = (id) => {
      if (userProfile) {
        var myHeaders = new Headers();
        myHeaders.append("Authorization", token);
        myHeaders.append("Content-Type", "application/json");

        var raw = JSON.stringify({
          id: id,
        });

        var requestOptions = {
          method: "POST",
          headers: myHeaders,
          body: raw,
          redirect: "follow",
        };

        fetch(
          `http://localhost:8080/courses/${userProfile.username}`,
          requestOptions
        )
          .then((response) => response.json())
          .then((result) => console.log(result))
          .catch((error) => console.log("error", error));

        handleDialogClose();
      }
    };

    useEffect(() => {
      if (userProfile) {
        fetch(`http://localhost:8080/courses/${userProfile.username}`, {
          headers: {
            Authorization: token,
          },
        })
          .then((response) => response.json())
          .then((data) => {
            console.log(data);
            setCourses(data);
          })
          .catch((error) => console.log(error));
      }
    }, [userProfile]);

    console.log(courses);
    return (
      <Dialog open={isDialogOpen} onClose={handleDialogClose}>
        <DialogTitle>
          <p className="dialog-add-course-title">
            {courses.length === 0 ? "Нет доступных курсов" : "Заявка на курс"}
            <MdClose
              className="dialog-add-course-title-icon"
              onClick={handleDialogClose}
            />
          </p>
        </DialogTitle>
        <DialogContent>
          <p className="dialog-apply-course-notification">
            Выберите курс нажатием на него
          </p>
          <ul className="dialog-apply-course-list">
            {courses.map((course) => {
              return (
                <li
                  className="dialog-apply-course-list-item"
                  key={course.id}
                  onClick={() => handleChooseCourse(course.id)}
                >
                  <strong>{course.courseName}</strong>
                  <p>{course.description}</p>
                  <p>{course.skillLevel}</p>
                </li>
              );
            })}
          </ul>
        </DialogContent>
      </Dialog>
    );
  }
);

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

export default connect(mapStateToProps)(StudentMentorCourseApplyDialog);
