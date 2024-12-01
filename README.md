H2 Database is used. Admin user is created automatically with the start script. 

Run the app with
java -jar loanapi-0.0.1-SNAPSHOT.jar

# Create Customer
curl -u admin:password123 -X POST http://localhost:8080/api/customers \
-H "Content-Type: application/json" \
-d '{
  "name": "John",
  "surname": "Doe",
  "creditLimit": 10000,
  "usedCreditLimit": 0
}'

# Create Loan 1
curl -u admin:password123 -X POST http://localhost:8080/api/loans/1 \
-H "Content-Type: application/json" \
-d '{
  "loanAmount": 6000,
  "numberOfInstallments": 12,
  "interestRate": 0.2
}'

# List loans
curl -u admin:password123 -X GET http://localhost:8080/api/loans/customer/1 -H "Content-Type: application/json"

# Pay loan
curl -u admin:password123 -X POST http://localhost:8080/api/loans/1/pay -H "Content-Type: application/json" -d '{
  "amount": 600 
}'

Gradle and Java 21 is necessary to build the project.
