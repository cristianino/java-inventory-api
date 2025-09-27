#!/bin/bash

# Test script for Java Inventory API
# This script demonstrates the main API endpoints

BASE_URL="http://localhost:8080/api/inventory"
HEADERS="Content-Type: application/json"

echo "🚀 Testing Java Inventory API"
echo "==============================="
echo

# Health check
echo "1. Health Check"
echo "---------------"
curl -s http://localhost:8080/actuator/health | jq '.'
echo
echo

# Create new inventory entry
echo "2. Create Inventory Entry"
echo "-------------------------"
INVENTORY_RESPONSE=$(curl -s -X POST "${BASE_URL}" \
  -H "${HEADERS}" \
  -d '{
    "productId": "product-123",
    "quantity": 100
  }')
echo "$INVENTORY_RESPONSE" | jq '.'
INVENTORY_ID=$(echo "$INVENTORY_RESPONSE" | jq -r '.data.id')
echo
echo

# Get all inventory entries
echo "3. Get All Inventory Entries"
echo "-----------------------------"
curl -s "${BASE_URL}" | jq '.'
echo
echo

# Get inventory by ID
echo "4. Get Inventory by ID"
echo "----------------------"
curl -s "${BASE_URL}/${INVENTORY_ID}" | jq '.'
echo
echo

# Get inventory by Product ID
echo "5. Get Inventory by Product ID"
echo "-------------------------------"
curl -s "${BASE_URL}/product/product-123" | jq '.'
echo
echo

# Update inventory quantity
echo "6. Update Inventory Quantity"
echo "----------------------------"
curl -s -X PUT "${BASE_URL}/${INVENTORY_ID}/quantity" \
  -H "${HEADERS}" \
  -d '{
    "quantity": 150
  }' | jq '.'
echo
echo

# Get updated inventory
echo "7. Get Updated Inventory"
echo "------------------------"
curl -s "${BASE_URL}/${INVENTORY_ID}" | jq '.'
echo
echo

# Get low stock items (threshold = 200)
echo "8. Get Low Stock Items (threshold=200)"
echo "--------------------------------------"
curl -s "${BASE_URL}?lowStockThreshold=200" | jq '.'
echo
echo

# Clean up - delete inventory
echo "9. Delete Inventory Entry"
echo "-------------------------"
curl -s -X DELETE "${BASE_URL}/${INVENTORY_ID}"
echo "Inventory entry deleted"
echo
echo

# Verify deletion
echo "10. Verify Deletion"
echo "-------------------"
curl -s "${BASE_URL}/${INVENTORY_ID}" || echo "✅ Inventory entry successfully deleted"
echo
echo

echo "🎉 API Test Completed!"
echo "======================"
echo
echo "📚 For more information, visit:"
echo "   - API Documentation: http://localhost:8080/swagger-ui/index.html"
echo "   - Health Endpoint: http://localhost:8080/actuator/health"