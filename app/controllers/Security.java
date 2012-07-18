package controllers;

public class Security {
   static boolean authenticate(String username, String password) {
      return "tul".equals(username) && "tul".equals(password);
   }
}