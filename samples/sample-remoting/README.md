# sample-remoting

## No remoting

Open terminal in the root directory and run:

    ./gradlew :samples:sample-remoting:bootRun

1. No authentication.
    
        curl http://localhost:8080/sample/no-auth/abc  
        
    Result: error 500.
    
2. Various types of authentication:

        curl http://localhost:8080/sample/user-auth/abc
        curl http://localhost:8080/sample/system-auth/abc
        curl http://localhost:8080/sample/system-user-auth/abc
    
    Result: success.

## Client-server with trusted client

Open first terminal in the root directory and run:

    ./gradlew :samples:sample-remoting:bootRun \
      --args='--spring.profiles.active=remoting,server --jmix.clientToken=123'     

Open second terminal in the root directory and run:

    ./gradlew :samples:sample-remoting:bootRun \
      --args='--spring.profiles.active=remoting,client --jmix.clientToken=123'     

1. No authentication on client.

        curl http://localhost:8081/sample/no-auth/abc 

    Result: error 500

2. Various types of authentication (both `Authenticator` and `UserSessionManager`):

        curl http://localhost:8080/sample/user-auth/abc
        curl http://localhost:8080/sample/system-auth/abc
        curl http://localhost:8080/sample/system-user-auth/abc
    
    Result: success.

## Client-server with non-trusted client

Open first terminal in the root directory and run:

    ./gradlew :samples:sample-remoting:bootRun \
      --args='--spring.profiles.active=remoting,server --jmix.clientToken=123'     

Open second terminal in the root directory and run:

    ./gradlew :samples:sample-remoting:bootRun \
      --args='--spring.profiles.active=remoting,client --jmix.clientToken=invalid'     

1. No authentication on client.

        curl http://localhost:8081/sample/no-auth/abc 

    Result: error 500
    
2. Authentication using `Authenticator` on the client (without user password). 

        curl http://localhost:8081/sample/system-auth/abc
        curl http://localhost:8081/sample/system-user-auth/abc
         
    Result: error 500 or 403.

3. Authentication using `UserSessionManager` on the client. 

        curl http://localhost:8081/sample/user-auth/abc
         
    Result: success.

