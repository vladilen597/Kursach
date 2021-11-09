const initialState = {
  course: {},
  token:
    "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2bGFkaWxlbiIsInJvbGVzIjpbIlJPTEVfTUVOVE9SIl0sImlhdCI6MTYzNjQ0NjU1NywiZXhwIjoxNjM2NDgyNTU3fQ.hhFt_LLUD38qMk01YZZODm-Lu8f97KmaK_C5w4y6iFA",
};

const mentorReducer = (state = initialState, action) => {
  switch (action.type) {
    case "SET_TOKEN":
      console.log(action.payload);
      return { ...state, token: action.payload };
    case "SET_COURSE":
      return { ...state, course: action.payload };
    default:
      return state;
  }
};

export default mentorReducer;
