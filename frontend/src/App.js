import React from "react";
import './App.css';
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import ExercisePage from './pages/ExercisePage/ExercisePage';
import HomePage from './pages/HomePage/HomePage';
import LoginPage from './pages/LoginPage/LoginPage';
import ProfilePage from './pages/ProfilePage/ProfilePage';
import SubmissionPage from './pages/SubmissionPage/SubmissionPage';
import Header from './components/Header/Header';

function App() {
  document.body.style = 'background-color: #f2f4f5; font-family: "Overpass", sans-serif;';
  return (
    <Router>
        <Header />
        <Switch>
          <Route path="">
            <HomePage/>


          </Route>
          <Route path="/exercise">
            {/* TODO: figure out how to render specific exercises */}
            <ExercisePage />
          </Route>
          <Route path="/login">
            <LoginPage />
          </Route>
          <Route path="/profile">
            <ProfilePage />
          </Route>
          <Route path="/submit">
            <SubmissionPage />
          </Route>
        </Switch>
    </Router>
  );
}

export default App;
