# My Java MongoDB Application

### Description
This is a Java application using MongoDB for data management, built with Spring Boot. The application performs [brief description of functionality].

## Requirements
- **Java 17**: [Install Java](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- **Maven**: [Install Maven](https://maven.apache.org/install.html)
- **MongoDB**: Local instance or MongoDB Atlas

## Installation

### 1. Clone the repository:
      git clone https://github.com/vgoyenechec/btg-tech-test.git
      cd yourproject
      
### 2. Install dependencies
      mvn install

### 3. Configure MongoDB Cluster
Create your own MongoDB Cluster if you want to run it on local:
1. Database name: tech_test.
2. For example, replace username = bgt-user and password = bgt-password in the application.yaml file.
3. After creating your cluster, you should have 3 tables: clients, funds, and transactions
4. Add your default client: 
   ```json
     {
     "_id": {
     "$oid": "66febf3eae5fa66b652750cf"
     },
     "email": "correo@gmail.com",
     "phone": "317272818",
     "name": "Usuario Default",
     "balance": 0,
     "preferred_notification": "email",
     "subscriptions": []      
     }
5. Add your funds: 
   ```json
      [{
      "_id": "1",
      "name": "FPV_BTG_PACTUAL_RECAUDADORA",
      "minimum_subscription_amount": 75000,
      "category": "FPV"
      },
      {
      "_id": "4",
      "name": "FDO-ACCIONES",
      "minimum_subscription_amount": 250000,
      "category": "FIC"
      },
      {
      "_id": "5",
      "name": "FPV_BTG_PACTUAL_DINAMICA",
      "minimum_subscription_amount": 100000,
      "category": "FPV"
      },
      {
      "_id": "2",
      "name": "FPV_BTG_PACTUAL_ECOPETROL",
      "minimum_subscription_amount": 125000,
      "category": "FPV"
      },
      {
      "_id": "3",
      "name": "DEUDAPRIVADA",
      "minimum_subscription_amount": 50000,
      "category": "FIC"
      }]

### Run the app
      mvn spring-boot:run


### Postman Collection
You can find the collection in the /resources folder.