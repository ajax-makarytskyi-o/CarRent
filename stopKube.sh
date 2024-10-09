kubectl delete -f k8s/ingress.yaml
kubectl delete -f k8s/mongo-config-map.yaml
kubectl delete -f k8s/mongo-secret.yaml
kubectl delete -f k8s/mongo.yaml
kubectl delete -f k8s/app.yaml
kubectl delete -f k8s/express.yaml

minikube addons disable ingress

minikube stop
