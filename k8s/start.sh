minikube start
cd ..
./gradlew build
eval $(minikube docker-env)
docker build -t application .
cd k8s

kubectl apply -f mongo-config-map.yaml
kubectl apply -f mongo-secret.yaml
kubectl apply -f mongo.yaml
kubectl apply -f app.yaml
kubectl apply -f express.yaml
kubectl apply -f persistence-volume.yaml
kubectl apply -f persistence-volume-claim.yaml

minikube addons enable ingress

kubectl rollout status deployment/ingress-nginx-controller -n ingress-nginx
kubectl apply -f ingress.yaml

sudo minikube tunnel
