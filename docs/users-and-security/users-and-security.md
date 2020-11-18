## [Users and Security](./users-and-security.md)

The Core Bennu Framework provides support for users and pluggable authentication mechanisms.

In an effort to keep the Framework generic, only basic information about the user is stored (such the user's name, login username, email address, preferred locale and account expiration date). Applications requiring extra information to be stored about a user, should create a new type of object, and connect it to the User instance

* Programmatic API
  + Mocking
* Requiring Login
User Management
Login Providers
Local Login
CAS Provider
Rolling your own
Customizing the Login Page
Authentication Listeners
