package edu.brown.cs.everybody.feedComponents;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.everybody.data.PostgresDatabase;
import edu.brown.cs.everybody.utils.ErrorConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

/**
 * Contains handler classes for feed-related logic.
 */
public class FeedHandlers {
  private static final Gson GSON = new Gson();

  /**
   * Handler for uploading exercises.
   */
  public static class UploadExerciseHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      Map<String, Object> variables;
      
      String username = "";
      String exerciseName = data.getString("exerciseName");
      String mediaLink = data.getString("mediaLink");
      Integer duration = data.getInt("duration");
      JSONArray tagsJSON = data.getJSONArray("tags");
      String description = data.getString("description");

      // Retrieve session
      Session session = request.session(false);
      if (session != null) {
        // Retrieval successful, get username
        username = session.attribute("username");
      } else {
        // Retrieval failed
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        System.out.println(ErrorConstants.ERROR_NULL_SESSION);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_NULL_SESSION);
        return GSON.toJson(variables);
      }
      if(username.equals("")) {
        System.out.println(ErrorConstants.ERROR_SESSION_USERNAME);
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_SESSION_USERNAME);
        return GSON.toJson(variables);
      }
      // Extract tags from JSONArray
      List<String> tags = new ArrayList<>();
      for (int i = 0; i < tagsJSON.length(); i++) {
        tags.add((String) tagsJSON.get(i));
      }

      try {
        PostgresDatabase.insertUserExercise(username, exerciseName, mediaLink, duration, tags,
          description);
      } catch(SQLException ex) {
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_INSERT_EXERCISE);
        return GSON.toJson(variables);
      }

      variables = ImmutableMap.of("isValid", true);
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler for uploading workouts.
   */
  public static class UploadWorkoutHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      Map<String, Object> variables;
      List<Integer> exerciseIds = new ArrayList<>(); // To store a workout's exercise IDs

      JSONArray jsonObjects = data.getJSONArray("exerciseList");
      for (int i=0; i<jsonObjects.length(); i++) {
        exerciseIds.add(jsonObjects.getInt(i));
      }
      int duration = 0;
      for(int id: exerciseIds) {
        duration += PostgresDatabase.getDuration(id);
      }
      String mediaLink = data.getString("mediaLink");
      Integer totalLikes = 0; // 0 likes upon initial upload
      String description = data.getString("description");
      String username = "";
      String workoutName = data.getString("workoutName");

      // Retrieve session
      Session session = request.session(false);
      if (session != null) {
        // Retrieval successful, get username
        username = session.attribute("username");
      } else {
        // Retrieval failed
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        System.out.println(ErrorConstants.ERROR_NULL_SESSION);
         variables = ImmutableMap.of("error", ErrorConstants.ERROR_NULL_SESSION);
        return GSON.toJson(variables);
      }
      if(username.equals("")) {
        System.out.println(ErrorConstants.ERROR_SESSION_USERNAME);
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_SESSION_USERNAME);
        return GSON.toJson(variables);
      }
      try {
        PostgresDatabase.insertUserWorkout(duration, mediaLink, totalLikes,
          description, username, workoutName, exerciseIds);
      } catch (SQLException ex) {
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_INSERT_WORKOUT);
        return GSON.toJson(variables);
      }

      variables = ImmutableMap.of("isValid", true);
      return GSON.toJson(variables);
    }
  }

  /**
   * Retrieves all workouts posted by a user (for profile).
   */
  public static class GetWorkoutsHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      Map<String, Object> variables;
      List<Map<String, String>> output = new ArrayList<>();
      String username = "";
      PriorityQueue<Workout> workouts;

      // Retrieve session
      Session session = request.session(false);
      if (session != null) {
        // Retrieval successful, get username
        username = session.attribute("username");
      } else {
        // Retrieval failed
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        System.out.println(ErrorConstants.ERROR_NULL_SESSION);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_NULL_SESSION);
        return GSON.toJson(variables);
      }
      if(username.equals("")) {
        System.out.println(ErrorConstants.ERROR_SESSION_USERNAME);
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_SESSION_USERNAME);
        return GSON.toJson(variables);
      }
      try {
        workouts = PostgresDatabase.getUserWorkouts(username);
      } catch(SQLException ex) {
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_GET_LIKED_WORKOUTS);
        return GSON.toJson(variables);
      }

      Workout finalWorkout = workouts.poll();
      while (finalWorkout != null) {
        output.add(finalWorkout.toMap());
        finalWorkout = workouts.poll();
      }
      variables = ImmutableMap.of("workouts", output);
      return GSON.toJson(variables);
    }
  }

  /**
   * Retrieves all workouts liked by a user (for profile).
   */
  public static class GetLikedWorkoutsHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      Map<String, Object> variables;
      List<Map<String, String>> output = new ArrayList<>();
      String username = "";
      PriorityQueue<Workout> workouts;

      // Retrieve session
      Session session = request.session(false);
      if (session != null) {
        // Retrieval successful, get username
        username = session.attribute("username");
      } else {
        // Retrieval failed
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        System.out.println(ErrorConstants.ERROR_NULL_SESSION);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_NULL_SESSION);
        return GSON.toJson(variables);
      }
      if(username.equals("")) {
        System.out.println(ErrorConstants.ERROR_SESSION_USERNAME);
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_SESSION_USERNAME);
        return GSON.toJson(variables);
      }
      try {
        workouts = PostgresDatabase.getLikedWorkouts(username);
      } catch(SQLException ex) {
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_GET_WORKOUTS);
        return GSON.toJson(variables);
      }

      Workout finalWorkout = workouts.poll();
      while (finalWorkout != null) {
        output.add(finalWorkout.toMap());
        finalWorkout = workouts.poll();
      }
      variables = ImmutableMap.of("workouts", output);
      return GSON.toJson(variables);
    }
  }

  /**
   * Retrieves all exercises within a workout posted by a user (for profile).
   */
  public static class GetExercisesHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      Map<String, Object> variables;
      Map<Integer, List<Object>> exercises;

      String username = data.getString("username");
      String workoutName = data.getString("workoutName");

      try {
        exercises = PostgresDatabase.getUserExercises(username, workoutName);
      } catch(SQLException ex) {
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_GET_EXERCISES);
        return GSON.toJson(variables);
      }

      ImmutableMap<Integer, List<Object>> exerciseResults = ImmutableMap.copyOf(exercises);
      return GSON.toJson(exerciseResults);
    }
  }

  /**
   * Retrieves exercises for the public exercises page.
   */
  public static class GetPublicExercisesHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      Map<Integer, List<Object>> exercises;

      try {
        exercises = PostgresDatabase.getExercises();
      } catch(SQLException ex) {
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Map<String, Object> variables = ImmutableMap.of("error", ErrorConstants.ERROR_GET_PUBLIC_EXERCISES);
        return GSON.toJson(variables);
      }
      ImmutableMap<Integer, List<Object>> variables = ImmutableMap.copyOf(exercises);
      return GSON.toJson(variables);
    }
  }

  /**
   * Retrieves 20 similar exercise names as user query.
   */
  public static class SearchExercisesHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String query = data.getString("query");
      Map<Integer, List<Object>> exercises;

      try {
        exercises = PostgresDatabase.getSimilarExercises(query);
      } catch(SQLException ex) {
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Map<String, Object> variables = ImmutableMap.of("error", ErrorConstants.ERROR_GET_SIMILAR_EXERCISES);
        return GSON.toJson(variables);
      }
      ImmutableMap<Integer, List<Object>> variables = ImmutableMap.copyOf(exercises);
      return GSON.toJson(variables);
    }
  }

  /**
   * Register a like on a workout.
   */
  public static class LikeHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      Map<String, Object> variables = null;

      String username = "";
      String workoutName = data.getString("workoutName");
      String poster = data.getString("poster");

      // Retrieve session
      Session session = request.session(false);
      if (session != null) {
        // Retrieval successful, get username
        username = session.attribute("username");
      } else {
        // Retrieval failed
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        System.out.println(ErrorConstants.ERROR_NULL_SESSION);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_NULL_SESSION);
        return GSON.toJson(variables);
      }
      if(username.equals("")) {
        System.out.println(ErrorConstants.ERROR_SESSION_USERNAME);
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_SESSION_USERNAME);
        return GSON.toJson(variables);
      }
      // Retrieve workout ID
      Integer workoutId = PostgresDatabase.getWorkoutId(workoutName, poster);

      if (workoutId != null) {
        try {
          PostgresDatabase.insertLike(username, workoutId);
        } catch(SQLException | URISyntaxException ex) {
          // Failed to insert like
          response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          variables = ImmutableMap.of("error", ErrorConstants.ERROR_INSERT_LIKE);
          return GSON.toJson(variables);
        }
      } else {
        // Failed to retrieve workout ID
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        System.out.println(ErrorConstants.ERROR_GET_WORKOUTID);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_GET_WORKOUTID);
        return GSON.toJson(variables);
      }
      variables = ImmutableMap.of("isValid", true);
      return GSON.toJson(variables);
    }
  }

  /**
   * Register an unlike on a workout.
   */
  public static class UnlikeHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      Map<String, Object> variables = null;

      String username = "";
      String workoutName = data.getString("workoutName");
      String poster = data.getString("poster");

      // Retrieve session
      Session session = request.session(false);
      if (session != null) {
        // Retrieval successful, get username
        username = session.attribute("username");
      } else {
        // Retrieval failed
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        System.out.println(ErrorConstants.ERROR_NULL_SESSION);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_NULL_SESSION);
        return GSON.toJson(variables);
      }
      if(username.equals("")) {
        System.out.println(ErrorConstants.ERROR_SESSION_USERNAME);
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_SESSION_USERNAME);
        return GSON.toJson(variables);
      }
      // Retrieve workout ID
      Integer workoutId = PostgresDatabase.getWorkoutId(workoutName, poster);

      if (workoutId != null) {
        try {
          PostgresDatabase.removeLike(username, workoutId);
        } catch(SQLException | URISyntaxException ex) {
          // Failed to remove like
          response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          variables = ImmutableMap.of("error", ErrorConstants.ERROR_REMOVE_LIKE);
          return GSON.toJson(variables);
        }
      } else {
        // Failed to retrieve workout ID
        response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        System.out.println(ErrorConstants.ERROR_GET_WORKOUTID);
        variables = ImmutableMap.of("error", ErrorConstants.ERROR_GET_WORKOUTID);
        return GSON.toJson(variables);
      }
      variables = ImmutableMap.of("isValid", true);
      return GSON.toJson(variables);
    }
  }
}

