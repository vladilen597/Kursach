import React, { useEffect, useState } from "react";
import ReactStars from "react-rating-stars-component";
import { connect } from "react-redux";
import { useLocation } from "react-router";
import blankProfilePicture from "../../../../resources/blankProfilePicture.png";
import StudentMentorCourseApplyDialog from "./StudentMentorCourseApplyDialog/StudentMentorCourseApplyDialog";

import "./StudentMentorProfile.scss";

const StudentMentorProfile = ({ token }) => {
  const [userProfile, setUserProfile] = useState(null);
  const [pictureSrc, setPictureSrc] = useState("");
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const username = useLocation().pathname.split("/").pop();

  const handleDialogOpen = () => {
    setIsDialogOpen(true);
  };

  const handleDialogClose = () => {
    setIsDialogOpen(false);
  };

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

  useEffect(() => {
    if (userProfile) {
      var myHeaders = new Headers();
      myHeaders.append("Authorization", token);

      var requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow",
      };

      fetch(userProfile.profilePicture, requestOptions)
        .then((response) => response.blob())
        .then((result) => {
          const imageObjectURL = URL.createObjectURL(result);
          setPictureSrc(imageObjectURL);
        })
        .catch((error) => console.log("error", error));
    }
  }, [userProfile]);

  if (!userProfile) {
    return <h1>Загрузка...</h1>;
  }

  console.log(userProfile);
  return (
    <main className="student-mentor-profile">
      <form className="student-mentor-profile-form">
        <section className="student-mentor-profile-form-rating-block">
          <img
            className="profile-info-picture"
            src={pictureSrc || blankProfilePicture}
            onError={(event) => {
              event.target.src = blankProfilePicture;
            }}
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
      <button className="course-apply-button" onClick={handleDialogOpen}>
        Подать заявку на курс
      </button>
      <StudentMentorCourseApplyDialog
        handleDialogClose={handleDialogClose}
        isDialogOpen={isDialogOpen}
        userProfile={userProfile}
      />
    </main>
  );
};

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

export default connect(mapStateToProps)(StudentMentorProfile);
