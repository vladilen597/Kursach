import React from "react";

import { Link } from "react-router-dom";

import "./Logging.scss";

const Logging = () => {
  return (
    <section className="logging-page">
      <Link to="/logging/mentor_login" className="mentor-side">
        <article className="login-line-wrapper">
          <p className="login-line">Я ПРЕПОДАВАТЕЛЬ</p>
          <div className="hover-mentor">
            <i>
              "Всему, что необходимо знать, научить нельзя,
              <br />
              учитель может сделать только одно — указать дорогу."
              <br />- Ричард Олдингтон
            </i>
          </div>
        </article>
        <div className="color-over" />
      </Link>
      <Link to="/logging/student_login" className="student-side">
        <article className="login-line-wrapper">
          <p className="login-line">Я СТУДЕНТ</p>
          <div className="hover-student">
            <i>
              "Самые лучшие студенты те,
              <br />
              кто никогда до конца не доверяет своим профессорам"
              <br />- Джим Коллинз
            </i>
          </div>
        </article>
        <div className="color-over" />
      </Link>
    </section>
  );
};

export default Logging;
