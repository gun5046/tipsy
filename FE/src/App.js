import './App.css';
import { BrowserRouter, Route, Routes } from 'react-router-dom';


//page
import Preview from './pages/Preview';
import Login from './pages/Login';
import Mypage from './pages/Mypage';
import Meeting from './pages/Meeting';

//component

function App() {
  return (
    <BrowserRouter>
      <div>
        <Routes>
          <Route path='/' element={<Preview/>}/>
          <Route path='/login' element={<Login/>}/>
          <Route path='/mypage' element={<Mypage/>}/>
          <Route path='/meeting' element={<Meeting/>}/>
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
