#!/bin/bash
# Script to prepare directories on deployment server for Supply Service
# Run this on: internal.example.com or internal.example.com

set -e

echo "🚀 Preparing directories for Supply Service deployment..."

# Create directories
echo "Creating directories..."
sudo mkdir -p /opt/service-supply/logs
sudo mkdir -p /opt/service-supply/certs
sudo mkdir -p /opt/service-supply/data

# Set ownership (UID 1000 = spring user in Docker container)
echo "Setting ownership to UID 1000 (spring user)..."
sudo chown -R 1000:1000 /opt/service-supply/logs
sudo chown -R 1000:1000 /opt/service-supply/data

# Set permissions
echo "Setting permissions..."
sudo chmod -R 755 /opt/service-supply/logs
sudo chmod -R 755 /opt/service-supply/data
sudo chmod -R 755 /opt/service-supply/certs

# Verify
echo ""
echo "✅ Directory structure created:"
ls -lah /opt/service-supply/

echo ""
echo "✅ Setup completed successfully!"
echo ""
echo "Next steps:"
echo "  1. Copy SSL certificates to /opt/service-supply/certs/ (if needed)"
echo "  2. Deploy application via GitLab CI/CD pipeline"
