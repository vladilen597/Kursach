import React from "react";
import { NavLink, Switch, Route } from "react-router-dom";
import MentorProfile from "./MentorProfile/MentorProfile";
import MentorStudentsList from "./MentorStudentsList/MentorStudentsList";
import { BiUser } from "react-icons/bi";
import { BsCardList } from "react-icons/bs";

import "./Mentor.scss";
import { connect } from "react-redux";
import MentorStudentProfile from "./MentorStudentsList/MentorStudentProfile/MentorStudentProfile";

const Mentor = ({ token }) => {
  if (!token) {
    return <h1>Загрузка...</h1>;
  }

  return (
    <>
      <header className="header">
        <h2 className="header-user">MENTOR.LAB</h2>
      </header>
      <main className="mentor-window">
        <aside className="mentor-navigation-bar">
          <ul className="mentor-navigation-bar-list">
            <li className="mentor-navigation-bar-list-item">
              <NavLink
                className="mentor-navigation-bar-list-item-link"
                activeClassName="mentor-navigation-bar-list-item-link_active"
                to="/mentor/profile"
              >
                <BiUser className="mentor-navigation-bar-list-item-icon" />
                Профиль
              </NavLink>
            </li>
            <li className="mentor-navigation-bar-list-item">
              <NavLink
                className="mentor-navigation-bar-list-item-link"
                activeClassName="mentor-navigation-bar-list-item-link_active"
                to="/mentor/students_list"
              >
                <BsCardList className="mentor-navigation-bar-list-item-icon" />
                Список студентов
              </NavLink>
            </li>
          </ul>
        </aside>
        <Switch>
          <Route path="/mentor/profile" component={MentorProfile} />
          <Route path="/mentor/students_list" component={MentorStudentsList} />
          <Route path="/mentor/student/:id" component={MentorStudentProfile} />
        </Switch>
      </main>
    </>
  );
};

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

export default connect(mapStateToProps)(Mentor);
