import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { motion } from "framer-motion";
import { Link } from "react-router-dom";

import "./StudentCourseList.scss";

const StudentCourseList = ({ token }) => {
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/courses/requests/approved", {
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
  }, []);

  return (
    <motion.main
      initial={{ position: "absolute", opacity: 0 }}
      animate={{ position: "initial", opacity: 1 }}
      exit={{ position: "absolute", opacity: 0 }}
      transition={{ duration: 0.5 }}
      className="mentor-course-display"
    >
      <section className="student-course-display-list">
        {courses.length === 0 ? (
          "Активные курсы отсутствуют. Перейдите в профиль к преподавателю и подайте заявку на курс"
        ) : (
          <>
            <p className="student-course-line">Активные курсы</p>
            <ul className="student-course-list">
              {courses.map((course) => {
                if (course.students !== undefined) {
                  return (
                    <li
                      className="student-course-display-list-item"
                      key={course.requestId}
                    >
                      <div className="student-course-display-list-item-description-block">
                        <p className="student-course-display-list-item-description">
                          {course.courseRepresentation.courseName}
                        </p>
                        <p className="student-course-display-list-item-description">
                          Сложность: {course.courseRepresentation.skillLevel}
                        </p>
                      </div>
                      <div className="mentor-requests-list-right-part">
                        <p className="student-course-display-list-item-description">
                          Преподаватель:{" "}
                          <Link
                            to={`/student/mentor/${course.courseRepresentation.mentorName.username}`}
                            className="mentor-requests-list-item-asignee-link"
                          >
                            {course.courseRepresentation.mentorName.lastName}{" "}
                            {course.courseRepresentation.mentorName.firstName}{" "}
                            {course.courseRepresentation.mentorName.patronymic}
                          </Link>
                        </p>
                      </div>
                    </li>
                  );
                }
              })}
            </ul>
          </>
        )}
      </section>
    </motion.main>
  );
};

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

export default connect(mapStateToProps)(StudentCourseList);
