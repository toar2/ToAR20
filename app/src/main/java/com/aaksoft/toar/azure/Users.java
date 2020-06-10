package com.aaksoft.toar.azure;

/*
 *   Created By Aasharib on 27,March,2019
 */

/*
    This class handles User in cloud database
 */

public class Users {
     private String id;
     private String email;
     private String name;
     private String password;
     private String username;


     public Users(){               // Empty constructor

     }

     public String getPassword() {
          return password;
     }


     public Users(String name, String username, String email, String id, String password) {
          this.id = id;
          this.email = email;
          this.name = name;
          this.password = password;
          this.username = username;           // As when user is first created, he has no images online
     }

     public void setPassword(String password) {
          this.password = password;
     }

     public String getUsername() {
          return username;
     }

     public void setUsername(String username) {
          this.username = username;
     }


     public String getId() {
          return id;
     }

     public void setId(String id) {
          this.id = id;
     }

     public String getEmail() {
          return email;
     }

     public void setEmail(String email) {
          this.email = email;
     }

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

}
