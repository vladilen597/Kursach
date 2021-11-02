const initialState = {
  token: "",
};

const mentorReducer = (state = initialState, action) => {
  switch (action.type) {
    case "SET_TOKEN":
      return (state.token = action.payload);
    default:
      return state;
  }
};

export default mentorReducer;
