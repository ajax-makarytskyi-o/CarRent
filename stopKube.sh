kubectl delete -f k8s/nats.yaml
kubectl delete -f k8s/nats-config-map.yaml
kubectl delete -f k8s/mongo-config-map.yaml
kubectl delete -f k8s/mongo-secret.yaml
kubectl delete -f k8s/mongo.yaml
kubectl delete -f k8s/gateway.yaml
kubectl delete -f k8s/app.yaml
kubectl delete -f k8s/express.yaml
kubectl delete -f k8s/app-ingress.yaml
kubectl delete -f k8s/gateway-ingress.yaml

minikube addons disable ingress

minikube stop
