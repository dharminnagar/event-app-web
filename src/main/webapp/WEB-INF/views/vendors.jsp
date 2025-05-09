<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Browse Services" />
</jsp:include>

<!-- Page Header -->
<section class="bg-light py-5 mb-4">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h1 class="fw-bold">Browse Services</h1>
                <p class="lead text-muted">Find and book the perfect services for your event</p>
            </div>
            <div class="col-md-4">
                <form action="${pageContext.request.contextPath}/vendors" method="get">
                    <div class="input-group">
                        <input type="text" class="form-control" placeholder="Search services..." name="search" value="${searchTerm}">
                        <button class="btn btn-primary" type="submit">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>

<div class="container">
    <div class="row">
        <!-- Filters Sidebar -->
        <div class="col-md-3 mb-4">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Filter Services</h5>
                    <form action="${pageContext.request.contextPath}/vendors" method="get" id="filterForm">
                        <!-- Preserve search term if it exists -->
                        <c:if test="${not empty searchTerm}">
                            <input type="hidden" name="search" value="${searchTerm}">
                        </c:if>
                        <!-- Preserve sort option if it exists -->
                        <c:if test="${not empty currentSort}">
                            <input type="hidden" name="sort" value="${currentSort}">
                        </c:if>
                        
                        <div class="mb-3">
                            <label class="form-label">Service Type</label>
                            <select class="form-select" name="type" id="typeFilter">
                                <option value="">All Types</option>
                                <c:forEach items="${vendorTypes}" var="type">
                                    <option value="${type}" ${type eq selectedType ? 'selected' : ''}>${type}</option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Price Range</label>
                            <div class="row g-2">
                                <div class="col-6">
                                    <input type="number" class="form-control form-control-sm" 
                                           placeholder="Min" name="minPrice" value="${minPrice}">
                                </div>
                                <div class="col-6">
                                    <input type="number" class="form-control form-control-sm" 
                                           placeholder="Max" name="maxPrice" value="${maxPrice}">
                                </div>
                            </div>
                        </div>
                        
                        <div class="d-grid gap-2 mt-4">
                            <button type="submit" class="btn btn-primary">Apply Filters</button>
                            <button type="button" class="btn btn-outline-secondary" id="resetFilters">Reset Filters</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        
        <!-- Vendors Listing -->
        <div class="col-md-9">
            <c:choose>
                <c:when test="${empty vendors}">
                    <div class="alert alert-info" role="alert">
                        <i class="fas fa-info-circle me-2"></i>No vendors found matching your criteria.
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Results count and sort options -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <p class="mb-0 text-muted"><strong>${vendors.size()}</strong> services found</p>
                        <div class="dropdown">
                            <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" id="sortDropdown" 
                                    data-bs-toggle="dropdown" aria-expanded="false">
                                Sort by: 
                                <span class="fw-medium">
                                    <c:choose>
                                        <c:when test="${currentSort == 'name_asc'}">Name (A-Z)</c:when>
                                        <c:when test="${currentSort == 'name_desc'}">Name (Z-A)</c:when>
                                        <c:when test="${currentSort == 'price_asc'}">Price (Low to High)</c:when>
                                        <c:when test="${currentSort == 'price_desc'}">Price (High to Low)</c:when>
                                        <c:otherwise>Default</c:otherwise>
                                    </c:choose>
                                </span>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="sortDropdown">
                                <li>
                                    <a class="dropdown-item ${currentSort == 'name_asc' ? 'active' : ''}" 
                                       href="${pageContext.request.contextPath}/vendors?sort=name_asc
                                             ${not empty searchTerm ? '&search='.concat(searchTerm) : ''}
                                             ${not empty selectedType ? '&type='.concat(selectedType) : ''}
                                             ${not empty minPrice ? '&minPrice='.concat(minPrice) : ''}
                                             ${not empty maxPrice ? '&maxPrice='.concat(maxPrice) : ''}">
                                       Name (A-Z)
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item ${currentSort == 'name_desc' ? 'active' : ''}" 
                                       href="${pageContext.request.contextPath}/vendors?sort=name_desc
                                             ${not empty searchTerm ? '&search='.concat(searchTerm) : ''}
                                             ${not empty selectedType ? '&type='.concat(selectedType) : ''}
                                             ${not empty minPrice ? '&minPrice='.concat(minPrice) : ''}
                                             ${not empty maxPrice ? '&maxPrice='.concat(maxPrice) : ''}">
                                       Name (Z-A)
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item ${currentSort == 'price_asc' ? 'active' : ''}" 
                                       href="${pageContext.request.contextPath}/vendors?sort=price_asc
                                             ${not empty searchTerm ? '&search='.concat(searchTerm) : ''}
                                             ${not empty selectedType ? '&type='.concat(selectedType) : ''}
                                             ${not empty minPrice ? '&minPrice='.concat(minPrice) : ''}
                                             ${not empty maxPrice ? '&maxPrice='.concat(maxPrice) : ''}">
                                       Price (Low to High)
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item ${currentSort == 'price_desc' ? 'active' : ''}" 
                                       href="${pageContext.request.contextPath}/vendors?sort=price_desc
                                             ${not empty searchTerm ? '&search='.concat(searchTerm) : ''}
                                             ${not empty selectedType ? '&type='.concat(selectedType) : ''}
                                             ${not empty minPrice ? '&minPrice='.concat(minPrice) : ''}
                                             ${not empty maxPrice ? '&maxPrice='.concat(maxPrice) : ''}">
                                       Price (High to Low)
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </div>
                    
                    <!-- Show any active filters -->
                    <c:if test="${not empty selectedType || not empty minPrice || not empty maxPrice || not empty searchTerm}">
                        <div class="mb-3 d-flex align-items-center">
                            <span class="me-2">Active filters:</span>
                            <div class="d-flex flex-wrap gap-2">
                                <c:if test="${not empty searchTerm}">
                                    <span class="badge bg-primary">Search: ${searchTerm}
                                        <a href="${pageContext.request.contextPath}/vendors?
                                           ${not empty selectedType ? 'type='.concat(selectedType) : ''}
                                           ${not empty minPrice ? '&minPrice='.concat(minPrice) : ''}
                                           ${not empty maxPrice ? '&maxPrice='.concat(maxPrice) : ''}
                                           ${not empty currentSort ? '&sort='.concat(currentSort) : ''}" 
                                           class="text-white ms-1">×</a>
                                    </span>
                                </c:if>
                                <c:if test="${not empty selectedType}">
                                    <span class="badge bg-primary">Type: ${selectedType}
                                        <a href="${pageContext.request.contextPath}/vendors?
                                           ${not empty searchTerm ? 'search='.concat(searchTerm) : ''}
                                           ${not empty minPrice ? '&minPrice='.concat(minPrice) : ''}
                                           ${not empty maxPrice ? '&maxPrice='.concat(maxPrice) : ''}
                                           ${not empty currentSort ? '&sort='.concat(currentSort) : ''}" 
                                           class="text-white ms-1">×</a>
                                    </span>
                                </c:if>
                                <c:if test="${not empty minPrice}">
                                    <span class="badge bg-primary">Min Price: $${minPrice}
                                        <a href="${pageContext.request.contextPath}/vendors?
                                           ${not empty searchTerm ? 'search='.concat(searchTerm) : ''}
                                           ${not empty selectedType ? '&type='.concat(selectedType) : ''}
                                           ${not empty maxPrice ? '&maxPrice='.concat(maxPrice) : ''}
                                           ${not empty currentSort ? '&sort='.concat(currentSort) : ''}" 
                                           class="text-white ms-1">×</a>
                                    </span>
                                </c:if>
                                <c:if test="${not empty maxPrice}">
                                    <span class="badge bg-primary">Max Price: $${maxPrice}
                                        <a href="${pageContext.request.contextPath}/vendors?
                                           ${not empty searchTerm ? 'search='.concat(searchTerm) : ''}
                                           ${not empty selectedType ? '&type='.concat(selectedType) : ''}
                                           ${not empty minPrice ? '&minPrice='.concat(minPrice) : ''}
                                           ${not empty currentSort ? '&sort='.concat(currentSort) : ''}" 
                                           class="text-white ms-1">×</a>
                                    </span>
                                </c:if>
                            </div>
                            <a href="${pageContext.request.contextPath}/vendors" class="ms-auto btn btn-sm btn-outline-secondary">
                                Clear All
                            </a>
                        </div>
                    </c:if>
                    
                    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                        <c:forEach items="${vendors}" var="vendor">
                            <div class="col">
                                <div class="card vendor-card h-100 shadow-sm">
                                    <c:choose>
                                        <c:when test="${not empty vendor.imageUrl}">
                                            <c:choose>
                                                <c:when test="${fn:startsWith(vendor.imageUrl, 'http://') or fn:startsWith(vendor.imageUrl, 'https://')}">
                                                    <img src="${vendor.imageUrl}" class="card-img-top" alt="${vendor.name}">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="${pageContext.request.contextPath}${vendor.imageUrl}" class="card-img-top" alt="${vendor.name}">
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <img src="https://via.placeholder.com/300x200?text=No+Image" class="card-img-top" alt="No Image Available">
                                        </c:otherwise>
                                    </c:choose>
                                    
                                    <div class="card-body d-flex flex-column">
                                        <div class="d-flex justify-content-between align-items-start mb-2">
                                            <h5 class="card-title mb-0">${vendor.name}</h5>
                                            <span class="badge bg-primary">${vendor.type}</span>
                                        </div>
                                        <p class="card-text flex-grow-1">${vendor.description}</p>
                                        <div class="d-flex justify-content-between align-items-center mt-3">
                                            <strong class="text-primary">$<fmt:formatNumber value="${vendor.baseCost}" pattern="#,##0.00"/></strong>
                                            <div>
                                                <a href="${pageContext.request.contextPath}/vendor/${vendor.id}" class="btn btn-sm btn-outline-primary me-1">Details</a>
                                                <c:if test="${sessionScope.user != null}">
                                                    <form action="${pageContext.request.contextPath}/add-to-cart" method="post" style="display: inline;">
                                                        <input type="hidden" name="vendorId" value="${vendor.id}">
                                                        <button type="submit" class="btn btn-sm btn-primary">
                                                            <i class="fas fa-plus"></i> Add
                                                        </button>
                                                    </form>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<!-- Add JavaScript for filter functionality -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Reset filters button
        const resetFilters = document.getElementById('resetFilters');
        if(resetFilters) {
            resetFilters.addEventListener('click', function() {
                window.location.href = '${pageContext.request.contextPath}/vendors';
            });
        }
        
        // Auto-submit on type change
        const typeFilter = document.getElementById('typeFilter');
        if(typeFilter) {
            typeFilter.addEventListener('change', function() {
                document.getElementById('filterForm').submit();
            });
        }
    });
</script>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />