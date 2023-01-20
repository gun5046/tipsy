import './App.css';
import { BrowserRouter, Route, Routes } from 'react-router-dom';


//page
import Preview from './pages/Preview';
import Login from './pages/Login';
import Mypage from './pages/Mypage';

//component

function App() {
  return (
    <BrowserRouter>
      <div>
        <p>App</p>

        <Routes>
          <Route path='/' element={<Preview/>}/>
          <Route path='/login' element={<Login/>}/>
          <Route path='/mypage' element={<Mypage/>}/>
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
