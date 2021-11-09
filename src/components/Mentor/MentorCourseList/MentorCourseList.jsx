import React, { useEffect, useState } from "react";
import DialogMentorAddCourse from "./DialogMentorAddCourse/DialogMentorAddCourse.jsx";
import { Link } from "react-router-dom";
import { connect } from "react-redux";
import { FcPlus } from "react-icons/fc";
import { BsTrash } from "react-icons/bs";
import { motion } from "framer-motion";
import setCourse from "../../../store/actions/setCourse.jsx";

import "./MentorCourseList.scss";

const MentorCourseList = ({ token, setCourse }) => {
  const [activeCourses, setActiveCourses] = useState([]);
  const [courses, setCourses] = useState([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const handleDialogOpen = () => {
    setIsDialogOpen(true);
  };
  console.log(courses);

  const handleDialogClose = () => {
    setIsDialogOpen(false);
  };

  const handleSingleCourseOpen = (course) => {
    setCourse(course);
  };

  const handleRemoveCourse = (id) => {
    let tempArray = courses;

    tempArray = tempArray.filter((course) => {
      return course.id !== id;
    });

    setCourses(tempArray);

    var raw = JSON.stringify({
      id: id,
    });

    var requestOptions = {
      method: "DELETE",
      headers: { headers: { Authorization: token } },
      body: raw,
      redirect: "follow",
    };

    fetch("http://localhost:8080/courses/current", requestOptions)
      .then((response) => response.json())
      .then((result) => console.log(result))
      .catch((error) => console.log("error", error));
  };

  useEffect(() => {
    fetch("http://localhost:8080/courses/current", {
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

  useEffect(() => {
    fetch("http://localhost:8080/courses/current/active", {
      headers: {
        Authorization: token,
      },
    })
      .then((response) => response.json())
      .then((data) => {
        console.log(data);
        setActiveCourses(data);
      })
      .catch((error) => console.log(error));
  }, []);

  return (
    <>
      <motion.main
        initial={{ position: "absolute", opacity: 0 }}
        animate={{ position: "initial", opacity: 1 }}
        exit={{ position: "absolute", opacity: 0 }}
        transition={{ duration: 0.5 }}
        className="mentor-course-display"
      >
        <button
          className="mentor-course-display-add-course"
          onClick={handleDialogOpen}
        >
          ДОБАВИТЬ КУРС
          <FcPlus className="mentor-course-display-add-course-icon" />
        </button>
        <section className="mentor-course-display-list">
          {courses.length === 0 ? (
            "Курсы отсутсвуют"
          ) : (
            <ul>
              {courses.map((course) => {
                return (
                  <li
                    className="mentor-course-display-list-item"
                    key={course.id}
                  >
                    <div className="mentor-course-display-list-item-delete-block">
                      <BsTrash
                        className="mentor-course-display-list-item-icon"
                        onClick={() => handleRemoveCourse(course.id)}
                      />
                      <p>{course.courseName}</p>
                    </div>
                    <p>{course.description}</p>
                    <p>{course.skillLevel}</p>
                  </li>
                );
              })}
            </ul>
          )}
        </section>

        <DialogMentorAddCourse
          isDialogOpen={isDialogOpen}
          handleDialogClose={handleDialogClose}
          setCourses={setCourses}
          courses={courses}
        />
      </motion.main>
      <motion.main
        initial={{ position: "absolute", opacity: 0 }}
        animate={{ position: "initial", opacity: 1 }}
        exit={{ position: "absolute", opacity: 0 }}
        transition={{ duration: 0.5 }}
        className="mentor-course-display"
      >
        <h4 className="mentor-course-display-header">Активные курсы</h4>
        <section className="mentor-course-display-list">
          <ul>
            {activeCourses.map((course) => {
              return (
                <Link
                  onClick={() => handleSingleCourseOpen(course)}
                  className="mentor-course-display-active-list-item-link"
                  to={`/mentor/course/${course.id}`}
                >
                  <li
                    className="mentor-course-display-active-list-item"
                    key={course.id}
                  >
                    <p>{course.courseName}</p>
                    <ul className="mentor-course-display-active-students-list">
                      Студенты
                      {course.students.map((student) => {
                        return (
                          <li
                            key={student.id}
                            className="mentor-course-display-active-students-list-item"
                          >
                            {student.lastName} {student.firstName}{" "}
                            {student.patronymic}
                          </li>
                        );
                      })}
                    </ul>
                  </li>
                </Link>
              );
            })}
          </ul>
        </section>
      </motion.main>
    </>
  );
};

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    setCourse: (course) => dispatch(setCourse(course)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(MentorCourseList);
