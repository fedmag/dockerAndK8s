# Kubernetes (K8s)
## Basic concepts
k8s is a container orchestration tool.  
MORE TO ADD

The k8s apiserver is running on the master node and exposes REST APIs that is the only point of communication for k8s clusters.

The desired state is expressed through a YAML file. Such desired state is sent to the cluster thorugh the CLI (kubectl), which uses the aforementioned REST APIs. Other applications such as dashboards communicates through the same REST APIs.

The `.kube` folder (C:/Users/[userName]/.kube) stores the information used by kubectl (such as the config and all the clusters information).

## Context
A group of access parameters to a k8s cluster. It contains:
- cluster name
- a user
- a namespace

The current context is the k8s cluster against which the kubectl commands will be executed.

### Commands

- `kubectl config current-context` -> get the current context
- `kubectl config get-contexts` -> list all context
- `kubectl config use-context [contextName]` -> set the current context
- `kubectl config delete-context [contextName]` -> delete a context from the config file
- `kubectl config rename-context [oldName] [newName]` -> rename context

## Declarative vs Imperative 
There are two ways of creating resources in k8s:
1. <strong>Imperative:</strong> using kubectl commands, issue a series of commands to create resources:
   ```
   kubectl run mynginx --image=nginx --port=80
   kubectl create deploy mynginx --image=nginx --port=80 --replicas=3
   kubectl create service nodeport myservice --targetPort=8080
   kubectl delete pod nginx
   ```
2. <strong>Declarative:</strong> using kubectl and YAML manifests that define the resources that are needed. The advantage is that this is reproducible, can be saved in source-control and can be parsed and modified:
   ```
    apiVersion: v1
    kind: Pod 
    metadata: 
      name: myapp-pod 
      labels:
        app: myapp
        type: front-end
    spec:
      containers:
      - name: nginx-container
      image: nginx  
   ```
    then kubectl is used to send the YAML file to the k8s cluster: `kubectl create -f [yamlFile]`

## Namespace
Allows to group resources (ex: dev, test, prod). Namespaces are kind of logical folders in which resources are grouped together.

k8s creates a default namespace. 

Objects in one namespace can access objects in a different one: `objectName.PROD.svc.cluster`.

Deleting a namespace will delete all its child objects.

![namespace](images/namespace.png)
### Commands
- `kubectl get namespace/ns` -> lists all namespaces in a cluster.
- `kubectl config  set-context --current --namespace=[nameSpace]` -> set the current context to a specific namespace
- `kubectl create ns [nameSpace]` -> create a namespace 
- `kubectl delete ns [nameSpace]` -> delete a namespace
- `kubectl get pods --all-namespaces` -> list all pods in all namespaces

## Nodes
![namespace](images/nodes.png)
Nodes are physical or virtual VMs. A group of nodes forms a <strong>cluster</strong>.
Among all the nodes there is one that control the others, the <storng>master node</strong>.

### Master Node
The master node (or control plane) contains the master components, a collection of k8s elements that are needed for the overall orchestration and functioning of k8s:

- etcd: key-value data store. Stores the cluster state. Not inteded to be used as DB by applications. Inside k8s this is the single source of truth.
- kube-apiserver: the only component talking to etcd. It exposes a REST interface and tools like k8s cli communicate through it. All the clients interact with it, never directly with the data store.
- kube-control-manager: the controller of controllers. It runs the other k8s controllers.
- cloud-control-manager: interact with the cloud providers controlleers. 
    - Node: for checking the cloud provider to determine is a node has been deleted in the cloud after it stops respondeing
    - Route: for setting up routes in the underlying cloud infrastracture
    - Service: for creating, updating and deleting cloud provider load balancers
    - Volume: for creating, attaching and mounting volumes. Also interacts with the cloud provider to orchestrates volumes.
- kube-scheduler: watches for newly created pods that have no nodes assigned and selects a node to run into such pods. For the scheduling decision the following is taken into accout:
  - Individual and collective resource requirements
  - HW/SW constraints
  - Affinity and anty-affinity specifications
  - Data locality
- Addons: there are many addons that can be installed on the master node to enhance the capabilities of k8s. For example:
  - DNS
  - Web UI (dashboard)
  - Cluster-level logging
  - Container resource monitoring
  
 Usually no applications are run on the master node.

 ### Worker Node
 When a worker node is added to the cluster a few k8s services are installed by default:

 - container runtime: runtimet to run containers. Must implement the Kubernetes Container Runtimer Interface (CRI).
 - kubelet: manages the pod life cycle. Ensures that the containers described in the Pod specs are running and healty.
 - kube-proxy: network proxy that manages network rules on the node. All network traffic goes through kube-proxy.

These services are needed to run tasks and are managed by master components on the master node.

#### Node pool
A group of virtual machines, all with the same size.  
A cluster can have many node pools:
- these pools can host different sizes of VMs
- each pool can be autoscaled independently from the other pools.

## Pods

Atomic (smallest) unit of work of k8s.  
Encapsulates an application's container.  
Represents a unit of deployment.

Pods can run one or more containers -> within a pod containers share IP address and volume. -> they communicate with each other using `localhost`.

Pods are ephemeral (pods are not updated, but replaced with an updated version) and deploying one is an atomic operation (it succeeds or not). If a pod fails, it is replaced with a new one with a new IP address.

The scaling happens by adding more pods, not more containers within the pod.
![pods](images/pods.png)

![pod](images/pod.png)

### Pod state
- Pending -> acccepted but not yet created. For example if there are not enough resources.
- Running -> all good.
- Succeeded -> exited with status 0.
- Failed -> all containers exdit and at least one exited with a non-zero status.
- Unknown -> communication issues with the pod.
- CrashLoopBackOff -> started, crashed, started again and then crashed again.

### Define and run pods
To define a pod a YAML file is used. The type of resource (`Pod`) in this case is defined in the value kind:

```
apiVersion: v1
kind: Pod #-> object type
metadata: 
  name: myapp-pod
  labels: #-> used to identify, describe and group related sets of objs/resources
    app: myapp
    type: front-end
specs:
  containers:
  - name: nginx-container
    image: nginx #-> image that is going to be pulled by docker hub
    ports:
    - containerPort: 80 #-> port the container will listen on
      name: http
      protocol: TCP
    env: #-> set env variables. Not the best idea to set them here tho
    - name: DBCON
      value: connectionstring
    # specify commands to run when the container starts. Equiv. to Docker ENTRYPOINT
    command: ["/bin/sh", "-c"]
    args: ["echo ${DBCON}"]
```

#### Commands

- `kubectl create -f [podDefinition.yml]` -> create a pod using a file (imperative object way)
- `kubectl run [podName] --image= busybox -- /bin/sh -c "sleep 3600` -> run a pod without yaml file (imparative way) 
- `kubectl apply -f [podDefinition].yaml` -> declarative way
  - more info: [object management](https://kubernetes.io/docs/concepts/overview/working-with-objects/object-management/)
- `kubectl get pods` -> list running pods
  - `... -o wide` -> get more info on running pods
- `kubectl describe pod [podName]` -> show pod info
- `kubectl get pod [podName] -o yaml > [fileName].yaml` -> extract the pod definition in YAML and save it in a file 
- `kubectl exec -it [podName] -- sh` -> interactive mode
- `kubectl delete -f [podDefinition.yml]` -> delete pod
- `kubectl delete pod [podName]` -> delete pod using pod's name

### Init containers
If the application has some dependency (files, apis, validate that a DB is up and running) we can tell the pod to initialize a contaier (the so called `init container`) before the application contaier runs. When the init container is initialized and completed, the app container will start.

The main advantage is that we can separate the application container (main logic) from its dependecies (infrastructure code).

Each init contaier always run to completion. There can be more than one, but each must complete successfully before the next one starts.

As example, here we have 2 init containers that check that 2 services are up and running:
![initContainers](images/initContainers.png)