const SET_COURSE = "SET_COURSE";

const setCourse = (course) => {
  return { type: SET_COURSE, payload: course };
};

export default setCourse;
