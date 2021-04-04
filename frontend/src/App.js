import React from "react";
import './App.css';
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import ExercisePage from './pages/ExercisePage/ExercisePage';
import HomePage from './pages/HomePage/HomePage';
import LoginPage from './pages/LoginPage/LoginPage';
import ProfilePage from './pages/ProfilePage/ProfilePage';
import SubmissionPage from './pages/SubmissionPage/SubmissionPage';
import Toolbar from './components/Toolbar/Toolbar';
import 'bootstrap/dist/css/bootstrap.min.css';

function App() {
  document.body.style = 'background-color: #f2f4f5; font-family: "Overpass", sans-serif;';
  return (
    <Router>
        <Toolbar />
        <Switch>
          <Route path="/exercises">
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
          <Route path="/">
            <HomePage />
          </Route>
        </Switch>
    </Router>
  );
}

export default App;
