param(
    [string]$image = "<YOUR_REGISTRY>/gen-ai-java-spring:latest",
    [string]$kubeNamespace = "default"
)

# Build jar
mvn -DskipTests package

# Build docker image (requires Docker installed and a Dockerfile at repo root)
docker build -t $image .

docker push $image

# Deploy Loki to cluster (namespace: loki)
kubectl apply -f k8s/loki.yaml

# Deploy app (replace image placeholder)
( Get-Content k8s/app-deployment.yaml ) -replace '<YOUR_REGISTRY>/gen-ai-java-spring:latest', $image | Set-Content k8s/app-deployment.tmp.yaml
kubectl apply -f k8s/app-deployment.tmp.yaml -n $kubeNamespace
