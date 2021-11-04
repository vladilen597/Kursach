import React, { useEffect, useState } from "react";
import ReactStars from "react-rating-stars-component";
import { connect } from "react-redux";
import { useLocation } from "react-router";
import blankProfilePicture from "../../../../resourses/blankProfilePicture.png";

import "./MentorStudentProfile.scss";

const MentorStudentProfile = ({ token }) => {
  const [userProfile, setUserProfile] = useState(null);
  const username = useLocation().pathname.split("/").pop();

  useEffect(() => {
    fetch(`http://localhost:8080/users/${username}`, {
      headers: {
        Authorization: token,
      },
    })
      .then((response) => response.json())
      .then((data) => {
        console.log(data);
        setUserProfile(data);
      })
      .catch((error) => console.log(error));
  }, []);

  if (!userProfile) {
    return <h1>Загрузка...</h1>;
  }

  console.log(userProfile);
  return (
    <main className="mentor-student-profile">
      <form className="mentor-student-profile-form">
        <section className="mentor-student-profile-form-rating-block">
          <img
            className="profile-info-picture"
            src={userProfile.profilePicture || blankProfilePicture}
            alt="Profile"
          />
          <ReactStars
            classNames="profile-info-rating"
            size={24}
            isHalf={true}
            edit={false}
          />
        </section>
        <div className="profile-info-block">
          <label className="profile-info-name-label">ФИО</label>
          <p className="profile-info-name-line">
            {userProfile.lastName +
              " " +
              userProfile.firstName +
              " " +
              userProfile.patronymic}
          </p>

          <label className="profile-info-name-label">Логин</label>
          <p className="profile-info-username">{userProfile.username}</p>

          <label className="profile-info-name-label">Технологии</label>
          <ul className="profile-info-username">
            {!userProfile
              ? "Загрузка..."
              : userProfile.skillsNames.length === 0
              ? "Нет выбранных технологий"
              : userProfile.skillsNames.map((skill) => {
                  return <li key={skill}>{skill}</li>;
                })}
          </ul>

          <label className="profile-info-name-label">Отзывы</label>
          <p className="profile-info-name-line"></p>
        </div>
      </form>
    </main>
  );
};

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

export default connect(mapStateToProps)(MentorStudentProfile);
