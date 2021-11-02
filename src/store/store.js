import { createStore } from "redux";

import mentorReducer from "./reducers/mentorReducer.jsx";

const store = createStore(mentorReducer);

export default store;
