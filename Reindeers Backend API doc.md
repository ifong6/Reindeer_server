# Reindeers Backend API doc

+ In the following api doc, **sub** is an UUID generated by aws cognito, it was used to identity user auth.

+ each service has a health check

+ check donation-user-workflow.drawio to see the workflow of a donation (open by draw.io, which is a website)

## 1. User Service `/api/user`

1) `/add`POST
   
   + add basic userinfo from cognito to rds database, triggered by lambda
   
   + if you want to run the server on you local machine, you'll also need this

2) `/check-role/{sub}` GET
   
   + check if the user has choose the role. If not, show them the select role page in frontend page

3) `/update-role/{sub}` POST
   
   + user select role.  
   
   + **can not be changed in the future**

4) `/update-profile/{sub}` POST
   
   + update profile (avatar not included)

5) `/check-address/{sub}` GET
   
   + check if user has submitted the address
   
   + address is recommanded before create gift request

6) `/update-avatar/{sub}` POST
   
   + update avatar

7) `/user-info/{sub}`GET
   
   + return user profile

8) `/user-public-info/{sub}`GET
   
   + return user profile (not need auth, only for receiver profile display)

## 2. Gift Service

### 1) Public `/api/gifts`(no auth for public api)

1. `/` GET
   
   + show wishes in the wish page. with pagination. no filter.

2. `/search` GET
   
   + show wishes but has filter (keyword, type, price...)

3. `/{requestId}`GET
   
   + return details for specific wish page

4. `/review/{donationId}` GET
   + return details for specific Donation Review 

5. `/subReview/{donationId}/{subReviewId}` GET
   + return details for specific SubReview


### 2) Receiver `/api/gifts/receiver`

1. `/request/new` POST
   
   + create a new wish request

2. `/request/cancel/{requestId}` POST
   
   + cancel a wish request

3. `/request/{requestId}` GET
   
   + return details of a request

4. `/request/{requestId}/donations`  GET
   
   + return donations related to this request

5. `/request/update/{requestId}` POST
   
   + update a request

6. `/donation/confirm/{donationId}` PATCH
   
   + confirm a donation from a donor, which means this donation is now processing

7. `/donation/receive/{donationId}` PATCH
   
   + mark a donation as received, which means this donation is now completed

8. `/donation/cancel/{donationId}` PATCH
   
   + apply to cancel a donation

9. `/received-donation` GET
   
   + return all donations

10. `/posted-request` GET
    
    + return all requests

11. `/posted-request/search` GET
    
    + search requests

12. `/donation/{donationId}/review` POST
    
    + write reviews for completed donation
    + Param: donationId (UUID)
    + Body : DonationReview object

13. `/donation/{donationId}/review` POST

    + Write a review for a completed donation.
    + Param: donationId (UUID)
    + Body : SubReview object.

14. `/donation/{donationId}/review` DELETE

    + Delete a review for a donation.
    + Param: donationId (UUID)

15. `/donation/{donationId}/subReview` POST

    + Write a sub-review for a donation review.
    + Param: donationId (UUID)

16. `/donation/{donationId}/subReview/{subReviewId}` DELETE

    + Delete a specific sub-review for a donation.
    + Param: donationId (UUID) subReviewId (UUID)

### 3) Donor `/api/gifts/donor`

1. `/donation/new/{requestId}` POST
   
   + create new donation based on a request

2. `/donation/cancel/{donationId}` PATCH
   
   + cancel a donation

3. `/donation/update/{donationId}` POST
   
   + update a donaton

4. `/handled-donation` GET
   
   + return all the donations by the user

5. `/donation/{donationId}` PATCH
   
   + add tracking number for a donation

## 3. Notification Service `/api/notifications`

1. `/` GET
   
   + get this user's all notification (with pagination)

2. `/{notificationId}`
   
   + get details for a notification
