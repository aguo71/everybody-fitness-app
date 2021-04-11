package edu.brown.cs.everybody.userComponents;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.everybody.data.PostgresDatabase;
import edu.brown.cs.everybody.feedComponents.Workout;
import edu.brown.cs.everybody.utils.Main;
import edu.brown.cs.everybody.utils.WorkoutComparator;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Contains handler classes for user-related logic.
 */
public class UserHandlers {
  private static final Gson GSON = new Gson();

  /**
   * Handles requests made for user creation.
   */
  public static class NewUserHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());

      // Parse request from client
      // TODO: verify info sent from frontend
      String fName = data.getString("firstName");
      String lName = data.getString("lastName");
      String createdAtStr = data.getString("createdAt");
      String username = data.getString("username");
      String workoutType = data.getString("workoutType");
      Integer workoutDuration = data.getInt("workoutDuration");

      // Format date
      // TODO: verify date format
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date createdAt = sdf.parse(createdAtStr);

      List<Object> listData = new ArrayList<>(Arrays.asList(fName, lName, createdAt, username, workoutType, workoutDuration));
      PostgresDatabase.insertUser(listData);
      return null;
    }
  }

  public static class DeleteUserHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());


      Map<String, Object> variables = ImmutableMap.of("foo", "bar");
      return GSON.toJson(variables);
    }
  }

  public static class GetUserInfoHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String username = data.getString("username");

      List<Object> userInfo = PostgresDatabase.getUserInfo(username);

      Map<String, Object> variables = ImmutableMap.of("firstName", userInfo.get(0), "lastName", userInfo.get(1),
      "workoutType", userInfo.get(2), "workoutDuration", userInfo.get(3));
    }

  /**
   * Handles requests made for home feed recommendations.
   */
  public static class GetRecommendationsHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      // TODO: filter out workouts which don't match preferences in the for loops


      JSONObject data = new JSONObject(request.body());
      // List of Workouts to return to frontend
      List<Map<String, String>> output = new ArrayList<>();

      // Parse request from client and extract user info
      int userID = Integer.parseInt(data.getString("userID"));
      AppUser user;
      if(Main.getUsers().containsKey(userID)) {
        user = Main.getUsers().get(userID);
      } else {
        user = PostgresDatabase.getUser(userID);
        // TODO: instead of checking for null, catch the exception
        if(user == null) {
          Map<String, Object> variables = ImmutableMap.of("error", "UserID Not Found.");
          return GSON.toJson(variables);
        }
      }

      // inserts 10 workouts from people user follows into finalSortedWorkouts
      PriorityQueue<Workout> finalSortedWorkouts = new PriorityQueue<>(new WorkoutComparator());
      int counter = 0;
      List<Integer> following = user.getFollowing();
      Collections.shuffle(following);
      for (int followingUserID : following) {
        if (counter == 10) {
          break;
        }
        AppUser followingUser = PostgresDatabase.getUser(followingUserID);
        // TODO: catch exceptions
        PriorityQueue<Workout> followingUserWorkouts = new PriorityQueue<>(new WorkoutComparator());
        if(followingUser.getWorkouts() != null) {
          followingUserWorkouts.addAll(followingUser.getWorkouts());
        }
        Workout recentPost = followingUserWorkouts.poll();
        while (recentPost != null) {
          if (!user.getRecentlyViewed().contains(recentPost.getWorkoutId())) {
            user.addRecentlViewed(recentPost.getWorkoutId());
            finalSortedWorkouts.add(recentPost);
            counter++;
            break;
          } else {
            recentPost = followingUserWorkouts.poll();
          }
        }
      }

      // inserts 4 workouts from strongly connected users into output
      counter = 0;
      List<Integer> scc = user.getStronglyConnected();
      Collections.shuffle(scc);
      for (int connectedUserID : scc) {
        if (counter == 4) {
          break;
        }
        AppUser connectedUser = PostgresDatabase.getUser(connectedUserID);
        // TODO: catch exceptions
        PriorityQueue<Workout> connectedUserWorkouts = new PriorityQueue<>(new WorkoutComparator());
        if(connectedUser.getWorkouts() != null) {
          connectedUserWorkouts.addAll(connectedUser.getWorkouts());
        }
        Workout recentPost = connectedUserWorkouts.poll();
        while (recentPost != null) {
          if (!user.getRecentlyViewed().contains(recentPost.getWorkoutId())) {
            user.addRecentlViewed(recentPost.getWorkoutId());
            finalSortedWorkouts.add(recentPost);
            counter++;
            break;
          } else {
            recentPost = connectedUserWorkouts.poll();
          }
        }
      }
      Workout finalWorkout = finalSortedWorkouts.poll();
      while (finalWorkout != null) {
        output.add(finalWorkout.toMap());
      }
      Map<String, Object> variables = ImmutableMap.of("output", output);
      return GSON.toJson(variables);
    }
  }
}