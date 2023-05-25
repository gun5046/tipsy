import { createSlice } from '@reduxjs/toolkit'

const initialState = {
  scene: "",
  store: -1,
  table: -1, 
  chair: -1,
  street_position: -1,
}
// reducers만드는 것을 도와줌
export const gameSlice = createSlice({
  name: 'game',
  initialState,
  reducers: {
    getScene: (state, action) => {
      state.scene = action.payload;
    },
    getStore: (state, action) => {
      state.store = action.payload;
    },
    getTable: (state, action) => {
      state.table = action.payload;
    },
    getChair: (state, action) => {
      state.chair = action.payload;
    },
    getStreetPosition: (state, action) =>{
      state.street_position = action.payload
    }


  },
})

// Action creators are generated for each case reducer function
export const { getScene, getTable, getChair, getStreetPosition, getStore } = gameSlice.actions

export default gameSlice.reducer