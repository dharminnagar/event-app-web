package com.eventapp.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.eventapp.model.User;
import com.eventapp.model.Vendor;
import com.eventapp.model.Cart;
import com.eventapp.util.DatabaseUtil;

/**
 * Servlet for handling vendor service operations.
 */
@WebServlet(urlPatterns = {"/vendors", "/vendor/*", "/add-to-cart", "/remove-from-cart", "/vendor-dashboard"})
public class VendorServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String action = request.getParameter("action");
        
        if ("/vendors".equals(path)) {
            // List all vendors
            handleListVendors(request, response);
        } else if ("/vendor-dashboard".equals(path)) {
            // Vendor dashboard - requires vendor access
            if (checkLogin(request, response) && checkVendorAccess(request, response)) {
                handleVendorDashboard(request, response);
            }
        } else if (path.startsWith("/vendor/")) {
            // Check if this is the dashboard route
            if (pathInfo != null && pathInfo.equals("/dashboard")) {
                // New vendor dashboard route - requires vendor access
                if (checkLogin(request, response) && checkVendorAccess(request, response)) {
                    if ("getService".equals(action)) {
                        handleGetServiceDetails(request, response);
                    } else {
                        handleVendorDashboardNew(request, response);
                    }
                }
            } else {
                // Display individual vendor details
                handleViewVendor(request, response);
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String action = request.getParameter("action");
        
        if ("/add-to-cart".equals(path)) {
            handleAddToCart(request, response);
        } else if ("/remove-from-cart".equals(path)) {
            handleRemoveFromCart(request, response);
        } else if ("/vendor-dashboard".equals(path)) {
            // Handle vendor updates
            if (checkLogin(request, response) && checkVendorAccess(request, response)) {
                handleUpdateVendorProfile(request, response);
            }
        } else if (path.startsWith("/vendor/") && pathInfo != null && pathInfo.equals("/dashboard")) {
            // Handle actions for the new vendor dashboard
            if (checkLogin(request, response) && checkVendorAccess(request, response)) {
                if ("updateProfile".equals(action)) {
                    handleUpdateVendorProfile(request, response);
                } else if ("addService".equals(action)) {
                    handleAddService(request, response);
                } else if ("editService".equals(action)) {
                    handleEditService(request, response);
                } else if ("deleteService".equals(action)) {
                    handleDeleteService(request, response);
                } else {
                    // Default action
                    handleVendorDashboardNew(request, response);
                }
            }
        }
    }
    
    /**
     * Handles listing all vendors.
     */
    private void handleListVendors(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get filter parameters
        String typeFilter = request.getParameter("type");
        String searchTerm = request.getParameter("search");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String sortBy = request.getParameter("sort");
        
        // Parse price parameters if they exist
        Double minPrice = null;
        Double maxPrice = null;
    
        
        try {
            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                minPrice = Double.parseDouble(minPriceStr);
            }
        } catch (NumberFormatException e) {
            // Invalid min price, ignore filter
        }
        
        try {
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceStr);
            }
        } catch (NumberFormatException e) {
            // Invalid max price, ignore filter
        }
        
        // Get vendors with initial filters
        List<Vendor> vendors;
        if (typeFilter != null && !typeFilter.isEmpty()) {
            vendors = getVendorsByType(typeFilter);
        } else if (searchTerm != null && !searchTerm.isEmpty()) {
            vendors = searchVendors(searchTerm);
        } else {
            vendors = getAllVendors();
        }
        
        // Apply additional filters (price range) in memory
        if (minPrice != null || maxPrice != null) {
            List<Vendor> filteredVendors = new ArrayList<>();
            
            for (Vendor vendor : vendors) {
                boolean matchesFilter = true;
                
                // Apply min price filter if specified
                if (minPrice != null && vendor.getBaseCost() < minPrice) {
                    matchesFilter = false;
                }
                
                // Apply max price filter if specified
                if (maxPrice != null && vendor.getBaseCost() > maxPrice) {
                    matchesFilter = false;
                }
                
                if (matchesFilter) {
                    filteredVendors.add(vendor);
                }
            }
            
            vendors = filteredVendors;
        }
        
        // Apply sorting
        if (sortBy != null) {
            switch (sortBy) {
                case "name_asc":
                    vendors.sort((v1, v2) -> v1.getName().compareToIgnoreCase(v2.getName()));
                    break;
                case "name_desc":
                    vendors.sort((v1, v2) -> v2.getName().compareToIgnoreCase(v1.getName()));
                    break;
                case "price_asc":
                    vendors.sort((v1, v2) -> Double.compare(v1.getBaseCost(), v2.getBaseCost()));
                    break;
                case "price_desc":
                    vendors.sort((v1, v2) -> Double.compare(v2.getBaseCost(), v1.getBaseCost()));
                    break;
                default:
                    // Default sorting (by name)
                    vendors.sort((v1, v2) -> v1.getName().compareToIgnoreCase(v2.getName()));
            }
        }
        
        // Get list of distinct vendor types for filter dropdown
        List<String> vendorTypes = getVendorTypes();
        
        // Set attributes for the view
        request.setAttribute("vendors", vendors);
        request.setAttribute("vendorTypes", vendorTypes);
        request.setAttribute("selectedType", typeFilter);
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("minPrice", minPriceStr);
        request.setAttribute("maxPrice", maxPriceStr);
        request.setAttribute("currentSort", sortBy);
        request.getRequestDispatcher("/WEB-INF/views/vendors.jsp").forward(request, response);
    }
    
    /**
     * Handles displaying a single vendor's details.
     */
    private void handleViewVendor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int vendorId = Integer.parseInt(pathInfo.substring(1));
                Vendor vendor = getVendorById(vendorId);
                
                if (vendor != null) {
                    request.setAttribute("vendor", vendor);
                    request.getRequestDispatcher("/WEB-INF/views/vendor-details.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // Invalid vendor ID format
            }
        }
        
        // Vendor not found
        response.sendRedirect(request.getContextPath() + "/vendors");
    }
    
    /**
     * Handles adding a vendor service to the cart.
     */
    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkLogin(request, response)) {
            return;
        }
        
        String vendorIdStr = request.getParameter("vendorId");
        if (vendorIdStr != null && !vendorIdStr.isEmpty()) {
            try {
                int vendorId = Integer.parseInt(vendorIdStr);
                Vendor vendor = getVendorById(vendorId);
                
                if (vendor != null) {
                    HttpSession session = request.getSession();
                    Cart cart = (Cart) session.getAttribute("cart");
                    
                    if (cart == null) {
                        User user = getLoggedInUser(request);
                        cart = new Cart(user.getId());
                        session.setAttribute("cart", cart);
                    }
                    
                    // Add to session cart
                    cart.addVendor(vendorId, vendor.getBaseCost());
                    
                    // Add to database
                    com.eventapp.dao.CartDAO cartDAO = new com.eventapp.dao.CartDAO();
                    cartDAO.addToCart(cart.getUserId(), vendorId);
                    
                    request.setAttribute("successMessage", vendor.getName() + " added to your cart.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid vendor selection.");
            }
        }
        
        // Redirect back to the vendors page
        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : request.getContextPath() + "/vendors");
    }
    
    /**
     * Handles removing a vendor service from the cart.
     */
    private void handleRemoveFromCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkLogin(request, response)) {
            return;
        }
        
        String vendorIdStr = request.getParameter("vendorId");
        if (vendorIdStr != null && !vendorIdStr.isEmpty()) {
            try {
                int vendorId = Integer.parseInt(vendorIdStr);
                Vendor vendor = getVendorById(vendorId);
                
                if (vendor != null) {
                    HttpSession session = request.getSession();
                    Cart cart = (Cart) session.getAttribute("cart");
                    
                    if (cart != null) {
                        // Remove from session cart
                        cart.removeVendor(vendorId, vendor.getBaseCost());
                        
                        // Remove from database
                        com.eventapp.dao.CartDAO cartDAO = new com.eventapp.dao.CartDAO();
                        cartDAO.removeFromCart(cart.getUserId(), vendorId);
                        
                        request.setAttribute("successMessage", vendor.getName() + " removed from your cart.");
                    }
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid vendor selection.");
            }
        }
        
        // Redirect back to the cart page
        response.sendRedirect(request.getContextPath() + "/cart");
    }
    
    /**
     * Handles the vendor dashboard view.
     */
    private void handleVendorDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getLoggedInUser(request);
        Vendor vendorProfile = getVendorByUserId(user.getId());
        
        if (vendorProfile == null) {
            // New vendor - show profile creation form
            request.getRequestDispatcher("/WEB-INF/views/vendor-create-profile.jsp").forward(request, response);
        } else {
            // Existing vendor - show dashboard with profile and bookings
            request.setAttribute("vendorProfile", vendorProfile);
            request.getRequestDispatcher("/WEB-INF/views/vendor-dashboard.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles the new vendor dashboard view at /vendor/dashboard.
     * This dashboard includes service management functionality.
     */
    private void handleVendorDashboardNew(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getLoggedInUser(request);
        System.out.println("Dashboard requested for user ID: " + user.getId());
        
        Vendor vendorProfile = getVendorByUserId(user.getId());
        
        if (vendorProfile == null) {
            // New vendor - show profile creation form
            System.out.println("No vendor profile found for user ID: " + user.getId());
            request.getRequestDispatcher("/WEB-INF/views/vendor-create-profile.jsp").forward(request, response);
        } else {
            // Existing vendor - show dashboard with profile, services, and bookings
            System.out.println("Found vendor profile: " + vendorProfile.getId() + " - " + vendorProfile.getName());
            request.setAttribute("vendorProfile", vendorProfile);
            
            // Get vendor services
            List<com.eventapp.model.VendorService> services = getVendorServices(vendorProfile.getId());
            System.out.println("Found " + services.size() + " vendor services");
            request.setAttribute("vendorServices", services);
            
            // Get vendor bookings
            List<com.eventapp.model.Booking> bookings = getVendorBookings(vendorProfile.getId());
            System.out.println("Found " + bookings.size() + " vendor bookings");
            request.setAttribute("vendorBookings", bookings);
            
            // Forward to the dashboard view
            request.getRequestDispatcher("/WEB-INF/views/vendor-dashboard.jsp").forward(request, response);
        }
    }

    /**
     * Handles adding a new vendor service.
     */
    private void handleAddService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getLoggedInUser(request);
        Vendor vendor = getVendorByUserId(user.getId());
        
        if (vendor == null) {
            request.setAttribute("errorMessage", "You need to create a vendor profile first.");
            response.sendRedirect(request.getContextPath() + "/vendor/dashboard");
            return;
        }
        
        String serviceName = request.getParameter("serviceName");
        String serviceType = request.getParameter("serviceType");
        String serviceDescription = request.getParameter("serviceDescription");
        String servicePriceStr = request.getParameter("servicePrice");
        String serviceLocation = request.getParameter("serviceLocation");
        String serviceImageUrl = request.getParameter("serviceImageUrl");
        
        if (serviceName != null && !serviceName.isEmpty() && 
            serviceType != null && !serviceType.isEmpty() && 
            serviceDescription != null && !serviceDescription.isEmpty() && 
            servicePriceStr != null && !servicePriceStr.isEmpty()) {
            
            try {
                double servicePrice = Double.parseDouble(servicePriceStr);
                
                // Add new service
                int serviceId = addVendorService(vendor.getId(), serviceName, serviceDescription, serviceType, 
                                                 servicePrice, serviceLocation, serviceImageUrl);
                
                if (serviceId > 0) {
                    request.setAttribute("successMessage", "Service added successfully!");
                } else {
                    request.setAttribute("errorMessage", "Failed to add service. Please try again.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Service price must be a valid number.");
            }
        } else {
            request.setAttribute("errorMessage", "All required service fields must be filled out.");
        }
        
        // Send back to the dashboard
        handleVendorDashboardNew(request, response);
    }
    
    /**
     * Handles editing an existing vendor service.
     */
    private void handleEditService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getLoggedInUser(request);
        Vendor vendor = getVendorByUserId(user.getId());
        
        if (vendor == null) {
            request.setAttribute("errorMessage", "You need to create a vendor profile first.");
            response.sendRedirect(request.getContextPath() + "/vendor/dashboard");
            return;
        }
        
        String serviceIdStr = request.getParameter("serviceId");
        String serviceName = request.getParameter("serviceName");
        String serviceType = request.getParameter("serviceType");
        String serviceDescription = request.getParameter("serviceDescription");
        String servicePriceStr = request.getParameter("servicePrice");
        String serviceLocation = request.getParameter("serviceLocation");
        String serviceImageUrl = request.getParameter("serviceImageUrl");
        
        if (serviceIdStr != null && !serviceIdStr.isEmpty() &&
            serviceName != null && !serviceName.isEmpty() && 
            serviceType != null && !serviceType.isEmpty() && 
            serviceDescription != null && !serviceDescription.isEmpty() && 
            servicePriceStr != null && !servicePriceStr.isEmpty()) {
            
            try {
                int serviceId = Integer.parseInt(serviceIdStr);
                double servicePrice = Double.parseDouble(servicePriceStr);
                
                // Verify that this service belongs to this vendor
                com.eventapp.model.VendorService existingService = getServiceById(serviceId);
                
                if (existingService != null && existingService.getVendorId() == vendor.getId()) {
                    // Update the service
                    boolean updated = updateVendorService(serviceId, serviceName, serviceDescription, 
                                                        serviceType, servicePrice, serviceLocation, serviceImageUrl);
                    
                    if (updated) {
                        request.setAttribute("successMessage", "Service updated successfully!");
                    } else {
                        request.setAttribute("errorMessage", "Failed to update service. Please try again.");
                    }
                } else {
                    request.setAttribute("errorMessage", "You do not have permission to edit this service.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid service ID or price.");
            }
        } else {
            request.setAttribute("errorMessage", "All required service fields must be filled out.");
        }
        
        // Send back to the dashboard
        handleVendorDashboardNew(request, response);
    }
    
    /**
     * Handles deleting a vendor service.
     */
    private void handleDeleteService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getLoggedInUser(request);
        Vendor vendor = getVendorByUserId(user.getId());
        
        if (vendor == null) {
            request.setAttribute("errorMessage", "You need to create a vendor profile first.");
            response.sendRedirect(request.getContextPath() + "/vendor/dashboard");
            return;
        }
        
        String serviceIdStr = request.getParameter("serviceId");
        
        if (serviceIdStr != null && !serviceIdStr.isEmpty()) {
            try {
                int serviceId = Integer.parseInt(serviceIdStr);
                
                // Verify that this service belongs to this vendor
                com.eventapp.model.VendorService existingService = getServiceById(serviceId);
                
                if (existingService != null && existingService.getVendorId() == vendor.getId()) {
                    // Delete the service
                    boolean deleted = deleteVendorService(serviceId);
                    
                    if (deleted) {
                        request.setAttribute("successMessage", "Service deleted successfully!");
                    } else {
                        request.setAttribute("errorMessage", "Failed to delete service. Please try again.");
                    }
                } else {
                    request.setAttribute("errorMessage", "You do not have permission to delete this service.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid service ID.");
            }
        } else {
            request.setAttribute("errorMessage", "Service ID is required.");
        }
        
        // Send back to the dashboard
        handleVendorDashboardNew(request, response);
    }
    
    /**
     * Handles retrieving service details for AJAX requests.
     */
    private void handleGetServiceDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getLoggedInUser(request);
        Vendor vendor = getVendorByUserId(user.getId());
        
        if (vendor == null) {
            sendJsonError(response, "You need to create a vendor profile first.");
            return;
        }
        
        String serviceIdStr = request.getParameter("serviceId");
        
        if (serviceIdStr != null && !serviceIdStr.isEmpty()) {
            try {
                int serviceId = Integer.parseInt(serviceIdStr);
                com.eventapp.model.VendorService service = getServiceById(serviceId);
                
                if (service != null && service.getVendorId() == vendor.getId()) {
                    // Send JSON response with service details
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    
                    // Create JSON manually since we might not have a JSON library
                    String jsonResponse = "{" +
                        "\"id\":" + service.getId() + "," +
                        "\"name\":\"" + escapeJsonString(service.getName()) + "\"," +
                        "\"description\":\"" + escapeJsonString(service.getDescription()) + "\"," +
                        "\"type\":\"" + escapeJsonString(service.getServiceType()) + "\"," +
                        "\"price\":" + service.getPrice() + "," +
                        "\"location\":\"" + escapeJsonString(service.getLocation() != null ? service.getLocation() : "") + "\"," +
                        "\"imageUrl\":\"" + escapeJsonString(service.getImageUrl() != null ? service.getImageUrl() : "") + "\"" +
                    "}";
                    
                    response.getWriter().write(jsonResponse);
                } else {
                    sendJsonError(response, "Service not found or you do not have permission to access it.");
                }
            } catch (NumberFormatException e) {
                sendJsonError(response, "Invalid service ID.");
            }
        } else {
            sendJsonError(response, "Service ID is required.");
        }
    }
    
    /**
     * Helper method to send error responses for AJAX requests.
     */
    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonError = "{\"error\":true,\"message\":\"" + escapeJsonString(message) + "\"}";
        response.getWriter().write(jsonError);
    }
    
    /**
     * Helper method to escape strings for JSON output.
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    /**
     * Adds a new vendor service.
     */
    private int addVendorService(int vendorId, String name, String description, String serviceType,
                               double price, String location, String imageUrl) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "INSERT INTO vendor_services (vendor_id, name, description, service_type, price, location, image_url) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, vendorId);
            statement.setString(2, name);
            statement.setString(3, description);
            statement.setString(4, serviceType);
            statement.setDouble(5, price);
            statement.setString(6, location);
            statement.setString(7, imageUrl);
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return -1;
    }
    
    /**
     * Updates an existing vendor service.
     */
    private boolean updateVendorService(int serviceId, String name, String description, String serviceType,
                                      double price, String location, String imageUrl) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "UPDATE vendor_services SET name = ?, description = ?, service_type = ?, " +
                         "price = ?, location = ?, image_url = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setString(3, serviceType);
            statement.setDouble(4, price);
            statement.setString(5, location);
            statement.setString(6, imageUrl);
            statement.setInt(7, serviceId);
            
            int affectedRows = statement.executeUpdate();
            return (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Deletes a vendor service.
     */
    private boolean deleteVendorService(int serviceId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "DELETE FROM vendor_services WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, serviceId);
            
            int affectedRows = statement.executeUpdate();
            return (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Gets a vendor service by ID.
     */
    private com.eventapp.model.VendorService getServiceById(int serviceId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM vendor_services WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, serviceId);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return extractVendorServiceFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     * Gets all services for a vendor.
     */
    private List<com.eventapp.model.VendorService> getVendorServices(int vendorId) {
        List<com.eventapp.model.VendorService> services = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM vendor_services WHERE vendor_id = ? ORDER BY name";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, vendorId);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                com.eventapp.model.VendorService service = extractVendorServiceFromResultSet(resultSet);
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return services;
    }
    
    /**
     * Gets all bookings for a vendor.
     */
    private List<com.eventapp.model.Booking> getVendorBookings(int vendorId) {
        List<com.eventapp.model.Booking> bookings = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT b.* FROM bookings b " +
                         "JOIN booking_vendors bv ON b.id = bv.booking_id " +
                         "WHERE bv.vendor_id = ? ORDER BY b.event_date DESC";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, vendorId);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                com.eventapp.model.Booking booking = new com.eventapp.model.Booking();
                booking.setId(resultSet.getInt("id"));
                booking.setUserId(resultSet.getInt("user_id"));
                booking.setEventDate(resultSet.getDate("event_date"));
                booking.setEventLocation(resultSet.getString("event_location"));
                booking.setStatus(resultSet.getString("status"));
                booking.setTotalCost(resultSet.getDouble("total_cost"));
                booking.setBookingDate(resultSet.getTimestamp("booking_date"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return bookings;
    }
    
    /**
     * Helper method to extract vendor service info from a result set.
     */
    private com.eventapp.model.VendorService extractVendorServiceFromResultSet(ResultSet rs) throws SQLException {
        com.eventapp.model.VendorService service = new com.eventapp.model.VendorService();
        service.setId(rs.getInt("id"));
        service.setVendorId(rs.getInt("vendor_id"));
        service.setName(rs.getString("name"));
        service.setDescription(rs.getString("description"));
        service.setServiceType(rs.getString("service_type"));
        service.setPrice(rs.getDouble("price"));
        
        try {
            service.setLocation(rs.getString("location"));
        } catch (SQLException e) {
            // Location might be null or not exist in some older schemas
            service.setLocation("");
        }
        
        try {
            service.setImageUrl(rs.getString("image_url"));
        } catch (SQLException e) {
            // Image URL might be null or not exist in some older schemas
        }
        
        try {
            service.setActive(rs.getBoolean("is_active"));
        } catch (SQLException e) {
            // Default to active if the column doesn't exist
            service.setActive(true);
        }
        
        try {
            service.setCreatedAt(rs.getTimestamp("created_at"));
            service.setUpdatedAt(rs.getTimestamp("updated_at"));
        } catch (SQLException e) {
            // Timestamps might not exist in the schema
        }
        
        // If image URL is null or empty, generate an Unsplash image based on service type
        if (service.getImageUrl() == null || service.getImageUrl().isEmpty()) {
            String serviceType = service.getServiceType().toLowerCase();
            int serviceId = service.getId();
            
            if (serviceType.contains("wedding") || serviceType.contains("marriage")) {
                service.setImageUrl("https://source.unsplash.com/random/800x600/?wedding,celebration&service=" + serviceId);
            } else if (serviceType.contains("catering") || serviceType.contains("food")) {
                service.setImageUrl("https://source.unsplash.com/random/800x600/?catering,food&service=" + serviceId);
            } else if (serviceType.contains("photography")) {
                service.setImageUrl("https://source.unsplash.com/random/800x600/?photography,camera&service=" + serviceId);
            } else if (serviceType.contains("venue") || serviceType.contains("hall")) {
                service.setImageUrl("https://source.unsplash.com/random/800x600/?venue,hall&service=" + serviceId);
            } else if (serviceType.contains("decor") || serviceType.contains("decoration")) {
                service.setImageUrl("https://source.unsplash.com/random/800x600/?decoration,event-decor&service=" + serviceId);
            } else if (serviceType.contains("music") || serviceType.contains("dj")) {
                service.setImageUrl("https://source.unsplash.com/random/800x600/?music,dj&service=" + serviceId);
            } else if (serviceType.contains("transport") || serviceType.contains("limo")) {
                service.setImageUrl("https://source.unsplash.com/random/800x600/?limousine,car&service=" + serviceId);
            } else {
                service.setImageUrl("https://source.unsplash.com/random/800x600/?event,party&service=" + serviceId);
            }
        }
        
        return service;
    }
    
    /**
     * Handles updating vendor profile information.
     */
    private void handleUpdateVendorProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getLoggedInUser(request);
        
        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String description = request.getParameter("description");
        String contactEmail = request.getParameter("contactEmail");
        String contactPhone = request.getParameter("contactPhone");
        String location = request.getParameter("location");
        String baseCostStr = request.getParameter("baseCost");
        String imageUrl = request.getParameter("imageUrl");
        
        // Combine contact information
        String contactInfo = "";
        if (contactEmail != null && !contactEmail.isEmpty()) {
            contactInfo += contactEmail;
        }
        if (contactPhone != null && !contactPhone.isEmpty()) {
            if (!contactInfo.isEmpty()) {
                contactInfo += " | ";
            }
            contactInfo += contactPhone;
        }
        
        if (name != null && !name.isEmpty() && type != null && !type.isEmpty() && 
            description != null && !description.isEmpty() && baseCostStr != null && !baseCostStr.isEmpty()) {
            
            try {
                double baseCost = Double.parseDouble(baseCostStr);
                
                Vendor existing = getVendorByUserId(user.getId());
                if (existing == null) {
                    // Create new vendor profile
                    createVendorProfile(user.getId(), name, type, description, contactEmail, contactPhone, location, baseCost, imageUrl);
                    request.setAttribute("successMessage", "Vendor profile created successfully!");
                } else {
                    // Update existing profile
                    updateVendorProfile(existing.getId(), name, type, description, contactEmail, contactPhone, location, baseCost, imageUrl);
                    request.setAttribute("successMessage", "Vendor profile updated successfully!");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Base cost must be a valid number.");
            }
        } else {
            request.setAttribute("errorMessage", "All required fields must be filled out.");
        }
        
        // Redirect back to the dashboard
        if (request.getServletPath().startsWith("/vendor/")) {
            handleVendorDashboardNew(request, response);
        } else {
            handleVendorDashboard(request, response);
        }
    }
    
    /**
     * Retrieves all vendors from the database.
     */
    private List<Vendor> getAllVendors() {
        List<Vendor> vendors = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            System.out.println("Database connection established: " + (connection != null));
            
            String sql = "SELECT * FROM vendors ORDER BY business_name";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            System.out.println("Vendor query executed, checking for results...");
            int vendorCount = 0;
            
            while (resultSet.next()) {
                Vendor vendor = extractVendorFromResultSet(resultSet);
                vendors.add(vendor);
                vendorCount++;
            }
            
            System.out.println("Found " + vendorCount + " vendors in the database");
        } catch (SQLException e) {
            System.out.println("SQL Error in getAllVendors: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(resultSet, statement, connection);
        }
        
        return vendors;
    }
    
    /**
     * Retrieves vendors filtered by type.
     */
    private List<Vendor> getVendorsByType(String type) {
        List<Vendor> vendors = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM vendors WHERE service_type = ? ORDER BY business_name";
            statement = connection.prepareStatement(sql);
            statement.setString(1, type);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Vendor vendor = extractVendorFromResultSet(resultSet);
                vendors.add(vendor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(resultSet, statement, connection);
        }
        
        return vendors;
    }
    
    /**
     * Searches for vendors by name or description.
     */
    private List<Vendor> searchVendors(String searchTerm) {
        List<Vendor> vendors = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM vendors WHERE (business_name LIKE ? OR description LIKE ? OR service_type LIKE ?) " +
                         "ORDER BY business_name";
            statement = connection.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Vendor vendor = extractVendorFromResultSet(resultSet);
                vendors.add(vendor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(resultSet, statement, connection);
        }
        
        return vendors;
    }
    
    /**
     * Retrieves distinct vendor types for filtering.
     */
    private List<String> getVendorTypes() {
        List<String> types = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT DISTINCT service_type FROM vendors WHERE is_active = true ORDER BY service_type";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                types.add(resultSet.getString("service_type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(resultSet, statement, connection);
        }
        
        return types;
    }
    
    /**
     * Retrieves a vendor by ID.
     */
    private Vendor getVendorById(int vendorId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM vendors WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, vendorId);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return extractVendorFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(resultSet, statement, connection);
        }
        
        return null;
    }
    
    /**
     * Retrieves a vendor by user ID (for vendor user accounts).
     */
    private Vendor getVendorByUserId(int userId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM vendors WHERE user_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return extractVendorFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(resultSet, statement, connection);
        }
        
        return null;
    }
    
    /**
     * Creates a new vendor profile.
     */
    private int createVendorProfile(int userId, String name, String type, String description, 
                                   String contactEmail, String contactPhone, String location, double baseCost, String imageUrl) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "INSERT INTO vendors (user_id, business_name, service_type, description, " +
                         "contact_email, contact_phone, location, base_cost, image_url, is_active) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, true)";
            statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            statement.setString(2, name);
            statement.setString(3, type);
            statement.setString(4, description);
            statement.setString(5, contactEmail);
            statement.setString(6, contactPhone);
            statement.setString(7, location);
            statement.setDouble(8, baseCost);
            statement.setString(9, imageUrl);
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return -1;
    }
    
    /**
     * Updates an existing vendor profile.
     */
    private boolean updateVendorProfile(int vendorId, String name, String type, String description, 
                                     String contactEmail, String contactPhone, String location, double baseCost, String imageUrl) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "UPDATE vendors SET business_name = ?, service_type = ?, description = ?, " +
                        "contact_email = ?, contact_phone = ?, location = ?, base_cost = ?, image_url = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, type);
            statement.setString(3, description);
            statement.setString(4, contactEmail);
            statement.setString(5, contactPhone);
            statement.setString(6, location);
            statement.setDouble(7, baseCost);
            statement.setString(8, imageUrl);
            statement.setInt(9, vendorId);
            
            int affectedRows = statement.executeUpdate();
            return (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Helper method to extract a vendor from a result set.
     */
    private Vendor extractVendorFromResultSet(ResultSet rs) throws SQLException {
        Vendor vendor = new Vendor();
        vendor.setId(rs.getInt("id"));
        vendor.setUserId(rs.getInt("user_id"));
        vendor.setName(rs.getString("business_name"));
        
        // Check column names for type - some DBs might use service_type or just type
        try {
            vendor.setType(rs.getString("service_type"));
        } catch (SQLException e) {
            try {
                vendor.setType(rs.getString("type"));
            } catch (SQLException e2) {
                vendor.setType("General");
            }
        }
        
        vendor.setDescription(rs.getString("description"));
        
        // Handle contact info fields
        try {
            String contactEmail = rs.getString("contact_email");
            String contactPhone = rs.getString("contact_phone");
            
            String contactInfo = "";
            if (contactEmail != null && !contactEmail.isEmpty()) {
                contactInfo += contactEmail;
            }
            if (contactPhone != null && !contactPhone.isEmpty()) {
                if (!contactInfo.isEmpty()) {
                    contactInfo += " | ";
                }
                contactInfo += contactPhone;
            }
            vendor.setContactInfo(contactInfo);
        } catch (SQLException e) {
            // If specific contact columns don't exist, try the combined column
            try {
                vendor.setContactInfo(rs.getString("contact_info"));
            } catch (SQLException e2) {
                vendor.setContactInfo("");
            }
        }
        
        // Get location if available
        try {
            vendor.setLocation(rs.getString("location"));
        } catch (SQLException e) {
            vendor.setLocation("");
        }
        
        // Check column names for price - some DBs might use price or base_cost
        try {
            vendor.setBaseCost(rs.getDouble("base_cost"));
        } catch (SQLException e) {
            try {
                vendor.setBaseCost(rs.getDouble("price"));
            } catch (SQLException e2) {
                vendor.setBaseCost(0.0);
            }
        }
        
        // Get image URL
        try {
            vendor.setImageUrl(rs.getString("image_url"));
        } catch (SQLException e) {
            vendor.setImageUrl(null);
        }
        
        // Get active status if available
        try {
            vendor.setActive(rs.getBoolean("is_active"));
        } catch (SQLException e) {
            vendor.setActive(true);
        }
        
        // Get rating if available, default to 0.0 if not found
        try {
            vendor.setRating(rs.getDouble("rating"));
        } catch (SQLException e) {
            vendor.setRating(0.0);
        }
        
        // If image URL is null or empty, generate a placeholder based on vendor type
        if (vendor.getImageUrl() == null || vendor.getImageUrl().isEmpty()) {
            String vendorType = vendor.getType().toLowerCase();
            int vendorId = vendor.getId();
            
            if (vendorType.contains("catering")) {
                vendor.setImageUrl("https://source.unsplash.com/random/800x600/?catering,food&vendor=" + vendorId);
            } else if (vendorType.contains("photography")) {
                vendor.setImageUrl("https://source.unsplash.com/random/800x600/?photography,camera&vendor=" + vendorId);
            } else if (vendorType.contains("venue")) {
                vendor.setImageUrl("https://source.unsplash.com/random/800x600/?venue,event-hall&vendor=" + vendorId);
            } else if (vendorType.contains("decor")) {
                vendor.setImageUrl("https://source.unsplash.com/random/800x600/?decoration,event-decor&vendor=" + vendorId);
            } else if (vendorType.contains("music") || vendorType.contains("dj")) {
                vendor.setImageUrl("https://source.unsplash.com/random/800x600/?dj,music&vendor=" + vendorId);
            } else {
                vendor.setImageUrl("https://source.unsplash.com/random/800x600/?event,celebration&vendor=" + vendorId);
            }
        }
        
        return vendor;
    }
    
    /**
     * Helper method to close database resources.
     */
    private void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}