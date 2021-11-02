import React, { useState } from "react";
import { useHistory } from "react-router";
import CustomInput from "../../GeneralComponents/CustomInput/CustomInput";
import { connect } from "react-redux";

import "./MentorLogin.scss";
import setToken from "../../../store/actions/setToken";

const MentorLogin = ({ setToken, token }) => {
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
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        username: loginUsername,
        password: loginPassword,
      }),
      redirect: "follow",
    };

    fetch("http://localhost:8080/login", requestOptions)
      .then((response) => response.json())
      .then((data) => {
        if (data.message) {
          history.push("/mentor/students_list");
        } else {
          alert(data.error);
        }
        setIsLoading(false);
      })
      .catch((error) => alert("error", error));
  };

  const handleClickSignup = (event) => {
    event.preventDefault();

    setIsLoading(true);

    let requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        username: loginUsername,
        password: loginPassword,
      }),
      redirect: "follow",
    };

    fetch("http://localhost:8080/registration/mentor", requestOptions)
      .then((response) => response.json())
      .then((result) => {
        console.log(result);
        setIsLoading(false);
        if (result.message) {
          alert("Регистрация прошла успешно!");
        }
        console.log(result.json());
        setLoginUsername("");
        setLoginPassword("");
      })
      .catch((error) => console.log("error", error));
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
        <button
          disabled={isLoading}
          className="mentor-signup-button"
          onClick={handleClickSignup}
        >
          {isLoading ? "ЗАГРУЗКА" : "ЗАРЕГИСТРИРОВАТЬСЯ"}
        </button>
      </form>
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
    setToken: (id) => dispatch(setToken),
  };
};

export default connect(mapStateToProps, maDispatchToProps)(MentorLogin);
