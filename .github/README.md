# GitHub Actions CI/CD Pipeline

This directory contains GitHub Actions workflows for automated testing, security scanning, building, and deployment of the Smart Clinic Management System.

## Workflows Overview

### 1. Main CI/CD Pipeline (`ci-cd.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches
- Release publications

**Jobs:**
1. **Test** - Runs unit and integration tests with MySQL and MongoDB services
2. **Code Quality** - SonarCloud analysis and SpotBugs static analysis
3. **Security Scan** - OWASP dependency check and Trivy vulnerability scanning
4. **Build & Push** - Builds Docker image and pushes to GitHub Container Registry
5. **Deploy** - Deploys to production environment (main branch only)
6. **Cleanup** - Removes old container image versions

### 2. Pull Request Validation (`pr-validation.yml`)

**Triggers:**
- Pull requests to `main` or `develop` branches

**Features:**
- Fast validation for PR feedback
- Code formatting checks
- Quick unit test execution
- Breaking change detection

### 3. Nightly Security Scan (`security-scan.yml`)

**Triggers:**
- Daily at 2 AM UTC
- Manual workflow dispatch

**Features:**
- Comprehensive filesystem vulnerability scanning
- Container image security analysis
- Automated security reporting
- Slack notifications for high-severity issues

## Prerequisites

### Required Secrets

Set up the following secrets in your GitHub repository settings:

#### Authentication & Registry
- `GITHUB_TOKEN` - Automatically provided by GitHub
- `SONAR_TOKEN` - SonarCloud authentication token

#### Database Credentials
- `MYSQL_ROOT_PASSWORD` - MySQL root password for production
- `MYSQL_PASSWORD` - MySQL application user password
- `MONGO_PASSWORD` - MongoDB authentication password

#### Application Security
- `JWT_SECRET` - Base64 encoded JWT signing secret

#### Notifications (Optional)
- `SLACK_WEBHOOK_URL` - Slack webhook for deployment notifications

### Required Repository Settings

1. **Branch Protection Rules:**
   - Require pull request reviews for `main` branch
   - Require status checks to pass (CI/CD pipeline)
   - Require branches to be up to date

2. **Environments:**
   - Create a `production` environment
   - Add required reviewers for production deployments
   - Configure environment-specific secrets

3. **Actions Permissions:**
   - Enable "Read and write permissions" for Actions
   - Allow Actions to create and approve pull requests

## Workflow Configuration

### Environment Variables

```yaml
env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}
  JAVA_VERSION: '17'
  MAVEN_VERSION: '3.9.4'
```

### Service Dependencies

The workflows use containerized services for testing:

- **MySQL 8.0** - Relational database testing
- **MongoDB 7.0** - Document database testing

### Build Optimization

- **Maven Dependency Caching** - Speeds up builds by caching Maven dependencies
- **Docker Layer Caching** - Uses GitHub Actions cache for Docker builds
- **Multi-platform Builds** - Supports both AMD64 and ARM64 architectures

## Security Features

### Vulnerability Scanning
- **Trivy** - Scans both filesystem and container images
- **OWASP Dependency Check** - Identifies vulnerable dependencies
- **SARIF Integration** - Results appear in GitHub Security tab

### Container Security
- **Multi-stage Builds** - Reduces attack surface
- **Non-root User** - Containers run as non-privileged user
- **Minimal Base Images** - Uses Alpine-based images where possible

### Secrets Management
- Environment-based secrets for different deployment stages
- No secrets exposed in logs or artifacts
- Automatic secret rotation support

## Quality Gates

### Code Quality
- **SonarCloud** - Code coverage, maintainability, and security analysis
- **SpotBugs** - Static analysis for Java code
- **Code Formatting** - Spotless Maven plugin for consistent formatting

### Testing
- **Unit Tests** - Fast, isolated component tests
- **Integration Tests** - End-to-end testing with real databases
- **Test Reporting** - Automated test result publishing

## Deployment Strategy

### Development Flow
```
Feature Branch → Pull Request → Code Review → Merge to Develop
```

### Production Flow
```
Develop → Pull Request to Main → Approval → Merge → Automatic Deployment
```

### Rollback Strategy
- Tagged Docker images for version management
- Blue-green deployment support
- Automated health checks post-deployment

## Monitoring and Notifications

### Slack Integration
- Deployment status notifications
- Security vulnerability alerts
- Build failure notifications

### Artifact Management
- Test reports and coverage data
- Security scan results
- Build artifacts and logs

## Usage Examples

### Manual Workflow Trigger
```bash
# Trigger nightly security scan manually
gh workflow run security-scan.yml

# Check workflow status
gh run list --workflow=ci-cd.yml
```

### Local Testing
```bash
# Simulate CI environment locally using act
act -j test --container-architecture linux/amd64
```

### Viewing Results
```bash
# Download artifacts
gh run download [RUN_ID]

# View logs
gh run view [RUN_ID] --log
```

## Troubleshooting

### Common Issues

1. **Test Failures**
   - Check database service health
   - Verify test environment configuration
   - Review test logs in Actions tab

2. **Build Failures**
   - Ensure Maven dependencies are correctly specified
   - Check Java version compatibility
   - Verify Docker build context

3. **Deployment Issues**
   - Confirm all required secrets are set
   - Check environment configuration
   - Verify container registry permissions

### Debugging Tips

1. **Enable Debug Logging**
   ```yaml
   - name: Debug step
     run: echo "Debug information"
     env:
       ACTIONS_STEP_DEBUG: true
   ```

2. **SSH into Runner**
   ```yaml
   - name: Setup tmate session
     uses: mxschmitt/action-tmate@v3
   ```

3. **Artifact Inspection**
   - Download build artifacts from failed runs
   - Check log files and test reports
   - Use workflow run annotations

## Best Practices

### Performance
- Use caching for dependencies and build artifacts
- Parallelize independent jobs
- Minimize artifact size and retention

### Security
- Regularly update action versions
- Use specific action versions (not `@main`)
- Audit third-party actions before use

### Maintainability
- Document workflow changes
- Use consistent naming conventions
- Regular review and optimization

## Integration with External Tools

### SonarCloud Setup
1. Create account at sonarcloud.io
2. Import your GitHub repository
3. Generate authentication token
4. Add `SONAR_TOKEN` to repository secrets

### Container Registry
- Uses GitHub Container Registry (ghcr.io)
- Images are automatically tagged with commit SHA
- Support for semantic versioning on releases

### Monitoring Integration
- Ready for Prometheus metrics collection
- Health check endpoints for monitoring
- Log aggregation support

## Continuous Improvement

The CI/CD pipeline is designed to evolve with your project needs:

- Add new testing frameworks as required
- Integrate additional security scanning tools
- Extend deployment targets (staging, production)
- Implement advanced deployment strategies (canary, blue-green)