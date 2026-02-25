## API Mockup
<img width="1771" height="1151" alt="project 2 mockup drawio (1)" src="https://github.com/user-attachments/assets/47664eff-52e9-482b-8d5f-5310e7f1c7c5" />

## ERD
<img width="931" height="182" alt="project-2_ERD drawio" src="https://github.com/user-attachments/assets/a82e7098-a0fc-4481-bdc4-bcd1d3d3a54d" />
## Running the Application with Docker

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running
- [Java 17+](https://adoptium.net/) installed

### Steps

**1.
``` 
**2. Build the JAR file**

On Mac/Linux:
```
bash ./mvnw package
``` 
On Windows:
```
cmd .\mvnw.cmd package

``` 
**3. Build the Docker image**
```
bash docker build -t photo-album .
``` 

**4. Run the container**
```
bash docker run -p 8080:8080 --name photo-album-container photo-album
``` 

**5. Test it**

Open your browser and go to:
```
[http://localhost:8080/](http://localhost:8080/)


You should see: `Photo Album API Test`

### Stopping the Container
```bash
docker stop photo-album-container
```

### Restarting the Container
```bash
docker start photo-album-container
```
