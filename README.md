This is a simple netty project use for modify your hosts file, You can make changes on this basis.

## Demo



## Introduction

This project divided into two parts:

- netty-server

    - listen on port 8888, you can change bind port in [this file](https://github.com/zoyua/netty-dns/blob/93d4aae18af0da7b2976903136acaf4255511477/netty-server/src/main/java/cn/zoyua/server/netty/Server.java#L24)
    
    - update you local hosts file

- netty-client

    - connect to netty server
    
    - show the hosts list on web page

## Usage

You can run this project by two ways.

### Start sequentially

1. Start netty-server.

2. Start netty-client.

### Run the image i made
 
1. Pull the image.

```
docker pull zoyua/netty-dns:1.0
```

2. Run the image.

```
docker run -p 8080:8080 -d zoyua/netty-dns:1.0
```

## Finally

Visit in this url:

```
localhost:8080
```