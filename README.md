# CS0320 Software Engineering - Self-designed term project 2021: EveryBODY App :runner:
![](everyBODY_logo.png)

**Team Members:**
Lauren Choi (@lauren-choi), Alex Guo (@aguo71), David Moon (@damoon843), Joshua Woo (@jwoo153)

## App Overview 

With the new circumstances brought about by COVID-19, going to the gym for a workout with weights is not as accessible as before. Hence, there has been a noticeable increase in consumption of simple guided bodyweight workouts (strength or cardio-related), whether from user-uploaded videos on youtube or paid collections from a fitness organization. EveryBODY is the fitness social media app for all, by all, serving as a mobile platform to share and view other users' bodyweight workouts.

**Final Presentation:** A link to the final product presentation can be found [here](https://docs.google.com/presentation/d/1__YhvloPYGBAoIf8TNcaFXsW2IOT7QlMt8n_5kbyh5w/edit#slide=id.gd2a99047a1_0_10).

## Key Features

- Account Creation page: serving as the sign-up/log-in page, this page allows users to either log-in to an existing account or sign-up for a new account.

- Recommended 'For You' page: serving as the home page of the application, this page uses Kosarajus's algorithm (explained further below) to determine strongly-connected components for a given logged-in user. 

- Creation page: also residing on the home page, this feature allows users to create exercises and workouts. Workout creation allows users to select from a public pool of exercises uploaded by all users, resulting in a 'playlist' of exercises. 

- Discover Exercises page: this page displays exercises posted by all users of the everyBODY app, allowing users to filter by workout type and body part (for strength exercises).

- My Profile page: this page displays information about a logged-in user, displaying a user's uploaded workouts and liked workouts. The user's workout preferences are also displayed here. The profile page contains functionality to delete all references of a user in the database.

## Backend 
The backend is divided up into four folders: data, feedComponents, userComponents, and utils. 

The data package contains all database-level logic: the DataSourcePool class is a wrapper for a connections pool, the PostgresDatabase class is driver class for all database queries (which extensively relies on JDBC API), and the Queries class is a constants class containing query strings that are used in the PostgresDatabase class. the feedComponents package contains all logic for a user's feed (exercises and workouts). The Workout class is a wrapper for a Workout object. In addition, there are two handler classes: RecommendationHandler handles the home-page algorithm (to return recommended workouts) and returns results from Kosaraju's algorithm, and FeedHandler handles all other workout and exercise-related logic (i.e. uploading an exercise, retrieving exercises for the Discover page, etc). The userComponents package contains all user-related logic with an AppUser class to encapsulate a user on the app. The UserHandlers class handles following/unfollowing, log-in/sign-up, user deletion, and user profile information retrieval. Lastly, the utils package contain miscellaneous utilities needed for the application. The ErrorConstants class holds all error message printed to the backend. The KosarajusAlgorithm class is a generic implementation of Kosaraju's Algorithm, taking in a user and finding that user's strongly-connected component in the graph. The Server class holds logic to start the server, as well as all endpoints that the frontend can request data from. The WorkoutComparator is used to compare two workouts by time created and like count.

## Frontend

We created the frontend website in React and used React Bootstrap + Font Awesome for general styling. The frontend is divided into four main parts: `components`, `pages`, `api.js`/`calculations.js`, and `assets`.

### Components

To keep the frontend extensible, we put commonly used components (specifically `ExerciseItem` -- which renders exercises on the Exercises page and on individual workout pages -- and `Toolbar`) in the `components` folder to share them across the website.

### Pages

#### Organization/Styling

Additionally, to keep our code modular, we created a separate directory for each page (`ExercisePage`, `HomePage`, `LoginPage`, `ProfilePage`, and `WorkoutPage`). For pages with more components, we created a `components` sub-directory to break down the page further, and we gave each page its own CSS file to prevent overlap in styling. However, we put styling for common elements (buttons, headers, etc) in `App.css` to share them across the website.

#### State/Navigation/Validation

We used `useRef` to maintain state for each page, and we implemented sessions/cookies to keep the current user logged in on the app. Additionally, we used the React Router library to define separate routes for each page, allowing the user to navigate around the site. Finally, we validated the user input for each form (login, signing up, submitting new exercises and workouts) to ensure users sent correctly formatted data to the backend.

#### api.js and calculations.js

We separated some of our commonly used GET/POST requests (such as following/unfollowing a user, liking/unliking a post) into `api.js`. Additionally, we put commonly used functions (like finding the checked values from a checkbox group) into `calculations.js`. Like the `components` folder, this allowed us to reuse the functions across the website without redefining them each time.

#### Assets

The `assets` folder holds images that we use across the website.

## Algorithm 

#### Home Feed Recommendations
Our home feed is tailored to always recommend 9 workouts:
4 are from people you follow,
3 are from people strongly connected to you (Kosaraju’s), and
2 are from the highly rated community workout pool.
Workouts are displayed on your feed in order of ranking.
Ranks are based on recency of post date and like-count, where the most recent workouts are favored, but every 5 likes compensates for 24 hours of time passing
Additionally, the final 9 workouts are chosen, preferential filtering based on duration (and eventually type) is applied
Once a workout is recommended, it will not be recommended again in the future to ensure a dynamic feed.

#### Kosaraju's Algorithm
Kosaraju’s Algorithm is a linear-time graph traversal algorithm, which typically uses DFS on both a graph and the transposed version of the graph to find strongly connected components.
For context, our app’s graph consists of nodes (users) and edges (following relations), so a strongly connected component is a group of users who share some sort of mutual cyclic following relation.
For the purpose of our app, we modified Kosaraju’s algorithm to instead use BFS, and only find the strongly connected component a specific given (input) user is part of rather than traverse the entire graph. 

The psuedocode for the algorithm is simple: perform BFS on a graph from a starting node, keeping track of all reachable nodes. Reverse the edges in the graph, and perform BFS again on the graph
from the same starting node, keeping track of all reachable nodes. Return the intersection of these two sets of reachable nodes to find the
strongly connected component the starting node is part of.



## Deployment 
App deployment involved the following key services:

- Heroku: this was the core service used to deploy and scale the app under a unique domain name. Heroku provides dynos (application containers) in a fully-managed runtime environment

- Docker: this was used to build Docker images on Heroku's deployment process. Images provide pre-configured environemnts that are ensured to work in development and production-level environments.

- PostgreSQL Database: this was the relational database management system (RDBMS) used to store all relational data and structure the database schema. This Postgres DB is hosted on Heroku provisioned as an add-on to the app.

- Amazon S3: this was the cloud-based storage service used to hold all images for workouts/exercises (upload to S3 occurs when a user first creates a workout/exercise).

## How to Build and Run
Note that the following commands are meant to be run in a development/testing context; the production-level app is deployed on Heroku. The following commands can be used to run individual backend / frontend components.

To compile the source code of this project:
```
mvn compile
```
To compile and package code:
```
mvn package
```
To run the test suite:
```
mvn test
```
To run the frontend/client:
```
npm install
npm start
```

The following command can be used to run the containerized backend / frontend.

To build the Docker images:
```
docker-compose build
```
To create containers from images and start/restart all Docker services to run the application:
```
docker-compose up
```
To build containers before running them in a single step:
```
docker-compose up --build
```

## Known Bugs and Limitations

**Heroku Database Limitation:** Because the Postgres database is fully managed by Heroku, there are connection limitations. The 'Hobby' tier provides 20 connections at a time and going beyond this limit will result in connection timeouts/exceptions. If this does occur, please contact a collaborator on this repo so the database connections can be reset (completed from the Heroku dashboard). You can read more about this limitation [here](https://devcenter.heroku.com/articles/postgres-logs-errors#fatal-too-many-connections-for-role).

**Recommendation Algorithm Bug** There is a bug where the home feed will display duplicated workouts in its attempt to always return 9 workouts. If we had additional time, this is the main bug we need to fix before our application is fully functional and bug-free.
