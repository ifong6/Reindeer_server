# Api

## Anonymous User

+ 初始gift page 分页获取gift request 一次获取15个
  
  GET `/gifts`

+ search & filter 通过keyword requestType status maxPrice minPrice
  
  GET `/gifts/search`

+ 查看某个request的具体信息
  
  GET `/gifts/{requestId}`

## Care Agency and Recipient

1. 基础功能
   
   + create gift request
     
     POST `/gifts/receiver/request/new`
   
   + cancel gift request
     
     POST `/gifts/receiver/request/cancel/{requestId}`
   
   + update gift request
     
     POST `/gifts/receiver/request/update/{requestId}`

2. dashboard中查看所有request
   
   + 查看所有的gift request
     
     GET `/gifts/receiver/request`
   
   + 根据gift name过滤所有的gift request
     
     GET `/gifts/receiver/request/search`
   
   + specific request page中显示具体信息
     
     GET `/gifts/receiver/request/{requestId}`
   
   + specific request page中显示相关的donation
     
     GET `/request/{requestId}/donations`

3. dashboard中查看所有的donation
   
   + 查看所有的donation
     
     GET `/gifts/receiver/received-donation`
   
   + 根据gift name过滤所有的donation
     
     GET `/gifts/receiver/received-donation/search`
   
   + specific donation page中显示具体信息
     
     GET `/gifts/receiver/donation/{donationId}`
   
   + specific donation page中显示相关的request （可用public api)
     
     GET `/gifts/{requestId}`
   
   + confirm某一个donation 即正式开始捐赠流程
     
     PATCH `/gifts/receiver/donation/confirm/{donationId}`
   
   + cancel donation
     
     PATCH `/gifts/receiver/donation/cancel/{donationId}`
   
   + receive donation 确认收货
     
     PATCH `/gifts/receiver/donation/receive/{donationId}`

## Donor

1. 基础功能
   
   + create new donation
     
     POST `/gifts/donor/donation/new/{requestId}`
   
   + cancel donation
     
     POST `/gifts/donor/donation/cancel/{donationId}`
   
   + update donation
     
     POST `/gifts/donor/donation/update/{donationId}`

2. dashboard查看所有的donation
   
   + 查看所有donation
     
     GET `/gifts/donor/donation`
   
   + 根据gift name过滤所有的donation
     
     GET `/gifts/donor/donation/search`
   
   + 查看某个donation
     
     GET `/gifts/donor/donation/{donationId}`
   
   + specific donation page中显示相关的request（可用public api）
     
     GET `/gifts/{requestId}`
   
   + 更新tracking number
     
     PATCH `/gifts/donor/donation/{donationId}`
