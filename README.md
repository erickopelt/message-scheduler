# Message scheduler

Serviço de agendamento de mensagens

Índice
=================

- [Message scheduler](#message-scheduler)
  * [Arquitetura](#arquitetura)
    + [API](#api)
    + [Database migration](#database-migration)
  * [Build](#build)
    + [Requisitos](#requisitos)
    + [Testes](#testes)
        * [Testes unitarios](#testes-unitarios)
        * [Testes Integrados](#testes-integrados)
        * [Coverage](#coverage)
  * [Executando](#executando)
    + [docker-compose](#docker-compose)
      - [Prometheus](#prometheus)
      - [Grafana](#grafana)
    + [Kubernetes](#kubernetes)
      - [minikube (local)](#minikube--local-)
          + [Requisitos](#requisitos-1)
      - [Google Cloud Kubernetes (gke)](#google-cloud-kubernetes--gke-)
          + [Requisitos](#requisitos-2)

## Arquitetura

A arquitetura do projeto é baseada no padrão Clean Archictecture. Um exemplo do modelo utilizado com a explicação de cada pacote pode ser consultado nesse [repositório](https://github.com/erickopelt/clean-architecture-spring).

### API 

A API foi construida no modelo RESTFul utilizando o padrão HATEOAS. A documentação está disponivel via Swagger e pode ser acessada pelo no caminho [/swagger-ui](http://localhost:8080/swagger-ui).

### Database migration

Os scripts de migração de base são executados pelo **FlyWay**. Os arquivos estão na pasta [src/main/resources/db/migration](src/main/resources/db/migration)

## Build

### Requisitos

- maven
- java 11
- docker

O build é feito pelo maven utilizando o comando 

```
mvn clean install
```

Durante o build também é realizado a etapa de testes:

### Testes

Existem dois tipos de testes na suite, untarios e integrados.

##### Testes unitarios

Os testes unitários foram feitos utilzando o **JUnit** e o **Mockito**.

##### Testes Integrados

Os testes integrados foram escritos utilizando a biblioteca **RestAssured**. O banco de dados é provido pela biblioteca **TestContainers**, na hora de execução do teste a lib sobe um container com o postgres.

##### Coverage

No final do build é verificado o coverage dos testes pelo **Jacoco** e o coverage de mutação é verificado pelo **Pit Mutation Testing**

## Executando

### docker-compose

Primeiro faça o build da imagem via:

```
docker-compose build
```

Em seguida basta rodar o comando:

```
docker-compose up
```

#### Prometheus

Uma instância do prometheus ficará disponivel na porta ```:9090``` para realizar query's com base nos dados coletados no endpoint [/actuator/prometheus](http://localhost:8080/actuator/prometheus)

#### Grafana

Junto com prometheus o compose também contêm sobe uma instância do grafana com o dashboard da aplicação disponivel no link [http://localhost:3000/d/YxadKCyGk/message-scheduler?orgId=1&refresh=30s](http://localhost:3000/d/YxadKCyGk/message-scheduler?orgId=1&refresh=30s), o usuário default é admin com a senha admin, após o login o grafana irá pedir uma nova senha, pode ser colocado qualquer valor.

### Kubernetes

No diretório [kubernetes](kubernetes) existe a configuração para rodar localmente via minikube ou no gke

#### minikube (local)

###### Requisitos

- minikube
- kubectl

Na versão é local é feito do deploy do postgres e da aplicação.

Primeiro inicie o minikube:

```
minikube start
```

Após inicializado realize o deploy dos arquivos na seguinte ordem:

```
kubectl apply -f configmap/
kubectl apply -f volumes/
kubectl apply -f services/
kubectl apply -f deployments/
```

#### Google Cloud Kubernetes (gke)

###### Requisitos
- kubectl
- terraform
- gcloud

No gke é realizado apenas o deploy da aplicação e banco de dados é criado no **CloudSQL**.

Para que o deploy possa ser realizado primeiro é preciso criar o projeto e configurar como padrão no **gcloud**.

```
gcloud projects create io-opelt-message-scheduler
gcloud config set project io-opelt-message-scheduler
```

Para a criação de toda a infraestrutura no **google cloud** foi utilizado o **terraform**. Na configuração criada no diretório ```terraform``` irá ser criado um cluster com um node-pool separado com apenas um nó, um instância do postgresql no CloudSQL com usuário e database e uma VPC para habilitar a comunicação do cluster com o banco.


 Entre no diretório [terraform](terraform) e execute os seguintes comandos:

```
terraform init
terraform apply
```

O processo pode ser lento e chegar até 30 minutos de duração.

Após a criação é preciso somente substituir o ip do banco no configmap do aplicação pelo gerado dentro da VPC. O ip é exposto com o comando: 

```
gcloud sql instances describe master-instance | grep ipAddress
```

O valor devolvido deve ser copiado para o arquivo [kubernetes/gke/configmap/scheduler.yaml](kubernetes/gke/configmap/scheduler.yaml) na variavel ```DB_URL```.

Com toda a infra devidamente criada já é possível fazer o deploy dos arquivos do kubernetes, para configurar o ambiente do gke no kubectl local execute o comando:

```
gcloud container clusters get-credentials io-opelt-message-scheduler-gke --region us-central1-a
```

Então realize o deploy da aplicação:

```
kubectl apply -f configmap/
kubectl apply -f services/
kubectl apply -f deployments/
```

Caso queria apagar os recursos criados no google-cloud pode se executar o comando:

```
terraform destroy
```