# GigMatch Pro: Freelancer-Customer Matching System

## 📖 Project Overview
GigMatch Pro is a scalable **gig economy simulation platform** developed in Java, inspired by real-world service marketplaces. The system efficiently matches customers with skilled freelancers using a sophisticated ranking algorithm.

The project simulates a dynamic marketplace where thousands of requests, job completions, and cancellations occur, requiring optimized data structures to handle high-load scenarios within strict time limits.

## 🚀 Key Features
* [cite_start]**Smart Matching Engine:** Utilizes a **Integer Composite Score** algorithm to rank freelancers based on skills, ratings, reliability, and burnout status[cite: 809].
* [cite_start]**Dynamic Skill Evolution:** Freelancers' skills (Technical, Communication, Creativity, Efficiency, Attention to Detail) evolve dynamically based on job performance and customer ratings[cite: 561, 580].
* **Simulation Mechanics:**
    * [cite_start]**Burnout System:** Freelancers experience performance drops if overworked[cite: 522].
    * [cite_start]**Loyalty Tiers:** Customers gain status (Bronze, Silver, Gold, Platinum) and discounts based on spending[cite: 854, 857].
    * [cite_start]**Time Simulation:** A `simulate_month` feature processes long-term changes like skill updates and loyalty calculations[cite: 539].
* [cite_start]**Service Variety:** Supports 10 different service types (e.g., Web Development, Painting, Tutoring) with unique skill requirement profiles[cite: 529].

## 🛠️ Technical Details
* **Language:** Java
* **Architecture:** Object-Oriented Design (Freelancer, Customer, Service classes)
* [cite_start]**Data Structures:** Custom implementations using `ArrayList` (Strict constraint: No other Java Collections allowed)[cite: 926].
* [cite_start]**Performance:** Designed to handle ~500,000 operations (Large Test Case) efficiently[cite: 911].

## 🧪 How It Works
The system processes commands from an input file to manage the marketplace lifecycle:
1.  **Registration:** `register_freelancer` / `register_customer`
2.  **Job Request:** `request_job` (Automatically finds and employs the best candidate)
3.  **Operations:** `complete_and_rate`, `cancel_by_customer`, `cancel_by_freelancer`
4.  **Analysis:** `query_freelancer`, `query_customer`, `find_winning`

## 💻 How to Run
1.  Clone the repository:
    ```bash
    git clone [https://github.com/Nisanurdogan38618/Freelancer-Customer-Matching.git](https://github.com/Nisanurdogan38618/Freelancer-Customer-Matching.git)
    ```
2.  Navigate to the source folder:
    ```bash
    cd src
    ```
3.  Compile the project:
    ```bash
    javac *.java
    ```
4.  Run with input/output files:
    ```bash
    java Main input.txt output.txt
    ```

---
*This project was developed as part of the CMPE250 Data Structures and Algorithms course (Fall 2025).*
