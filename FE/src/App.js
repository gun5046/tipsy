import './App.css';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { CookiesProvider } from 'react-cookie';

//page
import Preview from './pages/Preview';
import Login from './pages/Login';
import Mypage from './pages/Mypage';
import Temp from './pages/Temp';
//component

function App() {
  return (
    <CookiesProvider>
      <BrowserRouter>
        <div>
          <Routes>
            <Route path='/' element={<Preview/>}/>
            <Route path='/login' element={<Login/>}/>
            <Route path='/mypage' element={<Mypage/>}/>
            <Route path='/temp' element= {<Temp/>}/>
          </Routes>
        </div>
      </BrowserRouter>
    </CookiesProvider>
  );
}

export default App;
