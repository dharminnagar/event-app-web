#!/bin/bash

# Create a backup directory
mkdir -p lib_backup

# Move old JSTL JARs to backup
echo "Backing up old JSTL libraries..."
mv lib/jstl-1.2.jar lib_backup/ 2>/dev/null || true
mv lib/jakarta.servlet.jsp.jstl-api.jar lib_backup/ 2>/dev/null || true

# Download correct JSTL libraries for Jakarta EE 10 / GlassFish 7
echo "Downloading correct JSTL libraries for Jakarta EE 10..."
wget -nc https://repo1.maven.org/maven2/jakarta/servlet/jsp/jstl/jakarta.servlet.jsp.jstl-api/3.0.0/jakarta.servlet.jsp.jstl-api-3.0.0.jar -P lib/
wget -nc https://repo1.maven.org/maven2/org/glassfish/web/jakarta.servlet.jsp.jstl/3.0.1/jakarta.servlet.jsp.jstl-3.0.1.jar -P lib/

echo "JSTL libraries updated successfully!"
