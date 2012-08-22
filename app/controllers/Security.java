package controllers;

public class Security {
   static boolean authenticate(String username, String password) {
      return "tul".equals(username) && "2417e0501d028f2566cfea42ace55004".equals(password);
   }
}