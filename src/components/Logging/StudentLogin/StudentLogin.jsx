import React, { useState } from "react";
import { useHistory } from "react-router";
import CustomInput from "../../GeneralComponents/CustomInput/CustomInput";
import { connect } from "react-redux";

import "./StudentLogin.scss";
import setToken from "../../../store/actions/setToken";
import StudentSignupForm from "./StudentSignupForm/StudentSignupForm";

const StudentLogin = ({ setToken, token }) => {
  const [loginUsername, setLoginUsername] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const history = useHistory();

  const handleLoginUsernameChange = (event) => {
    setLoginUsername(event.target.value);
  };

  const handleLoginPasswordChange = (event) => {
    setLoginPassword(event.target.value);
  };

  const handleClickLogin = (event) => {
    event.preventDefault();

    setIsLoading(true);

    let requestOptions = {
      method: "POST",
      headers: { Autorization: token, "Content-Type": "application/json" },
      body: JSON.stringify({
        username: loginUsername,
        password: loginPassword,
      }),
      redirect: "follow",
    };

    fetch("http://localhost:8080/login", requestOptions)
      .then((response) => response.json())
      .then((data) => {
        history.push("/student/profile");
        setIsLoading(false);
        setToken(data.message);
      })
      .catch((error) => {
        alert("error", error);
        setIsLoading(false);
      });
  };

  return (
    <main className="mentor-login">
      <form className="mentor-login-form">
        <CustomInput
          type="text"
          label="Логин"
          value={loginUsername}
          handleChange={handleLoginUsernameChange}
        />

        <CustomInput
          type="password"
          label="Пароль"
          value={loginPassword}
          handleChange={handleLoginPasswordChange}
        />
        <button
          disabled={isLoading}
          className="mentor-login-button"
          onClick={handleClickLogin}
        >
          {isLoading ? "ЗАГРУЗКА" : "ВОЙТИ"}
        </button>
      </form>
      <StudentSignupForm />
    </main>
  );
};

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

const maDispatchToProps = (dispatch) => {
  return {
    setToken: (id) => dispatch(setToken(id)),
  };
};

export default connect(mapStateToProps, maDispatchToProps)(StudentLogin);
