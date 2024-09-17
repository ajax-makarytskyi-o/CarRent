minikube start
cd ..
./gradlew build
eval $(minikube docker-env)
docker build -t application .
cd k8s

kubectl apply -f config-map.yaml
kubectl apply -f secret_mongo.yaml
kubectl apply -f mongo-depl.yaml
kubectl apply -f app-depl.yaml
kubectl apply -f express.yaml

minikube addons enable ingress

kubectl rollout status deployment/ingress-nginx-controller -n ingress-nginx
kubectl apply -f ingress.yaml

sudo minikube tunnel
