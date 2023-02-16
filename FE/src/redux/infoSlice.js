import { createSlice } from '@reduxjs/toolkit'

const initialState = {
    buildingInfo: [],
    tableInfo1: [],
    tableInfo2: [],
    createRoom: false,
    publicRoom: true,
    roomNumber: '',
    roomPassword: null,
    openMypage: false,
    logout: false,
}
// reducers만드는 것을 도와줌
export const infoSlice = createSlice({
  name: 'info',
  initialState,
  reducers: {
    getBuilding: (state, action) => {
      state.buildingInfo = action.payload;
    },
    getTable1: (state, action) => {
      state.tableInfo1 = action.payload;
    },
    getTable2: (state, action) => {
      state.tableInfo2 = action.payload;
    },
    isCreateRoom: (state, action) => {
      state.createRoom = action.payload
    },
    getRoomNum: (state, action) => {
      state.roomNumber = action.payload;
    },
    getPassword: (state, action) => {
      state.roomPassword = action.payload;
    },
    isPublic: (state, action) => {
      state.publicRoom = action.payload;
    },
    isMyPage: (state, action) => {
      state.openMypage = action.payload;
    },
    isLogout: (state, action) =>{
      state.logout = action.payload;
    }
  }
})

// Action creators are generated for each case reducer function
export const infoActions = infoSlice.actions

export default infoSlice.reducer