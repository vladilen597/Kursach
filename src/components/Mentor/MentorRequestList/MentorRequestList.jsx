import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { motion } from "framer-motion";
import { Link } from "react-router-dom";
import { ImCheckmark } from "react-icons/im";
import { ImCross } from "react-icons/im";
import Tooltip from "@mui/material/Tooltip";
import moment from "moment";
import "moment/locale/ru";

import "./MentorRequestList.scss";

const MentorRequestList = ({ token }) => {
  const [pendingRequests, setPendingRequests] = useState([]);

  useEffect(() => {
    var requestOptions = {
      method: "GET",
      headers: { Authorization: token },
    };

    fetch("http://localhost:8080/courses/students/requests", requestOptions)
      .then((response) => response.json())
      .then((result) => setPendingRequests(result))
      .catch((error) => console.log("error", error));

    fetch("http://localhost:8080/courses/current", requestOptions)
      .then((response) => response.json())
      .then((result) => console.log(result))
      .catch((error) => console.log("error", error));
  }, []);

  const handleApproveRequest = (id) => {
    let tempArray = pendingRequests;

    tempArray = tempArray.filter((request) => {
      return request.requestId !== id;
    });

    setPendingRequests(tempArray);

    fetch(`http://localhost:8080/courses/students/request/${id}/approve`, {
      method: "POST",
      headers: { Authorization: token },
    })
      .then((response) => response.json())
      .then((data) => console.log(data))
      .catch((error) => console.log(error));
  };

  const handleDisapproveRequest = (id) => {
    let tempArray = pendingRequests;

    tempArray = tempArray.filter((request) => {
      return request.id !== id;
    });

    setPendingRequests(tempArray);

    fetch(`http://localhost:8080/courses/students/request/${id}/disapprove`, {
      method: "POST",
      headers: { Authorization: token },
    })
      .then((response) => response.json())
      .then((data) => console.log(data))
      .catch((error) => console.log(error));
  };

  console.log(pendingRequests);
  return (
    <motion.main
      initial={{ position: "absolute", opacity: 0 }}
      animate={{ position: "initial", opacity: 1 }}
      exit={{ position: "absolute", opacity: 0 }}
      transition={{ duration: 0.5 }}
      className="mentor-requests"
    >
      {pendingRequests.length === 0 ? (
        <p className="mentor-no-requests">Запросы отсутствуют</p>
      ) : (
        <ul className="mentor-requests-list">
          {pendingRequests.map((request) => {
            const sendDate = new Date(request.creationTime).getTime();
            return (
              <li className="mentor-requests-list-item" key={request.requestId}>
                <div className="mentor-requests-list-item-top-line">
                  <p className="mentor-requests-list-item-name">
                    {request.courseRepresentation.courseName}
                  </p>
                  <p className="mentor-requests-list-item-date">
                    {moment(sendDate).locale("ru").format("LLL")}
                  </p>
                </div>
                <div className="mentor-requests-list-right-part">
                  <p className="mentor-requests-list-item-asignee">
                    Студент:{" "}
                    <Link
                      to={`/mentor/student/${request.requester.username}`}
                      className="mentor-requests-list-item-asignee-link"
                    >
                      {request.requester.lastName} {request.requester.firstName}{" "}
                      {request.requester.patronymic}
                    </Link>
                  </p>
                  <div className="mentor-requests-list-item-actions">
                    <Tooltip title="Подтвердить заявку" placement="top-end">
                      <button
                        onClick={() => handleApproveRequest(request.requestId)}
                        className="mentor-requests-list-item-action mentor-requests-list-item-action-yes"
                      >
                        <ImCheckmark />
                      </button>
                    </Tooltip>
                    <Tooltip title="Отклонить заявку" placement="bottom-end">
                      <button
                        onClick={() =>
                          handleDisapproveRequest(request.requestId)
                        }
                        className="mentor-requests-list-item-action mentor-requests-list-item-action-no"
                      >
                        <ImCross />
                      </button>
                    </Tooltip>
                  </div>
                </div>
              </li>
            );
          })}
        </ul>
      )}
    </motion.main>
  );
};

const mapStateToProps = (state) => {
  return {
    token: state.token,
  };
};

export default connect(mapStateToProps)(MentorRequestList);
