#!/bin/bash

# Create a backup directory
mkdir -p lib_backup

# Download correct JSP API JAR for Jakarta EE 10 / GlassFish 7
echo "Downloading correct JSP API library for Jakarta EE 10..."
wget -nc https://repo1.maven.org/maven2/jakarta/servlet/jsp/jakarta.servlet.jsp-api/3.1.0/jakarta.servlet.jsp-api-3.1.0.jar -P lib/

# Add tag libraries WAR files needed for TLD files
wget -nc https://repo1.maven.org/maven2/org/glassfish/web/jakarta.servlet.jsp.jstl/3.0.1/jakarta.servlet.jsp.jstl-3.0.1.jar -P lib/
wget -nc https://repo1.maven.org/maven2/jakarta/servlet/jsp/jstl/jakarta.servlet.jsp.jstl-api/3.0.0/jakarta.servlet.jsp.jstl-api-3.0.0.jar -P lib/

# Ensure the libraries are correctly organized
echo "Making sure only one version of each library is used..."
mv lib/jakarta.servlet.jsp.jstl-2.0.0.jar lib_backup/ 2>/dev/null || true
mv lib/jakarta.servlet.jsp.jstl-api-2.0.0.jar lib_backup/ 2>/dev/null || true

echo "JSP and JSTL libraries updated successfully!"
