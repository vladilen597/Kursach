import React from "react";
import { connect } from "react-redux";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import { ImCheckmark } from "react-icons/im";

import "./MentorCoursePage.scss";

const MentorCoursePage = ({ course, token }) => {
  const handleFinishTraining = (id) => {
    let requestOptions = {
      method: "POST",
      headers: { Authorization: token },
      redirect: "follow",
    };

    fetch(
      `http://localhost:8080/courses/students/training/${id}/finish`,
      requestOptions
    )
      .then((response) => response.json())
      .then((result) => console.log(result))
      .catch((error) => console.log("error", error));
  };

  console.log(course);
  return (
    <motion.main
      initial={{ position: "absolute", opacity: 0 }}
      animate={{ position: "initial", opacity: 1 }}
      exit={{ position: "absolute", opacity: 0 }}
      transition={{ duration: 0.5 }}
      className="course-page-wrapper"
    >
      <div className="course-page-title">
        <h4>{course.courseName}</h4>
        <button
          onClick={() => handleFinishTraining(course.id)}
          className="course-page-title-finish"
        >
          <p className="course-page-title-finish-text">Закончить обучение</p>
          <ImCheckmark className="course-page-title-finish-icon" />
        </button>
      </div>
      <div className="course-page-description">
        <p>{course.description}</p>
        <p>{course.skillLevel}</p>
        <div>
          <p className="course-page-students-label">Список студентов: </p>
          <ul className="course-page-students-list">
            {course.students.map((student) => {
              return (
                <Link
                  key={student.id}
                  className="course-page-students-list-item-link"
                  to={`/mentor/student/${student.username}`}
                >
                  <li className="course-page-students-list-item">
                    {student.lastName} {student.firstName} {student.patronymic}
                  </li>
                </Link>
              );
            })}
          </ul>
        </div>
      </div>
    </motion.main>
  );
};

const mapStateToProps = (state) => {
  return {
    course: state.course,
    token: state.token,
  };
};

export default connect(mapStateToProps)(MentorCoursePage);
