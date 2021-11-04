import React, { useState } from "react";
import CustomInput from "../../../GeneralComponents/CustomInput/CustomInput";

const StudentSignupForm = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [firstname, setFirstname] = useState("");
  const [lastname, setLastname] = useState("");
  const [patronymic, setPatronymic] = useState("");

  const handleLastnameChange = (event) => {
    setLastname(event.target.value);
  };

  const handleFirstnameChange = (event) => {
    setFirstname(event.target.value);
  };

  const handlePatronymicChange = (event) => {
    setPatronymic(event.target.value);
  };

  const handleUsernameChange = (event) => {
    setUsername(event.target.value);
  };

  const handlePasswordChange = (event) => {
    setPassword(event.target.value);
  };

  const handleClickSignup = (event) => {
    event.preventDefault();

    setIsLoading(true);

    let requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        username: username,
        password: password,
        lastName: lastname,
        firstName: firstname,
        patronymic: patronymic,
      }),
      redirect: "follow",
    };

    fetch("http://localhost:8080/registration/student", requestOptions)
      .then((response) => response.json())
      .then((result) => {
        console.log(result);
        setIsLoading(false);
        if (result.message) {
          alert("Регистрация прошла успешно!");
        }
        console.log(result.json());
        setUsername("");
        setPassword("");
      })
      .catch((error) => console.log("error", error));
  };
  return (
    <form className="mentor-login-form">
      <CustomInput
        type="text"
        label="Фамилия"
        value={lastname}
        handleChange={handleLastnameChange}
      />

      <CustomInput
        type="text"
        label="Имя"
        value={firstname}
        handleChange={handleFirstnameChange}
      />

      <CustomInput
        type="text"
        label="Отчество"
        value={patronymic}
        handleChange={handlePatronymicChange}
      />

      <CustomInput
        type="text"
        label="Логин"
        value={username}
        handleChange={handleUsernameChange}
      />

      <CustomInput
        type="password"
        label="Пароль"
        value={password}
        handleChange={handlePasswordChange}
      />
      <button
        disabled={
          isLoading ||
          lastname.length === 0 ||
          firstname === 0 ||
          patronymic.length === 0 ||
          username.length === 0 ||
          password.length === 0
        }
        className="mentor-signup-button"
        onClick={handleClickSignup}
      >
        {isLoading ? "ЗАГРУЗКА" : "ЗАРЕГИСТРИРОВАТЬСЯ"}
      </button>
      <p style={{ fontSize: "10px" }}>Заполните все необходимые поля</p>
    </form>
  );
};

export default StudentSignupForm;
