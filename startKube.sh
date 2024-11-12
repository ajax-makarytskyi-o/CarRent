minikube start

eval $(minikube docker-env)
docker build -t application domainservice/
docker build -t gateway gateway/

kubectl apply -f k8s/nats.yaml
kubectl apply -f k8s/nats-config-map.yaml
kubectl apply -f k8s/mongo-config-map.yaml
kubectl apply -f k8s/mongo-secret.yaml
kubectl apply -f k8s/mongo.yaml
kubectl apply -f k8s/gateway.yaml
kubectl apply -f k8s/app.yaml
kubectl apply -f k8s/express.yaml
kubectl apply -f k8s/persistence-volume.yaml
kubectl apply -f k8s/persistence-volume-claim.yaml

minikube addons enable ingress

kubectl rollout status deployment/ingress-nginx-controller -n ingress-nginx
kubectl apply -f k8s/app-ingress.yaml
kubectl apply -f k8s/gateway-ingress.yaml

sudo minikube tunnel
