kubectl delete -f app-depl.yaml
kubectl delete -f config-map.yaml
kubectl delete -f mongo-depl.yaml
kubectl delete -f secret_mongo.yaml
kubectl delete -f ingress.yaml
kubectl delete -f express.yaml

minikube addons disable ingress

minikube stop
