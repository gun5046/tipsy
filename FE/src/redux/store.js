import { configureStore } from '@reduxjs/toolkit'
import gameReducer from './gameSlice'
import authReducer from './authSlice'
import infoReducer from './infoSlice'

export const store = configureStore({
  reducer: {
    auth: authReducer,
    game: gameReducer,
    info: infoReducer,
  },
})