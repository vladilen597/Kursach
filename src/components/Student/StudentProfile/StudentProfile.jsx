import React, { useEffect, useState } from "react";
import blankProfilePicture from "../../../resources/blankProfilePicture.png";
import imageupload from "../../../resources/imageupload.png";
import MentorTechModal from "./StudentTechModal/StudentTechModal";
import ReactStars from "react-rating-stars-component";
import { connect } from "react-redux";
import CircularProgress from "@mui/material/CircularProgress";
import { motion } from "framer-motion";

import "./StudentProfile.scss";

const StudentProfile = ({ token }) => {
  const [profile, setProfile] = useState({});
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [chosenCategoriesArray, setChosenCategoriesArray] = useState([]);

  // eslint-disable-next-line no-unused-vars
  const [picture, setPicture] = useState();
  const [pictureSrc, setPictureSrc] = useState("");

  const handleDialogClose = () => {
    setIsModalOpen(false);
  };

  useEffect(() => {
    setIsLoading(true);
    fetch("http://localhost:8080/profile/current", {
      headers: { Authorization: token },
    })
      .then((response) => response.json())
      .then((data) => {
        setProfile(data);
        setIsLoading(false);
      })
      .catch((error) => {
        console.log(error);
        setIsLoading(false);
      });
  }, [token]);

  useEffect(() => {
    if (profile) {
      var myHeaders = new Headers();
      myHeaders.append("Authorization", token);

      var requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow",
      };

      fetch(profile.profilePicture, requestOptions)
        .then((response) => response.blob())
        .then((result) => {
          const imageObjectURL = URL.createObjectURL(result);
          setPictureSrc(imageObjectURL);
        })
        .catch((error) => console.log("error", error));
    }
  }, [profile]);

  const handleDialogOpen = (event) => {
    event.preventDefault();
    setIsModalOpen(true);
  };

  const handlePictureSelected = (event) => {
    if (event.target.files.length !== 0) {
      const picture = event.target.files[0];
      const src = URL.createObjectURL(picture);

      if (picture.type.match("image")) {
        const image = new FormData();
        image.append("File", picture);

        setPictureSrc(src);
        setPicture(picture);

        console.log(image.get("File"));

        let requestOptions = {
          method: "POST",
          headers: {
            Authorization: token,
          },
          body: image,

          redirect: "follow",
        };

        fetch(
          "http://localhost:8080/profile/current/uploadImage",
          requestOptions
        )
          .then((response) => response.json())
          .then((result) => console.log(result))
          .catch((error) => console.log("error", error));
      } else alert("Please, choose valid image");
    }
  };

  return (
    <motion.main
      initial={{ position: "absolute", opacity: 0 }}
      animate={{ position: "initial", opacity: 1 }}
      exit={{ position: "absolute", opacity: 0 }}
      transition={{ duration: 0.5 }}
      className="mentor-profile"
    >
      <section className="profile-info">
        <section className="profile-info-rating-block">
          <div className="profile-info-upload-block">
            <input
              className="profile-info-upload-input"
              type="file"
              onChange={handlePictureSelected}
            />
            <div className="profile-info-upload-background">
              <img
                className="profile-info-upload-icon"
                src={imageupload}
                alt="Upload"
              />
            </div>
            <img
              className="profile-info-picture"
              src={pictureSrc || blankProfilePicture}
              onError={(event) => (event.target.src = blankProfilePicture)}
              alt="Profile"
            />
          </div>
          <ReactStars
            classNames="profile-info-rating"
            size={24}
            isHalf={true}
            edit={false}
          />
        </section>
        {isLoading ? (
          <CircularProgress />
        ) : (
          <form className="profile-info-form">
            <label className="profile-info-name-label">ФИО</label>
            <p className="profile-info-name-line">
              {profile.lastName +
                " " +
                profile.firstName +
                " " +
                profile.patronymic}
            </p>

            <label className="profile-info-name-label">Логин</label>
            <p className="profile-info-username">{profile.username}</p>

            <label className="profile-info-name-label">Технологии</label>
            <ul className="profile-info-username">
              {isLoading
                ? "Загрузка..."
                : chosenCategoriesArray.length === 0
                ? "Нет выбранных технологий"
                : chosenCategoriesArray.map((skill) => {
                    return <li key={skill}>{skill}</li>;
                  })}
            </ul>
            <button
              className="blue-button"
              type="button"
              onClick={handleDialogOpen}
            >
              ИЗМЕНИТЬ
            </button>
          </form>
        )}
        <MentorTechModal
          isModalOpen={isModalOpen}
          handleDialogClose={handleDialogClose}
          profile={profile}
          isLoading={isLoading}
          setChosenCategoriesArray={setChosenCategoriesArray}
          chosenCategoriesArray={chosenCategoriesArray}
          profilePicture={pictureSrc}
        />
      </section>
    </motion.main>
  );
};

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

export default connect(mapStateToProps)(StudentProfile);
