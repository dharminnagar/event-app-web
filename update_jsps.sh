#!/bin/bash

# Update JSTL core taglib URIs
find src/main/webapp -name "*.jsp" -type f -exec sed -i 's/taglib uri="http:\/\/java.sun.com\/jsp\/jstl\/core" prefix="c"/taglib uri="jakarta.tags.core" prefix="c"/g' {} \;

# Update JSTL fmt taglib URIs
find src/main/webapp -name "*.jsp" -type f -exec sed -i 's/taglib uri="http:\/\/java.sun.com\/jsp\/jstl\/fmt" prefix="fmt"/taglib uri="jakarta.tags.fmt" prefix="fmt"/g' {} \;

# Update JSTL functions taglib URIs
find src/main/webapp -name "*.jsp" -type f -exec sed -i 's/taglib uri="http:\/\/java.sun.com\/jsp\/jstl\/functions" prefix="fn"/taglib uri="jakarta.tags.functions" prefix="fn"/g' {} \;

echo "All JSP files updated with Jakarta JSTL URIs."
