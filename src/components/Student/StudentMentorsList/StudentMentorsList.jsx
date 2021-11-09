import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { Link } from "react-router-dom";
import CircularProgress from "@mui/material/CircularProgress";
import { motion } from "framer-motion";
import { BsSearch } from "react-icons/bs";

import "./StudentMentorsList.scss";

const StudentMentorsList = ({ token }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [studentsList, setStudentsList] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  console.log(studentsList);

  const handleQueryChange = (event) => {
    event.preventDefault();
    setSearchQuery(event.target.value);
  };

  useEffect(() => {
    setIsLoading(true);
    fetch("http://localhost:8080/users/mentors", {
      headers: {
        Authorization: token,
      },
    })
      .then((response) => response.json())
      .then((data) => {
        setStudentsList(data);
        setIsLoading(false);
      })
      .catch((error) => {
        setIsLoading(false);
        console.log("error", error);
      });
  }, []);

  return (
    <motion.ul
      initial={{ position: "absolute", opacity: 0 }}
      animate={{ position: "initial", opacity: 1 }}
      exit={{ position: "absolute", opacity: 0 }}
      transition={{ duration: 0.5 }}
      className="students-list"
    >
      <li className="students-top-list-item">
        <p>ФИО</p>
        <p>Рейтинг</p>
        <p>Приоритетные технологии</p>
      </li>
      {isLoading ? (
        <CircularProgress />
      ) : (
        studentsList.map((mentor) => {
          const fullName =
            mentor.lastName + " " + mentor.firstName + " " + mentor.patronymic;
          if (
            fullName.toLocaleLowerCase().match(searchQuery.toLocaleLowerCase())
          ) {
            return (
              <Link
                className="students-list-item-link"
                to={`/student/mentor/${mentor.username}`}
                key={mentor.id}
              >
                <li className="students-list-item">
                  <p>
                    {mentor.lastName +
                      " " +
                      mentor.firstName +
                      " " +
                      mentor.patronymic}
                  </p>
                  <p>{mentor.averageRating}</p>
                  <ul className="technology-list">
                    {mentor.skills.map((technology) => {
                      return (
                        <li className="technology-list-item" key={technology}>
                          {technology}
                        </li>
                      );
                    })}
                  </ul>
                </li>
              </Link>
            );
          }
        })
      )}
      <li className="students-bottom-list-item">
        <form className="students-list-item-form">
          <input
            className="students-list-item-search-field"
            type="text"
            onChange={handleQueryChange}
          />
          <BsSearch className="students-list-item-search-field-icon" />
        </form>
      </li>
    </motion.ul>
  );
};

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

export default connect(mapStateToProps)(StudentMentorsList);
