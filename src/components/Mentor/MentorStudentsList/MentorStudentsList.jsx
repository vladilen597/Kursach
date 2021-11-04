import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { Link } from "react-router-dom";
import setProfileId from "../../../store/actions/setProfileId";

import "./MentorStudentsList.scss";

const MentorStudentsList = ({ profileId, token, setProfileId }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [studentsList, setStudentsList] = useState([]);
  console.log(studentsList);

  useEffect(() => {
    setIsLoading(true);
    fetch("http://localhost:8080/users/students", {
      headers: {
        Authorization: token,
      },
    })
      .then((response) => response.json())
      .then((data) => {
        setStudentsList(data);
        setIsLoading(false);
      })
      .catch((error) => {
        setIsLoading(false);
        console.log("error", error);
      });
  }, []);

  if (isLoading) {
    return <h1>Загрузка</h1>;
  }

  return (
    <ul className="students-list">
      <li className="students-top-list-item">
        <p>ФИО</p>
        <p>Оценка студента</p>
        <p>Приоритетные технологии</p>
      </li>
      {studentsList.map((student) => {
        return (
          <Link
            className="students-list-item-link"
            to={`/mentor/student/${student.username}`}
            key={student.id}
          >
            <li className="students-list-item">
              <p>
                {student.lastName +
                  " " +
                  student.firstName +
                  " " +
                  student.patronymic}
              </p>
              <p>{student.averageRating}</p>
              <ul className="technology-list">
                {student.skills.map((technology) => {
                  return (
                    <li className="technology-list-item" key={technology}>
                      {technology}
                    </li>
                  );
                })}
              </ul>
            </li>
          </Link>
        );
      })}
      <li className="students-list-item"></li>
    </ul>
  );
};

const mapStateToProps = (state) => {
  return {
    token: state.token,
    profileId: state.profileId,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    setProfileId: (id) => dispatch(setProfileId(id)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(MentorStudentsList);
