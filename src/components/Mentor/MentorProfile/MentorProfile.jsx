import React, { useEffect, useState } from "react";
import blankProfilePicture from "../../../resourses/blankProfilePicture.png";
import imageupload from "../../../resourses/imageupload.png";
import MentorTechModal from "./MentorTechModal/MentorTechModal";
import ReactStars from "react-rating-stars-component";
import { connect } from "react-redux";
import "./MentorProfile.scss";

const MentorProfile = ({ token }) => {
  const [profile, setProfile] = useState({});
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [chosenCategoriesArray, setChosenCategoriesArray] = useState([]);

  const [picture, setPicture] = useState();
  const [pictureSrc, setPictureSrc] = useState("");

  const handleDialogClose = () => {
    setIsModalOpen(false);
  };

  useEffect(() => {
    if (profile) {
      setPictureSrc("../../../../server" + profile.profilePicture);
    }
  }, [profile]);

  console.log(pictureSrc);
  const handleDialogOpen = (event) => {
    event.preventDefault();
    setIsModalOpen(true);
  };

  const handlePictureSelected = (event) => {
    if (event.target.files.length !== 0) {
      const picture = event.target.files[0];
      const src = URL.createObjectURL(picture);
      if (picture.type.match("image")) {
        setPictureSrc(src);
        setPicture(picture);

        let raw = JSON.stringify({
          file: src,
        });

        let requestOptions = {
          method: "POST",
          headers: { Authorization: token },
          body: raw,
          redirect: "follow",
        };

        fetch("http://localhost:8080/profile/uploadImage", requestOptions)
          .then((response) => response.json())
          .then((result) => console.log(result))
          .catch((error) => console.log("error", error));
      } else alert("Please, choose valid image");
    }
  };

  useEffect(() => {
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

  return (
    <main className="mentor-profile">
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
    </main>
  );
};

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

export default connect(mapStateToProps)(MentorProfile);
