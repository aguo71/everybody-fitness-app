import React from 'react'; 
import Main from './components/Main/Main';
import UserProfile from './components/UserProfile/UserProfile';
import ExerciseModal from './components/ExerciseModal/ExerciseModal'
import WorkoutModal from './components/WorkoutModal/WorkoutModal'
import './HomePage.css';

function Home(props) {
  return (
    <div className="home">
      <Main user={props.user} />
      <div className="upload">
        <h3>Upload</h3>
        <ExerciseModal id="exercise-modal"/>
        <WorkoutModal id="workout-modal"/>
      </div>
    </div>
  );
}

export default Home;
