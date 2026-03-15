## API Mockup
<img width="1771" height="1151" alt="project 2 mockup drawio (1)" src="https://github.com/user-attachments/assets/47664eff-52e9-482b-8d5f-5310e7f1c7c5" />

## ERD
<img width="931" height="182" alt="project-2_ERD drawio" src="https://github.com/user-attachments/assets/a82e7098-a0fc-4481-bdc4-bcd1d3d3a54d" />

---

## Docker Setup

This project uses Docker so that teammates do not need Java, JDK, or Maven installed to run the app.

---

## Running Locally (IntelliJ)

### Prerequisites
- Java 17
- Maven
- IntelliJ IDEA
- Docker Desktop (optional, for Docker setup)
- Google account added to the `photoapi-57629`
### Step 1 — Install Google Cloud CLI
Download from: https://cloud.google.com/sdk/docs/install

During installation it will prompt you to log in — sign in with your Google account and select project `photoapi-57629`.

Then run this to set up application credentials:
```
gcloud auth application-default login
```

### Step 2 — Download Cloud SQL Auth Proxy
```
curl -o cloud-sql-proxy.exe https://storage.googleapis.com/cloud-sql-connectors/cloud-sql-proxy/v2.15.2/cloud-sql-proxy.x64.exe
```

### Step 3 — Run the Proxy
Open a terminal (cmd, PowerShell, or terminal in your IDE), navigate to the folder where you saved `cloud-sql-proxy.exe`, and run:
```
.\cloud-sql-proxy.exe photoapi-57629:us-west2:photoapi-57629-instance
```
Leave this terminal open every time before starting the app.



### Step 4 — Run the App
Start the app in IntelliJ. The app will be available at `http://localhost:8080`.

---

## Running with Docker

Make sure the proxy is running with:
```
.\cloud-sql-proxy.exe --address 0.0.0.0 --port 5432 photoapi-57629:us-west2:photoapi-57629-instance
```

Then:
```
docker compose up --build, exclude --build if reusing docker image
```

To stop:
```
Ctrl+C
```

---

