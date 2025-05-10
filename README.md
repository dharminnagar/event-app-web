# Eventify - Event Planning Platform

## Overview
Eventify is a comprehensive web-based platform that connects event planners with various service providers for events like weddings, corporate gatherings, and birthday parties. The platform allows users to discover, compare, and book vendors offering services such as venues, catering, photography, decorations, and more.

## Features

### For Event Organizers
- **User Authentication**: Register and login to manage your event planning
- **Vendor Discovery**: Browse through a wide range of vendors filtered by service type, location, and budget
- **Shopping Cart**: Add desired vendors to cart before finalizing booking
- **Booking Management**: View, manage, and cancel bookings
- **User Profile**: Update personal information and manage account settings
- **Booking Details**: View comprehensive information about each booking including selected vendors

### For Vendors
- **Vendor Dashboard**: Dedicated interface for vendors to manage their services
- **Profile Management**: Create and update business profile information
- **Service Management**: Add, edit, or remove service offerings
- **Booking Overview**: Track and manage upcoming bookings
- **Analytics**: Get insights about service performance (coming soon)

### General Features
- **Responsive Design**: Built with Bootstrap 5 for a seamless experience across all devices
- **Interactive UI**: Modern interface with intuitive navigation and user-friendly forms
- **Data Persistence**: All booking and vendor information stored in database

## Technology Stack

### Backend
- **Java EE (Jakarta EE)**: Core backend technology
- **Servlets**: Request handling and controller layer
- **JSP**: Dynamic view generation
- **JSTL**: Tag library for JSP pages
- **MySQL**: Database for storing vendor, user, and booking information
- **JDBC**: Database connectivity

### Frontend
- **HTML5/CSS3**: Markup and styling
- **Bootstrap 5**: Responsive design framework
- **JavaScript**: Client-side functionality
- **Font Awesome**: Icon library
- **Google Fonts**: Typography

### Build & Deployment
- **Maven**: Dependency management and build tool
- **Apache Tomcat**: Servlet container for deployment

## Project Structure
```
event-app-web/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/eventapp/
│   │   │       ├── controller/    # Servlet controllers
│   │   │       ├── dao/           # Database access objects
│   │   │       ├── exception/     # Custom exceptions
│   │   │       ├── filter/        # Request filters
│   │   │       ├── interfaces/    # Interfaces
│   │   │       ├── model/         # Entity classes
│   │   │       ├── service/       # Business logic
│   │   │       └── util/          # Utility classes
│   │   ├── resources/             # Database scripts
│   │   └── webapp/                # Web resources
│   │       ├── WEB-INF/
│   │       │   ├── views/         # JSP pages
│   │       │   ├── web.xml        # Web configuration
│   │       │   └── glassfish-web.xml
│   │       └── index.jsp          # Home page
├── target/                        # Build output
├── lib/                           # External libraries
└── pom.xml                        # Maven configuration
```

## Setup and Installation

### Prerequisites
- JDK 17 or higher
- Apache Maven 3.6+
- MySQL 8.0+ or compatible database
- Apache Tomcat 10.0+ or other Jakarta EE 9+ compatible servlet container

### Database Setup
1. Create a new MySQL database
2. Run the database script in `src/main/resources/database_schema.sql` to create tables
3. (Optional) Run `src/main/resources/db_update.sql` if provided for any updates

### Configuration
1. Update database connection settings in the appropriate configuration file (details may vary based on your specific setup)
2. Configure context path in `WEB-INF/glassfish-web.xml` if needed

### Build and Deploy
1. Clone the repository
   ```bash
   git clone <repository-url>
   cd event-app-web
   ```

2. Build the project with Maven
   ```bash
   mvn clean package
   ```

3. Deploy the generated WAR file to your servlet container
   ```bash
   # Example for Tomcat (copy the WAR file to the webapps directory)
   cp target/event-aggregator-web.war $TOMCAT_HOME/webapps/
   ```

4. Start your servlet container if not already running
   ```bash
   # Example for Tomcat
   $TOMCAT_HOME/bin/startup.sh
   ```

5. Access the application at `http://localhost:8080/event-aggregator-web/`

## Usage

### For Event Planners
1. Register for an account or log in if you already have one
2. Browse vendors by type, location, or other filters
3. Add desired vendors to your cart
4. Proceed to checkout and fill in event details
5. Complete the booking process
6. Manage your bookings in the "My Bookings" section

### For Vendors
1. Register for a vendor account
2. Complete your business profile from the Vendor Dashboard
3. Add your services with descriptions, pricing, and images
4. Manage incoming bookings and track your business performance

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## License
This project is licensed under the [MIT License](LICENSE).

## Acknowledgments
- Bootstrap for the responsive UI framework
- Font Awesome for icons
- All contributors who have helped with the development