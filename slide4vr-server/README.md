curl -X POST "http://localhost:8080/slide" -H "accept: */*" -H "Content-Type: multipart/form-data" -F "title=Hello World2" -F "slide=@../../hello.txt"
./mvnw compile quarkus:dev
