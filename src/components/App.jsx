import React from "react";

import {
  BrowserRouter as Router,
  Switch,
  Route,
  Redirect,
} from "react-router-dom";

import MentorLogin from "./Logging/MentorLogin/MentorLogin.jsx";
import StudentLogin from "./Logging/StudentLogin/StudentLogin.jsx";
import Logging from "./Logging/Logging.jsx";
import Mentor from "./Mentor/Mentor.jsx";
import Student from "./Student/Student.jsx";

import "./App.scss";

const App = () => {
  return (
    <Router>
      <Switch>
        <Route exact path="/logging" component={Logging} />
        <Route path="/logging/student_login" component={StudentLogin} />
        <Route path="/logging/mentor_login" component={MentorLogin} />
        <Route path="/mentor" component={Mentor} />
        <Route path="/student" component={Student} />
        <Redirect to="/logging" />
      </Switch>
    </Router>
  );
};

export default App;
