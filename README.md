## API Mockup
<img width="1771" height="1151" alt="project 2 mockup drawio (1)" src="https://github.com/user-attachments/assets/47664eff-52e9-482b-8d5f-5310e7f1c7c5" />

## ERD
<img width="931" height="182" alt="project-2_ERD drawio" src="https://github.com/user-attachments/assets/a82e7098-a0fc-4481-bdc4-bcd1d3d3a54d" />

---

## Docker Setup

This project uses Docker so that teammates do not need Java, JDK, or Maven installed to run the app.

## Running the App (Teammates)

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running

### Steps

**1. Clone the repo (if you haven't already)**
```
git clone https://github.com/cst438-project-2/back-end.git
cd back-end
```

**2 Run 
```
docker build -t photo-album-backend .
```
**3 Build 
```
 docker run -p 8080:8080 photo-album-backend
```
**4 to Stop Docker 
```
 Ctrl+C  in terminal its running in 
```