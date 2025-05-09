package com.eventapp.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
        
        if ("/vendors".equals(path)) {
            // List all vendors
            handleListVendors(request, response);
        } else if ("/vendor-dashboard".equals(path)) {
            // Vendor dashboard - requires vendor access
            if (checkLogin(request, response) && checkVendorAccess(request, response)) {
                handleVendorDashboard(request, response);
            }
        } else if (path.startsWith("/vendor/")) {
            // Display individual vendor details
            handleViewVendor(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        
        if ("/add-to-cart".equals(path)) {
            handleAddToCart(request, response);
        } else if ("/remove-from-cart".equals(path)) {
            handleRemoveFromCart(request, response);
        } else if ("/vendor-dashboard".equals(path)) {
            // Handle vendor updates
            if (checkLogin(request, response) && checkVendorAccess(request, response)) {
                handleUpdateVendorProfile(request, response);
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
     * Handles updating vendor profile information.
     */
    private void handleUpdateVendorProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getLoggedInUser(request);
        
        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String description = request.getParameter("description");
        String contactInfo = request.getParameter("contactInfo");
        String baseCostStr = request.getParameter("baseCost");
        String imageUrl = request.getParameter("imageUrl");
        
        if (name != null && !name.isEmpty() && type != null && !type.isEmpty() && 
            description != null && !description.isEmpty() && baseCostStr != null && !baseCostStr.isEmpty()) {
            
            try {
                double baseCost = Double.parseDouble(baseCostStr);
                
                Vendor existing = getVendorByUserId(user.getId());
                if (existing == null) {
                    // Create new vendor profile
                    createVendorProfile(user.getId(), name, type, description, contactInfo, baseCost, imageUrl);
                    request.setAttribute("successMessage", "Vendor profile created successfully!");
                } else {
                    // Update existing profile
                    updateVendorProfile(existing.getId(), name, type, description, contactInfo, baseCost, imageUrl);
                    request.setAttribute("successMessage", "Vendor profile updated successfully!");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Base cost must be a valid number.");
            }
        } else {
            request.setAttribute("errorMessage", "All fields are required.");
        }
        
        handleVendorDashboard(request, response);
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
            String sql = "SELECT * FROM vendors ORDER BY business_name";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Vendor vendor = extractVendorFromResultSet(resultSet);
                vendors.add(vendor);
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
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
            String sql = "SELECT * FROM vendors WHERE business_name LIKE ? OR description LIKE ? ORDER BY business_name";
            statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Vendor vendor = extractVendorFromResultSet(resultSet);
                vendors.add(vendor);
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
            String sql = "SELECT DISTINCT service_type FROM vendors ORDER BY service_type";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                types.add(resultSet.getString("service_type"));
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
     * Creates a new vendor profile.
     */
    private int createVendorProfile(int userId, String name, String type, String description, 
                                   String contactInfo, double baseCost, String imageUrl) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "INSERT INTO vendors (user_id, business_name, type, description, contact_info, base_cost, image_url) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            statement.setString(2, name);
            statement.setString(3, type);
            statement.setString(4, description);
            statement.setString(5, contactInfo);
            statement.setDouble(6, baseCost);
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
     * Updates an existing vendor profile.
     */
    private boolean updateVendorProfile(int vendorId, String name, String type, String description, 
                                      String contactInfo, double baseCost, String imageUrl) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "UPDATE vendors SET business_name = ?, service_type = ?, description = ?, " +
                         "contact_info = ?, base_cost = ?, image_url = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, type);
            statement.setString(3, description);
            statement.setString(4, contactInfo);
            statement.setDouble(5, baseCost);
            statement.setString(6, imageUrl);
            statement.setInt(7, vendorId);
            
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
        try {
            vendor.setId(rs.getInt("id"));
            vendor.setName(rs.getString("business_name"));
            
            // Check column names for type - try both service_type and type
            try {
                vendor.setType(rs.getString("service_type"));
            } catch (SQLException e) {
                vendor.setType(rs.getString("type"));
            }
            
            vendor.setDescription(rs.getString("description"));
            
            // Handle contact info based on available columns
            try {
                String contactEmail = rs.getString("contact_email");
                String contactPhone = rs.getString("contact_phone");
                vendor.setContactInfo(contactEmail + " | " + contactPhone);
            } catch (SQLException e) {
                // If specific contact columns don't exist, try the combined column
                vendor.setContactInfo(rs.getString("contact_info"));
            }
            
            // Check column names for price - try both price and base_cost
            try {
                vendor.setBaseCost(rs.getDouble("price"));
            } catch (SQLException e) {
                vendor.setBaseCost(rs.getDouble("base_cost"));
            }
            
            // Fix image URL by ensuring it has the correct context path
            String imageUrl = rs.getString("image_url");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // If the URL doesn't start with http:// or https://, it's a relative URL
                if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                    // If the URL already starts with a slash, use as is; otherwise add a slash
                    if (!imageUrl.startsWith("/")) {
                        imageUrl = "/" + imageUrl;
                    }
                    // The context path will be prepended by the JSP using ${pageContext.request.contextPath}
                    vendor.setImageUrl(imageUrl);
                } else {
                    // If it's an absolute URL, use as is
                    vendor.setImageUrl(imageUrl);
                }
            } else {
                vendor.setImageUrl(null);
            }            
        } catch (SQLException e) {
            System.out.println("Error extracting vendor from result set: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return vendor;
    }
}