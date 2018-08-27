### Guide To Get JenkinsX on Minikube
The simple command, here: https://jenkins-x.io/getting-started/create-cluster/#using-minikube-local seems to work.

That command is: `sudo jx create cluster minikube`

I set CPUs to 5 and memory to 20480

However... I did have the problem listed here which was resolved by installing the latest Helm binaries, as recommended
in this post: https://github.com/jenkins-x/jx/issues/1271#issuecomment-405623127

### Next steps
Admin pwd: swoopblack
uid: admin

Use monocular to deploy?? Find its url (and all URLs) with: `kubectl get ingress
`


To get services talking across namespaces, see: https://stackoverflow.com/questions/37221483/kubernetes-service-located-in-another-namespace

Follow install instructions here: https://github.com/Yolean/kubernetes-kafka

I couldn't get the basic install to work from here, but it has some goodies: https://technology.amis.nl/2018/04/19/15-minutes-to-get-a-kafka-cluster-running-on-kubernetes-and-start-producing-and-consuming-from-a-node-application/

To access kafka from outside the cluster, see here: https://github.com/Yolean/kubernetes-kafka/tree/master/outside-services

### Jenkins x CheatSheet
https://github.com/jenkins-x/jx

### Gitea AddOn
https://github.com/jenkins-x/jx-docs/blob/master/content/commands/jx_create_addon_gitea.md

created Gitea instance at http://gitea-gitea.jx.192.168.99.100.nip.io/

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

### Alternative Kafka Setup with Confluent Helm Charts

**See these docs**: https://docs.confluent.io/current/quickstart/cp-helm-charts/docs/index.html

After deploying charts, the following was produced in the console:

nt-oss cp-helm-charts
NAME:   my-confluent-oss
LAST DEPLOYED: Sat Aug 25 17:27:30 2018
NAMESPACE: default
STATUS: DEPLOYED

RESOURCES:
==> v1beta1/StatefulSet
NAME                           DESIRED  CURRENT  AGE
my-confluent-oss-cp-kafka      3        1        2s
my-confluent-oss-cp-zookeeper  3        1        2s

==> v1beta1/PodDisruptionBudget
NAME                               MIN AVAILABLE  MAX UNAVAILABLE  ALLOWED DISRUPTIONS  AGE
my-confluent-oss-cp-zookeeper-pdb  N/A            1                0                    2s

==> v1/Pod(related)
NAME                                                  READY  STATUS             RESTARTS  AGE
my-confluent-oss-cp-kafka-connect-5b6f5f74f9-9qxf8    0/2    ContainerCreating  0         2s
my-confluent-oss-cp-kafka-rest-7bbf6ff4d-xsp58        0/2    Pending            0         2s
my-confluent-oss-cp-ksql-server-55c947f556-rtx2c      0/2    ContainerCreating  0         2s
my-confluent-oss-cp-schema-registry-59d8877584-pz2m4  0/2    ContainerCreating  0         2s
my-confluent-oss-cp-kafka-0                           0/2    Pending            0         2s
my-confluent-oss-cp-zookeeper-0                       0/2    Pending            0         1s

==> v1/ConfigMap
NAME                                                    DATA  AGE
my-confluent-oss-cp-kafka-connect-jmx-configmap         1     2s
my-confluent-oss-cp-kafka-rest-jmx-configmap            1     2s
my-confluent-oss-cp-kafka-jmx-configmap                 1     2s
my-confluent-oss-cp-ksql-server-jmx-configmap           1     2s
my-confluent-oss-cp-ksql-server-ksql-queries-configmap  1     2s
my-confluent-oss-cp-schema-registry-jmx-configmap       1     2s
my-confluent-oss-cp-zookeeper-jmx-configmap             1     2s

==> v1/Service
NAME                                    TYPE       CLUSTER-IP     EXTERNAL-IP  PORT(S)            AGE
my-confluent-oss-cp-kafka-connect       ClusterIP  10.98.156.195  <none>       8083/TCP           2s
my-confluent-oss-cp-kafka-rest          ClusterIP  10.108.113.18  <none>       8082/TCP           2s
my-confluent-oss-cp-kafka-headless      ClusterIP  None           <none>       9092/TCP           2s
my-confluent-oss-cp-kafka               ClusterIP  10.106.68.142  <none>       9092/TCP           2s
my-confluent-oss-cp-ksql-server         ClusterIP  10.103.25.182  <none>       8088/TCP           2s
my-confluent-oss-cp-schema-registry     ClusterIP  10.110.18.68   <none>       8081/TCP           2s
my-confluent-oss-cp-zookeeper-headless  ClusterIP  None           <none>       2888/TCP,3888/TCP  2s
my-confluent-oss-cp-zookeeper           ClusterIP  10.103.20.134  <none>       2181/TCP           2s

==> v1beta2/Deployment
NAME                                 DESIRED  CURRENT  UP-TO-DATE  AVAILABLE  AGE
my-confluent-oss-cp-kafka-connect    1        1        1           0          2s
my-confluent-oss-cp-kafka-rest       1        1        1           0          2s
my-confluent-oss-cp-ksql-server      1        1        1           0          2s
my-confluent-oss-cp-schema-registry  1        1        1           0          2s


NOTES:
## ------------------------------------------------------
## Zookeeper
## ------------------------------------------------------
Connection string for Confluent Kafka:
  my-confluent-oss-cp-zookeeper-0.my-confluent-oss-cp-zookeeper-headless:2181,my-confluent-oss-cp-zookeeper-1.my-confluent-oss-cp-zookeeper-headless:2181,...

To connect from a client pod:

1. Deploy a zookeeper client pod with configuration:

    apiVersion: v1
    kind: Pod
    metadata:
      name: zookeeper-client
      namespace: default
    spec:
      containers:
      - name: zookeeper-client
        image: confluentinc/cp-zookeeper:5.0.0
        command:
          - sh
          - -c
          - "exec tail -f /dev/null"

2. Log into the Pod

  kubectl exec -it zookeeper-client -- /bin/bash

3. Use zookeeper-shell to connect in the zookeeper-client Pod:

  zookeeper-shell my-confluent-oss-cp-zookeeper:2181

4. Explore with zookeeper commands, for example:

  # Gives the list of active brokers
  ls /brokers/ids

  # Gives the list of topics
  ls /brokers/topics

  # Gives more detailed information of the broker id '0'
  get /brokers/ids/0## ------------------------------------------------------
## Kafka
## ------------------------------------------------------
To connect from a client pod:

1. Deploy a kafka client pod with configuration:

    apiVersion: v1
    kind: Pod
    metadata:
      name: kafka-client
      namespace: default
    spec:
      containers:
      - name: kafka-client
        image: confluentinc/cp-kafka:5.0.0
        command:
          - sh
          - -c
          - "exec tail -f /dev/null"

2. Log into the Pod

  kubectl exec -it kafka-client -- /bin/bash

3. Explore with kafka commands:

  # Create the topic
  kafka-topics --zookeeper my-confluent-oss-cp-zookeeper-headless:2181 --topic my-confluent-oss-topic --create --partitions 1 --replication-factor 1 --if-not-exists

  # Create a message
  MESSAGE="`date -u`"

  # Produce a test message to the topic
  echo "$MESSAGE" | kafka-console-producer --broker-list my-confluent-oss-cp-kafka-headless:9092 --topic my-confluent-oss-topic

  # Consume a test message from the topic
  kafka-console-consumer --bootstrap-server my-confluent-oss-cp-kafka-headless:9092 --topic my-confluent-oss-topic --from-beginning --timeout-ms 2000 --max-messages 1 | grep "$MESSAGE"
