import React from "react";
import { NavLink, Switch, Route } from "react-router-dom";
import { BiUser } from "react-icons/bi";
import { BsCardList } from "react-icons/bs";
import { MdOutlinePlayLesson } from "react-icons/md";
import { useLocation } from "react-router";
import { connect } from "react-redux";

import "./Student.scss";
import { AnimatePresence } from "framer-motion";
import StudentMentorsList from "./StudentMentorsList/StudentMentorsList";
import StudentProfile from "./StudentProfile/StudentProfile";
import StudentCourseList from "./StudentCourseList/StudentCourseList";
import StudentMentorProfile from "./StudentMentorsList/StudentMentorProfile/StudentMentorProfile";

const Student = ({ token }) => {
  const location = useLocation();

  if (!token) {
    return <h1>Загрузка...</h1>;
  }

  return (
    <>
      <header className="header">
        <h2 className="header-user">STUDENT.LAB</h2>
      </header>
      <main className="mentor-window">
        <aside className="mentor-navigation-bar">
          <ul className="mentor-navigation-bar-list">
            <li className="mentor-navigation-bar-list-item">
              <NavLink
                className="mentor-navigation-bar-list-item-link"
                activeClassName="mentor-navigation-bar-list-item-link_active"
                to="/student/profile"
              >
                <BiUser className="mentor-navigation-bar-list-item-icon" />
                Профиль
              </NavLink>
            </li>

            <li className="mentor-navigation-bar-list-item">
              <NavLink
                className="mentor-navigation-bar-list-item-link"
                activeClassName="mentor-navigation-bar-list-item-link_active"
                to="/student/mentors_list"
              >
                <BsCardList className="mentor-navigation-bar-list-item-icon" />
                Список преподавателей
              </NavLink>
            </li>

            <li className="mentor-navigation-bar-list-item">
              <NavLink
                className="mentor-navigation-bar-list-item-link"
                activeClassName="mentor-navigation-bar-list-item-link_active"
                to="/student/course_list"
              >
                <MdOutlinePlayLesson className="mentor-navigation-bar-list-item-icon" />
                Курсы
              </NavLink>
            </li>
          </ul>
        </aside>
        <section className="mentor-switch">
          <AnimatePresence>
            <Switch location={location} key={location.pathname}>
              <Route path="/student/profile" component={StudentProfile} />
              <Route
                path="/student/mentors_list"
                component={StudentMentorsList}
              />
              <Route
                path="/student/mentor/:id"
                component={StudentMentorProfile}
              />
              <Route
                path="/student/course_list"
                component={StudentCourseList}
              />
            </Switch>
          </AnimatePresence>
        </section>
      </main>
    </>
  );
};

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

export default connect(mapStateToProps)(Student);
