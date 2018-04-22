# microblog
A simple, low fidelity microblogging platform.

# build microblog-app image
docker build -t microblog-app -f docker-images/microblog/Dockerfile .

# build the microblogs database, postgresql
docker build -t microblog-db -f docker-images/postgres/Dockerfile .

# run the database
# connect through localhost:5432
docker run --name microblog-db \
       --detach \
       -p 5432:5432 \
       -e POSTGRES_PASSWORD=secret \
       $DATABASE_NAME

# run the microblog app
docker run --name microblog \
       --detach \
       --link microblog-db:postgres \
       microblog-app
