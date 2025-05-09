<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
                    <form action="${pageContext.request.contextPath}/vendors" method="get">
                        <div class="mb-3">
                            <label class="form-label">Service Type</label>
                            <select class="form-select" name="type">
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
                                    <input type="number" class="form-control form-control-sm" placeholder="Min" name="minPrice">
                                </div>
                                <div class="col-6">
                                    <input type="number" class="form-control form-control-sm" placeholder="Max" name="maxPrice">
                                </div>
                            </div>
                        </div>
                        
                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary">Apply Filters</button>
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
                        <div class="btn-group">
                            <button type="button" class="btn btn-sm btn-outline-secondary dropdown-toggle" data-bs-toggle="dropdown">
                                Sort by
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="?sort=name_asc">Name (A-Z)</a></li>
                                <li><a class="dropdown-item" href="?sort=name_desc">Name (Z-A)</a></li>
                                <li><a class="dropdown-item" href="?sort=price_asc">Price (Low to High)</a></li>
                                <li><a class="dropdown-item" href="?sort=price_desc">Price (High to Low)</a></li>
                            </ul>
                        </div>
                    </div>
                    
                    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                        <c:forEach items="${vendors}" var="vendor">
                            <div class="col">
                                <div class="card vendor-card h-100 shadow-sm">
                                    <c:choose>
                                        <c:when test="${not empty vendor.imageUrl}">
                                            <img src="${vendor.imageUrl}" class="card-img-top" alt="${vendor.name}">
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

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />