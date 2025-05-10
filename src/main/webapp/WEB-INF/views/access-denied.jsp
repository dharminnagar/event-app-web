<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Access Denied" />
</jsp:include>
    
    <div class="container mt-5">
        <div class="row">
            <div class="col-md-8 offset-md-2">
                <div class="card">
                    <div class="card-header bg-danger text-white">
                        <h4>Access Denied</h4>
                    </div>
                    <div class="card-body text-center">
                        <h1 class="display-1 text-danger"><i class="bi bi-exclamation-triangle-fill"></i></h1>
                        <h3 class="mt-3 mb-4">You don't have permission to access this page.</h3>
                        <p>Please contact the administrator if you believe this is an error.</p>
                        <div class="mt-4">
                            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go to Home</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/views/includes/footer.jsp" />