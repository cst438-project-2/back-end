## API Mockup
<img width="1771" height="1151" alt="project 2 mockup drawio (1)" src="https://github.com/user-attachments/assets/47664eff-52e9-482b-8d5f-5310e7f1c7c5" />

## ERD
<img width="931" height="182" alt="project-2_ERD drawio" src="https://github.com/user-attachments/assets/a82e7098-a0fc-4481-bdc4-bcd1d3d3a54d" />

---

## Docker Setup

This project uses Docker so that teammates do not need Java, JDK, or Maven installed to run the app.

### How It Works

- The `Dockerfile` uses a **multi-stage build**: it compiles the Spring Boot app inside Docker using the Maven Wrapper, then packages it into a lightweight image. No local Java or Maven required.
- The **`production` branch** is the trigger for building a new Docker image. Whenever code is pushed to `production`, GitHub Actions automatically builds a new Docker image and uploads it to GitHub Container Registry (ghcr.io).
- The **`docker-compose.yml`** allows teammates to pull and run that pre-built image with a single command — no building needed on their end.

---

## Running the App (Teammates)

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running

### Steps

**1. Clone the repo (if you haven't already)**
```bash
git clone https://github.com/cst438-project-2/back-end.git
cd back-end
```

**2. Start the app**
```bash
docker compose up
```

Docker will pull the latest pre-built image from ghcr.io and start the server.

**3. Test it**

Open your browser and go to:
```
http://localhost:8080/
```
You should see: `Photo Album API Test`

**4. Stop the app**
```bash
docker compose down
```



---

## Updating the Docker Image (Maintainers)

The Docker image is only rebuilt when someone pushes to the `production` branch and prevents GitHub Actions minutes from being used on every feature branch push.

### Steps to publish a new image

**1. Switch to the production branch**
```bash
git checkout production
```

**2. Merge in the changes you want to deploy**
```bash
git merge main
```

**3. Push to production**
```bash
git push origin production
```

GitHub Actions will automatically:
1. Build the Docker image using the `Dockerfile`
2. Push it to `ghcr.io/cst438-project-2/back-end:latest`

Teammates can then run `docker compose up` to get the updated version.

---

## File Reference

| File | Purpose |
|---|---|
| `Dockerfile` | Defines how the Docker image is built (compiles and packages the app) |
| `docker-compose.yml` | Lets teammates run the app with `docker compose up` |
| `.github/workflows/docker.yml` | GitHub Actions workflow — triggers a Docker build/push on every push to `production` |