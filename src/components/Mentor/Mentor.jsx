import React from "react";
import { NavLink, Switch, Route } from "react-router-dom";
import MentorProfile from "./MentorProfile/MentorProfile";
import MentorStudentsList from "./MentorStudentsList/MentorStudentsList";
import { BiUser } from "react-icons/bi";
import { BsCardList } from "react-icons/bs";
import { MdOutlinePlayLesson } from "react-icons/md";
import MentorStudentProfile from "./MentorStudentsList/MentorStudentProfile/MentorStudentProfile";
import MentorCourseList from "./MentorCourseList/MentorCourseList";
import { useLocation } from "react-router";
import { GrNotification } from "react-icons/gr";
import MentorCoursePage from "./MentorCourseList/MentorCoursePage/MentorCoursePage";
import { connect } from "react-redux";

import "./Mentor.scss";
import { AnimatePresence } from "framer-motion";
import MentorRequestList from "./MentorRequestList/MentorRequestList";

const Mentor = ({ token }) => {
  const location = useLocation();

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

            <li className="mentor-navigation-bar-list-item">
              <NavLink
                className="mentor-navigation-bar-list-item-link"
                activeClassName="mentor-navigation-bar-list-item-link_active"
                to="/mentor/course_list"
              >
                <MdOutlinePlayLesson className="mentor-navigation-bar-list-item-icon" />
                Список курсов
              </NavLink>
            </li>

            <li className="mentor-navigation-bar-list-item">
              <NavLink
                className="mentor-navigation-bar-list-item-link"
                activeClassName="mentor-navigation-bar-list-item-link_active"
                to="/mentor/course_requests"
              >
                <GrNotification className="mentor-navigation-bar-list-item-icon" />
                Заявки на курсы
              </NavLink>
            </li>
          </ul>
        </aside>
        <section className="mentor-switch">
          <AnimatePresence>
            <Switch location={location} key={location.pathname}>
              <Route path="/mentor/profile" component={MentorProfile} />
              <Route
                path="/mentor/students_list"
                component={MentorStudentsList}
              />
              <Route
                path="/mentor/student/:id"
                component={MentorStudentProfile}
              />
              <Route path="/mentor/course_list" component={MentorCourseList} />
              <Route
                path="/mentor/course_requests"
                component={MentorRequestList}
              />
              <Route path="/mentor/course/:id" component={MentorCoursePage} />
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

export default connect(mapStateToProps)(Mentor);