### Guide To Get JenkinsX on Minikube
The simple command, here: https://jenkins-x.io/getting-started/create-cluster/#using-minikube-local seems to work.

That command is: `sudo jx create cluster minikube`

I set CPUs to 5 and memory to 20480

However... I did have the problem listed here which was resolved by installing the latest Helm binaries, as recommended
in this post: https://github.com/jenkins-x/jx/issues/1271#issuecomment-405623127

### Next steps
Admin pwd: chillglitter
uid: admin

Use monocular to deploy?? Find its url (and all URLs) with: `kubectl get ingress
`


To get services talking across namespaces, see: https://stackoverflow.com/questions/37221483/kubernetes-service-located-in-another-namespace

Follow install instructions here: https://github.com/Yolean/kubernetes-kafka

I couldn't get the basic install to work from here, but it has some goodies: https://technology.amis.nl/2018/04/19/15-minutes-to-get-a-kafka-cluster-running-on-kubernetes-and-start-producing-and-consuming-from-a-node-application/

To access kafka from outside the cluster, see here: https://github.com/Yolean/kubernetes-kafka/tree/master/outside-services

#### Yahoo Kafka Manager
To get manager running to create and view a kafka cluster you need to:

- delete the **service** that got deployed for kafka-manager (not the **pod**)
- create a new service with the following YAML:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: kafka-manager
  namespace: kafka
spec:
  type: NodePort
  ports:
    - port: 80
      nodePort: 30002
  selector:
    app: kafka-manager
```
- next, create a cluster in the manager. It needs a zookeeper host.  Use "zookeeper".
- create "outside" connections per the Yolean instructions, with the following application.properties (sub in the proper URL and port):
```yaml
spring.cloud.stream.bindings.input.binder=kafka
spring.cloud.stream.bindings.output.binder=kafka
spring.cloud.stream.kafka.binder.auto-create-topics=true
#spring.cloud.stream.kafka.binder.brokers=broker.kafka:9092 #use in k8s cluster
spring.cloud.stream.kafka.binder.brokers=http://192.168.39.59:32401
spring.cloud.stream.kafka.binder.defaultBrokerPort=32401
```
