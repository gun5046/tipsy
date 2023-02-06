import { createStore } from "redux";

export default createStore(function(state, action) {
    if (state === undefined){
        return {
            nickname: '',
            profile: '',
            interest: [],
        }
    }
    if (action.type === 'submit') {
        const newState = {
            ...state,
            nickname: action.state.nickname,
            profile: action.state.profile,
            interest: action.state.interest
        }
        //local storage에 저장
        localStorage.setItem('userData', JSON.stringify(newState))
        return newState
    }
    return state;
}, window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__())