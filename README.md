# http-clients

## Ref

 [http-clients](https://spring.io/blog/2021/01/11/ymnnalft-http-clients)

## issues 
  
  1. executor not accepting a task by ReactiveDataflowWithProjectReactor 

    2021年3月31日11:31:23 fix executor not accepting a task 
    add thread sleep to ReactiveDataflowWithProjectReactor.begin method 

  2. executor not accepting a task by HttpClientsApplication

    2021年3月31日11:45:17 fix executor not accepting a task 
    add thread sleep to HttpClientsApplication.ready method 

## Upgrade Record 

  1. 2021年3月25日13:25:03 spring boot version 2.4.3 the to 2.4.4
