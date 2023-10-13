# Esup-Agape
Application de Gestion des Aides Particulières aux Etudiants

## Introduction

Cette application permet la gestion des étudiants en situation de handicap. Les principales fonctionnalités sont :

* Gestion des dossiers étudiants
* Gestion des aides
* Gestion des aménagements d’examens
* Construction de l’enquête ministérielle

## Prérequis

* OpenJDK 17
* Maven 3.8
* PostgreSQL
* LDAP Supann

 ### Prérequis facultatifs
* Apogee
* Esup-signature

## Installation

### Configuration de l’application

La configuration s’effectue dans le fichier `src/main/resources/application.yml`.

Les paramètres préfixés par `application.` sont documentés ici :
`src/main/java/org/esupportail/esupagape/config/ApplicationProperties.java`

Les paramètres préfixés par `ldap.` sont documentés ici :
src/main/java/org/esupportail/esupagape/config/ldap/LdapProperties.java

Les parties APOGEE et Esup-signature sont facultatives.

### Configuration PostgreSQL

* pg_hba.conf : ajout de
```
host all all 127.0.0.1/32 password
```

* redémarrage de postgresql
* psql
```
create database esupagape;
create USER esupagape with password 'esup';
grant ALL ON DATABASE esupagape to esupagape;
ALTER DATABASE esupagape OWNER TO esupagape;
```
### Paramétrage mémoire JVM :

Pensez à paramétrer les espaces mémoire JVM :
```
export JAVA_OPTS="-Xms1024m -Xmx1024m -XX:MaxPermSize=256m"
```

Pour maven :
```
export MAVEN_OPTS="-Xms1024m -Xmx1024m -XX:MaxPermSize=256m"
```

## Compilation

```
mvn clean package
```

## Démarrage

```
mvn spring-boot:run
```
 ou 
```
java -jar target/esup-agape-x.x.x-SNAPSHOT.jar
```

## Licenses

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
