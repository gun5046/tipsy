// import { createStore } from "redux";

// export default createStore(
//     function(state, action) {
//     if (state === undefined){
//         return localStorage.state ?
//             JSON.parse(localStorage.state)
//             :
//             {
//                 birth: '',
//                 email: '',
//                 gender: '',
//                 image: '',
//                 interest: '',
//                 kakao_id: '',
//                 name: '',
//                 nickname: '',
//                 reportcnt: '',
//                 uid: 0,
//               }
//     }
//     if (action.type === 'submit') {
//         const newState = action.state
//         //local storage에 저장
//         console.log(action.state)
//         localStorage.setItem('state', JSON.stringify(newState))
//         return newState
//     }
//     return state;
// }, window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__())