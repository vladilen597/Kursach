import React, { memo, useEffect, useState } from "react";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogActions from "@mui/material/DialogActions";
import { IoCloseSharp, IoSearchOutline } from "react-icons/io5";
import { IoMdCheckmark } from "react-icons/io";

import "./StudentTechModal.scss";
import { connect } from "react-redux";

const techList = ["Java", ".NET", "React", "Vue"];

const StudentTechModal = memo(
  ({
    handleDialogClose,
    isModalOpen,
    profile,
    isLoading,
    setChosenCategoriesArray,
    chosenCategoriesArray,
    profilePicture,
    token,
  }) => {
    const [searchQuery, setSearchQuery] = useState("");

    const handleQueryChange = (event) => {
      event.preventDefault();
      setSearchQuery(event.target.value);
    };
    console.log(profile);
    useEffect(() => {
      if (profile) {
        setChosenCategoriesArray(profile.skillsNames);
      }
    }, [profile]);

    const handleSaveButton = (event) => {
      event.preventDefault();
      setSearchQuery("");

      let myHeaders = new Headers();
      myHeaders.append("Authorization", token);
      myHeaders.append("Content-Type", "application/json");
      let raw = JSON.stringify({
        lastName: profile.lastName,
        firstName: profile.firstName,
        patronymic: profile.patronymic,
        coreSkills: chosenCategoriesArray,
        profilePicture: profilePicture,
      });

      var requestOptions = {
        method: "PUT",
        headers: myHeaders,
        body: raw,
        redirect: "follow",
      };

      fetch("http://localhost:8080/profile/current", requestOptions)
        .then((response) => response.json())
        .then((result) => console.log(result))
        .catch((error) => console.log("error", error));

      handleDialogClose();
    };

    const handleCheckboxChange = (event, id) => {
      if (!event.target.checked) {
        setChosenCategoriesArray(
          chosenCategoriesArray.filter((item) => {
            return item !== id;
          })
        );
      }

      if (event.target.checked) {
        setChosenCategoriesArray([...chosenCategoriesArray, id]);
      }
    };

    if (isLoading) {
      return <h1>Загрузка...</h1>;
    }

    return (
      <Dialog
        open={isModalOpen}
        onClose={() => {
          handleDialogClose();
          setSearchQuery("");
        }}
      >
        <DialogTitle>
          <div className="add-category-title">
            <h3 className="add-category-title-text">
              Выберите свои технологии
            </h3>
            <button
              className="add-category-modal-close"
              onClick={handleDialogClose}
            >
              <IoCloseSharp />
            </button>
          </div>
        </DialogTitle>
        <DialogActions>
          <section className="add-category-search-field-block">
            <IoSearchOutline className="add-category-search-field-icon" />
            <input
              type="text"
              className="add-category-search-field"
              placeholder="Search"
              onChange={handleQueryChange}
            />
          </section>
        </DialogActions>
        <ul className="add-category-list">
          {techList.map((skill) => {
            let checked = false;

            // eslint-disable-next-line no-lone-blocks
            {
              chosenCategoriesArray.map((item) => {
                if (item === skill) {
                  checked = true;
                }
              });
            }

            if (
              skill.toLocaleLowerCase().match(searchQuery.toLocaleLowerCase())
            ) {
              return (
                <li className="add-category-list-item" key={skill}>
                  <div className="add-category-list-item-left-side">
                    <div className="add-category-list-item-image-block"></div>
                    <p className="add-category-list-item-title">{skill}</p>
                  </div>
                  <div className="add-category-list-item-right-side">
                    <input
                      type="checkbox"
                      className="add-category-list-item-checkbox"
                      name="checkbox"
                      onChange={(event) => handleCheckboxChange(event, skill)}
                      checked={checked}
                    />
                    <label
                      htmlFor="checkbox"
                      className="add-category-list-item-checkbox-mark"
                    >
                      <IoMdCheckmark />
                    </label>
                  </div>
                </li>
              );
            }
          })}
        </ul>
        <section className="add-category-bottom-section">
          <button
            className="add-category-save-button"
            onClick={handleSaveButton}
          >
            SAVE
          </button>
        </section>
      </Dialog>
    );
  }
);

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

export default connect(mapStateToProps)(StudentTechModal);
