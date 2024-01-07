package KanyaRasi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

interface RentalItem {
    double calculateRentalCost(Date startDate, Date endDate);
}

class RentalEntity {
    private String name;

    public RentalEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Car extends RentalEntity implements RentalItem {
    private String carNumber;
    private boolean available;

    public Car(String carNumber, String carName) {
        super(carName);
        this.carNumber = carNumber;
        this.available = true;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public double calculateRentalCost(Date startDate, Date endDate) {
        double ratePerDay = 500.0;
        long rentalDays = calculateRentalDays(startDate, endDate);
        double rentalCost = ratePerDay * rentalDays;
        return rentalCost;
    }

    private long calculateRentalDays(Date startDate, Date endDate) {
        long rentalDays = 0;
        long millisecondsPerDay = 24 * 60 * 60 * 1000;
        long difference = endDate.getTime() - startDate.getTime();
        rentalDays = difference / millisecondsPerDay;
        return rentalDays;
    }
}

class Customer extends RentalEntity {
    private String email;
    private String phoneNumber;
    private String drivingLicenceNumber;

    public Customer(String name, String email, String phoneNumber, String drivingLicenceNumber) {
        super(name);
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.drivingLicenceNumber = drivingLicenceNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDrivingLicenceNumber() {
        return drivingLicenceNumber;
    }
}

class RentalRecord {
    private Car car;
    private Customer customer;
    private Date startDate;
    private Date endDate;

    public RentalRecord(Car car, Customer customer, Date startDate, Date endDate) {
        this.car = car;
        this.customer = customer;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Car getCar() {
        return car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}

class CarRentalSystem {
    private List<Car> cars;
    private Map<String, List<RentalRecord>> rentedCars;
    private List<Customer> customers;

    public CarRentalSystem() {
        cars = new ArrayList<>();
        rentedCars = new HashMap<>();
        customers = new ArrayList<>();
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public List<Car> getAvailableCars() {
        List<Car> availableCars = new ArrayList<>();
        for (Car car : cars) {
            if (car.isAvailable()) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    public void registerCustomer(Customer customer) {
        customers.add(customer);
        System.out.println("Registered Customer: " + customer.getName());
    }

    public void removeCustomer(Customer customer) {
        customers.remove(customer);
        System.out.println("Removed Customer: " + customer.getName());
    }

    public Customer getCustomerByID(String customerID) {
        for (Customer customer : customers) {
            if (customerID.equals(customer.getDrivingLicenceNumber())) {
                return customer;
            }
        }
        return null;
    }

    public RentalRecord createRentalRecord(Car car, Customer customer, Date startDate, Date endDate) {
        RentalRecord rentalRecord = new RentalRecord(car, customer, startDate, endDate);
        car.setAvailable(false);
        List<RentalRecord> rentalRecords = rentedCars.getOrDefault(customer.getDrivingLicenceNumber(), new ArrayList<>());
        rentalRecords.add(rentalRecord);
        rentedCars.put(customer.getDrivingLicenceNumber(), rentalRecords);
        System.out.println("Created Rental Record for Car: " + car.getCarNumber() + ", Customer: " + customer.getName());
        return rentalRecord;
    }

    public void returnCar(RentalRecord rentalRecord) {
        Car car = rentalRecord.getCar();
        car.setAvailable(true);
        System.out.println("Returned Car: " + car.getCarNumber());

        double rentalCost = car.calculateRentalCost(rentalRecord.getStartDate(), rentalRecord.getEndDate());
        System.out.println("Rental Cost: $" + rentalCost);
    }

    public List<RentalRecord> getRentalRecordsForCustomer(Customer customer) {
        List<RentalRecord> rentalRecords = rentedCars.getOrDefault(customer.getDrivingLicenceNumber(), new ArrayList<>());
        System.out.println("Rental Records for Customer: " + customer.getName());
        for (RentalRecord record : rentalRecords) {
            System.out.println("Car: " + record.getCar().getCarNumber());
        }
        return rentalRecords;
    }

    public List<RentalRecord> getRentalRecordsForCar(Car car) {
        List<RentalRecord> rentalRecords = new ArrayList<>();
        for (List<RentalRecord> records : rentedCars.values()) {
            for (RentalRecord record : records) {
                if (record.getCar().equals(car)) {
                    rentalRecords.add(record);
                }
            }
        }
        System.out.println("Rental Records for Car: " + car.getCarNumber());
        for (RentalRecord record : rentalRecords) {
            System.out.println("Customer: " + record.getCustomer().getName());
        }
        return rentalRecords;
    }

    public void extendReturnTime(RentalRecord rentalRecord, Date newEndDate) {
        Car car = rentalRecord.getCar();
        Customer customer = rentalRecord.getCustomer();
        Date oldEndDate = rentalRecord.getEndDate();

        if (car.isAvailable()) {
            System.out.println("Car already returned. Extension not possible.");
        } else if (newEndDate.before(oldEndDate)) {
            System.out.println("Invalid extension date. New return date must be after the current return date.");
        } else {
            rentalRecord.setEndDate(newEndDate);
            System.out.println("Extended return time for Car: " + car.getCarNumber() + ", Customer: " + customer.getName());

            double rentalCost = car.calculateRentalCost(rentalRecord.getStartDate(), newEndDate);
            System.out.println("New Rental Cost: $" + rentalCost);

            // Update car's availability status based on the new return date
            if (newEndDate.before(new Date())) {
                car.setAvailable(true);
            }
        }
    }
}

public class carRentalApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CarRentalSystem rentalSystem = new CarRentalSystem();

        System.out.println("Welcome to the Car Rental System!");

        System.out.print("Enter the number of cars to add: ");
        int numCars = scanner.nextInt();
        scanner.nextLine();

        for (int i = 1; i <= numCars; i++) {
            System.out.println("Enter details for Car " + i);
            System.out.print("Car Number: ");
            String carNumber = scanner.nextLine();
            System.out.print("Car Name: ");
            String carName = scanner.nextLine();

            Car car = new Car(carNumber, carName);
            rentalSystem.addCar(car);
        }

        System.out.print("Enter the number of customers to register: ");
        int numCustomers = scanner.nextInt();
        scanner.nextLine();

        for (int i = 1; i <= numCustomers; i++) {
            System.out.println("Enter details for Customer " + i);
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Phone Number: ");
            String phoneNumber = scanner.nextLine();
            System.out.print("Driving License Number: ");
            String drivingLicenseNumber = scanner.nextLine();

            Customer customer = new Customer(name, email, phoneNumber, drivingLicenseNumber);
            rentalSystem.registerCustomer(customer);
        }

        boolean exit = false;

        while (!exit) {
            System.out.println();
            System.out.println("Select an option:");
            System.out.println("1. Rent a Car");
            System.out.println("2. Return a Car");
            System.out.println("3. Display Available Cars");
            System.out.println("4. Display Rental Records for a Customer");
            System.out.println("5. Extend Return Time");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Rent a Car");
                    System.out.print("Enter the customer's driving license number: ");
                    String rentCustomerID = scanner.nextLine();
                    Customer rentCustomer = rentalSystem.getCustomerByID(rentCustomerID);

                    if (rentCustomer == null) {
                        System.out.println("Customer not found!");
                        break;
                    }

                    System.out.print("Enter the car number to rent: ");
                    String rentCarNumber = scanner.nextLine();
                    Car rentCar = null;

                    for (Car car : rentalSystem.getAvailableCars()) {
                        if (car.getCarNumber().equals(rentCarNumber)) {
                            rentCar = car;
                            break;
                        }
                    }

                    if (rentCar == null) {
                        System.out.println("Car not found or unavailable!");
                        break;
                    }

                    System.out.print("Enter the start date (yyyy-MM-dd): ");
                    String startDateString = scanner.nextLine();
                    System.out.print("Enter the end date (yyyy-MM-dd): ");
                    String endDateString = scanner.nextLine();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate;
                    Date endDate;

                    try {
                        startDate = dateFormat.parse(startDateString);
                        endDate = dateFormat.parse(endDateString);

                        if (startDate.after(endDate)) {
                            System.out.println("Invalid date range! Start date must be before the end date.");
                            break;
                        }

                        rentalSystem.createRentalRecord(rentCar, rentCustomer, startDate, endDate);
                    } catch (ParseException e) {
                        System.out.println("Invalid date format! Please enter dates in yyyy-MM-dd format.");
                    }

                    break;
                case 2:
                    System.out.println("Return a Car");
                    System.out.print("Enter the customer's driving license number: ");
                    String returnCustomerID = scanner.nextLine();
                    Customer returnCustomer = rentalSystem.getCustomerByID(returnCustomerID);

                    if (returnCustomer == null) {
                        System.out.println("Customer not found!");
                        break;
                    }

                    List<RentalRecord> customerRentalRecords = rentalSystem.getRentalRecordsForCustomer(returnCustomer);
                    System.out.print("Enter the car number to return: ");
                    String returnCarNumber = scanner.nextLine();
                    Car returnCar = null;
                    RentalRecord returnRecord = null;

                    for (RentalRecord record : customerRentalRecords) {
                        if (record.getCar().getCarNumber().equals(returnCarNumber)) {
                            returnCar = record.getCar();
                            returnRecord = record;
                            break;
                        }
                    }

                    if (returnCar == null) {
                        System.out.println("Car not found or already returned!");
                        break;
                    }

                    rentalSystem.returnCar(returnRecord);
                    break;
                case 3:
                    System.out.println("Available Cars:");
                    List<Car> availableCars = rentalSystem.getAvailableCars();
                    if (availableCars.isEmpty()) {
                        System.out.println("No available cars at the moment.");
                    } else {
                        for (Car car : availableCars) {
                            System.out.println("Car Number: " + car.getCarNumber() + ", Car Name: " + car.getName());
                        }
                    }
                    break;
                case 4:
                    System.out.print("Enter the customer's driving license number: ");
                    String customerID = scanner.nextLine();
                    Customer customer = rentalSystem.getCustomerByID(customerID);

                    if (customer == null) {
                        System.out.println("Customer not found!");
                        break;
                    }

                    rentalSystem.getRentalRecordsForCustomer(customer);
                    break;
                case 5:
                    System.out.println("Extend Return Time");
                    System.out.print("Enter the customer's driving license number: ");
                    String extendCustomerID = scanner.nextLine();
                    Customer extendCustomer = rentalSystem.getCustomerByID(extendCustomerID);

                    if (extendCustomer == null) {
                        System.out.println("Customer not found!");
                        break;
                    }

                    List<RentalRecord> extendCustomerRentalRecords = rentalSystem.getRentalRecordsForCustomer(extendCustomer);
                    System.out.print("Enter the car number to extend return time: ");
                    String extendCarNumber = scanner.nextLine();
                    Car extendCar = null;
                    RentalRecord extendRecord = null;

                    for (RentalRecord record : extendCustomerRentalRecords) {
                        if (record.getCar().getCarNumber().equals(extendCarNumber)) {
                            extendCar = record.getCar();
                            extendRecord = record;
                            break;
                        }
                    }

                    if (extendCar == null) {
                        System.out.println("Car not found or not rented by the customer!");
                        break;
                    }

                    if (extendCar.isAvailable()) {
                        System.out.println("Car already returned. Extension not possible.");
                    } else {
                        System.out.print("Enter the new return date (yyyy-MM-dd): ");
                        String newReturnDateStr = scanner.nextLine();
                        SimpleDateFormat dateFormatt = new SimpleDateFormat("yyyy-MM-dd");
                        Date newReturnDate;

                        try {
                            newReturnDate = dateFormatt.parse(newReturnDateStr);

                            if (newReturnDate.before(extendRecord.getEndDate())) {
                                System.out.println("Invalid extension date. New return date must be after the current return date.");
                            } else {
                                rentalSystem.extendReturnTime(extendRecord, newReturnDate);
                            }
                        } catch (ParseException e) {
                            System.out.println("Invalid date format! Please enter dates in yyyy-MM-dd format.");
                        }
                    }

                    break;

                case 6:
                    exit = true;
                    System.out.println("Exiting Car Rental System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}
