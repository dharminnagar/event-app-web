<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Vendor Dashboard" />
</jsp:include>

<div class="container py-5">
    <!-- Status Messages -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i>${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <div class="row">
        <div class="col-lg-3">
            <div class="card shadow-sm mb-4">
                <div class="card-body text-center">
                    <div class="mb-3">
                        <c:choose>
                            <c:when test="${not empty vendorProfile.imageUrl}">
                                <img src="${vendorProfile.imageUrl}" class="rounded-circle img-thumbnail" style="width: 120px; height: 120px; object-fit: cover;" alt="${vendorProfile.name}">
                            </c:when>
                            <c:otherwise>
                                <div class="bg-light rounded-circle d-inline-flex align-items-center justify-content-center" style="width: 120px; height: 120px;">
                                    <i class="fas fa-store fa-4x text-secondary"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <h4 class="mb-1">${vendorProfile.name}</h4>
                    <p class="text-muted">${vendorProfile.type}</p>
                </div>
                <div class="list-group list-group-flush">
                    <a href="#profile" class="list-group-item list-group-item-action active" data-bs-toggle="list">
                        <i class="fas fa-user-circle me-2"></i>Profile
                    </a>
                    <a href="#services" class="list-group-item list-group-item-action" data-bs-toggle="list">
                        <i class="fas fa-clipboard-list me-2"></i>Services
                    </a>
                    <a href="#bookings" class="list-group-item list-group-item-action" data-bs-toggle="list">
                        <i class="fas fa-calendar-alt me-2"></i>Bookings
                    </a>
                    <a href="#analytics" class="list-group-item list-group-item-action" data-bs-toggle="list">
                        <i class="fas fa-chart-bar me-2"></i>Analytics
                    </a>
                </div>
            </div>
            
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Quick Stats</h5>
                    <div class="mb-3">
                        <div class="d-flex justify-content-between mb-1">
                            <span>Total Bookings</span>
                            <span class="fw-bold">${vendorBookings.size()}</span>
                        </div>
                        <div class="d-flex justify-content-between mb-1">
                            <span>Active Services</span>
                            <span class="fw-bold">${vendorServices.size()}</span>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span>Rating</span>
                            <span class="fw-bold text-warning">
                                <i class="fas fa-star"></i> ${vendorProfile.rating > 0 ? vendorProfile.rating : 'N/A'}
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-lg-9">
            <div class="tab-content">
                <!-- Profile Tab -->
                <div class="tab-pane fade show active" id="profile">
                    <div class="card shadow-sm">
                        <div class="card-header bg-white">
                            <h4 class="mb-0">Vendor Profile</h4>
                        </div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/vendor/dashboard" method="post">
                                <input type="hidden" name="action" value="updateProfile">
                                
                                <div class="mb-3">
                                    <label for="name" class="form-label">Business Name</label>
                                    <input type="text" class="form-control" id="name" name="name" value="${vendorProfile.name}" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="type" class="form-label">Service Type</label>
                                    <select class="form-select" id="type" name="type" required>
                                        <option value="">Select a service type</option>
                                        <option value="Venue" ${vendorProfile.type eq 'Venue' ? 'selected' : ''}>Venue</option>
                                        <option value="Catering" ${vendorProfile.type eq 'Catering' ? 'selected' : ''}>Catering</option>
                                        <option value="Photography" ${vendorProfile.type eq 'Photography' ? 'selected' : ''}>Photography</option>
                                        <option value="Decoration" ${vendorProfile.type eq 'Decoration' ? 'selected' : ''}>Decoration</option>
                                        <option value="Entertainment" ${vendorProfile.type eq 'Entertainment' ? 'selected' : ''}>Entertainment</option>
                                        <option value="Transport" ${vendorProfile.type eq 'Transport' ? 'selected' : ''}>Transport</option>
                                        <option value="Event Planning" ${vendorProfile.type eq 'Event Planning' ? 'selected' : ''}>Event Planning</option>
                                        <option value="Other" ${vendorProfile.type eq 'Other' ? 'selected' : ''}>Other</option>
                                    </select>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="description" class="form-label">Business Description</label>
                                    <textarea class="form-control" id="description" name="description" rows="4" required>${vendorProfile.description}</textarea>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="contactInfo" class="form-label">Contact Information</label>
                                    <input type="text" class="form-control" id="contactInfo" name="contactInfo" value="${vendorProfile.contactInfo}" required>
                                    <div class="form-text">Format: email | phone</div>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="baseCost" class="form-label">Base Cost ($)</label>
                                    <input type="number" class="form-control" id="baseCost" name="baseCost" value="${vendorProfile.baseCost}" step="0.01" min="0" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="imageUrl" class="form-label">Image URL</label>
                                    <input type="url" class="form-control" id="imageUrl" name="imageUrl" value="${vendorProfile.imageUrl}">
                                    <div class="form-text">Leave blank for an automatic image based on service type</div>
                                </div>
                                
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary">Update Profile</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                
                <!-- Services Tab -->
                <div class="tab-pane fade" id="services">
                    <div class="card shadow-sm">
                        <div class="card-header bg-white d-flex justify-content-between align-items-center">
                            <h4 class="mb-0">Manage Services</h4>
                            <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addServiceModal">
                                <i class="fas fa-plus me-2"></i>Add Service
                            </button>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty vendorServices}">
                                    <div class="table-responsive">
                                        <table class="table table-hover align-middle">
                                            <thead class="table-light">
                                                <tr>
                                                    <th>Service</th>
                                                    <th>Type</th>
                                                    <th>Price</th>
                                                    <th>Status</th>
                                                    <th>Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="service" items="${vendorServices}">
                                                    <tr>
                                                        <td>
                                                            <div class="d-flex align-items-center">
                                                                <c:if test="${not empty service.imageUrl}">
                                                                    <img src="${service.imageUrl}" class="rounded me-3" width="48" height="48" style="object-fit: cover;" alt="${service.name}">
                                                                </c:if>
                                                                <div>
                                                                    <h6 class="mb-0">${service.name}</h6>
                                                                    <small class="text-muted">${service.description.length() > 50 ? service.description.substring(0, 50).concat('...') : service.description}</small>
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>${service.serviceType}</td>
                                                        <td>$<fmt:formatNumber value="${service.price}" pattern="#,##0.00"/></td>
                                                        <td><span class="badge bg-success">Active</span></td>
                                                        <td>
                                                            <div class="btn-group btn-group-sm">
                                                                <button type="button" class="btn btn-outline-primary" onclick="editService('${service.id}')">
                                                                    <i class="fas fa-edit"></i>
                                                                </button>
                                                                <button type="button" class="btn btn-outline-danger" onclick="confirmDeleteService('${service.id}', '${service.name}')">
                                                                    <i class="fas fa-trash"></i>
                                                                </button>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-center py-5">
                                        <div class="mb-3">
                                            <i class="fas fa-clipboard-list fa-4x text-muted"></i>
                                        </div>
                                        <h5>No services yet</h5>
                                        <p class="text-muted">Click "Add Service" to create your first service offering.</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                
                <!-- Bookings Tab -->
                <div class="tab-pane fade" id="bookings">
                    <div class="card shadow-sm">
                        <div class="card-header bg-white">
                            <h4 class="mb-0">Upcoming Bookings</h4>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty vendorBookings}">
                                    <div class="table-responsive">
                                        <table class="table table-hover align-middle">
                                            <thead class="table-light">
                                                <tr>
                                                    <th>Booking #</th>
                                                    <th>Event Date</th>
                                                    <th>Location</th>
                                                    <th>Cost</th>
                                                    <th>Status</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="booking" items="${vendorBookings}">
                                                    <tr>
                                                        <td>#${booking.id}</td>
                                                        <td><fmt:formatDate value="${booking.eventDate}" pattern="MMM dd, yyyy" /></td>
                                                        <td>${booking.eventLocation}</td>
                                                        <td>$<fmt:formatNumber value="${booking.totalCost}" pattern="#,##0.00"/></td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${booking.status eq 'CONFIRMED'}">
                                                                    <span class="badge bg-success">Confirmed</span>
                                                                </c:when>
                                                                <c:when test="${booking.status eq 'PENDING'}">
                                                                    <span class="badge bg-warning">Pending</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge bg-danger">Cancelled</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-center py-5">
                                        <div class="mb-3">
                                            <i class="fas fa-calendar-alt fa-4x text-muted"></i>
                                        </div>
                                        <h5>No bookings yet</h5>
                                        <p class="text-muted">Bookings will appear here when customers book your services.</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                
                <!-- Analytics Tab -->
                <div class="tab-pane fade" id="analytics">
                    <div class="card shadow-sm">
                        <div class="card-header bg-white">
                            <h4 class="mb-0">Analytics & Insights</h4>
                        </div>
                        <div class="card-body">
                            <div class="text-center py-5">
                                <div class="mb-3">
                                    <i class="fas fa-chart-bar fa-4x text-muted"></i>
                                </div>
                                <h5>Analytics Coming Soon</h5>
                                <p class="text-muted">Detailed analytics will be available once you have bookings.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Add Service Modal -->
<div class="modal fade" id="addServiceModal" tabindex="-1" aria-labelledby="addServiceModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addServiceModalLabel">Add New Service</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/vendor/dashboard" method="post">
                <input type="hidden" name="action" value="addService">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="serviceName" class="form-label">Service Name</label>
                        <input type="text" class="form-control" id="serviceName" name="serviceName" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="serviceType" class="form-label">Service Type</label>
                        <select class="form-select" id="serviceType" name="serviceType" required>
                            <option value="">Select a service type</option>
                            <option value="Venue">Venue</option>
                            <option value="Catering">Catering</option>
                            <option value="Photography">Photography</option>
                            <option value="Decoration">Decoration</option>
                            <option value="Entertainment">Entertainment</option>
                            <option value="Transport">Transport</option>
                            <option value="Event Planning">Event Planning</option>
                            <option value="Other">Other</option>
                        </select>
                    </div>
                    
                    <div class="mb-3">
                        <label for="serviceDescription" class="form-label">Service Description</label>
                        <textarea class="form-control" id="serviceDescription" name="serviceDescription" rows="3" required></textarea>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="servicePrice" class="form-label">Price ($)</label>
                            <input type="number" class="form-control" id="servicePrice" name="servicePrice" step="0.01" min="0" required>
                        </div>
                        <div class="col-md-6">
                            <label for="serviceLocation" class="form-label">Location</label>
                            <input type="text" class="form-control" id="serviceLocation" name="serviceLocation" required>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="serviceImageUrl" class="form-label">Image URL</label>
                        <input type="url" class="form-control" id="serviceImageUrl" name="serviceImageUrl">
                        <div class="form-text">Leave blank for an automatic image based on service type</div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Add Service</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Service Modal -->
<div class="modal fade" id="editServiceModal" tabindex="-1" aria-labelledby="editServiceModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="editServiceModalLabel">Edit Service</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/vendor/dashboard" method="post" id="editServiceForm">
                <input type="hidden" name="action" value="editService">
                <input type="hidden" name="serviceId" id="editServiceId">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="editServiceName" class="form-label">Service Name</label>
                        <input type="text" class="form-control" id="editServiceName" name="serviceName" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="editServiceType" class="form-label">Service Type</label>
                        <select class="form-select" id="editServiceType" name="serviceType" required>
                            <option value="">Select a service type</option>
                            <option value="Venue">Venue</option>
                            <option value="Catering">Catering</option>
                            <option value="Photography">Photography</option>
                            <option value="Decoration">Decoration</option>
                            <option value="Entertainment">Entertainment</option>
                            <option value="Transport">Transport</option>
                            <option value="Event Planning">Event Planning</option>
                            <option value="Other">Other</option>
                        </select>
                    </div>
                    
                    <div class="mb-3">
                        <label for="editServiceDescription" class="form-label">Service Description</label>
                        <textarea class="form-control" id="editServiceDescription" name="serviceDescription" rows="3" required></textarea>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="editServicePrice" class="form-label">Price ($)</label>
                            <input type="number" class="form-control" id="editServicePrice" name="servicePrice" step="0.01" min="0" required>
                        </div>
                        <div class="col-md-6">
                            <label for="editServiceLocation" class="form-label">Location</label>
                            <input type="text" class="form-control" id="editServiceLocation" name="serviceLocation" required>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="editServiceImageUrl" class="form-label">Image URL</label>
                        <input type="url" class="form-control" id="editServiceImageUrl" name="serviceImageUrl">
                        <div class="form-text">Leave blank for an automatic image based on service type</div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Changes</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Delete Service Confirmation Modal -->
<div class="modal fade" id="deleteServiceModal" tabindex="-1" aria-labelledby="deleteServiceModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deleteServiceModalLabel">Confirm Delete</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to delete the service "<span id="deleteServiceName"></span>"?</p>
                <p class="text-danger">This action cannot be undone.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form action="${pageContext.request.contextPath}/vendor/dashboard" method="post">
                    <input type="hidden" name="action" value="deleteService">
                    <input type="hidden" name="serviceId" id="deleteServiceId">
                    <button type="submit" class="btn btn-danger">Delete Service</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    // Function to handle editing a service
    function editService(serviceId) {
        fetch('${pageContext.request.contextPath}/vendor/dashboard?action=getService&serviceId=' + serviceId)
            .then(response => response.json())
            .then(data => {
                document.getElementById('editServiceId').value = data.id;
                document.getElementById('editServiceName').value = data.name;
                document.getElementById('editServiceType').value = data.type;
                document.getElementById('editServiceDescription').value = data.description;
                document.getElementById('editServicePrice').value = data.baseCost;
                document.getElementById('editServiceLocation').value = data.location;
                document.getElementById('editServiceImageUrl').value = data.imageUrl || '';
                
                // Show the modal
                new bootstrap.Modal(document.getElementById('editServiceModal')).show();
            })
            .catch(error => console.error('Error fetching service details:', error));
    }
    
    // Function to handle deleting a service
    function confirmDeleteService(serviceId, serviceName) {
        document.getElementById('deleteServiceId').value = serviceId;
        document.getElementById('deleteServiceName').textContent = serviceName;
        
        // Show the modal
        new bootstrap.Modal(document.getElementById('deleteServiceModal')).show();
    }
</script>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />