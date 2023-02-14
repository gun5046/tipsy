import { createSlice } from '@reduxjs/toolkit'

const initialState = {
    buildingInfo: [],
    tableInfo1: [],
    openRoom: false
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
    isCreateRoom: (state, action) => {
      state.openRoom != state.openRoom
    }
  }
})

// Action creators are generated for each case reducer function
export const infoActions = infoSlice.actions

export default infoSlice.reducer