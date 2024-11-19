# UserService

## How it works

This repository is UserService of Reindeers Forever Gifts

1. Run idGenerator to create an id pool table.
   
   

2. Install rabbitmq
   
   ```shell
   # check rabbitmq is running
   sudo systemctl status rabbitmq-server
   # add user
   rabbitmqctl add_user rabbitmqUser reindeers
   rabbitmqctl set_user_tags rabbitmqUser administrator
   rabbitmqctl set_permissions -p / rabbitmqUser ".*" ".*" ".*"
   ```

3. edit application.properties and run the project

## API

1. health check
   
   GET `/user/health` 

2. add user
   
   POST `/user/add` 
   
   request body
   
   ```json
   {
       "username": "test",
       "email": "test@test.com",
       "sub": "c8f14340-90b1-70df-a53f-052d4bd3e5zz"
   }
   ```

3. check user role
   
   GET`/user/check-role/{sub}` 

4. update user role
   
   POST`/user/update-role/{sub}` 
   
   request body
   
   ```json
   {
       "role": "Recipient"
   }
   ```

5. check address
   
   GET `/user/check-address/{sub}` 

6. update avatar
   
   POST `/user/update-avatar/{sub}` 
   
   request body
   
   ```json
   {
       "avatarUrl": "test.com/url"
   }
   ```

7. get personal info
   
   GET`/user/user-info/{sub}`

8. update personal info
   
   POST `/user/update-profile/{sub}`
   
   request body
   
   ```json
   {
       "username": "test01",
       "age": "12",
       "address": "example address",
       "story": "I'm just a poor boy, I need no sympathy",
       "interest": "game, code"
   }
   ```

9. get receiver detail (request from GiftService)
   
   GET `/user/receiver-detial/{sub}`
