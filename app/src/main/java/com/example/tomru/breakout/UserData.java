package com.example.tomru.breakout;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Hoffs-Laptop on 2017-12-19.
 */

public class UserData implements Serializable {
    private String name;
    private int points;

    public UserData(String name) {
        this.name = name;
        new RegisterUser().execute(name);
        this.points = 0;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int adding) {
        new UpdatePointsTask(name).execute(adding);
        points += adding;
    }

    public void updateFromRemote() {
        new GetPointsTask(this).execute(name);
    }

    private static class UpdatePointsTask extends AsyncTask<Integer, Integer, Long> {
        private static final String url = "https://breakout-backend.herokuapp.com/add";
        private String user;

        UpdatePointsTask(String username) {
            user = username;
        }

        @Override
        protected Long doInBackground(Integer... integers) {
            try {
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                connection.setDoOutput(true);
                OutputStream output = new BufferedOutputStream(connection.getOutputStream());
                String out = "username=" + user + "&points=" + integers[0];
                output.write(out.getBytes());
                output.flush();

                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                input.close();
                output.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class GetPointsTask extends AsyncTask<String, Integer, Integer> {
        private static final String url = "https://breakout-backend.herokuapp.com/points/";
        private UserData userData;

        GetPointsTask(UserData data) {
            userData = data;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int result = 0;
            try {
                URL urlObj = new URL(url + strings[0]);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String pointString = input.readLine();
                result = Integer.parseInt(pointString);
                input.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            userData.setPoints(result);
            return result;
        }
    }

    private static class RegisterUser extends AsyncTask<String, Integer, Long> {
        private static final String url = "https://breakout-backend.herokuapp.com/register";

        @Override
        protected Long doInBackground(String... strings) {
            try {
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                connection.setDoOutput(true);
                OutputStream output = new BufferedOutputStream(connection.getOutputStream());
                String out = "username=" + strings[0];
                output.write(out.getBytes());
                output.flush();

                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                input.close();
                output.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
