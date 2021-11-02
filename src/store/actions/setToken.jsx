const SET_TOKEN = "SET_TOKEN";

const setToken = (id) => {
  return { type: SET_TOKEN, payload: id };
};

export default setToken;
