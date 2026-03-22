# SkyWay Airlines — Booking System

A full-stack airline reservation system built from scratch using 
Java Servlets and JDBC — no Spring, no Hibernate, pure Java.

## Features
- Browse and search flights by route
- Book tickets with real-time seat availability
- Cancel bookings using Booking ID
- View passenger booking history
- Revenue dashboard for admin
- Concurrent booking stress test
- Multi-threaded booking engine with race condition handling

## Tech Stack
| Layer | Technology |
|---|---|
| Frontend | HTML, CSS, Vanilla JavaScript |
| Backend | Java Servlets (Jakarta EE) |
| Database | PostgreSQL + JDBC |
| Server | Apache Tomcat 10.1 |
| Build | Maven |
| Deploy | Docker + Railway |

## Pages
- Home — quick flight search + stats
- Flights — browse, filter, sort by price
- Booking — passenger form + confirmation ticket
- My Bookings — booking history by passenger ID
- Cancel — cancel booking by booking ID
- Admin — revenue dashboard
- Stress Test — concurrent booking simulator

## Run Locally
1. Clone the repo
2. Set up PostgreSQL and create `airline_db`
3. Update `DBConnection.java` with your credentials
4. Run on Tomcat 10.1
5. Visit `http://localhost:8082/airline-booking-system/pages/index.html`

## Web App Screenshots

### Home Page
<img width="600" alt="Screenshot 2026-03-22 191603" src="https://github.com/user-attachments/assets/d71efe4a-6ef6-457d-85af-7a7442b3f981" />

### Flight Selection Page
<img width="600" alt="Screenshot 2026-03-22 191626" src="https://github.com/user-attachments/assets/b3f2ccfa-8285-4553-86bd-35c846c8d543" />

### Booking Page
<img width="600" alt="image" src="https://github.com/user-attachments/assets/248eaf01-0a20-49b2-9964-794276f8fea3" />

### Admin Dashboard
<img width="600" alt="Screenshot 2026-03-22 191615" src="https://github.com/user-attachments/assets/e50b4d7c-c939-4592-b51b-12ee253b09dc" />

### Concurrent Test
<img width="600" alt="Screenshot 2026-03-22 191741" src="https://github.com/user-attachments/assets/99a6d044-c10c-4530-97dc-b8c44c8e985d" />

## Railway Deployment Screenshots

<img width="600" alt="Screenshot 2026-03-22 191404" src="https://github.com/user-attachments/assets/013b2509-71c2-422c-a593-d3e2a73a49d9" />
<img width="600" alt="image" src="https://github.com/user-attachments/assets/a5fe24eb-4ee2-4cc3-be30-7523bb339729" />
<img width="600" alt="Screenshot 2026-03-22 191456" src="https://github.com/user-attachments/assets/7f25c210-0b27-4333-80f2-a51438e33e18" />
<img width="600" alt="Screenshot 2026-03-22 191419" src="https://github.com/user-attachments/assets/66a2d111-49ab-498e-8db9-ba6228f5e0c3" />



