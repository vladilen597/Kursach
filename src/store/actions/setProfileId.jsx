const SET_PROFILE_ID = "SET_PROFILE_ID";

const setProfileId = (id) => {
  return { type: SET_PROFILE_ID, payload: id };
};

export default setProfileId;
