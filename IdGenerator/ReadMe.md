# IdGeneratorService

## How it works

This repository is part of UserService of Reindeers Forever Gifts

install redis before you run the project

## API

1. health check
   
   GET `/id/health` 

2. get current number in id_pool
   
   GET `/id/currentNumber`

3. refill id manually
   
   POST `/id/refill`
