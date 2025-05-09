<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Home" />
</jsp:include>

<!-- Hero Section -->
<section class="hero-section">
    <div class="container text-center">
        <h1 class="display-4 fw-bold mb-4">Plan Your Perfect Event</h1>
        <p class="lead mb-5">Find and book the best vendors for any occasion - all in one place</p>
        <div class="d-flex justify-content-center gap-3">
            <a href="${pageContext.request.contextPath}/vendors" class="btn btn-primary btn-lg px-4 py-2">
                <i class="fas fa-search me-2"></i>Find Services
            </a>
            <a href="${pageContext.request.contextPath}/register" class="btn btn-outline-light btn-lg px-4 py-2">
                <i class="fas fa-user-plus me-2"></i>Join Us
            </a>
        </div>
    </div>
</section>

<!-- Main Content -->
<div class="container py-5">
    <!-- Event Categories -->
    <section class="mb-5">
        <h2 class="section-title text-center mb-4">Plan Any Event</h2>
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
            <div class="col">
                <div class="card h-100 border-0 shadow-sm">
                    <div class="card-body text-center p-4">
                        <div class="display-3 text-primary mb-3">
                            <i class="fas fa-glass-cheers"></i>
                        </div>
                        <h4>Weddings</h4>
                        <p class="text-muted">Find everything you need for your special day</p>
                        <a href="${pageContext.request.contextPath}/vendors?event=wedding" class="btn btn-sm btn-outline-primary">
                            Explore <i class="fas fa-arrow-right ms-1"></i>
                        </a>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="card h-100 border-0 shadow-sm">
                    <div class="card-body text-center p-4">
                        <div class="display-3 text-primary mb-3">
                            <i class="fas fa-briefcase"></i>
                        </div>
                        <h4>Corporate Events</h4>
                        <p class="text-muted">Professional services for meetings and conferences</p>
                        <a href="${pageContext.request.contextPath}/vendors?event=corporate" class="btn btn-sm btn-outline-primary">
                            Explore <i class="fas fa-arrow-right ms-1"></i>
                        </a>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="card h-100 border-0 shadow-sm">
                    <div class="card-body text-center p-4">
                        <div class="display-3 text-primary mb-3">
                            <i class="fas fa-birthday-cake"></i>
                        </div>
                        <h4>Birthdays</h4>
                        <p class="text-muted">Everything to make your celebration memorable</p>
                        <a href="${pageContext.request.contextPath}/vendors?event=birthday" class="btn btn-sm btn-outline-primary">
                            Explore <i class="fas fa-arrow-right ms-1"></i>
                        </a>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="card h-100 border-0 shadow-sm">
                    <div class="card-body text-center p-4">
                        <div class="display-3 text-primary mb-3">
                            <i class="fas fa-music"></i>
                        </div>
                        <h4>Parties</h4>
                        <p class="text-muted">Turn any gathering into an amazing experience</p>
                        <a href="${pageContext.request.contextPath}/vendors?event=party" class="btn btn-sm btn-outline-primary">
                            Explore <i class="fas fa-arrow-right ms-1"></i>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Popular Services -->
    <section class="mb-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="section-title mb-0">Popular Services</h2>
            <a href="${pageContext.request.contextPath}/vendors" class="btn btn-outline-primary">
                View All <i class="fas fa-arrow-right ms-1"></i>
            </a>
        </div>
        
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
            <c:forEach begin="1" end="6">
                <div class="col">
                    <div class="card h-100 border-0 shadow-sm vendor-card">
                        <img src="https://source.unsplash.com/random/300x200/?event,${status.index}" class="card-img-top" alt="Service">
                        <div class="card-body p-4">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="badge bg-primary text-white">Popular</span>
                                <div>
                                    <i class="fas fa-star text-warning"></i>
                                    <span>4.8 (120)</span>
                                </div>
                            </div>
                            <h5 class="card-title mb-1">Vendor Service Name</h5>
                            <p class="text-muted small mb-3">Service Category</p>
                            <p class="card-text">Professional service with customizable options for your event needs.</p>
                        </div>
                        <div class="card-footer bg-white p-4 border-top-0">
                            <div class="d-flex justify-content-between align-items-center">
                                <span class="fw-bold text-primary">$299.99</span>
                                <a href="${pageContext.request.contextPath}/vendor/details/1" class="btn btn-outline-primary">
                                    View Details
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>
    
    <!-- How It Works -->
    <section class="mb-5">
        <h2 class="section-title text-center">How It Works</h2>
        <p class="text-center text-muted mb-5">Plan and book your event in just a few simple steps</p>
        
        <div class="row row-cols-1 row-cols-md-3 g-4">
            <div class="col">
                <div class="card h-100 border-0 bg-light">
                    <div class="card-body text-center p-4">
                        <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center mx-auto mb-4" style="width: 80px; height: 80px;">
                            <h2 class="mb-0">1</h2>
                        </div>
                        <h4>Browse Services</h4>
                        <p class="text-muted">Explore our curated selection of verified vendors and service providers.</p>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="card h-100 border-0 bg-light">
                    <div class="card-body text-center p-4">
                        <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center mx-auto mb-4" style="width: 80px; height: 80px;">
                            <h2 class="mb-0">2</h2>
                        </div>
                        <h4>Select & Book</h4>
                        <p class="text-muted">Choose the services you need and add them to your event booking.</p>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="card h-100 border-0 bg-light">
                    <div class="card-body text-center p-4">
                        <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center mx-auto mb-4" style="width: 80px; height: 80px;">
                            <h2 class="mb-0">3</h2>
                        </div>
                        <h4>Enjoy Your Event</h4>
                        <p class="text-muted">Relax and enjoy your perfectly planned event with our trusted vendors.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Testimonials -->
    <section class="mb-5">
        <h2 class="section-title text-center">What Our Customers Say</h2>
        <div class="row row-cols-1 row-cols-md-3 g-4 mt-3">
            <div class="col">
                <div class="card h-100 border-0 shadow-sm">
                    <div class="card-body p-4">
                        <div class="mb-3 text-warning">
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                        </div>
                        <p class="card-text mb-4">"Event Aggregator made planning our wedding so much easier. We found all our vendors in one place and saved so much time!"</p>
                        <div class="d-flex align-items-center">
                            <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center me-3" style="width: 50px; height: 50px;">
                                <span class="h5 mb-0">JD</span>
                            </div>
                            <div>
                                <h6 class="mb-0">John & Dana</h6>
                                <small class="text-muted">Wedding Couple</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="card h-100 border-0 shadow-sm">
                    <div class="card-body p-4">
                        <div class="mb-3 text-warning">
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                        </div>
                        <p class="card-text mb-4">"As a corporate event planner, I rely on Event Aggregator for quality vendors. The platform has saved me countless hours of research."</p>
                        <div class="d-flex align-items-center">
                            <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center me-3" style="width: 50px; height: 50px;">
                                <span class="h5 mb-0">SM</span>
                            </div>
                            <div>
                                <h6 class="mb-0">Sarah M.</h6>
                                <small class="text-muted">Corporate Event Manager</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="card h-100 border-0 shadow-sm">
                    <div class="card-body p-4">
                        <div class="mb-3 text-warning">
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star-half-alt"></i>
                        </div>
                        <p class="card-text mb-4">"I organized my daughter's 16th birthday using Event Aggregator. The variety of vendors and easy booking process was exceptional!"</p>
                        <div class="d-flex align-items-center">
                            <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center me-3" style="width: 50px; height: 50px;">
                                <span class="h5 mb-0">RL</span>
                            </div>
                            <div>
                                <h6 class="mb-0">Robert L.</h6>
                                <small class="text-muted">Parent</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- CTA Section -->
    <section class="text-center py-5 px-3 bg-primary text-white rounded-3">
        <h2 class="mb-3">Ready to Plan Your Event?</h2>
        <p class="lead mb-4">Join thousands of satisfied customers who planned their perfect events with us</p>
        <div class="d-flex justify-content-center gap-3">
            <a href="${pageContext.request.contextPath}/register" class="btn btn-light btn-lg px-4">
                Create an Account
            </a>
            <a href="${pageContext.request.contextPath}/vendors" class="btn btn-outline-light btn-lg px-4">
                Browse Services
            </a>
        </div>
    </section>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />