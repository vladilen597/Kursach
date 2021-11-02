import React from "react";

import { Link } from "react-router-dom";

import "./Logging.scss";

const Logging = () => {
  return (
    <section className="logging-page">
      <Link to="/logging/mentor_login" className="mentor-side">
        <article className="login-line-wrapper">
          <p className="login-line">Я ПРЕПОДАВАТЕЛЬ</p>
        </article>
        <div className="color-over" />
      </Link>
      <Link to="/logging/student_login" className="student-side">
        <article className="login-line-wrapper">
          <p className="login-line">Я СТУДЕНТ</p>
        </article>
        <div className="color-over" />
      </Link>
    </section>
  );
};

export default Logging;
