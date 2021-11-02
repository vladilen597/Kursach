import React, { useEffect } from "react";

import "./MentorStudentsList.scss";

const mockedStudents = [
  {
    id: 1,
    name: "Исаеня Владилен Евгеньевич",
    mark: 3.5,
    stack: ["React", "Vue"],
  },
  {
    id: 2,
    name: "Вишняков Сергей Владимирович",
    mark: 4.5,
    stack: ["Node.JS"],
  },
  {
    id: 3,
    name: "Петров Антон Васильевич",
    mark: 5,
    stack: [".NET", "Vue"],
  },
];

const MentorStudentsList = () => {
  useEffect(() => {
    var myHeaders = new Headers();
    myHeaders.append(
      "Authorization",
      "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2bGFkaWxlbiIsInJvbGVzIjpbIlJPTEVfTUVOVE9SIl0sImlhdCI6MTYzNTg1ODg2NSwiZXhwIjoxNjM1ODk0ODY1fQ.S7vAh-iLjRTlVwrc-oJP-63ZcWAAJKGoO0mMsNbuNk4"
    );
    myHeaders.append("Access-Control-Allow-Origin", "*");
    myHeaders.append(
      "Access-Control-Allow-Headers",
      "Origin, X-Requested-With, Content-Type, Accept"
    );

    fetch("http://localhost:8080/users/students", {
      headers: {
        "Access-Control-Allow-Methods": "PUT, POST, GET, DELETE, OPTIONS",
        Authorization:
          "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2bGFkaWxlbiIsInJvbGVzIjpbIlJPTEVfTUVOVE9SIl0sImlhdCI6MTYzNTg1ODg2NSwiZXhwIjoxNjM1ODk0ODY1fQ.S7vAh-iLjRTlVwrc-oJP-63ZcWAAJKGoO0mMsNbuNk4",
        "Access-Control-Allow-Origin": "*",
      },
    })
      .then((response) => response.text())
      .then((result) => console.log(result))
      .catch((error) => console.log("error", error));
  }, []);

  return (
    <ul className="students-list">
      <li className="students-list-item">
        <p>ФИО</p>
        <p>Оценка студента</p>
        <p>Приоритетные технологии</p>
      </li>
      {mockedStudents.map((student, index) => {
        return (
          <li className="students-list-item" key={student.id}>
            <p>{student.name}</p>
            <p>{student.mark}</p>
            <div className="technology-list">
              {student.stack.map((technology) => {
                return (
                  <p className="technology-list-item" key={technology}>
                    {technology}
                  </p>
                );
              })}
            </div>
          </li>
        );
      })}
    </ul>
  );
};

export default MentorStudentsList;
