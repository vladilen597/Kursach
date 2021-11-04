const initialState = {
  profileId: null,
  token:
    "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2bGFkaWxlbiIsInJvbGVzIjpbIlJPTEVfTUVOVE9SIl0sImlhdCI6MTYzNjAxNjUwMywiZXhwIjoxNjM2MDUyNTAzfQ.SYUXzFHr_9IO1G6ZNsFd0Lp_V9YkIajYeSTBOfGTw28",
};

const mentorReducer = (state = initialState, action) => {
  switch (action.type) {
    case "SET_TOKEN":
      console.log(action.payload);
      return { ...state, token: action.payload };
    case "SET_PROFILE_ID":
      return { ...state, profileId: action.payload };
    default:
      return state;
  }
};

export default mentorReducer;
